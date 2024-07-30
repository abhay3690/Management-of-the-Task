package com.task.dto;

import lombok.Data;

import java.util.Date;

@Data
public class CommentDto {

    private Long id;

    private String content;

    private Date createAt;

    private Long taskId;

    private Long userId;

    private String postedBy;
}
