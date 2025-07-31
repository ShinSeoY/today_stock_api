package com.todaystock.api.controller

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
//    @PostMapping("/alarm")
//    fun saveAlarm(
//        @CurrentUser member: Member,
//        @RequestParam dto: AlimRequestDto
//    ): ApiResponse<String> {
//        memberService.saveAlarm(member, dto)
//        return ApiResponse.success("ok")
//    }
}
