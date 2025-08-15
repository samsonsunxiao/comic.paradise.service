#!/bin/bash

# 将指定目录压缩成指定的文件名（支持 ZIP 和 RAR 格式）

function compress_directory() {
    input_dir="$1"    # 要压缩的目录
    output_file="$2"  # 压缩后的文件名

    # 检查输入目录是否存在
    if [ ! -d "$input_dir" ]; then
        echo "错误：目录 $input_dir 不存在！"
        exit 1
    fi

    # 获取文件扩展名（zip 或 rar）
    file_ext="${output_file##*.}"
    file_ext=$(echo "$file_ext" | tr '[:upper:]' '[:lower:]') # 转换为小写

    # 根据文件扩展名选择压缩方式
    case "$file_ext" in
        zip)
            echo "正在将目录 $input_dir 压缩为 ZIP 文件：$output_file"
            zip -r "$output_file" "$input_dir" >/dev/null
            if [ $? -eq 0 ]; then
                echo "压缩成功：$output_file"
            else
                echo "压缩失败！"
                exit 1
            fi
            ;;
        rar)
            echo "正在将目录 $input_dir 压缩为 RAR 文件：$output_file"
            rar a -r "$output_file" "$input_dir" >/dev/null
            if [ $? -eq 0 ]; then
                echo "压缩成功：$output_file"
            else
                echo "压缩失败！"
                exit 1
            fi
            ;;
        *)
            echo "错误：不支持的压缩格式 $file_ext！请使用 zip 或 rar 作为文件扩展名。"
            exit 1
            ;;
    esac
}

# 主程序入口
if [ $# -lt 2 ]; then
    echo "用法：$0 目录路径 输出文件名"
    echo "示例：$0 /path/to/directory output.zip"
    echo "示例：$0 /path/to/directory output.rar"
    exit 1
fi

input_dir="$1"    # 要压缩的目录
output_file="$2"  # 压缩后的文件名

# 调用压缩函数
compress_directory "$input_dir" "$output_file"
