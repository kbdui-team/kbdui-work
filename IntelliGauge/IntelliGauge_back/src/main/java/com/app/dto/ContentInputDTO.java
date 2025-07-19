package com.app.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class ContentInputDTO {
    private Integer id;  // id, 主键

    private Integer lectureId;  // lecture_id
    private String contentType; // content_type, 内容类型
    private String fileName;  // file_name, 文件名
    private String filePath;  // file_path, 文件路径
    private Double fileSize;  // file_size, 文件大小
} 