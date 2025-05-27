/*
 * Copyright 2025 Mifos Initiative
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * See See https://github.com/openMF/kmp-project-template/blob/main/LICENSE
 */
package template.core.base.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import template.core.base.designsystem.component.variant.CardVariant
import template.core.base.designsystem.config.KptTestTags

/**
 * A flexible and theme-aware card component for the KPT design system.
 *
 * This composable abstracts over Material3 [Card], [ElevatedCard],
 * and [OutlinedCard], and selects the appropriate variant based on
 * [CardVariant].
 *
 * @param modifier Modifier applied to the card container. *(Default:
 *    [Modifier])*
 * @param onClick Lambda triggered when the card is clicked. *(Default:
 *    `{}`)*
 * @param enabled Whether the card is enabled and responds to click events.
 *    *(Default: `true`)*
 * @param variant Determines the visual style of the card. *(Default:
 *    [CardVariant.FILLED])*
 * @param shape The shape of the card. *(Optional; defaults to the
 *    variant's shape or design token)*
 * @param colors The color configuration of the card. *(Optional; defaults
 *    to the variant's colors via [CardDefaults])*
 * @param elevation Elevation of the card surface. *(Optional; defaults to
 *    the variant's elevation or design token)*
 * @param borderStroke Border stroke for the card. (Optional; defaults to
 *    [CardDefaults.outlinedCardBorder] when [variant] is
 *    [CardVariant.OUTLINED], `null` otherwise)*
 * @param interactionSource The [MutableInteractionSource] to observe user
 *    interaction states. *(Optional)*
 * @param testTag Tag for UI testing. *(Optional)*
 * @param contentDescription Content description for accessibility.
 *    *(Optional)*
 * @param content The content of the card, scoped to a [ColumnScope] for
 *    vertical layout flexibility.
 */
@Composable
fun KptCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    enabled: Boolean = true,
    variant: CardVariant = CardVariant.FILLED,
    shape: Shape? = null,
    colors: CardColors? = null,
    elevation: CardElevation? = null,
    borderStroke: BorderStroke? = null,
    interactionSource: MutableInteractionSource? = null,
    testTag: String? = null,
    contentDescription: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val cardModifier = modifier
        .then(Modifier.testTag(testTag ?: KptTestTags.Card))
        .then(
            if (contentDescription != null) Modifier.semantics {
                this.contentDescription = contentDescription
            } else Modifier,
        )

    when (variant) {
        CardVariant.FILLED -> Card(
            onClick = onClick,
            modifier = cardModifier,
            enabled = enabled,
            shape = shape ?: CardDefaults.shape,
            colors = colors ?: CardDefaults.cardColors(),
            elevation = elevation ?: CardDefaults.cardElevation(),
            border = borderStroke,
            interactionSource = interactionSource,
        ) { content() }

        CardVariant.ELEVATED -> ElevatedCard(
            onClick = onClick,
            modifier = cardModifier,
            enabled = enabled,
            shape = shape ?: CardDefaults.elevatedShape,
            colors = colors ?: CardDefaults.elevatedCardColors(),
            elevation = elevation ?: CardDefaults.elevatedCardElevation(),
            interactionSource = interactionSource,
        ) { content() }

        CardVariant.OUTLINED -> OutlinedCard(
            onClick = onClick,
            modifier = cardModifier,
            enabled = enabled,
            shape = shape ?: CardDefaults.outlinedShape,
            colors = colors ?: CardDefaults.outlinedCardColors(),
            elevation = elevation ?: CardDefaults.outlinedCardElevation(),
            border = borderStroke ?: CardDefaults.outlinedCardBorder(enabled),
            interactionSource = interactionSource,
        ) { content() }
    }
}