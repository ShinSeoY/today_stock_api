package com.todaystock.api.controller

import com.todaystock.api.dto.response.DetailResponseDto
import com.todaystock.api.dto.response.SearchResponseDto
import com.todaystock.api.dto.common.ApiResponse
import com.todaystock.api.dto.request.AlimRequestDto
import com.todaystock.api.dto.request.SearchRequestDto
import com.todaystock.api.dto.response.AlarmResponseDto
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

    @PostMapping("/stock/search")
    suspend fun getSearchList(
            @CurrentUser member: Member,
            @RequestBody dto: SearchRequestDto
    ): ApiResponse<List<SearchResponseDto> > {
        return try {
            println(member.memberId.email)
            val res = memberService.getSearchList(dto)
            ApiResponse.success(res)
        }catch (e: Exception){
            e.printStackTrace()
            ApiResponse.error("500", e.message ?: "error")
        }
    }

    @PostMapping("/stock/detail")
    suspend fun getStockDetail(
            @CurrentUser member: Member,
            @RequestBody dto: SearchRequestDto
    ): ApiResponse<DetailResponseDto?> {
        return try {
            println(member.memberId.email)
            val res = memberService.getStockDetail(dto.url!!)
            ApiResponse.success(res)
        }catch (e: Exception){
            e.printStackTrace()
            ApiResponse.error("500", e.message ?: "error")
        }
    }

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

    @GetMapping("/alarm")
    fun getAlarms(
            @CurrentUser member: Member
    ): ApiResponse<List<AlarmResponseDto>> {
        return try {
            println(member.memberId.email)
            val res = memberService.getAlarms(member)
            ApiResponse.success(res)
        }catch (e: Exception){
            e.printStackTrace()
            ApiResponse.error("500", e.message ?: "error")
        }
    }

    @DeleteMapping("/alarm/{code}")
    fun removeAlarm(
            @CurrentUser member: Member,
            @PathVariable code: String
    ): ApiResponse<String> {
        return try {
            println(member.memberId.email)
            memberService.removeAlarm(member, code)
            ApiResponse.success("ok")
        } catch (e: Exception){
            e.printStackTrace()
            ApiResponse.error("500", e.message ?: "error")
        }
    }
}
