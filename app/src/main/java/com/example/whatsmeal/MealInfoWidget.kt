package com.example.whatsmeal

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.widget.RemoteViews
import com.example.whatsmeal.api.RetrofitClient
import com.example.whatsmeal.api.Service
import com.example.whatsmeal.api.requestMeal
import com.example.whatsmeal.api.transformData
import java.lang.NullPointerException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread


const val TAG = "WhatsMeal"

class MealInfoWidget : AppWidgetProvider() {
    var breakfast: String = ""
    var lunch: String = ""
    var dinner: String = ""

    private var i = 0

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


        when (intent!!.action){
            "Refresh" -> {
                updateMeal(appWidgetManager, componentName, remoteViews, getDate(0))
            }
            "Previous" -> {
                updateMeal(appWidgetManager, componentName, remoteViews, getDate(--i))
            }
            "Next" -> {
                updateMeal(appWidgetManager, componentName, remoteViews, getDate(++i))
            }
        }

        appWidgetManager.updateAppWidget(componentName, remoteViews)
    }


    private fun updateMeal(appWidgetManager: AppWidgetManager, componentName: ComponentName, remoteViews: RemoteViews, date: String) {
        Log.d(TAG, "updateMeal() Called, i = $date")
        thread(start = true) {
            Log.d(TAG, "thread Started")

            val dateform = "${date.substring(0, 4)}년 ${date.substring(4, 6)}월 ${date.substring(6, 8)}일"

            remoteViews.setTextViewText(R.id.tvDate, dateform)

            //TODO 조식, 중식, 석식 등 Gone 이나 Invisible 로 하고 tvLoading Visible 하게 하기
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
    }



    fun getDate(a: Int): String {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))

        val year = Integer.parseInt(today.substring(0, 4))
        val month = Integer.parseInt(today.substring(4, 6))
        val day = Integer.parseInt(today.substring(6, 8)) + a

        return LocalDate.of(year, month, day).format(DateTimeFormatter.ofPattern("yyyyMMdd"))
    }
}