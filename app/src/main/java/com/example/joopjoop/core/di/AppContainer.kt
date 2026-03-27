package com.example.joopjoop.core.di

import android.content.Context
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.core.repository.MyPageRepository
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.feature.auth.data.repository.AuthRepositoryImpl
import com.example.joopjoop.feature.auth.data.source.FirebaseAuthSource
import com.example.joopjoop.feature.auth.data.source.FirestoreUserSource
import com.example.joopjoop.feature.note.data.source.FirestoreNoteSource
import com.example.joopjoop.feature.mypage.data.repository.MyPageRepositoryImpl
import com.example.joopjoop.feature.mypage.viewmodel.MyPageViewModelFactory
import com.example.joopjoop.feature.note.data.repository.NoteRepositoryImpl
import com.example.joopjoop.feature.note.viewmodel.NoteViewModelFactory
import com.example.joopjoop.feature.note.viewmodel.WriteNoteViewModelFactory
import kotlin.getValue

// 앱 전체의 의존성을 관리하는 중앙 컨테이너

class AppContainer(context: Context) {

    // 1. 데이터 소스 (싱글톤으로 관리)
    private val firebaseAuthSource by lazy { FirebaseAuthSource() }
    private val firestoreUserSource by lazy { FirestoreUserSource() }
    private val firestoreNoteSource by lazy { FirestoreNoteSource() }


    // 2. 리포지토리 구현체 주입
    // 인터페이스 타입으로 선언하여 외부(ViewModel)에서는 구현체를 몰라도 되게 함
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(firebaseAuthSource, firestoreUserSource)
    }

    // Note 리포지토리
    val noteRepository: NoteRepository by lazy {
        NoteRepositoryImpl(firestoreNoteSource)
    }

    // MyPage 리포지토리
    val myPageRepository: MyPageRepository by lazy {
        MyPageRepositoryImpl(firestoreUserSource)
    }

    // ViewModel Factory
    // 여기서 팩토리까지 관리하면 NavGraph 코드가 더 짧아짐
    val myPageViewModelFactory: MyPageViewModelFactory by lazy {
        MyPageViewModelFactory(myPageRepository)
    }

    // note (noteList, noteDetail 관련 Viewmodel Factory)
    val noteViewModelFactory: NoteViewModelFactory by lazy {
        // 구글 위치 서비스 클라이언트 생성
        val fusedLocationClient = com.google.android.gms.location.LocationServices
            .getFusedLocationProviderClient(context)

        NoteViewModelFactory(noteRepository, fusedLocationClient)
    }

    // writeNote
    val writeNoteViewModelFactory: WriteNoteViewModelFactory by lazy {
        // 구글 위치 서비스 클라이언트 생성
        val fusedLocationClient = com.google.android.gms.location.LocationServices
            .getFusedLocationProviderClient(context)

        WriteNoteViewModelFactory(noteRepository, fusedLocationClient)
    }
}