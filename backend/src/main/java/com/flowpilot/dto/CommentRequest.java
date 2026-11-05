package com.flowpilot.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentRequest {

    @NotBlank(message = "Comment text cannot be empty")
    private String commentText;

    public CommentRequest() {}

    public CommentRequest(String commentText) {
        this.commentText = commentText;
    }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { this.commentText = commentText; }
}
