# 🛠 JoopJoop Project Convention

이 문서는 **JoopJoop** 프로젝트의 일관된 코드 스타일과 유지보수를 위한 네이밍 및 구조 규칙을 정의합니다.

---

## 📂 1. 파일 네이밍 규칙 (File Naming)

계층과 역할에 따라 접미사를 명확히 구분하여 파일명만으로 역할을 짐작할 수 있게 합니다.

### **UI 계층 (feature/**/ui)**
* **Screen:** `{Feature}{Action}Screen.kt` (예: `LoginScreen.kt`)
* **State:** `{Feature}{Action}UiState.kt` (예: `LoginUiState.kt`)
* **Component:** `{Feature}{Description}.kt` (예: `NearbyNotePopup.kt`)
* **Design System (공통):** `JoopJoop{Component}.kt` (예: `JoopJoopButton.kt`)

### **ViewModel 계층 (feature/**/viewmodel)**
* **ViewModel:** `{Feature}{Action}ViewModel.kt` (예: `LoginViewModel.kt`)

### **데이터 계층 (feature/**/data)**
* **Repository 구현체:** `{Feature}RepositoryImpl.kt` (예: `AuthRepositoryImpl.kt`)
* **Data Source:** `Firebase{Entity}Source.kt` (예: `FirestoreNoteSource.kt`)
* **DI Module:** `{Feature}Module.kt` (예: `NoteModule.kt`)

---

## 🏷️ 2. 변수 및 함수 네이밍 (Variable & Function)

* **일반 변수/함수:** `camelCase` 사용 (예: `currentLocation`, `getNoteDetail()`)
* **상수 (Constants):** `UPPER_SNAKE_CASE` 사용 (예: `MAX_RADIUS_DISTANCE`)
* **StateFlow (Backing Property):** 가시성 구분을 위해 내부 변수에 `_` 접두어 사용
  ```kotlin
  private val _uiState = MutableStateFlow(NoteUiState())
  val uiState: StateFlow<NoteUiState> = _uiState.asStateFlow()
* **Firestore & DTO**: * DB 필드명: snake_case (예: created_at)
  Kotlin DTO 매핑 시: camelCase로 변환 (예: createdAt)
  
---

## 📦 3. 데이터 객체 규칙 (Data Object)

* 로직의 기준이 되는 객체 (Domain) (예: Note)
* Firestore 문서 구조와 1:1 매칭 (예: NoteDTO)
* 서버로 전송할 데이터 : feature/data/model  (예: NoteWriteRequest)
* 서버에서 받은 원본 데이터 : feature/data/model (예: NoteResponse)
  
---

## ⚡ 4. Compose 이벤트 및 함수 규칙

**UI에서 ViewModel로 전달되는 이벤트는 on{Action} 형태로 사용**
* 함수 매개변수 (예: `onNicknameChange : (String) -> Unit`)
* ViewModel 핸들러: (예: `handleIntent(intent: NoteIntent)` )

**Composable 함수: 반드시 명사로 시작하며 PascalCase (첫 글자 대문자) 사용**
(예: `@Composable fun SettingSectionTitle(...)` )
  
---

## 🎨 5. 리소스 네이밍 (Resource)

res/ 폴더 내의 모든 파일 및 ID는 **snake_case**를 사용
* **Icons:** `ic_{feature/category}_{name}`
* **Images:** `img_{description).png`
* **Strings:** `{feature}_{description}` (예: `auth_login_error_empty_email`)
