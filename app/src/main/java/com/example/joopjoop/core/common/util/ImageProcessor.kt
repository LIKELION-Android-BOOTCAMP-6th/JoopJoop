package com.example.joopjoop.core.common.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream

/**
 * [저장/가공용 유틸] ImageProcessor
 * - 역할: 서버 업로드 전 이미지를 리사이징하고 압축하는 '가위'
 * - 사용: ViewModel이나 Repository에서 저장 로직 수행 시 호출
 */
class ImageProcessor(private val context: Context) {

    //원본 이미지를 업로드용(1080px)으로 최적화하여 ByteArray로 반환
    fun processOriginal(uri: Uri): ByteArray? {
        return resizeAndCompress(uri, targetWidth = 1080)
    }

    //리스트 출력용 썸네일(200px)을 생성하여 ByteArray로 반환
    fun processThumbnail(uri: Uri): ByteArray? {
        return resizeAndCompress(uri, targetWidth = 200)
    }

    // 프로필 전용 가공
    fun processProfile(uri: Uri): ByteArray? {
        return resizeAndCompress(uri, targetWidth = 400)
    }

    //실제 리사이징 및 압축 로직
    private fun resizeAndCompress(uri: Uri, targetWidth: Int): ByteArray? {
        return try {
            // 1. Uri로부터 비트맵 불러오기
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close() ?: return null

            // 2. 비율 유지하며 가로 길이에 맞춰 리사이징
            val aspectRatio = originalBitmap.height.toFloat() / originalBitmap.width.toFloat()
            val targetHeight = (targetWidth * aspectRatio).toInt()
            val resizedBitmap =
                originalBitmap.scale(targetWidth, targetHeight)

            // 3. JPEG 형식으로 압축 (품질 80%가 용량 대비 화질이 가장 효율적)
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

            outputStream.toByteArray()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}