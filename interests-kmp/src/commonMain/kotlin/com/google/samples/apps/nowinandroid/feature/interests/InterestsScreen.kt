/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.nowinandroid.feature.interests

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaBackground
import com.google.samples.apps.nowinandroid.core.designsystem.component.NiaLoadingWheel
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme
import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic
import com.google.samples.apps.nowinandroid.core.ui.DevicePreviews
import com.google.samples.apps.nowinandroid.core.ui.FollowableTopicPreviewParameterProvider
import com.google.samples.apps.nowinandroid.core.ui.TrackScreenViewEvent
import interests.generated.resources.Res
import interests.generated.resources.feature_interests_empty_header
import interests.generated.resources.feature_interests_loading
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun InterestsRoute(
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    shouldHighlightSelectedTopic: Boolean = false,
    viewModel: InterestsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InterestsScreen(
        uiState = uiState,
        followTopic = viewModel::followTopic,
        onTopicClick = {
            viewModel.onTopicClick(it)
            onTopicClick(it)
        },
        shouldHighlightSelectedTopic = shouldHighlightSelectedTopic,
        modifier = modifier,
    )
}

@Composable
internal fun InterestsScreen(
    uiState: InterestsUiState,
    followTopic: (String, Boolean) -> Unit,
    onTopicClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    shouldHighlightSelectedTopic: Boolean = false,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (uiState) {
            InterestsUiState.Loading ->
                NiaLoadingWheel(
                    contentDesc = stringResource(Res.string.feature_interests_loading),
                )

            is InterestsUiState.Interests ->
                TopicsTabContent(
                    topics = uiState.topics,
                    onTopicClick = onTopicClick,
                    onFollowButtonClick = followTopic,
                    selectedTopicId = uiState.selectedTopicId,
                    shouldHighlightSelectedTopic = shouldHighlightSelectedTopic,
                )

            is InterestsUiState.Empty -> InterestsEmptyScreen()
        }
    }
    TrackScreenViewEvent(screenName = "Interests")
}

@Composable
private fun InterestsEmptyScreen() {
    Text(text = stringResource(Res.string.feature_interests_empty_header))
}

@DevicePreviews
@Composable
internal fun InterestsScreenPopulated(
    @PreviewParameter(FollowableTopicPreviewParameterProvider::class)
    followableTopics: List<FollowableTopic>,
) {
    NiaTheme {
        NiaBackground {
            InterestsScreen(
                uiState = InterestsUiState.Interests(
                    selectedTopicId = null,
                    topics = followableTopics,
                ),
                followTopic = { _, _ -> },
                onTopicClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
private fun InterestsScreenLoading() {
    NiaTheme {
        NiaBackground {
            InterestsScreen(
                uiState = InterestsUiState.Loading,
                followTopic = { _, _ -> },
                onTopicClick = {},
            )
        }
    }
}

@DevicePreviews
@Composable
private fun InterestsScreenEmpty() {
    NiaTheme {
        NiaBackground {
            InterestsScreen(
                uiState = InterestsUiState.Empty,
                followTopic = { _, _ -> },
                onTopicClick = {},
            )
        }
    }
}
