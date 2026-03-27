package com.example.joopjoop.feature.note.viewmodel

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.joopjoop.core.model.Note
import com.example.joopjoop.core.model.NoteLocation
import com.example.joopjoop.core.repository.NoteRepository
import com.example.joopjoop.feature.note.ui.list.NoteItem
import com.example.joopjoop.feature.note.ui.list.NoteListUiState
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteListViewModel(
    private val repository: NoteRepository,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteListUiState())
    val uiState: StateFlow<NoteListUiState> = _uiState.asStateFlow()

    init {
        fetchCurrentLocation()
    }
    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocation() {
        // 진입할 때, 마지막 위치 가져옴
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                _uiState.update { state ->
                    state.copy(
                        myLatitude = it.latitude,
                        myLongitude = it.longitude
                    )
                }
                loadNotes()
            }
        }
    }
    fun loadNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val myLat = _uiState.value.myLatitude
            val myLng = _uiState.value.myLongitude

            val allNotes = repository.getNotes()


            val notes = allNotes.map { note ->
                val results = FloatArray(1)
                Location.distanceBetween(
                    myLat,
                    myLng,
                    note.location.latitude,
                    note.location.longitude,
                    results)
                val distanceInMeters = results[0]
                NoteItem(
                    id = note.noteId,
                    content = note.contentText,
                    distance = "${distanceInMeters.toInt()}m",
                    isWithinRange = distanceInMeters <= 30f,
                    latitude = note.location.latitude,
                    longitude = note.location.longitude
                )
            }
            _uiState.update { it.copy(notes = notes, isLoading = false) }
        }
    }


}