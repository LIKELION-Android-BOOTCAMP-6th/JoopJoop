package com.example.joopjoop.core.di

import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.feature.auth.data.repository.AuthRepositoryImpl
import com.example.joopjoop.feature.auth.data.source.FirebaseAuthSource
import com.example.joopjoop.feature.auth.data.source.FirestoreUserSource
import kotlin.getValue

// 앱 전체의 의존성을 관리하는 중앙 컨테이너

class AppContainer {

    // 1. 데이터 소스 (싱글톤으로 관리)
    private val firebaseAuthSource by lazy { FirebaseAuthSource() }
    private val firestoreUserSource by lazy { FirestoreUserSource() }

    // 2. 리포지토리 구현체 주입
    // 인터페이스 타입으로 선언하여 외부(ViewModel)에서는 구현체를 몰라도 되게 함
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(firebaseAuthSource, firestoreUserSource)
    }


    // 추가되는 기능(MyPage, Setting 등)도 여기에 등록
}