package com.brainest.presentation.onboarding

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class OnboardingViewModel : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state

    fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.NameChanged -> _state.update {
                it.copy(name = action.value)
            }

            is OnboardingAction.GradeSelected -> _state.update {
                it.copy(gradeId = action.id)
            }

            is OnboardingAction.SubjectToggled -> _state.update {
                it.copy(subjectIds = toggleId(it.subjectIds, action.id))
            }

            OnboardingAction.ClearSubjects -> _state.update {
                it.copy(subjectIds = emptySet())
            }

            is OnboardingAction.GoalSelected -> _state.update {
                it.copy(goalId = action.id)
            }

            is OnboardingAction.ChallengeToggled -> _state.update {
                it.copy(challengeIds = toggleId(it.challengeIds, action.id))
            }

            OnboardingAction.ClearChallenges -> _state.update {
                it.copy(challengeIds = emptySet())
            }

            is OnboardingAction.StudyHoursChanged -> _state.update {
                it.copy(studyHours = action.hours)
            }

            is OnboardingAction.LearningMethodSelected -> _state.update {
                it.copy(learningMethodId = action.id)
            }

            is OnboardingAction.StudyTimeSelected -> _state.update {
                it.copy(studyTimeId = action.id)
            }

            is OnboardingAction.LanguageSelected -> _state.update {
                it.copy(languageId = action.id)
            }

            OnboardingAction.Reset -> _state.value = OnboardingState()
        }
    }

    private fun toggleId(current: Set<String>, id: String): Set<String> {
        return if (current.contains(id)) {
            current - id
        } else {
            current + id
        }
    }
}
