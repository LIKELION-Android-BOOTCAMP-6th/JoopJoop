package com.example.joopjoop.feature.notification.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
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
    fun startPeriodicNotification() {
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
        val operation: Operation = workManager.enqueueUniquePeriodicWork(
            NOTIFICATION_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // 이미 예약된 작업이 있으면 유지 (중복 방지)
            repeatingRequest
        )

        // 등록 요청이 성공했는지 확인
        operation.state.observeForever(object : Observer<Operation.State> {
            override fun onChanged(value: Operation.State) {
                when (value) {
                    is Operation.State.SUCCESS -> {
                        Log.d("JoopJoop_Worker", "알림 작업 등록 성공!")
                    }
                    is Operation.State.FAILURE -> {
                        Log.e("JoopJoop_Worker", "알림 작업 등록 실패")
                    }
                    else -> {}
                }
            }
        })
        // 실제 작업 상태(ENQUEUED 등) 실시간 관찰
        workManager.getWorkInfosForUniqueWorkLiveData(NOTIFICATION_WORK_NAME)
            .observeForever { workInfos ->
                workInfos?.forEach { workInfo ->
                    Log.d("JoopJoop_Worker", "현재 상태: ${workInfo.state}")
                }
            }
    }

    // 2. 예약 취소 함수 (함수 밖으로 분리됨)
    fun stopNotification() {
        workManager.cancelUniqueWork(NOTIFICATION_WORK_NAME)
        Log.d("JoopJoop_Worker", "알림 작업이 취소되었습니다.")
    }
}