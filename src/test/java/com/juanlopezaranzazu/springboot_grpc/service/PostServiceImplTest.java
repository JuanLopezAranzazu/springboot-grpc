package com.juanlopezaranzazu.springboot_grpc.service;


import com.juanlopezaranzazu.springboot_grpc.entity.Post;
import com.juanlopezaranzazu.springboot_grpc.repository.PostRepository;

import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private Post samplePost;

    @BeforeEach
    void setUp() {
        samplePost = Post.builder()
                .id(1L)
                .title("Título de prueba")
                .content("Contenido de prueba")
                .author("Juan")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createPost_shouldPersistAndReturnPost() {
        when(postRepository.save(any(Post.class))).thenReturn(samplePost);

        Post result = postService.createPost("Título de prueba", "Contenido", "Juan");

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Título de prueba");
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    void getPostById_existingId_shouldReturnPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(samplePost));

        Post result = postService.getPostById(1L);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getPostById_nonExistingId_shouldThrowNotFound() {
        when(postRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> postService.getPostById(99L))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }

    @Test
    void updatePost_shouldChangeFields() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(samplePost));
        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));

        Post result = postService.updatePost(1L, "Nuevo título", "Nuevo contenido", "Pedro");

        assertThat(result.getTitle()).isEqualTo("Nuevo título");
        assertThat(result.getAuthor()).isEqualTo("Pedro");
    }

    @Test
    void deletePost_existingId_shouldCallDelete() {
        when(postRepository.existsById(1L)).thenReturn(true);
        doNothing().when(postRepository).deleteById(1L);

        assertThatCode(() -> postService.deletePost(1L)).doesNotThrowAnyException();
        verify(postRepository).deleteById(1L);
    }

    @Test
    void deletePost_nonExistingId_shouldThrowNotFound() {
        when(postRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> postService.deletePost(99L))
                .isInstanceOf(StatusRuntimeException.class)
                .hasMessageContaining("NOT_FOUND");
    }

    @Test
    void listPosts_shouldReturnPage() {
        Page<Post> page = new PageImpl<>(List.of(samplePost), PageRequest.of(0, 10), 1);
        when(postRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(page);

        Page<Post> result = postService.listPosts(0, 10);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).hasSize(1);
    }
}
