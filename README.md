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

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=Kotlin&logoColor=white)

## UI Framework

![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=flat-square&logo=Android&logoColor=white)

## Asynchronous

![Coroutines](https://img.shields.io/badge/Coroutines-7F52FF?style=flat-square&logo=Kotlin&logoColor=white)
![Flow](https://img.shields.io/badge/Flow-7F52FF?style=flat-square&logo=Kotlin&logoColor=white)

## Architecture

![MVVM](https://img.shields.io/badge/MVVM-3DDC84?style=flat-square&logo=Android&logoColor=white)

## Database

![Firebase Auth](https://img.shields.io/badge/Firebase%20Auth-FFCA28?style=flat-square&logo=Firebase&logoColor=white) ![Cloud Firestore](https://img.shields.io/badge/Cloud%20Firestore-FFCA28?style=flat-square&logo=Firebase&logoColor=white) ![Firebase Storage](https://img.shields.io/badge/Firebase%20Storage-FFCA28?style=flat-square&logo=Firebase&logoColor=white)

## Image Processing

![Coil](https://img.shields.io/badge/Coil-31A8FF?style=flat-square&logo=Android&logoColor=white) ![ImageProcessor](https://img.shields.io/badge/Custom%20ImageProcessor-31A8FF?style=flat-square&logo=Android&logoColor=white)

## Design & AI Tool

![Stitch AI](https://img.shields.io/badge/Stitch%20AI-9A34FF?style=flat-square&logo=SparkAR&logoColor=white) ![Figma](https://img.shields.io/badge/Figma-F24E1E?style=flat-square&logo=Figma&logoColor=white)

---

# ➡️ 실행 가이드

## 실행 방법

- Android Studio Otter 3 Feature Drop | 2025.2.3 이상 권장
- JDK 21
- Android SDK min 24 / target 36
- 프로젝트 오픈 후 Gradle Sync
- 에뮬레이터 또는 실제 단말에서 실행

## 환경 설정

- google-services.json 파일이 필요합니다.
- 키를 사용하기 전에 SHA-1 정보가 필요합니다.
- 자세한 키 환경 설정 방법은 해당 [노션](https://www.notion.so/API-KEY-33a73873401a8009b5d7deeb286f3918)을 참고하세요.

---

# 🙌 팀원 소개

## 팀원 정보 및 역할

| 이름      | GitHub                                         | 역할                      |
|:--------|:-----------------------------------------------|:------------------------|
| **이제이** | [@juu124](https://github.com/juu124)           | **팀장**, Android 개발, PM  |
| **정원화** | [@sangsangcat](https://github.com/sangsangcat) | Android 개발, PL          |
| **이유빈** | [@yyukong](https://github.com/yyukong)         | Android 개발, 사용자 Flow 구축 |
| **은신**  | [@Theo-Brie](https://github.com/Theo-Brie)     | Android 개발, 사용자 Flow 구축 |
| **재훈**  | [@jjh83301476](https://github.com/jjh83301476) | Android 개발, 앱 UI/UX 디자인 |

## 상세 역할

| 코드      | 역할              | 담당 기능                                            |
|:--------|:----------------|:-------------------------------------------------|
| **F1**  | 위치 기반 서비스       | 현재 위치 수집, Geohash 변환 및 9개 격자 탐색 로직               |
| **F2**  | 지도 UI           | Google Maps SDK 연동                               |
| **F3**  | 쪽지              | 디지털 쪽지 작성·저장·수정·삭제, 특정 좌표에 쪽지 남기기                |
| **F4**  | 데이터베이스          | Firebase Firestore 연동 및 쿼리 최적화                   |
| **F5**  | 앱 골격·네비         | Jetpack Compose UI 구조 설계, Navigation Graph 구성    |
| **F6**  | 아키텍처            | MVVM 패턴 적용, Repository 패턴 구현                     |
| **F7**  | 디자인 시스템         | 공통 UI 컴포넌트 제작, 테마 및 리소스 관리                       |
| **F8**  | DI & Reactive   | 의존성 주입 및 Kotlin Flow를 이용한 비동기 데이터 스트림            |
| **F9**  | Social Auth     | Firebase Auth 기반 Google SDK 연동 및 소셜 로그인 프로세스 구현  |
| **F10** | Session Manager | 로그인 상태 유지(Auto Login), 만료된 토큰 처리 및 로그아웃/회원탈퇴 로직  |
| **F11** | User Profile    | Firebase 실시간 DB와 연동된 사용자 프로필(닉네임, 사진) 초기 설정 및 관리 |
| **F12** | QA & Git        | 기능 테스트 및 Git Flow 전략 수립                          |

## 담당 영역

| 이름      | 담당 코드              |
|:--------|:-------------------|
| **이제이** | F3, F4, F6, F7, F8 |
| **정원화** | F1, F2, F5, F6, F8 |
| **이유빈** | F4, F9, F10, F11   |
| **서은신** | F3, F4, F11, F12   |
| **정재훈** | F10                |

---

# 📱 앱 구조 (Example Architecture)

- 특정 DI 라이브러리(Hilt/Koin)의 학습 곡선이나 의존성을 피하고 직접 의존성 주입 패턴을 구현하여, 객체 생성의 책임을 외부(AppContainer 등)로
  분리했습니다.
- 전체 구조는 UI -> ViewModel -> Repository -> RepositoryImpl -> DataSource 단계로 흐름을 구성했습니다.
- NavGraph 분리함으로써 전체 앱의 화면 전환 로직을 한눈에 파악할 수 있도록 관리했습니다.
- feature 모듈별로 Route를 정의하여 컴파일 타임에 경로 오류를 방지하고, 화면 간 데이터 전달의 안정성을 확보했습니다.
- Scaffold와 BottomNavigation을 MainScreen에 배치하고, 내부 컨텐츠만 NavHost를 통해 교체하는 방식으로 UI 구조를 표준화했습니다.
- Repository를 인터페이스로 추상화하여, 향후 단위 테스트(Unit Test) 시 가짜 객체(Mock)를 주입하기 쉬운 구조로 설계했습니다.
-

```
com.example.joopjoop
├── JoopJoopApplication.kt           
├── MainActivity.kt                   
├── NavGraph.kt                       
├── MainViewModel.kt                  
├── MainScreen.kt                  
│
├── core
│   ├── designsystem                  
│   │   └── components
│   │       ├── Button.kt
│   │       ├── JoopJoopDialog.kt
│   │       ├── TextField.kt
│   │       └── BottomNavigation.kt
│   │
│   ├── model                        
│   │   ├── User.kt                
│   │   ├── Note.kt               
│   │   ├── Like.kt              
│   │   ├── View.kt              
│   │   ├── DialogState.kt              
│   │   └── Scrap.kt              
│   │
│   ├── repository                   
│   │   ├── AuthRepository.kt
│   │   ├── NoteRepository.kt    
│   │   ├── MyPageRepository.kt
│   │   └── NotiRepository.kt
│   │
│   ├── di                            
│   │   └── AppContainer.kt
│   │
│   └── common                       
│       ├── location
│       │   └── LocationProvider.kt
│       │
│       ├── policy
│       │   └── DistancePolicy.kt
│       │
│       ├── util
│       │   ├── ImageProcessor.kt     
│       │   ├── JoopJoopImage.kt      
│       │   ├── Util.kt               
│       │   ├── PermissionManager.kt  
│       │   └── LocationUtil.kt       
│       │
│       └── location
│           └── LocationProvider.kt   
│ 
├── ui/theme
│   ├── Theme.kt
│   ├── Color.kt
│   └── Type.kt
│   
├── feature/auth                     
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
│       │   └── AuthRepositoryImpl.kt  
│       │
│       ├── model
│       │   ├── SignupRequest.kt       
│       │   ├── UserResponse.kt        
│       │   └── AuthResult.kt          
│       │
│       └── source
│           ├── FirebaseAuthSource.kt
│           └── FirestoreUserSource.kt
│
├── feature/map                       
│   ├── ui
│   │   ├── components
│   │   │   ├── CurrentLocationButton.kt
│   │   │   ├── NearbyNoteCard.kt
│   │   │   ├── NoteMarker.kt
│   │   │   └── SearchNoteButton.kt
│   │   │
│   │   ├── MapScreen.kt              
│   │   └── MapUiState.kt             
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