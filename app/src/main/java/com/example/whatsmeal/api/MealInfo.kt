package com.example.whatsmeal.api

import com.google.gson.internal.LinkedTreeMap
import retrofit2.Response
import kotlin.collections.ArrayList

const val BASEURL = "https://open.neis.go.kr/hub/mealServiceDietInfo"
const val KEY = "35533bbbb92744a88c2557fd1691fad4"

fun requestMeal(service: Service, date: String): Response<RawRawResult>? {
    val getMeal = service.getResult(KEY, "json", "1", "100", "F10", "7380292", date)

    return getMeal.execute()
}

fun transformData(response: Response<RawRawResult>): mealServiceDietInfoResult? {
    var mealServiceDietInfoResult: mealServiceDietInfoResult

    val rrResult = response.body() as RawRawResult
    val mealServiceDietInfo = rrResult.mealServiceDietInfo
    val headWrap = mealServiceDietInfo[0] as LinkedTreeMap<String, Any>
    val head = headWrap["head"] as ArrayList<Any>

    val cntWrap = head[0] as LinkedTreeMap<String, Any>
    val list_total_count = cntWrap["list_total_count"]

    val resultWrap = head[1] as LinkedTreeMap<String, Any>
    val result = resultWrap["RESULT"] as LinkedTreeMap<String, Any>


    val headResult = Head(list_total_count as Double, Result(result["CODE"] as String, result["MESSAGE"] as String))


    val rowWrap = mealServiceDietInfo[1] as  LinkedTreeMap<String, Any>

    val row = rowWrap["row"] as ArrayList<LinkedTreeMap<String, Any>>

    val rowResult = addMeal(row)

    mealServiceDietInfoResult = mealServiceDietInfoResult(headResult, rowResult)

    return mealServiceDietInfoResult
}

fun addMeal(row: ArrayList<LinkedTreeMap<String, Any>>) : ArrayList<Meal> {


    val MealList = ArrayList<Meal>()

    for (i in row) {
        // 시도교육청코드
        val ATPT_OFCDC_SC_CODE: String = i["ATPT_OFCDC_SC_CODE"] as String
        // 시도교육청명
        val ATPT_OFCDC_SC_NM: String = i["ATPT_OFCDC_SC_NM"] as String

        // 표준학교코드
        val SD_SCHUL_CODE: String = i["SD_SCHUL_CODE"] as String
        // 학교명
        val SCHUL_NM: String = i["SCHUL_NM"] as String

        // 식사코드
        val MMEAL_SC_CODE: String = i["MMEAL_SC_CODE"] as String
        // 식사명
        val MMEAL_SC_NM: String = i["MMEAL_SC_NM"] as String

        // 급식일자
        val MLSV_YMD: String = i["MLSV_YMD"] as String
        // 급식인원수
        val MLSV_FGR: String = i["MLSV_FGR"] as String

        // 요리명
        val rawList = i["DDISH_NM"] as String

        var mealList = rawList.split("<br/>")

        var DDISH_NM = ""
//        var DDISH_NM: String = rawList.replace("<br/>", "\n")

        for (meal in mealList) {
            var st = meal.substringBefore(".")
            st = st.substringBefore("*")
            for (i in 0..9){
                st = st.substringBefore(i.toString())
            }
            DDISH_NM += st + "\n"
        }


        // 원산지정보
        val ORPLC_INFO: String = i["ORPLC_INFO"] as String
        // 칼로리정보
        val CAL_INFO: String = i["CAL_INFO"] as String
        // 영양정보
        val NTR_INFO: String = i["NTR_INFO"] as String

        // 급식시작일자
        val MLSV_FROM_YMD: String = i["MLSV_FROM_YMD"] as String
        // 급식종료일자
        val MLSV_TO_YMD: String = i["MLSV_TO_YMD"] as String

        MealList.add(
            Meal(
                ATPT_OFCDC_SC_CODE,
                ATPT_OFCDC_SC_NM,
                SD_SCHUL_CODE,
                SCHUL_NM,
                MMEAL_SC_CODE,
                MMEAL_SC_NM,
                MLSV_YMD,
                MLSV_FGR,
                DDISH_NM,
                ORPLC_INFO,
                CAL_INFO,
                NTR_INFO,
                MLSV_FROM_YMD,
                MLSV_TO_YMD
            )
        )
    }

    return MealList
}