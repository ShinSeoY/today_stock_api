package com.todaystock.api.controller

import com.todaystock.api.dto.common.ApiResponse
import com.todaystock.api.dto.request.AlimRequestDto
import com.todaystock.api.entity.Member
import com.todaystock.api.service.MemberService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/user")
class MemberController(
    private val memberService: MemberService,
) {
//    @GetMapping("/alarm")
//    fun getAlarmList(
//        @CurrentUser member: Member,
//    ): ApiResponse<Unit> {
//        println("요청한 사용자 이메일: ${member.email}")
//        memberService.getAlarmList(member)
//        return ApiResponse.success(Unit)
//    }
//
    @PostMapping("/alarm")
    fun saveAlarm(
    @CurrentUser member: Member,
    @RequestBody dto: AlimRequestDto
    ): ApiResponse<String> {
        return try {
            println(member.memberId.email)
            memberService.saveAlarm(member, dto)
            ApiResponse.success("ok")
        }catch (e: Exception){
            e.printStackTrace()
            ApiResponse.error("500", e.message ?: "error")
        }
    }
}
