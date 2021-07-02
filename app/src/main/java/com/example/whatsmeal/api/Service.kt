package com.example.whatsmeal.api

import retrofit2.Call
import retrofit2.http.*

interface Service {
    @GET("mealServiceDietInfo?")
    fun getResult(
        // 인증키
        @Query("KEY") KEY: String,
        // 호출 문서
        @Query("Type") Type: String,
        // 페이지 위치
        @Query("pIndex") pIndex: String = "1",
        // 페이지 당 신청 숫자
        @Query("pSize") pSize: String = "100",
        // 시도교육청 코드
        @Query("ATPT_OFCDC_SC_CODE") sidoCode: String,
        // 표준 학교 코드
        @Query("SD_SCHUL_CODE") schoolCode: String,
        // 급식 일자
        @Query("MLSV_YMD") key: String,
    ): Call<RawRawResult>
}