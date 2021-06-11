package com.example.whatsmeal

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import com.google.gson.internal.LinkedTreeMap
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread


const val TAG = "WhatsMeal"

class MealInfoWidget : AppWidgetProvider() {
    var textBreakfast: String = ""
    var textLunch: String = ""
    var textDinner: String = ""

    var i = 0

    private val service = RetrofitClient().getService().create(Service::class.java)

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        Log.d(TAG, "onUpdate() Called")

        val remoteViews = RemoteViews(context.packageName, R.layout.mealinfo_widget_1x1)

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

        val action = intent!!.action

        val appWidgetManager = AppWidgetManager.getInstance(context)
        val remoteViews = RemoteViews(context!!.packageName, R.layout.mealinfo_widget_1x1)
        val componentName = ComponentName(context, MealInfoWidget::class.java)


        if (action.equals("Refresh")) {
            i = 0
            updateMeal(remoteViews, getDate(i))

            remoteViews.setTextViewText(R.id.tvBreakfast, "왜 안돼 싸발!!!!!!!")
        }
        else if (action.equals("Previous")) {
            updateMeal(remoteViews, getDate(--i))
        }
        else if (action.equals("Next")) {
            updateMeal(remoteViews, getDate(++i))
        }

        appWidgetManager.updateAppWidget(componentName, remoteViews)
    }


    private fun updateMeal(remoteViews: RemoteViews, date: String) {
        Log.d(TAG, "updateMeal() Called, i = $date")
        thread(start = true) {
            Log.d(TAG, "thread Started")

            val result = transformData(HandleAs(service).rRR(date)!!)!!
            Log.d(TAG, result.toString())

            if (result.head.result.code.equals("INFO-200")) {
                remoteViews.setTextViewText(R.id.tvBreakfast, "정보 없음")
                remoteViews.setTextViewText(R.id.tvLunch, "정보 없음")
                remoteViews.setTextViewText(R.id.tvDinner, "정보 없음")
                
                textBreakfast = "정보 없음"
                textLunch = "정보 없음"
                textDinner = "정보 없음"
            }


            var breakfast = if (result.row[0].DDISH_NM != null) result.row[0].DDISH_NM else "정보 없음"
            Log.d(TAG, "breakfast: $breakfast")
            remoteViews.setTextViewText(R.id.tvBreakfast, breakfast)

            var lunch = if (result.row[1].DDISH_NM != null) result.row[1].DDISH_NM else "정보 없음"
            Log.d(TAG, "lunch: $lunch")
            remoteViews.setTextViewText(R.id.tvLunch, lunch)

            if (result.head.list_total_count == 3.0) {
                var dinner = if (result.row[2].DDISH_NM != null) result.row[2].DDISH_NM else "정보 없음"
                Log.d(TAG, "dinner: $dinner")
                remoteViews.setTextViewText(R.id.tvDinner, dinner)
            }
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
        var maxWidth = newOptions!!.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH)

        var minHeight = newOptions!!.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT)
        var maxHeight = newOptions!!.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT)

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