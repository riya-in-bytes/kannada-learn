package com.radriver.kannada.ui.quiz

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.radriver.kannada.R
import com.radriver.kannada.domain.model.QuizOption
import com.radriver.kannada.domain.model.QuizQuestion
import com.radriver.kannada.ui.components.SpeakerButton
import com.radriver.kannada.ui.components.TtsUnavailableHint
import com.radriver.kannada.ui.theme.CorrectGreen
import com.radriver.kannada.ui.theme.CorrectGreenContainer
import com.radriver.kannada.ui.theme.WrongRed
import com.radriver.kannada.ui.theme.WrongRedContainer
import com.radriver.kannada.util.TtsManager
import com.radriver.kannada.data.repository.WordRepository
import kotlin.math.roundToInt

/** Stateful entry point: owns the ViewModel, hoists state into stateless content. */
@Composable
fun QuizScreen(
    viewModelFactory: ViewModelProvider.Factory,
    ttsManager: TtsManager,
    modifier: Modifier = Modifier,
    viewModel: QuizViewModel = viewModel(factory = viewModelFactory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is QuizUiState.Setup -> QuizSetupContent(
            state = state,
            onQuestionCountChanged = viewModel::onQuestionCountChanged,
            onStart = viewModel::startQuiz,
            modifier = modifier
        )

        is QuizUiState.InProgress -> QuizQuestionContent(
            state = state,
            ttsManager = ttsManager,
            onOptionSelected = viewModel::onOptionSelected,
            onNext = viewModel::onNext,
            modifier = modifier
        )

        is QuizUiState.Finished -> QuizResultContent(
            state = state,
            onRestart = viewModel::onRestart,
            modifier = modifier
        )
    }
}

@Composable
private fun QuizSetupContent(
    state: QuizUiState.Setup,
    onQuestionCountChanged: (Int) -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.quiz_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.quiz_start_prompt),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.quiz_question_count_label) + ": ${state.questionCount}",
            style = MaterialTheme.typography.titleMedium
        )
        Slider(
            value = state.questionCount.toFloat(),
            // roundToInt, not toInt: snapped step values can land at 6.9999997f.
            onValueChange = { onQuestionCountChanged(it.roundToInt()) },
            valueRange = WordRepository.MIN_QUIZ_SIZE.toFloat()..WordRepository.MAX_QUIZ_SIZE.toFloat(),
            steps = WordRepository.MAX_QUIZ_SIZE - WordRepository.MIN_QUIZ_SIZE - 1,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (state.isBuilding) {
            CircularProgressIndicator()
        } else {
            Button(onClick = onStart) {
                Text(stringResource(R.string.quiz_start_button))
            }
        }

        if (state.notEnoughWords) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.quiz_not_enough_words),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
private fun QuizQuestionContent(
    state: QuizUiState.InProgress,
    ttsManager: TtsManager,
    onOptionSelected: (Int) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val question = state.currentQuestion

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.quiz_progress, state.currentIndex + 1, state.questions.size),
            style = MaterialTheme.typography.labelLarge
        )
        LinearProgressIndicator(
            progress = { (state.currentIndex + 1).toFloat() / state.questions.size.toFloat() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TtsUnavailableHint(ttsManager = ttsManager)

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.quiz_prompt, question.promptEnglish),
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        question.options.forEach { option ->
            QuizOptionCard(
                option = option,
                question = question,
                answered = state.answered,
                selectedWordId = state.selectedWordId,
                ttsManager = ttsManager,
                onOptionSelected = onOptionSelected,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onNext,
            enabled = state.answered,
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(
                text = stringResource(
                    if (state.isLastQuestion) R.string.quiz_finish else R.string.quiz_next
                )
            )
        }
    }
}

@Composable
private fun QuizOptionCard(
    option: QuizOption,
    question: QuizQuestion,
    answered: Boolean,
    selectedWordId: Int?,
    ttsManager: TtsManager,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isCorrectOption = option.wordId == question.correctWordId
    val isSelected = option.wordId == selectedWordId

    // Post-answer feedback: correct option always green, a wrong pick red.
    val containerColor = when {
        answered && isCorrectOption -> CorrectGreenContainer
        answered && isSelected -> WrongRedContainer
        else -> MaterialTheme.colorScheme.surface
    }
    val borderColor = when {
        answered && isCorrectOption -> CorrectGreen
        answered && isSelected -> WrongRed
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    val contentColor = if (answered && (isCorrectOption || isSelected)) {
        Color(0xFF1B1B13)
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Card(
        onClick = { onOptionSelected(option.wordId) },
        enabled = !answered,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            disabledContainerColor = containerColor,
            contentColor = contentColor,
            disabledContentColor = contentColor
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f, fill = false)) {
                Text(
                    text = option.kannada,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = option.transliteration,
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = FontStyle.Italic
                )
            }
            SpeakerButton(text = option.kannada, ttsManager = ttsManager)
        }
    }
}

@Composable
private fun QuizResultContent(
    state: QuizUiState.Finished,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fraction = if (state.total == 0) 0f else state.score.toFloat() / state.total.toFloat()
    val message = when {
        fraction >= 1f -> stringResource(R.string.quiz_score_perfect)
        fraction >= 0.6f -> stringResource(R.string.quiz_score_good)
        else -> stringResource(R.string.quiz_score_low)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.quiz_score_title),
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.quiz_score_value, state.score, state.total),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onRestart) {
            Text(stringResource(R.string.quiz_restart))
        }
    }
}
