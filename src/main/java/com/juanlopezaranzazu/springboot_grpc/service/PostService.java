package com.juanlopezaranzazu.springboot_grpc.service;

import org.springframework.data.domain.Page;
import com.juanlopezaranzazu.springboot_grpc.entity.Post;

public interface PostService {

    Post createPost(String title, String content, String author);

    Post getPostById(Long id);

    Post updatePost(Long id, String title, String content, String author);

    void deletePost(Long id);

    Page<Post> listPosts(int page, int size);
}
