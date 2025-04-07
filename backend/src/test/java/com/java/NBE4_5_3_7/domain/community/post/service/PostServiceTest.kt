package com.java.NBE4_5_3_7.domain.community.post.service

import com.java.NBE4_5_3_7.domain.community.comment.dto.AddCommentRequestDto
import com.java.NBE4_5_3_7.domain.community.comment.dto.EditCommentRequestDto
import com.java.NBE4_5_3_7.domain.community.comment.entity.Comment
import com.java.NBE4_5_3_7.domain.community.comment.repository.CommentRepository
import com.java.NBE4_5_3_7.domain.community.like.entity.PostLike
import com.java.NBE4_5_3_7.domain.community.post.entity.Post
import com.java.NBE4_5_3_7.domain.community.post.repository.PostRepository
import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.entity.Role
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.redisson.api.RedissonClient
import java.util.*
import kotlin.test.assertEquals

class PostServiceTest {

 private lateinit var postService: PostService

 private val postRepository: PostRepository = mockk()
 private val memberRepository: MemberRepository = mockk()
 private val commentRepository: CommentRepository = mockk()
 private val likeRepository = mockk<com.java.NBE4_5_3_7.domain.community.like.repository.PostLikeRepository>()
 private val redissonClient: RedissonClient = mockk()

 @BeforeEach
 fun setUp() {
  postService = PostService(
   postRepository = postRepository,
   memberRepository = memberRepository,
   commentRepository = commentRepository,
   likeRepository = likeRepository,
   redissonClient = redissonClient
  )
 }

 @Test
 fun comment_add_success() {
  // given
  val memberId = 1L
  val postId = 10L
  val member = Member().apply {
   id = memberId
   nickname = "최재우"
  }

  val post = Post()
  val postIdField = post.javaClass.getDeclaredField("postId")
  postIdField.isAccessible = true
  postIdField.set(post, postId)

  val dto = AddCommentRequestDto(postId, "댓글 내용입니다", null)

  every { memberRepository.findById(memberId) } returns Optional.of(member)
  every { postRepository.findById(postId) } returns Optional.of(post)
  every { commentRepository.save(any()) } answers { firstArg() }

  // when
  val result = postService.addComment(memberId, dto)

  // then
  assertEquals(postId, result.articleId)
  assertEquals("최재*", result.commentAuthorName)
  assertEquals("댓글 내용입니다", result.comment)
  assertEquals(true, result.isMyComment)

  verify(exactly = 1) { commentRepository.save(any()) }
 }



 @Test
 fun edit_comment_success_by_author() {
  // given
  val memberId = 1L
  val commentId = 100L
  val originalComment = "이전 댓글"
  val updatedContent = "수정된 댓글"

  val member = Member()
  setPrivate(member, "_id", memberId)
  member.nickname = "작성자"
  member.role = Role.USER

  val comment = Comment()
  setPrivate(comment, "id", commentId)
  setPrivate(comment, "comment", originalComment)
  setPrivate(comment, "author", member)

  val post = Post()
  setPrivate(post, "postId", 1L)
  setPrivate(comment, "post", post)

  val dto = EditCommentRequestDto(commentId, updatedContent)

  every { memberRepository.findById(memberId) } returns Optional.of(member)
  every { commentRepository.findById(commentId) } returns Optional.of(comment)
  every { commentRepository.save(any()) } answers { firstArg() }

  // when
  val result = postService.editComment(memberId, dto)

  // then
  assertEquals(updatedContent, result.comment)
  assertEquals("작성*", result.commentAuthorName)
  verify(exactly = 1) { commentRepository.save(any()) }
 }

 fun setPrivate(target: Any, fieldName: String, value: Any?) {
  var currentClass: Class<*>? = target.javaClass
  while (currentClass != null) {
   try {
    val field = currentClass.getDeclaredField(fieldName)
    field.isAccessible = true
    field.set(target, value)
    return
   } catch (e: NoSuchFieldException) {
    currentClass = currentClass.superclass
   }
  }
  throw NoSuchFieldException("Field $fieldName not found in class hierarchy of ${target.javaClass.name}")
 }

 @Test
 fun delete_comment_success_by_author() {
  // given
  val memberId = 1L
  val commentId = 100L

  val member = Member()
  setPrivate(member, "_id", memberId)
  member.nickname = "작성자"
  member.role = Role.USER

  val comment = Comment()
  setPrivate(comment, "id", commentId)
  setPrivate(comment, "author", member)

  every { memberRepository.findById(memberId) } returns Optional.of(member)
  every { commentRepository.findById(commentId) } returns Optional.of(comment)
  every { commentRepository.delete(comment) } just Runs

  // when
  postService.deleteComment(memberId, commentId)

  // then
  verify(exactly = 1) { commentRepository.delete(comment) }
 }


 @Test
 fun show_re_comments_success() {
  // given
  val parentId = 200L
  val currentUserId = 1L

  val author1 = Member()
  setPrivate(author1, "_id", 2L)
  author1.nickname = "유저1"

  val author2 = Member()
  setPrivate(author2, "_id", currentUserId)
  author2.nickname = "유저2"

  val child1 = Comment()
  setPrivate(child1, "id", 201L)
  setPrivate(child1, "author", author1)
  setPrivate(child1, "comment", "첫 번째 대댓글")

  val child2 = Comment()
  setPrivate(child2, "id", 202L)
  setPrivate(child2, "author", author2)
  setPrivate(child2, "comment", "내 댓글")

  val parent = Comment()
  setPrivate(parent, "id", parentId)
  setPrivate(parent, "children", listOf(child1, child2))
  setPrivate(parent, "post", Post().apply {
   setPrivate(this, "postId", 99L)
  })

  every { commentRepository.findById(parentId) } returns Optional.of(parent)

  // when
  val result = postService.showReComments(parentId, currentUserId)

  // then
  assertEquals(2, result.size)
  assertEquals("유저*", result[0].commentAuthorName)
  assertEquals(false, result[0].isMyComment)
  assertEquals("유저*", result[1].commentAuthorName)
  assertEquals(true, result[1].isMyComment)
 }

 @Test
 fun post_like_toggle_success() {
  // given
  val postId = 1L
  val memberId = 10L

  val post = Post().apply {
   setPrivate(this, "postId", postId)
  }

  val member = Member().apply {
   setPrivate(this, "_id", memberId)
  }

  val lock = mockk<org.redisson.api.RLock>()

  every { postRepository.findById(postId) } returns Optional.of(post)
  every { memberRepository.findById(memberId) } returns Optional.of(member)
  every { redissonClient.getLock("lock:post:like$postId") } returns lock
  every { lock.tryLock(any(), any(), any()) } returns true
  every { lock.isHeldByCurrentThread } returns true
  every { lock.unlock() } just Runs

  // 좋아요가 존재하지 않을 때 → 새로 추가
  every { likeRepository.findByPostAndMember(post, member) } returns Optional.empty()
  every { likeRepository.save(any()) } answers { firstArg() }
  every { likeRepository.countByPostPostId(postId) } returns 1

  // when
  val result = postService.postLike(memberId, postId)

  // then
  assertEquals("좋아요 추가", result.message)
  assertEquals(1, result.likeCount)

  verify { likeRepository.save(any()) }
  verify(exactly = 1) { lock.unlock() }
 }

 @Test
 fun post_like_toggle_cancel_success() {
  // given
  val postId = 1L
  val memberId = 10L

  val post = Post().apply {
   setPrivate(this, "postId", postId)
  }

  val member = Member().apply {
   setPrivate(this, "_id", memberId)
  }

  val lock = mockk<org.redisson.api.RLock>()

  every { postRepository.findById(postId) } returns Optional.of(post)
  every { memberRepository.findById(memberId) } returns Optional.of(member)
  every { redissonClient.getLock("lock:post:like$postId") } returns lock
  every { lock.tryLock(any(), any(), any()) } returns true
  every { lock.isHeldByCurrentThread } returns true
  every { lock.unlock() } just Runs

  // 좋아요가 이미 존재할 때 → 삭제
  val postLike = PostLike(null, post, member)
  every { likeRepository.findByPostAndMember(post, member) } returns Optional.of(postLike)
  every { likeRepository.delete(postLike) } just Runs
  every { likeRepository.countByPostPostId(postId) } returns 0

  // when
  val result = postService.postLike(memberId, postId)

  // then
  assertEquals("좋아요 취소", result.message)
  assertEquals(0, result.likeCount)

  verify { likeRepository.delete(postLike) }
  verify(exactly = 1) { lock.unlock() }
 }
 @Test
 fun post_like_lock_fail_throws_exception() {
  // given
  val postId = 1L
  val memberId = 10L

  val post = Post().apply {
   setPrivate(this, "postId", postId)
  }

  val member = Member().apply {
   setPrivate(this, "_id", memberId)
  }

  val lock = mockk<org.redisson.api.RLock>()

  every { postRepository.findById(postId) } returns Optional.of(post)
  every { memberRepository.findById(memberId) } returns Optional.of(member)
  every { redissonClient.getLock("lock:post:like$postId") } returns lock
  every { lock.tryLock(any(), any(), any()) } returns false // ⚠️ 락 실패

  // when & then
  val exception = assertThrows<RuntimeException> {
   postService.postLike(memberId, postId)
  }

  assertEquals("시스템이 바빠 요청을 처리할 수 없습니다. 잠시 후 다시 시도해주세요.", exception.message)
  verify(exactly = 0) { likeRepository.save(any()) }
  verify(exactly = 0) { likeRepository.delete(any()) }
 }

 @Test
 fun post_like_lock_interrupt_throws_exception() {
  // given
  val postId = 1L
  val memberId = 10L

  val post = Post().apply {
   setPrivate(this, "postId", postId)
  }

  val member = Member().apply {
   setPrivate(this, "_id", memberId)
  }

  val lock = mockk<org.redisson.api.RLock>()

  every { postRepository.findById(postId) } returns Optional.of(post)
  every { memberRepository.findById(memberId) } returns Optional.of(member)
  every { redissonClient.getLock("lock:post:like$postId") } returns lock
  every { lock.tryLock(any(), any(), any()) } throws InterruptedException("테스트 인터럽트")

  // when & then
  val exception = assertThrows<RuntimeException> {
   postService.postLike(memberId, postId)
  }

  assertEquals("락 획득 중 인터럽트 발생", exception.message)
  verify(exactly = 0) { likeRepository.save(any()) }
  verify(exactly = 0) { likeRepository.delete(any()) }
  verify(exactly = 0) { lock.unlock() } // 락 잡기 전에 예외 → unlock 호출 안 됨
 }


}
