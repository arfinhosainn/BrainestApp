package com.scelio.brainest.data.onboarding.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.scelio.brainest.domain.onboarding.OnboardingData
import com.scelio.brainest.domain.onboarding.OnboardingStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class OnboardingStoreImpl(
    private val dataStore: DataStore<Preferences>
) : OnboardingStore {

    override val data: Flow<OnboardingData> = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs ->
            OnboardingData(
                name = prefs[nameKey] ?: "",
                gradeId = prefs[gradeKey],
                subjectIds = prefs[subjectKey] ?: emptySet(),
                goalId = prefs[goalKey],
                challengeIds = prefs[challengeKey] ?: emptySet(),
                studyHours = prefs[studyHoursKey] ?: 2,
                learningMethodId = prefs[learningMethodKey],
                studyTimeId = prefs[studyTimeKey],
                languageId = prefs[languageKey]
            )
        }

    override suspend fun save(data: OnboardingData) {
        dataStore.edit { prefs ->
            prefs[nameKey] = data.name
            setOrRemove(prefs, gradeKey, data.gradeId)
            prefs[subjectKey] = data.subjectIds
            setOrRemove(prefs, goalKey, data.goalId)
            prefs[challengeKey] = data.challengeIds
            prefs[studyHoursKey] = data.studyHours
            setOrRemove(prefs, learningMethodKey, data.learningMethodId)
            setOrRemove(prefs, studyTimeKey, data.studyTimeId)
            setOrRemove(prefs, languageKey, data.languageId)
        }
    }

    override suspend fun clear() {
        dataStore.edit { it.clear() }
    }
}

private val nameKey = stringPreferencesKey("name")
private val gradeKey = stringPreferencesKey("grade_id")
private val subjectKey = stringSetPreferencesKey("subject_ids")
private val goalKey = stringPreferencesKey("goal_id")
private val challengeKey = stringSetPreferencesKey("challenge_ids")
private val studyHoursKey = intPreferencesKey("study_hours")
private val learningMethodKey = stringPreferencesKey("learning_method_id")
private val studyTimeKey = stringPreferencesKey("study_time_id")
private val languageKey = stringPreferencesKey("language_id")

private fun setOrRemove(
    prefs: MutablePreferences,
    key: Preferences.Key<String>,
    value: String?
) {
    if (value == null) {
        prefs.remove(key)
    } else {
        prefs[key] = value
    }
}
