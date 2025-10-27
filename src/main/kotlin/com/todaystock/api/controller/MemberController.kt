package com.todaystock.api.controller

import com.todaystock.api.dto.common.ApiResponse
import com.todaystock.api.dto.request.AlimRequestDto
import com.todaystock.api.dto.request.SearchRequestDto
import com.todaystock.api.dto.response.AlarmResponseDto
import com.todaystock.api.dto.response.DetailResponseDto
import com.todaystock.api.dto.response.SearchResponseDto
import com.todaystock.api.entity.Member
import com.todaystock.api.service.MemberService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/user")
class MemberController(
        private val memberService: MemberService,
) {
    /**
     * 주식 검색
     */
    @PostMapping("/stock/search")
    fun getSearchList(
            @CurrentUser member: Member,
            @RequestBody dto: SearchRequestDto,
    ): ApiResponse<List<SearchResponseDto>> {
        return try {
            val res = memberService.getSearchList(dto)
            ApiResponse.success(res)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponse.error("500", e.message ?: "error")
        }
    }

    /**
     * 주식 상세 정보
     */
    @PostMapping("/stock/detail")
    fun getStockDetail(
            @CurrentUser member: Member,
            @RequestBody dto: SearchRequestDto,
    ): ApiResponse<DetailResponseDto?> {
        return try {
            val res = memberService.getStockDetail(dto.url!!)
            ApiResponse.success(res)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponse.error("500", e.message ?: "error")
        }
    }

    /**
     * 알림 저장
     */
    @PostMapping("/alarm")
    fun saveAlarm(
            @CurrentUser member: Member,
            @RequestBody dto: AlimRequestDto,
    ): ApiResponse<String> {
        return try {
            memberService.saveAlarm(member, dto)
            ApiResponse.success("ok")
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponse.error("500", e.message ?: "error")
        }
    }

    /**
     * 알림 조회
     */
    @GetMapping("/alarm")
    fun getAlarms(
            @CurrentUser member: Member,
    ): ApiResponse<List<AlarmResponseDto>> {
        return try {
            val res = memberService.getAlarms(member)
            ApiResponse.success(res)
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponse.error("500", e.message ?: "error")
        }
    }

    /**
     * 알림 삭제
     */
    @DeleteMapping("/alarm/{code}")
    fun removeAlarm(
            @CurrentUser member: Member,
            @PathVariable code: String,
    ): ApiResponse<String> {
        return try {
            memberService.removeAlarm(member, code)
            ApiResponse.success("ok")
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponse.error("500", e.message ?: "error")
        }
    }

    /**
     * 알림 비활성화
     */
    @PutMapping("/alarm/{code}")
    fun disableAlarm(
            @CurrentUser member: Member,
            @PathVariable code: String,
    ): ApiResponse<String> {
        return try {
            memberService.disableAlarm(member, code)
            ApiResponse.success("ok")
        } catch (e: Exception) {
            e.printStackTrace()
            ApiResponse.error("500", e.message ?: "error")
        }
    }
}
