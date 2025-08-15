package com.mdd.front.vo.upload;

import lombok.Data;

import java.io.Serializable;

@Data
public class UploadImagesVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer cid;
    private Integer type;
    private String name;
    private String url;
    private String uri;

}
