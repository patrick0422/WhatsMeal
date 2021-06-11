package com.example.whatsmeal

import com.google.gson.internal.LinkedTreeMap

data class mealServiceDietInfo (
    var head: Head,
    val row:  ArrayList<LinkedTreeMap<String, Any>>
)
data class mealServiceDietInfoResult (
    var head: Head,
    val row:  ArrayList<Meal>
)

data class Head (
    val list_total_count: Double,
    val result: Result
)

data class Result (
    val code: String,
    val message: String
)

data class Meal (
    // 시도교육청코드
    val ATPT_OFCDC_SC_CODE: String,
    // 시도교육청명
    val ATPT_OFCDC_SC_NM: String,

    // 표준학교코드
    val SD_SCHUL_CODE: String,
    // 학교명
    val SCHUL_NM: String,

    // 식사코드
    val MMEAL_SC_CODE: String,
    // 식사명
    val MMEAL_SC_NM: String,

    // 급식일자
    val MLSV_YMD: String,
    // 급식인원수
    val MLSV_FGR: String,

    // 요리명
    val DDISH_NM: String,

    // 원산지정보
    val ORPLC_INFO: String,
    // 칼로리정보
    val CAL_INFO: String,
    // 영양정보
    val NTR_INFO: String,

    // 급식시작일자
    val MLSV_FROM_YMD: String,
    // 급식종료일자
    val MLSV_TO_YMD: String
)

data class RawRawResult (
    val mealServiceDietInfo : List<Any>
)