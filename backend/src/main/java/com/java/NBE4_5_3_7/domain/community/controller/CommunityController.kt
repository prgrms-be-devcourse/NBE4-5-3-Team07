package com.java.NBE4_5_3_7.domain.community.controller

import com.java.NBE4_5_3_7.domain.community.comment.dto.AddCommentRequestDto
import com.java.NBE4_5_3_7.domain.community.comment.dto.CommentResponseDto
import com.java.NBE4_5_3_7.domain.community.comment.dto.EditCommentRequestDto
import com.java.NBE4_5_3_7.domain.community.like.dto.LikeResponseDto
import com.java.NBE4_5_3_7.domain.community.post.dto.AddPostRequestDto
import com.java.NBE4_5_3_7.domain.community.post.dto.EditPostRequestDto
import com.java.NBE4_5_3_7.domain.community.post.dto.PostListResponseDto
import com.java.NBE4_5_3_7.domain.community.post.dto.PostResponseDto
import com.java.NBE4_5_3_7.domain.community.post.service.PostService
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/community")
class CommunityController // RequiredArgsConstructor 대신 명시적 생성자
    (private val postService: PostService, private val memberService: MemberService) {
    @PostMapping("/article/post")
    fun articlePost(@RequestBody postRequestDto: AddPostRequestDto): ResponseEntity<PostResponseDto> {
        return ResponseEntity.ok(postService.addPost(memberService.idFromRq, postRequestDto))
    }

    @PostMapping("/article/edit")
    fun articleEdit(@RequestBody editRequestDto: EditPostRequestDto): ResponseEntity<PostResponseDto> {
        return ResponseEntity.ok(postService.editPost(memberService.idFromRq, editRequestDto))
    }

    @PostMapping("/article/delete")
    fun articleDelete(postId: Long): ResponseEntity<Long> {
        return ResponseEntity.ok(postService.deletePost(memberService.idFromRq, postId))
    }

    @GetMapping("/article")
    fun showPost(@RequestParam("id") id: Long): ResponseEntity<PostResponseDto> {
        return ResponseEntity.ok(postService.showPost(id, memberService.idFromRq))
    }

    @GetMapping("/article/list/title")
    fun getPosts(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PostListResponseDto>> {
        return ResponseEntity.ok(postService.getPostList(page, size))
    }

    @GetMapping("/article/list/like/desc")
    fun getPostsLikeDesc(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PostListResponseDto>> {
        return ResponseEntity.ok(postService.getPostLikeDesc(page, size))
    }

    @GetMapping("/article/list/like/asc")
    fun getPostsLikeAsc(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PostListResponseDto>> {
        return ResponseEntity.ok(postService.getPostLikeAsc(page, size))
    }

    @GetMapping("/article/list/comment/desc")
    fun getPostsCommentDesc(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PostListResponseDto>> {
        return ResponseEntity.ok(postService.getPostCommentDesc(page, size))
    }

    @GetMapping("/article/list/comment/asc")
    fun getPostsCommentAsc(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PostListResponseDto>> {
        return ResponseEntity.ok(postService.getPostCommentAsc(page, size))
    }

    @GetMapping("/article/list/createdat/desc")
    fun getPostsCreatedAtDesc(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<Page<PostListResponseDto>> {
        return ResponseEntity.ok(postService.getPostCreatedAtDesc(page, size))
    }

    @PostMapping("/comment/add")
    fun addComment(@RequestBody dto: AddCommentRequestDto): ResponseEntity<CommentResponseDto> {
        val memberId = memberService.idFromRq
        val response = postService.addComment(memberId, dto)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/comment/edit")
    fun editComment(@RequestBody dto: EditCommentRequestDto): ResponseEntity<CommentResponseDto> {
        val memberId = memberService.idFromRq
        val response = postService.editComment(memberId, dto)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/comment/delete")
    fun deleteComment(@RequestParam commentId: Long): ResponseEntity<Void> {
        val memberId = memberService.idFromRq
        postService.deleteComment(memberId, commentId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/comment/re")
    fun showReComments(@RequestParam commentId: Long): ResponseEntity<List<CommentResponseDto>> {
        return ResponseEntity.ok(postService.showReComments(commentId, memberService.idFromRq))
    }

    @GetMapping("/post/like")
    fun postLike(@RequestParam postId: Long): ResponseEntity<LikeResponseDto> {
        return ResponseEntity.ok(postService.postLike(memberService.idFromRq, postId))
    }

    @GetMapping("/post/my")
    fun myPost(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<PostListResponseDto>> {
        return ResponseEntity.ok(postService.myPost(memberService.idFromRq, page, size))
    }
}
