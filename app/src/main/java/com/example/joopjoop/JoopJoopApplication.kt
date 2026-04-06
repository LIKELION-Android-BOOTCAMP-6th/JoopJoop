package com.example.joopjoop

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.joopjoop.core.di.AppContainer

// ImageLoaderFactory 인터페이스를 구현
class JoopJoopApplication : Application(), ImageLoaderFactory {
    // 앱이 살아있는 동안 단 하나만 존재할 창고
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    // ImageLoader를 재정의하여 앱 전역의 이미지 로딩 성능을 최적화
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            // 메모리 캐시: 앱 가용 메모리의 25%를 할당
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            // 디스크 캐시: 512MB 공간을 확보하여 오프라인 환경에서도 이미지를 출력
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache")) // 이제 에러가 나지 않습니다.
                    .maxSizeBytes(512L * 1024L * 1024L)
                    .build()
            }
            // 크로스페이드: 이미지가 로드될 때 자연스러운 전환 애니메이션을 적용
            .crossfade(true)
            .build()
    }
}