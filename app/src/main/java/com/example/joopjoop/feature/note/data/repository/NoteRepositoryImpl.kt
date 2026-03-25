package com.example.joopjoop.feature.note.data.repository

import com.example.joopjoop.feature.note.data.model.NoteDTO
import com.example.joopjoop.feature.note.data.model.NoteRequest

class NoteRepositoryImpl : NoteRepository {

    // 쪽지 목록 가져오기 (현재 테스트 데이터)
    override suspend fun getNotes(): List<NoteDTO> {
        return List(20) { index ->
            NoteDTO(
                id = index.toString(),
                authorName = "김철수 디자이너",
                createdAt = "2026.03.25",
                content = "맛있는 빵집 정보를 공유합니다!",
                distance = "50m"
            )
        }
    }

    // 쪽지 상세 가져오기 (현재 테스트 데이터)
    override suspend fun getNoteDetail(noteId: String): NoteDTO {
        return NoteDTO(
            id = noteId,
            authorName = "김철수 디자이너",
            createdAt = "2026.03.25",
            viewCount = 128,
            likeCount = 45,
            title = "오늘의 디자인 영감: 미니멀리즘과 공간의 미학",
            content = "공간이 주는 여백의 미는 현대 디자인에서 가장 중요한 요소 중 하나입니다. " +
                    "단순함 속에서 발견하는 풍요로움을 함께 탐구해보세요.",
        )
    }
    override suspend fun createNote(request: NoteRequest) {
        // FirestoreNoteSource.kt 에서 실제 저장 처리 예정
    }
}

