#!/bin/bash

# 解压 RAR 和 ZIP 文件的通用脚本，遇到文件名冲突时自动重命名

function extract_file() {
    file_path="$1"   # 要解压的文件路径
    output_dir="$2"  # 解压到的目标目录

    # 如果输出目录为空，默认为当前目录
    if [ -z "$output_dir" ]; then
        output_dir="."
    fi

    # 检查文件是否存在
    if [ ! -f "$file_path" ]; then
        echo "错误：文件 $file_path 不存在！"
        exit 1
    fi

    # 获取文件扩展名
    file_ext="${file_path##*.}"
    file_ext=$(echo "$file_ext" | tr '[:upper:]' '[:lower:]') # 转换为小写

    # 解压文件到临时目录
    temp_dir="${output_dir}/temp_extracted"
    mkdir -p "$temp_dir"

    # 根据文件类型解压
    case "$file_ext" in
        zip)
            echo "正在解压 ZIP 文件：$file_path"
            unzip -o "$file_path" -d "$temp_dir"
            if [ $? -eq 0 ]; then
                echo "ZIP 文件解压完成，临时目录：$temp_dir"
            else
                echo "ZIP 文件解压失败！"
                rm -rf "$temp_dir"
                exit 1
            fi
            ;;
        rar)
            echo "正在解压 RAR 文件：$file_path"
            unrar x -o+ "$file_path" "$temp_dir"
            if [ $? -eq 0 ]; then
                echo "RAR 文件解压完成，临时目录：$temp_dir"
            else
                echo "RAR 文件解压失败！"
                rm -rf "$temp_dir"
                exit 1
            fi
            ;;
        *)
            echo "错误：不支持的文件类型 $file_ext！"
            rm -rf "$temp_dir"
            exit 1
            ;;
    esac

    # 检测文件冲突并重命名
    for extracted_file in "$temp_dir"/*; do
        base_file=$(basename "$extracted_file")
        target_file="$output_dir/$base_file"

        # 如果目标文件已存在，重命名
        if [ -e "$target_file" ]; then
            extension="${base_file##*.}"
            filename="${base_file%.*}"
            counter=1
            while [ -e "$output_dir/${filename}_${counter}.${extension}" ]; do
                counter=$((counter + 1))
            done
            mv "$extracted_file" "$output_dir/${filename}_${counter}.${extension}"
            echo "文件重命名为：${filename}_${counter}.${extension}"
        else
            mv "$extracted_file" "$output_dir/"
        fi
    done

    # 删除临时目录
    rm -rf "$temp_dir"
}

# 主程序入口
if [ $# -lt 1 ]; then
    echo "用法：$0 文件路径 [解压目录]"
    echo "示例：$0 example.zip output_dir"
    exit 1
fi

file_path="$1"
output_dir="$2"
if [ -n "$output_dir" ]; then
    mkdir -p "$output_dir"
fi

# 调用解压函数
extract_file "$file_path" "$output_dir"

# 删除不要的文件
echo "正在删除指定类型的文件..."
rm -rf $output_dir/*.txt $output_dir/*.log $output_dir/*.html $output_dir/www.*

echo "解压和清理完成！"
