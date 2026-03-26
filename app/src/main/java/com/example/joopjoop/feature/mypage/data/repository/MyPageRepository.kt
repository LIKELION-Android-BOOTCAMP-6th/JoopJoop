package com.example.joopjoop.feature.mypage.data.repository

import com.example.joopjoop.feature.mypage.data.model.MyNoteSummary
import com.example.joopjoop.feature.mypage.data.model.ProfileSummary
import com.example.joopjoop.feature.mypage.data.model.ScrapSummary

interface MyPageRepository {
    fun getProfile(): ProfileSummary
    fun getMyNotes(): List<MyNoteSummary>
    fun getScraps(): List<ScrapSummary>
}
