# Brainest — AI-Powered Study Assistant

A **Kotlin Multiplatform** (KMP) educational app targeting **Android** and **iOS**, featuring AI-powered chat tutoring, flashcard generation, quiz sessions, and audio transcription.

![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS-blue)
![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-purple)
![Compose](https://img.shields.io/badge/Compose%20Multiplatform-1.9.3-green)

---

## 📱 Features

| Feature | Status | Description |
|---------|--------|-------------|
| **Auth** | ✅ Complete | Login, register, email verification, session management via Supabase Auth |
| **Onboarding** | ✅ Complete | Multi-step flow: name, grade, subjects, goals, learning style, permissions |
| **AI Chat** | ✅ Complete | GPT-powered streaming chat, file/image uploads, math rendering (KaTeX), local caching |
| **Flashcards** | ✅ Complete | Generate from text, documents (PDF/DOCX/TXT), or audio; swipe-based study sessions |
| **Quizzes** | ✅ Complete | AI-generated quiz questions, quiz sessions with progress tracking |
| **Smart Notes** | ✅ Complete | AI-generated study notes from documents |
| **Home Dashboard** | ✅ Complete | Study streak tracking, weekly progress, stats cards |
| **Settings** | ⚠️ Minimal | Profile card, basic navigation |
| **Scan** | ❌ Placeholder | Planned feature |

---

## 🏗️ Architecture

### Clean Architecture + MVI Pattern

```
Presentation (UI/ViewModel)  →  Domain (Interfaces/Use Cases)  →  Data (Repositories)  →  Database/Network
```

Each feature follows **MVI (Model-View-Intent)**:
- **Action** sealed interface — user intents
- **State** data class — UI state via `StateFlow`
- **Event** sealed interface — one-time side effects via `Channel`

### Module Structure

```
BrainestApp/
├── composeApp/              # Shared Compose entry point, navigation, DI init
├── androidApp/              # Android entry point (MainActivity, Application)
├── iosApp/                  # iOS Xcode project
│
├── core/
│   ├── domain/              # Domain contracts: AuthService, User, Result, Error types, validators
│   ├── data/                # SupabaseAuthService, Ktor HttpClient, KermitLogger, DTOs
│   ├── presentation/        # Shared UI utilities: UiText, ObserveAsEvents, PermissionController
│   └── designsystem/        # BrainestTheme, colors, typography, reusable components
│
├── feature/
│   ├── auth/presentation/   # Login, Register, Email Verification, Forgot Password
│   ├── onboarding/          # Multi-step onboarding with DataStore + Supabase sync
│   ├── chat/                # AI chat with OpenAI streaming, Room caching, file uploads
│   │   ├── domain/          # ChatRepository interface, models, use cases
│   │   ├── data/            # ChatRepositoryImpl, OpenAIApiService, SupabaseChatService
│   │   ├── presentation/    # ViewModels, Compose screens
│   │   └── database/        # Room entities: ChatEntity, MessageEntity, ChatDao
│   │
│   ├── study/               # Flashcards, quizzes, audio, smart notes
│   │   ├── domain/          # FlashcardsRepository, QuizRepository, services
│   │   ├── data/            # Repository implementations, service impls
│   │   ├── presentation/    # ViewModels, Compose screens
│   │   └── database/        # Room: decks, flashcards, sessions, quiz progress
│   │
│   ├── home/presentation/   # Home dashboard, streak tracking, stats
│   ├── settings/presentation/ # Settings screen
│   └── scan/                # Placeholder (planned)
│
└── supabase/                # Database schema SQL files with RLS policies
```

### Dependency Injection

**Koin** is used throughout with modular DI modules:
- `coreDataModule`, `corePresentationModule` — core services
- `authPresentationModule`, `chatPresentationModule`, `homePresentationModule`, etc. — feature ViewModels
- `chatDataModule`, `flashcardsDataModule`, etc. — repository implementations

All initialized in [`composeApp/src/commonMain/kotlin/com/scellio/brainest/di/initKoin.kt`](composeApp/src/commonMain/kotlin/com/scellio/brainest/di/initKoin.kt)

### Navigation

**Type-safe Compose Navigation** with `@Serializable` route objects:
- `HomeGraphRoutes`: Home, Settings
- `ChatGraphRoutes`: ChatList, ChatDetail(chatId)
- `FlashcardsGraphRoutes`: Generate, Session(deckId), QuizSession(deckId), AudioRecording
- `AuthGraphRoutes`: Login, Register, ForgotPassword, EmailVerification, etc.
- `OnboardingGraphRoutes`: Multi-step onboarding flow

Bottom navigation bar shows 4 tabs (Home, placeholder, Chat, Flashcards), hidden on detail screens.

### Data Layer

| Technology | Purpose |
|------------|---------|
| **Supabase** | Auth, PostgREST (database), Storage (file uploads), Realtime |
| **Room** | Local SQLite (chat messages, flashcards, decks, study sessions) |
| **DataStore** | Preferences storage (onboarding data) |
| **Ktor Client** | HTTP client for OpenAI API integration |
| **Kotlinx Coroutines** | Async operations, flow-based state management |

---

## 🚀 Getting Started

### Prerequisites

- **JDK 17+** (recommended: JDK 21)
- **Android Studio** Ladybug or newer
- **Xcode 16+** (for iOS development, macOS only)
- **Supabase project** with Auth, PostgREST, and Storage configured
- **OpenAI API key** for chat functionality

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/your-org/BrainestApp.git
   cd BrainestApp
   ```

2. **Configure API keys**
   
   The project uses **BuildKonfig** for build-time config generation. Create a `local.properties` file:
   ```properties
   openai_api_key=your_openai_api_key_here
   deepinfra_api_key=your_deepinfra_api_key_here
   ```

3. **Configure Supabase**
   
   Ensure your Supabase project has:
   - Auth enabled (email/password)
   - Tables: `decks`, `flashcards`, `study_sessions`, `session_records`, `study_sources`, `quiz_questions`
   - Storage buckets: `chat-images`, `chat-files`
   - Row Level Security (RLS) policies configured

   Schema files are in [`supabase/`](supabase/).

4. **Sync Gradle**
   ```bash
   ./gradlew clean build
   ```

### Build & Run

**Android:**
```bash
# Debug build
./gradlew :androidApp:assembleDebug

# Install on device/emulator
./gradlew :androidApp:installDebug
```

Or open in Android Studio and run the `androidApp` configuration.

**iOS:**
```bash
# Open in Xcode
open iosApp/Brainest.xcodeproj
```
Then run from Xcode.

---

## 🧪 Testing

Testing infrastructure is currently minimal. Planned:

```bash
# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Run all tests + lint
./gradlew check
```

### Test Structure (Planned)
- ViewModel tests with fake repositories
- Repository tests with in-memory SQLite database
- Use case unit tests
- Compose UI tests with `compose-ui-test`

---

## 📦 Key Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Koin | 4.1.1 | Dependency injection |
| Ktor | 3.3.3 | HTTP client |
| Supabase | 3.2.6 | Auth, storage, database, realtime |
| Room | 2.8.4 | Local SQLite database |
| DataStore | 1.2.0 | Preferences storage |
| Kotlinx Coroutines | 1.10.2 | Async operations |
| Kotlinx Serialization | 1.9.0 | JSON serialization |
| Coil | 3.3.0 | Image loading |
| Moko Permissions | 0.20.1 | Cross-platform permissions |
| KaTeX | 0.3.2 | Math rendering |
| Compottie | 2.1.0 | Lottie animations |
| Kermit | 2.0.6 | Cross-platform logging |
| BuildKonfig | 0.17.1 | Build config generation |

Full version catalog: [`gradle/libs.versions.toml`](gradle/libs.versions.toml)

---

## 🤝 Contributing

1. **Fork** the repository
2. Create a **feature branch** (`git checkout -b feat/amazing-feature`)
3. **Commit** your changes (`git commit -m 'feat: add amazing feature'`)
4. **Push** to the branch (`git push origin feat/amazing-feature`)
5. Open a **Pull Request**

### Code Style
- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use `./gradlew ktlintCheck` before committing
- Add KDoc to public APIs
- Write tests for new features

### Commit Message Format
We use [Conventional Commits](https://www.conventionalcommits.org/):
```
feat: add flashcard export
fix: resolve chat sync issue
docs: update README
refactor: extract title generation use case
```

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).

---

## 🙏 Acknowledgments

- [JetBrains](https://www.jetbrains.com/) for Kotlin Multiplatform and Compose
- [Supabase](https://supabase.com/) for backend infrastructure
- [OpenAI](https://openai.com/) for GPT-powered chat tutoring

---

## 📞 Support

For issues, questions, or contributions:
- Open an [issue](https://github.com/your-org/BrainestApp/issues)
- Join our [Discord](https://discord.gg/your-server) (if applicable)
