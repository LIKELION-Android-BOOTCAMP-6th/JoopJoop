package com.example.joopjoop.core.di

import android.content.Context
import com.example.joopjoop.MainViewModel
import com.example.joopjoop.core.common.util.ImageProcessor
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.core.repository.FakeNoteRepository
import com.example.joopjoop.core.repository.MyPageRepository
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.data.location.LocationProvider
import com.example.joopjoop.feature.auth.data.repository.AuthRepositoryImpl
import com.example.joopjoop.feature.auth.data.source.FirebaseAuthSource
import com.example.joopjoop.feature.auth.data.source.FirestoreUserSource
import com.example.joopjoop.feature.map.viewmodel.MapViewModelFactory
import com.example.joopjoop.feature.mypage.data.repository.MyPageRepositoryImpl
import com.example.joopjoop.feature.mypage.viewmodel.MyPageViewModelFactory
import com.example.joopjoop.feature.note.data.repository.NoteRepositoryImpl
import com.example.joopjoop.feature.note.data.source.FirestoreNoteSource
import com.example.joopjoop.feature.note.viewmodel.NoteViewModelFactory

// 앱 전체의 의존성을 관리하는 중앙 컨테이너
class AppContainer(context: Context) {

    // 데이터 소스 (싱글톤으로 관리)
    private val firebaseAuthSource by lazy { FirebaseAuthSource() }
    private val firestoreUserSource by lazy { FirestoreUserSource() }
    private val firestoreNoteSource by lazy { FirestoreNoteSource() }

    val mainViewModelFactory by lazy { MainViewModelFactory(authRepository) }

    //여러 ViewModelFactory에서 동일한 인스턴스를 공유할 수 있게 함
    private val locationProvider by lazy { LocationProvider(context) }

    // 리포지토리 구현체 주입
    // 인터페이스 타입으로 선언하여 외부(ViewModel)에서는 구현체를 몰라도 되게 함
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(firebaseAuthSource, firestoreUserSource)
    }

    // Note 리포지토리
    val noteRepository: NoteRepository by lazy {
        // 필요한 데이터에 따라 둘중 하나의 주석을 해제해서 사용하세요
        // 1. 실제 서버 데이터가 필요할 때
        NoteRepositoryImpl(firestoreNoteSource)

        // 2. 가짜 데이터가 필요할 때
//        FakeNoteRepository()
    }

    // MyPage 리포지토리
    val myPageRepository: MyPageRepository by lazy {
        MyPageRepositoryImpl(firestoreUserSource)
    }

    // ViewModel Factory
    // 여기서 팩토리까지 관리하면 NavGraph 코드가 더 짧아짐
    val myPageViewModelFactory: MyPageViewModelFactory by lazy {
        MyPageViewModelFactory(myPageRepository, authRepository)
    }

    // Map 화면을 위한 팩토리
    val mapViewModelFactory: MapViewModelFactory by lazy {
        MapViewModelFactory(noteRepository, locationProvider)
    }

    // note (noteList, noteDetail, writeNote)
    val noteViewModelFactory: NoteViewModelFactory by lazy {
        NoteViewModelFactory(noteRepository, locationProvider, authRepository)
    }

    // 저장용 유틸 등록
    val imageProcessor by lazy { ImageProcessor(context) }

    // 상단 noteViewModelFactory로 아래 write부분 병합됐기에 아래 코드 주석 처리함
    // WriteNoteViewModelFactory 파일은 삭제됨
    // writeNote
//    val writeNoteViewModelFactory: WriteNoteViewModelFactory by lazy {
//        // 구글 위치 서비스 클라이언트 생성
//        val fusedLocationClient = com.google.android.gms.location.LocationServices
//            .getFusedLocationProviderClient(context)
//
//        WriteNoteViewModelFactory(noteRepository, fusedLocationClient)
//    }

    class MainViewModelFactory(private val authRepository: AuthRepository) :
        androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}