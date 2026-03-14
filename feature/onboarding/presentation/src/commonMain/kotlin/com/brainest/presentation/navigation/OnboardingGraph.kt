package com.brainest.presentation.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import brainest.feature.onboarding.presentation.generated.resources.Res
import brainest.feature.onboarding.presentation.generated.resources.arabic
import brainest.feature.onboarding.presentation.generated.resources.chinese
import brainest.feature.onboarding.presentation.generated.resources.english
import brainest.feature.onboarding.presentation.generated.resources.french
import brainest.feature.onboarding.presentation.generated.resources.german
import brainest.feature.onboarding.presentation.generated.resources.spanish
import com.brainest.presentation.introduction.IntroductionScreen
import com.brainest.presentation.introduction.UserReviewScreen
import com.brainest.presentation.introduction.WelcomeScreen
import com.brainest.presentation.introduction.permission.PermissionScreenOnboarding
import com.brainest.presentation.introduction.about_learner.GradeScreen
import com.brainest.presentation.introduction.about_learner.MultiSelectOptionData
import com.brainest.presentation.introduction.about_learner.NameInputScreen
import com.brainest.presentation.introduction.about_learner.SelectSubjectScreen
import com.brainest.presentation.introduction.about_learner.SelectionOptionData
import com.brainest.presentation.introduction.goals.GoalScreen
import com.brainest.presentation.introduction.goals.SelectGradeOption
import com.brainest.presentation.introduction.goals.StudyHoursScreen
import com.brainest.presentation.introduction.growth_chart_screen.GrowthChartScreen
import com.brainest.presentation.introduction.learning_style.LearningMethodScreen
import com.brainest.presentation.introduction.learning_style.LearningMethodsData
import com.brainest.presentation.introduction.learning_style.StudyTimeData
import com.brainest.presentation.introduction.learning_style.StudyTimeScreen
import com.brainest.presentation.introduction.personalization.LanguageData
import com.brainest.presentation.introduction.personalization.LanguageSelectionScreen
import com.brainest.presentation.onboarding.OnboardingAction
import com.brainest.presentation.onboarding.OnboardingFlowSteps
import com.brainest.presentation.onboarding.OnboardingStepId
import com.brainest.presentation.onboarding.OnboardingViewModel
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.onboardingGraph(
    navController: NavController,
    onFinishOnboarding: () -> Unit
) {
    navigation<OnboardingGraphRoutes.Graph>(
        startDestination = OnboardingGraphRoutes.Welcome
    ) {
        composable<OnboardingGraphRoutes.Welcome> {
            WelcomeScreen(
                onGetStarted = {
                    navController.navigate(OnboardingGraphRoutes.Introduction)
                }
            )
        }

        composable<OnboardingGraphRoutes.Introduction>(
            enterTransition = {
                slideInHorizontally(
                    initialOffsetX = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 1200)
                )
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { fullHeight -> fullHeight },
                    animationSpec = tween(durationMillis = 300)
                )
            }
        ) {
            IntroductionScreen(
                onFinishOnboarding = {
                    navController.navigate(OnboardingGraphRoutes.Name)
                }
            )
        }

        composable<OnboardingGraphRoutes.Name> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsState()

            NameInputScreen(
                title = "What's your name?",
                subtitle = "This helps us personalize your experience",
                stepLabel = "About You",
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Name),
                totalSteps = OnboardingFlowSteps.totalSteps,
                inputValue = state.name,
                onInputValueChange = { viewModel.onAction(OnboardingAction.NameChanged(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.Grade) },
                placeholder = "Your Name"
            )
        }

        composable<OnboardingGraphRoutes.Grade> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsState()

            GradeScreen(
                title = "What's your grade level?",
                subtitle = "We'll tailor your learning path accordingly",
                stepLabel = "About You",
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Grade),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = gradeOptions,
                selectedOptionId = state.gradeId,
                onOptionSelected = { viewModel.onAction(OnboardingAction.GradeSelected(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.Subjects) }
            )
        }

        composable<OnboardingGraphRoutes.Subjects> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsState()

            SelectSubjectScreen(
                title = "What subjects are you studying?",
                subtitle = "Select all that apply",
                stepLabel = "About You",
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Subjects),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = subjectOptions,
                selectedOptionIds = state.subjectIds,
                onOptionToggle = { viewModel.onAction(OnboardingAction.SubjectToggled(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.Goal) },
                onNoneOfTheAboveClicked = {
                    viewModel.onAction(OnboardingAction.ClearSubjects)
                },
                allowEmptySelection = true
            )
        }

        composable<OnboardingGraphRoutes.Goal> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsState()

            GoalScreen(
                title = "What's your main goal?",
                subtitle = "We'll build a plan that gets you there",
                stepLabel = "Goals",
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Goal),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = goalOptions,
                selectedOptionId = state.goalId,
                onOptionSelected = { viewModel.onAction(OnboardingAction.GoalSelected(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.Growth) }
            )
        }

        composable<OnboardingGraphRoutes.Challenges> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsState()

            SelectSubjectScreen(
                title = "What challenges are you facing?",
                subtitle = "Select all that apply",
                stepLabel = "Challenges",
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Challenges),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = challengeOptions,
                selectedOptionIds = state.challengeIds,
                onOptionToggle = { viewModel.onAction(OnboardingAction.ChallengeToggled(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.Survey) },
                onNoneOfTheAboveClicked = {
                    viewModel.onAction(OnboardingAction.ClearChallenges)
                },
                allowEmptySelection = true
            )
        }

        composable<OnboardingGraphRoutes.Survey> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsState()

            StudyHoursScreen(
                title = "How many hours do you study daily?",
                subtitle = "This helps us plan your schedule",
                stepLabel = "Survey",
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Survey),
                totalSteps = OnboardingFlowSteps.totalSteps,
                hours = state.studyHours,
                onHoursChange = { viewModel.onAction(OnboardingAction.StudyHoursChanged(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.LearningMethod) }
            )
        }

        composable<OnboardingGraphRoutes.LearningMethod> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsState()

            LearningMethodScreen(
                title = "How do you learn best?",
                subtitle = "We'll personalize your experience",
                stepLabel = "Learning Style",
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.LearningMethod),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = learningMethodOptions,
                selectedOptionId = state.learningMethodId,
                onOptionSelected = { viewModel.onAction(OnboardingAction.LearningMethodSelected(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.StudyTime) }
            )
        }

        composable<OnboardingGraphRoutes.StudyTime> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsState()

            StudyTimeScreen(
                title = "When do you usually study?",
                subtitle = "We'll schedule your sessions at the right time",
                stepLabel = "Learning Style",
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.StudyTime),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = studyTimeOptions,
                selectedOptionId = state.studyTimeId,
                onOptionSelected = { viewModel.onAction(OnboardingAction.StudyTimeSelected(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.Language) }
            )
        }

        composable<OnboardingGraphRoutes.Language> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsState()

            LanguageSelectionScreen(
                title = "Select your language",
                subtitle = "We'll localize the experience for you",
                stepLabel = "Personalization",
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Language),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = languageOptions,
                selectedOptionId = state.languageId,
                onOptionSelected = { viewModel.onAction(OnboardingAction.LanguageSelected(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.Review) }
            )
        }

        composable<OnboardingGraphRoutes.Growth> {
            GrowthChartScreen(
                onContinueClick = { navController.navigate(OnboardingGraphRoutes.Challenges) }
            )
        }

        composable<OnboardingGraphRoutes.Review> {
            val viewModel = rememberOnboardingViewModel(navController)

            UserReviewScreen(
                onContinueClick = {
                    navController.navigate(OnboardingGraphRoutes.Permissions)
                }
            )
        }

        composable<OnboardingGraphRoutes.Permissions> {
            val viewModel = rememberOnboardingViewModel(navController)

            PermissionScreenOnboarding(
                onAllowClick = {
                    viewModel.onAction(OnboardingAction.Reset)
                    onFinishOnboarding()
                },
                onContinue = {
                    viewModel.onAction(OnboardingAction.Reset)
                    onFinishOnboarding()
                }
            )
        }
    }
}

@Composable
private fun rememberOnboardingViewModel(
    navController: NavController
): OnboardingViewModel {
    val parentEntry = remember(navController) {
        navController.getBackStackEntry(OnboardingGraphRoutes.Graph)
    }
    return koinViewModel(viewModelStoreOwner = parentEntry)
}

private val gradeOptions = listOf(
    SelectionOptionData(id = "elementary", label = "Elementary (Grade 1-5)"),
    SelectionOptionData(id = "middle_school", label = "Middle School (Grade 6-8)"),
    SelectionOptionData(id = "high_school", label = "High School (Grade 9-12)"),
    SelectionOptionData(id = "undergraduate", label = "Undergraduate"),
    SelectionOptionData(id = "postgraduate", label = "Postgraduate")
)

private val subjectOptions = listOf(
    MultiSelectOptionData(id = "math", label = "Mathematics"),
    MultiSelectOptionData(id = "science", label = "Science"),
    MultiSelectOptionData(id = "physics", label = "Physics"),
    MultiSelectOptionData(id = "chemistry", label = "Chemistry"),
    MultiSelectOptionData(id = "biology", label = "Biology"),
    MultiSelectOptionData(id = "history", label = "History"),
    MultiSelectOptionData(id = "geography", label = "Geography"),
    MultiSelectOptionData(id = "english", label = "English"),
    MultiSelectOptionData(id = "literature", label = "Literature"),
    MultiSelectOptionData(id = "computer_science", label = "Computer Science")
)

private val goalOptions = listOf(
    SelectGradeOption(id = "improve_grades", label = "Improve My Grades"),
    SelectGradeOption(id = "prepare_exams", label = "Prepare for Exams"),
    SelectGradeOption(id = "learn_new_skills", label = "Learn New Skills"),
    SelectGradeOption(id = "stay_consistent", label = "Stay Consistent Daily"),
    SelectGradeOption(id = "get_ahead", label = "Get Ahead of Class")
)

private val challengeOptions = listOf(
    MultiSelectOptionData(id = "focus", label = "Staying focused"),
    MultiSelectOptionData(id = "time_management", label = "Time management"),
    MultiSelectOptionData(id = "understanding", label = "Understanding concepts"),
    MultiSelectOptionData(id = "exam_anxiety", label = "Exam anxiety"),
    MultiSelectOptionData(id = "motivation", label = "Motivation"),
    MultiSelectOptionData(id = "note_taking", label = "Note-taking")
)

private val learningMethodOptions = listOf(
    LearningMethodsData(id = "videos", label = "Video Lessons"),
    LearningMethodsData(id = "quizzes", label = "Quizzes and Practice Tests"),
    LearningMethodsData(id = "flashcards", label = "Flashcards"),
    LearningMethodsData(id = "reading", label = "Reading and Summaries"),
    LearningMethodsData(id = "interactive", label = "Interactive Exercises")
)

private val studyTimeOptions = listOf(
    StudyTimeData(id = "early_morning", label = "Early Morning (5AM - 8AM)"),
    StudyTimeData(id = "morning", label = "Morning (8AM - 12PM)"),
    StudyTimeData(id = "afternoon", label = "Afternoon (12PM - 5PM)"),
    StudyTimeData(id = "evening", label = "Evening (5PM - 9PM)"),
    StudyTimeData(id = "night", label = "Night (9PM - 12AM)")
)

private val languageOptions = listOf(
    LanguageData(
        id = "english",
        label = "English",
        icon = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape)
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.english),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }
        }),
    LanguageData(
        id = "arabic",
        label = "Arabic",
        icon = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape)
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.arabic),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }
        }),
    LanguageData(
        id = "french",
        label = "French",
        icon = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape)
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.french),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }
        }),
    LanguageData(
        id = "spanish",
        label = "Spanish",
        icon = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape)
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.spanish),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }
        }),
    LanguageData(
        id = "german",
        label = "German",
        icon = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape)
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.german),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }
        }),
    LanguageData(
        id = "chinese",
        label = "Chinese",
        icon = {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.LightGray, CircleShape)
            ) {
                Icon(
                    imageVector = vectorResource(Res.drawable.chinese),
                    contentDescription = "",
                    tint = Color.Unspecified
                )
            }
        })
)
