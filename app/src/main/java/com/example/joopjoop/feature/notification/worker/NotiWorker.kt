package com.example.joopjoop.feature.notification.worker

import com.example.joopjoop.feature.notification.util.NotificationHelper
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.content.Context

class NotiWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.createNotificationChannel()
        notificationHelper.showNotification("JoopJoop", "주변에 새로운 쪽지가 있는지 확인해 보세요!")

        return Result.success()
    }
}