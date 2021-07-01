package com.example.whatsmeal

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.widget.RemoteViews
import java.lang.NullPointerException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread
import kotlin.math.absoluteValue
import kotlin.properties.Delegates


const val TAG = "WhatsMeal"

abstract class MealInfoWidget : AppWidgetProvider() {
    var breakfast: String = ""
    var lunch: String = ""
    var dinner: String = ""

    var i by Delegates.notNull<Int>()

    private val service = RetrofitClient().getService().create(Service::class.java)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d(TAG, "onUpdate() Called")

        val remoteViews = RemoteViews(context.packageName, R.layout.mealinfo_widget)

        val refreshIntent = Intent(context, MealInfoWidget::class.java).setAction("Refresh")
        remoteViews.setOnClickPendingIntent(R.id.btnRefresh, PendingIntent.getBroadcast(context, 0, refreshIntent, 0))

        val prevIntent = Intent(context, MealInfoWidget::class.java).setAction("Previous")
        remoteViews.setOnClickPendingIntent(R.id.btnBack, PendingIntent.getBroadcast(context, 0, prevIntent, 0))

        val nextIntent = Intent(context, MealInfoWidget::class.java).setAction("Next")
        remoteViews.setOnClickPendingIntent(R.id.btnNext, PendingIntent.getBroadcast(context, 0, nextIntent, 0))

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val remoteViews = RemoteViews(context!!.packageName, R.layout.mealinfo_widget)
        val componentName = ComponentName(context, MealInfoWidget::class.java)

        var date: LocalDate = getDate(0)

        when (intent!!.action){
            "Refresh" -> {
                i = 0
                date = getDate(i)
            }
            "Previous" -> {
                date = getDate(--i)
            }
            "Next" -> {
                date = getDate(++i)
            }
        }

        val dateString = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        updateMeal(appWidgetManager, componentName, remoteViews, dateString)

        appWidgetManager.updateAppWidget(componentName, remoteViews)
    }


    private fun updateMeal(appWidgetManager: AppWidgetManager, componentName: ComponentName, remoteViews: RemoteViews, date: String) {
        Log.d(TAG, "updateMeal() Called, i = $date")
        thread(start = true) {
            Log.d(TAG, "thread Started")

            val dateform = "${date.substring(0, 4)}년 ${date.substring(4, 6)}월 ${date.substring(6, 8)}일"

            remoteViews.setTextViewText(R.id.tvDate, dateform)
            remoteViews.setViewVisibility(R.id.mealWrap, GONE)
            remoteViews.setViewVisibility(R.id.tvLoading, VISIBLE)

            try {
                val result = transformData(requestMeal(service, date)!!)!!

                breakfast = result.row[0].DDISH_NM
                lunch = result.row[1].DDISH_NM
                dinner = if (result.head.list_total_count == 3.0) result.row[2].DDISH_NM else "정보 없음"

                remoteViews.setTextViewText(R.id.tvBreakfast, breakfast)
                remoteViews.setTextViewText(R.id.tvLunch, lunch)
                remoteViews.setTextViewText(R.id.tvDinner, dinner)
            } catch (e: NullPointerException) {
                Log.d(TAG, "Exception catch: ${e.stackTraceToString()}")

                remoteViews.setTextViewText(R.id.tvBreakfast, "정보 없음")
                remoteViews.setTextViewText(R.id.tvLunch, "정보 없음")
                remoteViews.setTextViewText(R.id.tvDinner, "정보 없음")
            }

            remoteViews.setViewVisibility(R.id.mealWrap, VISIBLE)
            remoteViews.setViewVisibility(R.id.tvLoading, GONE)

            appWidgetManager.updateAppWidget(componentName, remoteViews)
        }

    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created

//        onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds)
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    // 위젯 크기 변경시 호출
    override fun onAppWidgetOptionsChanged(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, newOptions: Bundle?) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)

        Log.d(TAG, "onAppWidgetOptionsChanged() Called")

        var minWidth = newOptions!!.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)
        var maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)

        var minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        var maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)

        Log.d(TAG, "min: ${minWidth}x${minHeight}, max: ${maxWidth}x${maxHeight}")


//        val remoteViews = RemoteViews(context!!.packageName, R.layout.mealinfo_widget)
//        val componentName = ComponentName(context, MealInfoWidget::class.java)
//
//        updateMeal(appWidgetManager!!, componentName, remoteViews, getDate(0))
//        appWidgetManager.updateAppWidget(componentName, remoteViews)
    }


//TODO 매 달 1일 일자에 0이 들어가 n월 0일을 반환하는 오류 수정 (로직 다시짜야 할 듯)
    fun getDate(a: Int): LocalDate {
        var today = LocalDate.now()

        if (a > 0) {
            return today.plusDays(a.toLong())
        } else if (a == 0) {
            return today
        } else {
            return today.minusDays(a.absoluteValue.toLong())
        }
    }
//    fun getDate(a: Int): String {
//        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
//
//        val year = Integer.parseInt(today.substring(0, 4))
//        val month = Integer.parseInt(today.substring(4, 6))
//        val day = Integer.parseInt(today.substring(6, 8)) + a
//
//        return LocalDate.of(year, month, day).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
//    }
}