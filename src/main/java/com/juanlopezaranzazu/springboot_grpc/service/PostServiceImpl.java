package com.juanlopezaranzazu.springboot_grpc.service;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juanlopezaranzazu.springboot_grpc.entity.Post;
import com.juanlopezaranzazu.springboot_grpc.repository.PostRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    @Transactional
    public Post createPost(String title, String content, String author) {
        log.info("Creando post: title={}, author={}", title, author);

        Post post = Post.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();

        return postRepository.save(post);
    }

    @Override
    @Transactional(readOnly = true)
    public Post getPostById(Long id) {
        log.info("Obteniendo post con id={}", id);

        return postRepository.findById(id)
                .orElseThrow(() -> new StatusRuntimeException(
                        Status.NOT_FOUND.withDescription("Post con id=" + id + " no encontrado")
                ));
    }

    @Override
    @Transactional
    public Post updatePost(Long id, String title, String content, String author) {
        log.info("Actualizando post con id={}", id);

        Post post = getPostById(id);

        if (title   != null && !title.isBlank())   post.setTitle(title);
        if (content != null && !content.isBlank())  post.setContent(content);
        if (author  != null && !author.isBlank())   post.setAuthor(author);

        return postRepository.save(post);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        log.info("Eliminando post con id={}", id);

        if (!postRepository.existsById(id)) {
            throw new StatusRuntimeException(
                    Status.NOT_FOUND.withDescription("Post con id=" + id + " no encontrado")
            );
        }
        postRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Post> listPosts(int page, int size) {
        log.info("Listando posts: page={}, size={}", page, size);
        int safeSize = (size <= 0) ? 10 : Math.min(size, 100);
        return postRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, safeSize));
    }
}