package com.juanlopezaranzazu.springboot_grpc.grpc;


import com.juanlopezaranzazu.grpc.*;
import com.juanlopezaranzazu.springboot_grpc.entity.Post;
import com.juanlopezaranzazu.springboot_grpc.mapper.PostMapper;
import com.juanlopezaranzazu.springboot_grpc.service.PostService;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class PostGrpcService extends PostServiceGrpc.PostServiceImplBase {

    private final PostService postService;

    @Override
    public void createPost(CreatePostRequest request,
                           StreamObserver<PostResponse> responseObserver) {
        try {
            validateCreateRequest(request);

            Post created = postService.createPost(
                    request.getTitle(),
                    request.getContent(),
                    request.getAuthor()
            );

            responseObserver.onNext(PostResponse.newBuilder()
                    .setPost(PostMapper.toProto(created))
                    .build());
            responseObserver.onCompleted();

        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Error al crear post", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error interno: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getPost(GetPostRequest request,
                        StreamObserver<PostResponse> responseObserver) {
        try {
            Post post = postService.getPostById(request.getId());

            responseObserver.onNext(PostResponse.newBuilder()
                    .setPost(PostMapper.toProto(post))
                    .build());
            responseObserver.onCompleted();

        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Error al obtener post id={}", request.getId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error interno: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void updatePost(UpdatePostRequest request,
                           StreamObserver<PostResponse> responseObserver) {
        try {
            Post updated = postService.updatePost(
                    request.getId(),
                    request.getTitle(),
                    request.getContent(),
                    request.getAuthor()
            );

            responseObserver.onNext(PostResponse.newBuilder()
                    .setPost(PostMapper.toProto(updated))
                    .build());
            responseObserver.onCompleted();

        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Error al actualizar post id={}", request.getId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error interno: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void deletePost(DeletePostRequest request,
                           StreamObserver<DeletePostResponse> responseObserver) {
        try {
            postService.deletePost(request.getId());

            responseObserver.onNext(DeletePostResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Post con id=" + request.getId() + " eliminado correctamente")
                    .build());
            responseObserver.onCompleted();

        } catch (StatusRuntimeException e) {
            responseObserver.onError(e);
        } catch (Exception e) {
            log.error("Error al eliminar post id={}", request.getId(), e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error interno: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void listPosts(ListPostsRequest request,
                          StreamObserver<ListPostsResponse> responseObserver) {
        try {
            Page<Post> page = postService.listPosts(request.getPage(), request.getSize());

            ListPostsResponse.Builder builder = ListPostsResponse.newBuilder()
                    .setTotalCount(page.getTotalElements());

            page.getContent().forEach(post -> builder.addPosts(PostMapper.toProto(post)));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Error al listar posts", e);
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Error interno: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private void validateCreateRequest(CreatePostRequest req) {
        if (req.getTitle().isBlank()) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("El campo 'title' es obligatorio")
                    .asRuntimeException();
        }
        if (req.getContent().isBlank()) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("El campo 'content' es obligatorio")
                    .asRuntimeException();
        }
        if (req.getAuthor().isBlank()) {
            throw Status.INVALID_ARGUMENT
                    .withDescription("El campo 'author' es obligatorio")
                    .asRuntimeException();
        }
    }
}
