package com.mdd.admin.service.impl.comic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.mdd.admin.service.comic.IComicDataService;
import com.mdd.admin.service.comic.IComicArticleService;
import com.mdd.admin.service.comic.ICategoryService;

import java.util.concurrent.CompletableFuture;
import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mdd.admin.service.impl.comic.model.ComicResponse;
import com.mdd.admin.service.impl.comic.model.Comic;
import com.mdd.admin.service.impl.comic.model.Article;
import com.mdd.admin.validate.comic.CategoryValidate;
import com.mdd.admin.validate.comic.ArticleValidate;
import com.mdd.admin.validate.comic.ChapterValidate;
import com.mdd.common.entity.comic.ComicItem;
import com.mdd.common.mapper.comic.ComicItemMapper;

import java.util.ArrayList;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.mdd.common.util.RedisUtils;
import com.mdd.common.util.StringUtils;

import java.util.Map;
import java.util.HashMap; // 添加缺失的导入语句

@RestController
@Slf4j
@Service
public class ComicDataServiceImpl implements IComicDataService {
    // 在类中添加以下字段定义
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    IComicArticleService iComicArticleService;

    @Autowired
    ICategoryService iCategoryService;

    @Autowired
    ComicItemMapper comicItemMapper;

    @Override
    public String queryComic() {
        // 生成Redis键
        String key = "comics_" + System.currentTimeMillis();
        String keyState = key + "_state";
        // 开启线程执行数据获取
        CompletableFuture.runAsync(() -> {
            List<ComicResponse> allComics = new ArrayList<>();
            int page = 1;
            // 获取第一页数据以确定总页数
            String url = "https://app.piaoxue6.com/api/app/comics?type=0&page=" + page;
            log.info("Fetching URL: {}", url);
            Map<String, Object> stateMap = new HashMap<>();
            ComicResponse response = restTemplate.getForObject(url, ComicResponse.class);
            if (response != null) {
                log.info("Received response with code: {} and total: {}", response.getCode(), response.getTotal());
                if (response.getCode() == 200) {
                    stateMap.put("state", 1);
                    allComics.add(response);
                    int total = response.getTotal();
                    int pagecount = response.getPagecount();
                    int percent = (1 / response.getPagecount()) * 100;
                    stateMap.put("percent", percent);
                    log.info("Total comics: {}, Page count: {}", total, pagecount);
                    // 获取剩余页面的数据

                    for (int i = 2; i <= pagecount; i++) {
                        url = "https://app.piaoxue6.com/api/app/comics?type=0&page=" + i;
                        log.info("Fetching URL: {}", url);
                        ComicResponse pageResponse = restTemplate.getForObject(url, ComicResponse.class);
                        if (pageResponse != null && pageResponse.getCode() == 200) {
                            allComics.add(pageResponse);
                            percent = (i / response.getPagecount()) * 100;
                            stateMap.put("percent", percent);
                            RedisUtils.set(keyState, stateMap);
                        } else {
                            stateMap.put("state", -1);
                            RedisUtils.set(keyState, stateMap);
                            return;
                        }
                    }
                }
            } else {
                log.warn("Received null response");
            }

            log.info("Returning {} comic responses", allComics.size());
            // 写入数据库，更新状态
            stateMap.put("percent", 100);
            RedisUtils.set(keyState, stateMap);
            Commit2Comic(allComics);
        });
        return key;
    }

    @Override
    public Object queryState(String key) {
        String keyState = key + "_state";
        return RedisUtils.get(keyState);
    }

    private void Commit2Comic(List<ComicResponse> allComics) {
        for (ComicResponse comic : allComics) {
            if (comic.getCode() != 200) {
                continue;
            }
            List<Comic> comics = comic.getList();
            for (Comic comicItem : comics) {
                if (!comicItem.getCategory().isEmpty()) {
                    CategoryValidate categoryValidate = new CategoryValidate();
                    categoryValidate.setTitle(comicItem.getCategory());
                    iCategoryService.commitSingleComic(categoryValidate, comicItem.getId());
                }
                ArticleValidate articleValidate = new ArticleValidate();
                articleValidate.setComicId(comicItem.getId());
                articleValidate.setTitle(comicItem.getTitle());
                articleValidate.setAuthor(comicItem.getAuthor());
                articleValidate.setCoverImage(comicItem.getCover_image());
                articleValidate.setDescript(comicItem.getDescript());
                articleValidate.setTags(StringUtils.str2List(comicItem.getTags(), ",", true, true));
                articleValidate.setType(comicItem.getType());
                articleValidate.setState(comicItem.getState());
                List<Article> articles = comicItem.getArticle();
                List<ChapterValidate> chapterValidates = new ArrayList<>();
                for (Article article : articles) {
                    ChapterValidate chapterValidate = new ChapterValidate();
                    chapterValidate.setNo(article.getId());
                    chapterValidate.setTitle(article.getTitle());
                    chapterValidates.add(chapterValidate);
                    comicItemMapper.delete(new QueryWrapper<ComicItem>().eq("comic_id", comicItem.getId())
                            .eq("chapter_no", article.getId()));
                    List<String> items = article.getItems();
                    for (String item : items) {
                        ComicItem itemArticle = new ComicItem();
                        itemArticle.setComicId(comicItem.getId());
                        itemArticle.setChapterNo(article.getId());
                        itemArticle.setUri(item);
                        comicItemMapper.insert(itemArticle);
                    }
                }
                articleValidate.setChapters(chapterValidates);
                iComicArticleService.save(articleValidate);
            }
        }
    }
}
