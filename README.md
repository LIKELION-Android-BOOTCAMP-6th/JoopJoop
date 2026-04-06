# 줍줍 (JoopJoop) 📍

> "발길이 닿는 곳에 당신의 흔적을 남기세요."

특정 위치에 디지털 쪽지를 남기고, 해당 장소를 방문한 사람이 발견할 수 있는 **위치 기반 소셜 네트워킹 서비스**입니다.  

---

# 📖 소개 (Overview)

**줍줍(JoopJoop)** 은 단순한 기록 앱이 아니라 **장소에 의미를 남기는 서비스**입니다.

사용자는 현재 위치의 **GPS 좌표**를 기반으로 쪽지를 지도에 남길 수 있습니다.  

이 서비스는 다음과 같은 상황에서 활용됩니다.

- 동네에서 숨겨진 메시지 남기기
- 여행지에서의 기록 공유
- 특정 장소에서만 볼 수 있는 이야기 전달

현실 공간과 디지털 메모를 연결하는 **위치 기반 커뮤니티 서비스**를 목표로 합니다.

---

# ✨ 주요 기능 (Key Features)

## 📝 쪽지 남기기
- 현재 위치(GPS)를 기반으로 텍스트와 사진을 포함한 쪽지를 생성합니다.
- 특정 범위(geoHash) 내의 사용자만 해당 쪽지를 발견할 수 있도록 설정합니다.

## 📍 쪽지 줍기
- 지도상에 표시된 근처의 쪽지 아이콘을 클릭하여 내용을 확인합니다.
- 실제 위치와 사용자의 거리를 계산하여 일정 거리 이내일 때만 '줍기'가 가능합니다.

## 📦 내 보관함
- 작성한 쪽지: 내가 과거에 남겼던 흔적들을 시간순으로 관리합니다.
- 스크랩한 쪽지: 다른 사람으로부터 발견한 소중한 기록들을 보관합니다.

## 🗺 지도 뷰
- Google Maps API를 연동하여 내 주변에 뿌려진 쪽지들을 직관적으로 시각화합니다.
- GeoHash를 통해 밀집된 지역의 쪽지 개수를 한눈에 파악합니다.

---

# 🛠 기술 스택 (Tech Stack)

## Language
- Kotlin

## UI Framework
- Jetpack Compose

## Asynchronous
- Coroutines
- Flow

## Architecture
- MVVM

## Database
- Firebase Firestore
- Firebase Storage
- Google Auth

## Image Processing
- Coil
- Custom ImageProcessor: 서버 업로드 전 비트맵 리사이징 및 EXIF 회전 보정 (IO 스레드 기반)

---

# 📱 앱 구조 (Example Architecture)
- di를 통해 객체 의존성을 줄였습니다.

```
com.example.joopjoop
├── JoopJoopApplication.kt            // Hilt 시작점 (DI)
├── MainActivity.kt                   // 앱의 메인 엔트리 포인트 (@AndroidEntryPoint)
├── NavGraph.kt                       // 전체 NavHost 및 화면 전환 로직
├── MainViewModel.kt                  
├── MainScreen.kt                  
│
├── core
│   ├── designsystem                  // 공통 UI 컴포넌트
│   │   └── components
│   │       ├── Button.kt
│   │       ├── JoopJoopDialog.kt
│   │       ├── TextField.kt
│   │       └── BottomNavigation.kt
│   │
│   ├── model                         // 공용 데이터 규격
│   │   ├── User.kt                
│   │   ├── Note.kt               
│   │   ├── Like.kt              
│   │   ├── View.kt              
│   │   ├── DialogState.kt              
│   │   └── Scrap.kt              
│   │
│   ├── repository                    // 비즈니스 로직 인터페이스
│   │   ├── AuthRepository.kt
│   │   ├── NoteRepository.kt    
│   │   ├── MyPageRepository.kt
│   │   └── NotiRepository.kt
│   │
│   ├── di                            
│   │   └── AppContainer.kt
│   │
│   └── common                        // 유틸 및 엔진
│       ├── location
│       │   └── LocationProvider.kt
│       │
│       ├── policy
│       │   └── DistancePolicy.kt
│       │
│       ├── util
│       │   ├── ImageProcessor.kt     // 이미지 가공 로직
│       │   ├── JoopJoopImage.kt      // 이미지 관련 로직
│       │   ├── Util.kt               // 기타 계산 로직 
│       │   ├── PermissionManager.kt  // 위치 권한, 갤러리 앱 권한 승인여부 확인 및 권한 요청
│       │   └── LocationUtil.kt       // 거리 계산 등 순수 함수
│       │
│       └── location
│           └── LocationProvider.kt   // 실시간 GPS 추출 엔진
│ 
├── ui/theme
│   ├── Theme.kt
│   ├── Color.kt
│   └── Type.kt
│   
├── feature/auth                      // [기능] 인증 및 세션
│   ├── ui
│   │   │
│   │   ├── components
│   │   │   └── PasswordField.kt
│   │   │
│   │   ├── intro
│   │   │   └── IntroScreen.kt
│   │   │
│   │   ├── login
│   │   │   ├── LoginScreen.kt
│   │   │   └── LoginUiState.kt
│   │   │
│   │   └── signup
│   │       ├── SignupScreen.kt
│   │       └── SignupUiState.kt
│   │
│   ├── viewmodel
│   │   ├── LoginViewModel.kt
│   │   ├── SignupViewModel.kt
│   │   ├── SignupViewModelFactory.kt
│   │   └── ViewModelFactory.kt
│   │
│   └── data
│       ├── repository
│       │   └── AuthRepositoryImpl.kt  // Firebase Auth 연동 실구현
│       │
│       ├── model
│       │   ├── SignupRequest.kt       // 가입 요청용
│       │   ├── UserResponse.kt        // 서버에서 받은 로우(Raw) 데이터 (문서 ID, 생성일 등)
│       │   └── AuthResult.kt          // 성공/실패 결과 봉투
│       │
│       └── source
│           ├── FirebaseAuthSource.kt
│           └── FirestoreUserSource.kt
│
├── feature/map                       // [기능] 지도 탐색 (데이터 소스 없음)
│   ├── ui
│   │   ├── components
│   │   │   ├── CurrentLocationButton.kt
│   │   │   ├── NearbyNoteCard.kt
│   │   │   ├── NoteMarker.kt
│   │   │   └── SearchNoteButton.kt
│   │   │
│   │   ├── MapScreen.kt              // [Screen] 전체 컴포넌트 조립 및 권한 제어
│   │   └── MapUiState.kt             // [State] UI에 필요한 모든 데이터 상태 정의
│   │   
│   └── viewmodel
│       ├── MapViewModelFactory.kt    
│       └── MapViewModel.kt            
│
├── feature/note
│   ├── ui
│   │   ├── detail
│   │   │   ├── NoteDetailScreen.kt
│   │   │   └── NoteDetailUiState.kt
│   │   │
│   │   ├── list
│   │   │   ├── NoteListScreen.kt
│   │   │   └── NoteListUiState.kt
│   │   │
│   │   └── write
│   │       ├── WriteNoteScreen.kt
│   │       └── WriteNoteUiState.kt
│   │
│   ├── viewmodel
│   │   ├── NoteDetailViewModel.kt
│   │   ├── NoteListViewModel.kt
│   │   └── WriteNoteViewModel.kt
│   │
│   └── data
│       ├── repository
│       │   ├── NoteRepository.kt
│       │   └── NoteRepositoryImpl.kt
│       │
│       ├── model
│       │   ├── NoteRequest.kt
│       │   └── NoteResponse.kt
│       │
│       └── source
│           ├── FirestoreNoteSource.kt
│           └── FirebaseStorageSource.kt
│
├── feature/mypage
│   ├── ui
│   │   ├── main
│   │   │   ├── MyPageScreen.kt
│   │   │   └── MyPageUiState.kt
│   │   ├── post
│   │   │   ├── MyPostListContent.kt
│   │   │   └── MyPostUiState.kt
│   │   ├── components
│   │   │   └── MyNoteGridItem.kt
│   │   └── scrap
│   │       ├── MyScrapListContent.kt
│   │       └── MyScrapUiState.kt
│   │
│   ├── viewmodel
│   │   ├── MyPageViewModel.kt
│   │   └── MyPageViewModelFactory.kt
│   │
│   └── data
│       └── repository
│           └── MyPageRepositoryImpl.kt
│
├── feature/notification
│   ├── worker
│   │   └── NotiWorker.kt
│   │
│   ├── util
│   │   └── NotificationHelper.kt
│   │
│   └── viewmodel
│       └── NotificationViewModel.kt
│   
└── feature/setting
    ├── ui
    │   └── SettingScreen.kt
    │
    └── viewmodel
        └── SettingViewModel.kt

```

---

# 🚀 향후 확장 기능 (Future Plans)
- 클러스터링 기술을 통해 밀집된 지역의 쪽지 개수를 한눈에 파악합니다.
- 기존의 SNS들은 다른 사용자들을 팔로우하는 기능이 기본이지만, 우리 앱은 장소에 특화되어 있으므로 스크랩한 쪽지의 위치를 팔로우 할 수 있는 기능을 추가합니다.
- 팔로우한 위치 영역 반경은 멀리서도 쪽지 열람 가능합니다.
- 팔로우한 사용자와의 디엠 기능을 추가합니다.

# 🚀 향후 개선 방안 
- Hilt를 사용하여 의존성 관리를 자동화합니다.