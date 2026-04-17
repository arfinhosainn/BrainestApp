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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import brainest.feature.onboarding.presentation.generated.resources.*
import com.brainest.presentation.introduction.IntroductionScreen
import com.brainest.presentation.introduction.UserReviewScreen
import com.brainest.presentation.introduction.WelcomeScreen
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
import org.jetbrains.compose.resources.stringResource
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
            val state by viewModel.state.collectAsStateWithLifecycle()

            NameInputScreen(
                title = stringResource(Res.string.what_is_your_name),
                subtitle = stringResource(Res.string.personalize_experience),
                stepLabel = stringResource(Res.string.about_you),
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Name),
                totalSteps = OnboardingFlowSteps.totalSteps,
                inputValue = state.name,
                onInputValueChange = { viewModel.onAction(OnboardingAction.NameChanged(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.Grade) },
                placeholder = stringResource(Res.string.your_name)
            )
        }

        composable<OnboardingGraphRoutes.Grade> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsStateWithLifecycle()

            GradeScreen(
                title = stringResource(Res.string.what_is_your_grade),
                subtitle = stringResource(Res.string.tailor_learning_path),
                stepLabel = stringResource(Res.string.about_you),
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Grade),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = gradeOptions(),
                selectedOptionId = state.gradeId,
                onOptionSelected = { viewModel.onAction(OnboardingAction.GradeSelected(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.Subjects) }
            )
        }

        composable<OnboardingGraphRoutes.Subjects> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsStateWithLifecycle()

            SelectSubjectScreen(
                title = stringResource(Res.string.what_subjects_studying),
                subtitle = stringResource(Res.string.select_all_apply),
                stepLabel = stringResource(Res.string.about_you),
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Subjects),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = subjectOptions(),
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
            val state by viewModel.state.collectAsStateWithLifecycle()

            GoalScreen(
                title = stringResource(Res.string.what_is_your_goal),
                subtitle = stringResource(Res.string.build_plan_goal),
                stepLabel = stringResource(Res.string.goals_label),
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Goal),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = goalOptions(),
                selectedOptionId = state.goalId,
                onOptionSelected = { viewModel.onAction(OnboardingAction.GoalSelected(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.Growth) }
            )
        }

        composable<OnboardingGraphRoutes.Challenges> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsStateWithLifecycle()

            SelectSubjectScreen(
                title = stringResource(Res.string.what_challenges_facing),
                subtitle = stringResource(Res.string.select_all_apply),
                stepLabel = stringResource(Res.string.challenges_label),
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Challenges),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = challengeOptions(),
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
            val state by viewModel.state.collectAsStateWithLifecycle()

            StudyHoursScreen(
                title = stringResource(Res.string.how_many_hours_study),
                subtitle = stringResource(Res.string.help_plan_schedule),
                stepLabel = stringResource(Res.string.survey_label),
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Survey),
                totalSteps = OnboardingFlowSteps.totalSteps,
                hours = state.studyHours,
                onHoursChange = { viewModel.onAction(OnboardingAction.StudyHoursChanged(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.LearningMethod) }
            )
        }

        composable<OnboardingGraphRoutes.LearningMethod> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsStateWithLifecycle()

            LearningMethodScreen(
                title = stringResource(Res.string.how_do_you_learn_best),
                subtitle = stringResource(Res.string.personalize_experience_short),
                stepLabel = stringResource(Res.string.learning_style_label),
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.LearningMethod),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = learningMethodOptions(),
                selectedOptionId = state.learningMethodId,
                onOptionSelected = { viewModel.onAction(OnboardingAction.LearningMethodSelected(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.StudyTime) }
            )
        }

        composable<OnboardingGraphRoutes.StudyTime> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsStateWithLifecycle()

            StudyTimeScreen(
                title = stringResource(Res.string.when_do_you_study),
                subtitle = stringResource(Res.string.schedule_sessions_time),
                stepLabel = stringResource(Res.string.learning_style_label),
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.StudyTime),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = studyTimeOptions(),
                selectedOptionId = state.studyTimeId,
                onOptionSelected = { viewModel.onAction(OnboardingAction.StudyTimeSelected(it)) },
                onContinueClicked = { navController.navigate(OnboardingGraphRoutes.Language) }
            )
        }

        composable<OnboardingGraphRoutes.Language> {
            val viewModel = rememberOnboardingViewModel(navController)
            val state by viewModel.state.collectAsStateWithLifecycle()

            LanguageSelectionScreen(
                title = stringResource(Res.string.select_your_language),
                subtitle = stringResource(Res.string.localize_experience),
                stepLabel = stringResource(Res.string.personalization_label),
                currentStep = OnboardingFlowSteps.indexOf(OnboardingStepId.Language),
                totalSteps = OnboardingFlowSteps.totalSteps,
                options = languageOptions(),
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

@Composable
private fun gradeOptions() = listOf(
    SelectionOptionData(id = "elementary", label = stringResource(Res.string.elementary)),
    SelectionOptionData(id = "middle_school", label = stringResource(Res.string.middle_school)),
    SelectionOptionData(id = "high_school", label = stringResource(Res.string.high_school)),
    SelectionOptionData(id = "undergraduate", label = stringResource(Res.string.undergraduate)),
    SelectionOptionData(id = "postgraduate", label = stringResource(Res.string.postgraduate))
)

@Composable
private fun subjectOptions() = listOf(
    MultiSelectOptionData(id = "math", label = stringResource(Res.string.math)),
    MultiSelectOptionData(id = "science", label = stringResource(Res.string.science)),
    MultiSelectOptionData(id = "physics", label = stringResource(Res.string.physics)),
    MultiSelectOptionData(id = "chemistry", label = stringResource(Res.string.chemistry)),
    MultiSelectOptionData(id = "biology", label = stringResource(Res.string.biology)),
    MultiSelectOptionData(id = "history", label = stringResource(Res.string.history)),
    MultiSelectOptionData(id = "geography", label = stringResource(Res.string.geography)),
    MultiSelectOptionData(id = "english", label = stringResource(Res.string.english)),
    MultiSelectOptionData(id = "literature", label = stringResource(Res.string.literature)),
    MultiSelectOptionData(id = "computer_science", label = stringResource(Res.string.computer_science))
)

@Composable
private fun goalOptions() = listOf(
    SelectGradeOption(id = "improve_grades", label = stringResource(Res.string.improve_grades)),
    SelectGradeOption(id = "prepare_exams", label = stringResource(Res.string.prepare_exams)),
    SelectGradeOption(id = "learn_new_skills", label = stringResource(Res.string.learn_new_skills)),
    SelectGradeOption(id = "stay_consistent", label = stringResource(Res.string.stay_consistent)),
    SelectGradeOption(id = "get_ahead", label = stringResource(Res.string.get_ahead))
)

@Composable
private fun challengeOptions() = listOf(
    MultiSelectOptionData(id = "focus", label = stringResource(Res.string.staying_focused)),
    MultiSelectOptionData(id = "time_management", label = stringResource(Res.string.time_management)),
    MultiSelectOptionData(id = "understanding", label = stringResource(Res.string.understanding_concepts)),
    MultiSelectOptionData(id = "exam_anxiety", label = stringResource(Res.string.exam_anxiety)),
    MultiSelectOptionData(id = "motivation", label = stringResource(Res.string.motivation)),
    MultiSelectOptionData(id = "note_taking", label = stringResource(Res.string.note_taking))
)

@Composable
private fun learningMethodOptions() = listOf(
    LearningMethodsData(id = "videos", label = stringResource(Res.string.video_lessons)),
    LearningMethodsData(id = "quizzes", label = stringResource(Res.string.quizzes_practice)),
    LearningMethodsData(id = "flashcards", label = stringResource(Res.string.flashcards)),
    LearningMethodsData(id = "reading", label = stringResource(Res.string.reading_summaries)),
    LearningMethodsData(id = "interactive", label = stringResource(Res.string.interactive_exercises))
)

@Composable
private fun studyTimeOptions() = listOf(
    StudyTimeData(id = "early_morning", label = stringResource(Res.string.early_morning)),
    StudyTimeData(id = "morning", label = stringResource(Res.string.morning)),
    StudyTimeData(id = "afternoon", label = stringResource(Res.string.afternoon)),
    StudyTimeData(id = "evening", label = stringResource(Res.string.evening)),
    StudyTimeData(id = "night", label = stringResource(Res.string.night))
)

@Composable
private fun languageOptions() = listOf(
    LanguageData(
        id = "english",
        label = stringResource(Res.string.english),
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
        label = stringResource(Res.string.arabic),
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
        label = stringResource(Res.string.french),
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
        label = stringResource(Res.string.spanish),
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
        label = stringResource(Res.string.german),
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
        label = stringResource(Res.string.chinese),
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
