package com.juanlopezaranzazu.springboot_grpc.mapper;

import com.juanlopezaranzazu.grpc.PostMessage;
import com.juanlopezaranzazu.springboot_grpc.entity.Post;

public final class PostMapper {

  private PostMapper() {
  }

  public static PostMessage toProto(Post post) {
    return PostMessage.newBuilder()
        .setId(post.getId())
        .setTitle(post.getTitle())
        .setContent(post.getContent())
        .setAuthor(post.getAuthor())
        .setCreatedAt(post.getCreatedAt() != null ? post.getCreatedAt().toString() : "")
        .setUpdatedAt(post.getUpdatedAt() != null ? post.getUpdatedAt().toString() : "")
        .build();
  }
}
