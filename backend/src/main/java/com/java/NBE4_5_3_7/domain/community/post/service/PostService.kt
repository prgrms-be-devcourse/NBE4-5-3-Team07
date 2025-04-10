package com.java.NBE4_5_3_7.domain.community.post.service

import com.java.NBE4_5_3_7.domain.community.comment.dto.*
import com.java.NBE4_5_3_7.domain.community.comment.entity.Comment
import com.java.NBE4_5_3_7.domain.community.comment.repository.CommentRepository
import com.java.NBE4_5_3_7.domain.community.like.dto.LikeResponseDto
import com.java.NBE4_5_3_7.domain.community.like.entity.PostLike
import com.java.NBE4_5_3_7.domain.community.like.repository.PostLikeRepository
import com.java.NBE4_5_3_7.domain.community.post.dto.*
import com.java.NBE4_5_3_7.domain.community.post.entity.Post
import com.java.NBE4_5_3_7.domain.community.post.repository.PostRepository
import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.repository.MemberRepository
import jakarta.transaction.Transactional
import org.redisson.api.RedissonClient
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.lang.reflect.Field
import java.util.concurrent.TimeUnit

@Transactional
@Service
class PostService(
    private val postRepository: PostRepository,
    private val memberRepository: MemberRepository,
    private val likeRepository: PostLikeRepository,
    private val commentRepository: CommentRepository,
    private val redissonClient: RedissonClient
) {
    fun showPost(postId: Long, currentMemberId: Long?): PostResponseDto {
        val post = postRepository.findById(postId).orElseThrow { RuntimeException("해당 게시글을 찾을 수 없습니다.") }

        val likedByCurrentUser = currentMemberId?.let {
            likeRepository.existsByPostPostIdAndMemberId(postId, it)
        } ?: false // 로그인 안 된 경우 false 처리

        // Redis 캐시에 저장된 좋아요 카운트 사용 (없으면 DB count로 대체)
        val redisKey = "post:like:$postId"
        val atomicLong = redissonClient.getAtomicLong(redisKey)
        val likeCount = if (atomicLong.isExists) atomicLong.get().toInt()
        else likeRepository.countByPostPostId(postId) ?: 0


        return PostResponseDto(
            id = post.postId,
            authorName = maskLastCharacter(getMemberField(post.author, "nickname") as? String),
            postTime = post.createdAt,
            title = post.title,
            content = post.content,
            like = likeCount,
            likedByCurrentUser = likedByCurrentUser,
            comments = getComments(post, currentMemberId)
        )
    }

    fun addPost(memberId: Long, postRequestDto: AddPostRequestDto): PostResponseDto {
        val author = memberRepository.findById(memberId).orElseThrow { RuntimeException("해당 멤버를 찾을 수 없습니다.") }

        // Post 생성 및 필드 설정
        val newPost = Post()
        newPost.update(EditPostRequestDto(0, postRequestDto.title, postRequestDto.content))

        // author 필드 설정 (리플렉션 사용)
        setField(newPost, "author", author)

        val savedPost = postRepository.save(newPost)

        return PostResponseDto(
            id = savedPost.postId,
            authorName = maskLastCharacter(getMemberField(author, "nickname") as? String),
            postTime = savedPost.createdAt,
            title = savedPost.title,
            content = savedPost.content,
            like = 0,
            likedByCurrentUser = false,
            comments = emptyList()
        )
    }

    fun editPost(memberId: Long, editPostRequestDto: EditPostRequestDto): PostResponseDto {
        val user = memberRepository.findById(memberId).orElseThrow { RuntimeException("해당 멤버를 찾을 수 없습니다.") }
        val post = postRepository.findById(editPostRequestDto.postId ?: 0).orElseThrow { RuntimeException("해당 게시글을 찾을 수 없습니다.") }

        // isAdmin 및 author.id 확인 (리플렉션 사용)
        val isAdmin = user.isAdmin
        val authorId = getMemberField(post.author, "id") as? Long

        if (!isAdmin && authorId != memberId) {
            throw RuntimeException("수정 권한이 없습니다.")
        }

        post.update(editPostRequestDto)

        val likeCount = likeRepository.countByPostPostId(post.postId) ?: 0

        val postId = post.postId ?: throw IllegalStateException("게시글 ID가 존재하지 않습니다.")
        val likedByCurrentUser = likeRepository.existsByPostPostIdAndMemberId(postId, memberId)

        return PostResponseDto(
            id = post.postId,
            authorName = maskLastCharacter(getMemberField(user, "nickname") as? String),
            postTime = post.updatedAt,
            title = post.title,
            content = post.content,
            like = likeCount,
            likedByCurrentUser = likedByCurrentUser,
            comments = getComments(post, memberId)
        )
    }

    fun deletePost(memberId: Long, postId: Long): Long {
        val user = memberRepository.findById(memberId).orElseThrow { RuntimeException("해당 멤버를 찾을 수 없습니다.") }
        val post = postRepository.findById(postId).orElseThrow { RuntimeException("해당 게시글을 찾을 수 없습니다.") }

        // isAdmin 및 author.id 확인 (리플렉션 사용)
        val isAdmin = user.isAdmin
        val authorId = getMemberField(post.author, "id") as? Long


        if (!isAdmin && authorId != memberId) {
            throw RuntimeException("삭제 권한이 없습니다.")
        }

        postRepository.deleteById(postId)
        return postId
    }

    fun getPostList(page: Int, size: Int): Page<PostListResponseDto> {
        val result = postRepository.findAllPostList(PageRequest.of(page, size))
        return processPostListPage(result)
    }

    fun getPostLikeDesc(page: Int, size: Int): Page<PostListResponseDto> {
        val result = postRepository.findAllOrderByLikesDesc(PageRequest.of(page, size))
        return processPostListPage(result)
    }

    fun getPostLikeAsc(page: Int, size: Int): Page<PostListResponseDto> {
        val result = postRepository.findAllOrderByLikesAsc(PageRequest.of(page, size))
        return processPostListPage(result)
    }

    fun getPostCommentDesc(page: Int, size: Int): Page<PostListResponseDto> {
        val result = postRepository.findAllOrderByCommentsDesc(PageRequest.of(page, size))
        return processPostListPage(result)
    }

    fun getPostCommentAsc(page: Int, size: Int): Page<PostListResponseDto> {
        val result = postRepository.findAllOrderByCommentsAsc(PageRequest.of(page, size))
        return processPostListPage(result)
    }

    fun getPostCreatedAtDesc(page: Int, size: Int): Page<PostListResponseDto> {
        val result = postRepository.findAllOrderByCreatedAtDesc(PageRequest.of(page, size))
        return processPostListPage(result)
    }

    // Page<PostListResponseDto?>? 타입을 처리하기 위한 헬퍼 메서드
    private fun processPostListPage(page: Page<PostListResponseDto?>?): Page<PostListResponseDto> {
        if (page == null) {
            return PageImpl(emptyList())
        }

        val processedContent = page.content.filterNotNull().map { dto ->
            dto.apply {
                author = maskLastCharacter(author)
            }
        }

        return PageImpl(
            processedContent,
            page.pageable,
            page.totalElements
        )
    }

    fun addComment(memberId: Long, dto: AddCommentRequestDto): CommentResponseDto {
        val post = postRepository.findById(dto.articleId ?: 0).orElseThrow { RuntimeException("Post not found") }
        val author = memberRepository.findById(memberId).orElseThrow { RuntimeException("해당 멤버를 찾을 수 없습니다.") }
        val parent = dto.parentId?.let {
            commentRepository.findById(it).orElseThrow { RuntimeException("Parent comment not found") }
        }

        // Comment 생성자 파라미터 준비
        val comment = Comment()
        setField(comment, "comment", dto.comment)
        setField(comment, "post", post)
        setField(comment, "author", author)
        setField(comment, "parent", parent)

        val saved = commentRepository.save(comment)

        return CommentResponseDto(
            post.postId ?: 0,
            saved.id ?: 0,
            maskLastCharacter(getMemberField(saved.author, "nickname") as? String),
            saved.createdDate,
            saved.comment ?: "",
            0,
            true
        )
    }

    fun editComment(memberId: Long, dto: EditCommentRequestDto): CommentResponseDto {
        val user = memberRepository.findById(memberId).orElseThrow { RuntimeException("해당 멤버를 찾을 수 없습니다.") }
        val comment = commentRepository.findById(dto.commentId ?: 0).orElseThrow { RuntimeException("해당 댓글을 찾을 수 없습니다.") }

        // isAdmin 및 author.id 확인 (리플렉션 사용)
//        val isAdmin = getMemberField(user, "isAdmin") as? Boolean ?: false
//        val authorId = getMemberField(comment.author, "id") as? Long

        val isAdmin = user.isAdmin
        val authorId = comment.author?.id

        if (!isAdmin && authorId != memberId) {
            throw RuntimeException("수정 권한이 없습니다.")
        }

        comment.update(dto.comment)
        val updated = commentRepository.save(comment)

        return CommentResponseDto(
            comment.post?.postId ?: 0,
            updated.id ?: 0,
            maskLastCharacter(getMemberField(updated.author, "nickname") as? String),
            updated.createdDate,
            updated.comment ?: "",
            updated.children?.size ?: 0,
            true
        )
    }

    fun deleteComment(memberId: Long, commentId: Long) {
        val user = memberRepository.findById(memberId).orElseThrow { RuntimeException("해당 멤버를 찾을 수 없습니다.") }
        val comment = commentRepository.findById(commentId).orElseThrow { RuntimeException("해당 댓글을 찾을 수 없습니다.") }

        val isAdmin = user.isAdmin
        val authorId = comment.author?.id

        if (!isAdmin && authorId != memberId) {
            throw RuntimeException("삭제 권한이 없습니다.")
        }

        comment.parent?.children?.remove(comment)
        commentRepository.delete(comment)
    }

    fun showReComments(commentId: Long, currentMemberId: Long?): List<CommentResponseDto> {
        val comment = commentRepository.findById(commentId).orElseThrow { RuntimeException("Comment not found") }

        return comment.children?.filterNotNull()?.map { childComment ->
            val authorId = getMemberField(childComment.author, "id") as? Long

            CommentResponseDto(
                comment.post?.postId ?: 0,
                childComment.id ?: 0,
                maskLastCharacter(getMemberField(childComment.author, "nickname") as? String),
                childComment.createdDate,
                childComment.comment ?: "",
                childComment.children?.size ?: 0,
                authorId == currentMemberId
            )
        } ?: emptyList()
    }

//    fun postLike(memberId: Long, postId: Long): LikeResponseDto {
//        val post = postRepository.findById(postId).orElseThrow { RuntimeException("해당 게시글을 찾을 수 없습니다.") }
//        val member = memberRepository.findById(memberId).orElseThrow { RuntimeException("해당 멤버를 찾을 수 없습니다.") }
//        val lockKey = "lock:post:like$postId"
//        val lock = redissonClient.getLock(lockKey)
//
//        var isLocked = false
//        try {
//            isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS)
//            if (!isLocked) throw RuntimeException("시스템이 바빠 요청을 처리할 수 없습니다. 잠시 후 다시 시도해주세요.")
//
//            val existingLike = likeRepository.findByPostAndMember(post, member)
//            return if (existingLike != null && existingLike.isPresent) {
//                likeRepository.delete(existingLike.get())
//                LikeResponseDto(postId, likeRepository.countByPostPostId(postId) ?: 0, "좋아요 취소")
//            } else {
//                val newLike = PostLike(null, post, member)
//                likeRepository.save(newLike)
//                LikeResponseDto(postId, likeRepository.countByPostPostId(postId) ?: 0, "좋아요 추가")
//            }
//        } catch (e: InterruptedException) {
//            Thread.currentThread().interrupt()
//            throw RuntimeException("락 획득 중 인터럽트 발생")
//        } finally {
//            if (isLocked && lock.isHeldByCurrentThread) lock.unlock()
//        }
//    }

    /**
     * 좋아요 처리에 캐시(Redis)를 적용한 메서드.
     * 기존처럼 DB에서 like 레코드 여부를 확인하지만, 이후의 좋아요 카운트는 Redis AtomicLong으로 관리.
     */
    fun postLike(memberId: Long, postId: Long): LikeResponseDto {
        val post = postRepository.findById(postId).orElseThrow { RuntimeException("해당 게시글을 찾을 수 없습니다.") }
        val member = memberRepository.findById(memberId).orElseThrow { RuntimeException("해당 멤버를 찾을 수 없습니다.") }
        val lockKey = "lock:post:like$postId"
        val lock = redissonClient.getLock(lockKey)
        val redisKey = "post:likes:$postId"
        val redis = redissonClient.getBucket<Int>(redisKey)

        var isLocked = false
        try {
            isLocked = lock.tryLock(5, 10, TimeUnit.SECONDS)
            if (!isLocked) throw RuntimeException("시스템이 바빠 요청을 처리할 수 없습니다. 잠시 후 다시 시도해주세요.")

            val existingLike = likeRepository.findByPostAndMember(post, member)
            val currentLikes = redis.get() ?: likeRepository.countByPostPostId(postId) ?: 0

            return if (existingLike != null && existingLike.isPresent) {
                likeRepository.delete(existingLike.get())
                redis.set(currentLikes - 1)
                LikeResponseDto(postId, currentLikes - 1, "좋아요 취소")
            } else {
                val newLike = PostLike(null, post, member)
                likeRepository.save(newLike)
                redis.set(currentLikes + 1)
                LikeResponseDto(postId, currentLikes + 1, "좋아요 추가")
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw RuntimeException("락 획득 중 인터럽트 발생")
        } finally {
            if (isLocked && lock.isHeldByCurrentThread) lock.unlock()
        }
    }


    fun myPost(memberId: Long, page: Int, size: Int): List<PostListResponseDto> {
        val member = memberRepository.findById(memberId).orElseThrow { RuntimeException("해당 멤버를 찾을 수 없습니다.") }
        val posts = postRepository.findAllByAuthor_Id(memberId) ?: return emptyList()

        return posts.filterNotNull().map { post ->
            PostListResponseDto(
                post.postId,
                post.title,
                maskLastCharacter(getMemberField(post.author, "nickname") as? String),
                post.createdAt
            )
        }
    }

    fun getComments(post: Post, currentMemberId: Long?): List<CommentResponseDto> {
        return post.comments?.filterNotNull()?.filter { it.parent == null }?.map { comment ->
            val authorId = getMemberField(comment.author, "id") as? Long

            CommentResponseDto(
                comment.post?.postId ?: 0,
                comment.id ?: 0,
                maskLastCharacter(getMemberField(comment.author, "nickname") as? String),
                comment.createdDate,
                comment.comment ?: "",
                comment.children?.size ?: 0,
                authorId == currentMemberId
            )
        } ?: emptyList()
    }

    // 범용 리플렉션 헬퍼 메서드들
    private fun getMemberField(obj: Any?, fieldName: String): Any? {
        if (obj == null) return null
        return try {
            // 1. 먼저 필드 찾기 시도
            val field = findField(obj.javaClass, fieldName)
            field.isAccessible = true
            field.get(obj)
        } catch (e: NoSuchFieldException) {
            try {
                // 2. 실패 시 getter 메서드 접근 시도
                val method = obj.javaClass.getMethod("get${fieldName.replaceFirstChar { it.uppercaseChar() }}")
                method.invoke(obj)
            } catch (ex: Exception) {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

//    private fun getMemberField(obj: Any?, fieldName: String): Any? {
//        if (obj == null) return null
//        return try {
//            val field = findField(obj.javaClass, fieldName)
//            field.isAccessible = true
//            field.get(obj)
//        } catch (e: Exception) {
//            null
//        }
//    }

    private fun setField(obj: Any, fieldName: String, value: Any?) {
        try {
            val field = findField(obj.javaClass, fieldName)
            field.isAccessible = true
            field.set(obj, value)
        } catch (e: Exception) {
            // 실패 시 조용히 무시
        }
    }

    private fun findField(clazz: Class<*>, fieldName: String): Field {
        var currentClass: Class<*>? = clazz
        while (currentClass != null) {
            try {
                return currentClass.getDeclaredField(fieldName)
            } catch (e: NoSuchFieldException) {
                currentClass = currentClass.superclass
            }
        }
        throw NoSuchFieldException("Field $fieldName not found in class hierarchy of ${clazz.name}")
    }

    companion object {
        fun maskLastCharacter(word: String?): String? {
            if (word.isNullOrEmpty()) return word
            return word.dropLast(1) + "*"
        }
    }

    /**
     * 3분마다 Redis에 저장된 좋아요 카운트를 DB에 반영하는 스케줄러.
     * 각 키명은 "post:like:{postId}"로 구성되며, 필요에 따라 Post 엔티티의
     * 좋아요 카운트 컬럼(예: likeCount)에 업데이트 할 수 있습니다.
     */
    @Scheduled(fixedRate = 180000)
    fun flushLikeCountsToDB() {
        // Redisson의 keys 기능을 통해 관련 키들 조회 (패턴: "post:like:*")
        val keys = redissonClient.keys.getKeysByPattern("post:like:*")
        for (key in keys) {
            val postIdStr = key.substringAfter("post:like:")
            val postId = postIdStr.toLongOrNull() ?: continue
            val atomicLong = redissonClient.getAtomicLong(key)
            val redisCount = atomicLong.get()
            // DB에 반영하는 로직 구현 (예: Post 엔티티의 likeCount 업데이트)
            // 예시) postRepository.updateLikeCount(postId, redisCount)
            println("Flushing postId $postId with likeCount $redisCount to DB")
        }
    }
}