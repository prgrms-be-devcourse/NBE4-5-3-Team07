package com.java.NBE4_5_3_7.domain.member.controller

import com.java.NBE4_5_3_7.domain.member.dto.MemberDto
import com.java.NBE4_5_3_7.domain.member.entity.Member
import com.java.NBE4_5_3_7.domain.member.service.MemberService
import com.java.NBE4_5_3_7.global.Rq
import com.java.NBE4_5_3_7.global.dto.RsData
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/member")
class MemberController(
    private val rq: Rq,
    private val memberService: MemberService
) {

    @DeleteMapping("/logout")
    fun logout(): RsData<Unit> {
        rq.deleteCookie("accessToken")
        rq.deleteCookie("refreshToken")
        rq.deleteCookie("apiKey")
        return RsData("200-1", "로그아웃이 완료되었습니다.")
    }





    @GetMapping("/me")
    fun me(): RsData<MemberDto?> {
        val actor: Member? = rq.actorOrNull

        if (actor == null) {
            // 로그인되어 있지 않은 경우
            return RsData("200-2", "로그인된 사용자가 없습니다.")
        }

        val realActor: Member = rq.getRealActor(actor)

        return RsData(
            "200-1",
            "내 정보 조회가 완료되었습니다.",
            MemberDto(realActor)
        )
    }


    @PostMapping("/role")
    fun changeRoleToAdmin(@RequestParam id: Long): RsData<String> {
        return RsData("200-1", "관리자 권한 변경이 완료되었습니다.", memberService.changeRole(id))
    }

    @GetMapping("/{id}/isAdmin")
    fun isAdmin(@PathVariable id: Long): RsData<Boolean> {
        return RsData("200-1", "관리자 권한 확인이 완료되었습니다.", memberService.isAdmin(id))
    }
}
