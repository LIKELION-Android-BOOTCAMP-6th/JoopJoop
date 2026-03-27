package com.example.joopjoop.feature.notification.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.joopjoop.feature.notification.worker.NotiWorker
import java.util.concurrent.TimeUnit

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)

    companion object{
        const val NOTIFICATION_WORK_NAME = "JoopJoopPeriodNoti"
        const val TAG_NOTIFICATION_WORK = "JoopJoopNotiTag"
    }

    // 2시간 주기 알림 예약
    fun startPeriodNotification() {
        // 제약 조건 (네트워크가 연결되어있지 않아도 알림은 받도록)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        // 2시간마다 반복되는 작업 생성
        val repeatingRequest = PeriodicWorkRequestBuilder<NotiWorker>(
            2, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag(TAG_NOTIFICATION_WORK)
            .build()

        // WorkManager에 등록
        workManager.enqueueUniquePeriodicWork(
            NOTIFICATION_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // 이미 예약된 작업이 있으면 유지 (중복 방지)
            repeatingRequest
        )

        // 예약된 작업 취소
        fun stopNotification(){
            workManager.cancelUniqueWork(NOTIFICATION_WORK_NAME)
        }
    }
}