package com.example.joopjoop.feature.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.joopjoop.core.repository.AuthRepository
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.data.location.LocationProvider

// NoteViewModel 인스턴스 생성을 담당하며, 필요한 Repository를 주입하는 팩토리 클래스
class NoteViewModelFactory(
    private val repository: NoteRepository,
//    private val fusedLocationClient: FusedLocationProviderClient
    private val locationProvider: LocationProvider, // [변경] 공용 Provider 주입
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // NoteDetailViewModel 생성 (위치 정보 미사용)
            modelClass.isAssignableFrom(NoteDetailViewModel::class.java) -> {
                NoteDetailViewModel(repository, authRepository) as T
            }

            // NoteListViewModel 생성 (내 위치 기준 거리 계산 및 줍기 판정)
            modelClass.isAssignableFrom(NoteListViewModel::class.java) -> {
                NoteListViewModel(repository, locationProvider) as T
            }

            // WriteNoteViewModel 생성 (작성 시 현재 위치 Geohash 생성)
            modelClass.isAssignableFrom(WriteNoteViewModel::class.java) -> {
                WriteNoteViewModel(repository, locationProvider) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}