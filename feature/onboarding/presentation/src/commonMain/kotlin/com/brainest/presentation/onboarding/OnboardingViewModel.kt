package com.brainest.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scelio.brainest.domain.onboarding.OnboardingData
import com.scelio.brainest.domain.onboarding.OnboardingStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val store: OnboardingStore
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state: StateFlow<OnboardingState> = _state

    init {
        viewModelScope.launch {
            store.data.collect { data ->
                _state.value = data.toState()
            }
        }
    }

    fun onAction(action: OnboardingAction) {
        when (action) {
            is OnboardingAction.NameChanged -> updateAndSave {
                it.copy(name = action.value)
            }

            is OnboardingAction.GradeSelected -> updateAndSave {
                it.copy(gradeId = action.id)
            }

            is OnboardingAction.SubjectToggled -> updateAndSave {
                it.copy(subjectIds = toggleId(it.subjectIds, action.id))
            }

            OnboardingAction.ClearSubjects -> updateAndSave {
                it.copy(subjectIds = emptySet())
            }

            is OnboardingAction.GoalSelected -> updateAndSave {
                it.copy(goalId = action.id)
            }

            is OnboardingAction.ChallengeToggled -> updateAndSave {
                it.copy(challengeIds = toggleId(it.challengeIds, action.id))
            }

            OnboardingAction.ClearChallenges -> updateAndSave {
                it.copy(challengeIds = emptySet())
            }

            is OnboardingAction.StudyHoursChanged -> updateAndSave {
                it.copy(studyHours = action.hours)
            }

            is OnboardingAction.LearningMethodSelected -> updateAndSave {
                it.copy(learningMethodId = action.id)
            }

            is OnboardingAction.StudyTimeSelected -> updateAndSave {
                it.copy(studyTimeId = action.id)
            }

            is OnboardingAction.LanguageSelected -> updateAndSave {
                it.copy(languageId = action.id)
            }

            OnboardingAction.Reset -> {
                _state.value = OnboardingState()
            }
        }
    }

    private fun updateAndSave(update: (OnboardingState) -> OnboardingState) {
        val newState = update(_state.value)
        _state.value = newState
        viewModelScope.launch { store.save(newState.toData()) }
    }

    private fun toggleId(current: Set<String>, id: String): Set<String> {
        return if (current.contains(id)) {
            current - id
        } else {
            current + id
        }
    }

    private fun OnboardingData.toState(): OnboardingState {
        return OnboardingState(
            name = name,
            gradeId = gradeId,
            subjectIds = subjectIds,
            goalId = goalId,
            challengeIds = challengeIds,
            studyHours = studyHours,
            learningMethodId = learningMethodId,
            studyTimeId = studyTimeId,
            languageId = languageId
        )
    }

    private fun OnboardingState.toData(): OnboardingData {
        return OnboardingData(
            name = name,
            gradeId = gradeId,
            subjectIds = subjectIds,
            goalId = goalId,
            challengeIds = challengeIds,
            studyHours = studyHours,
            learningMethodId = learningMethodId,
            studyTimeId = studyTimeId,
            languageId = languageId
        )
    }
}
