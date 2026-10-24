package com.radriver.kannada.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import com.radriver.kannada.data.local.entity.WordEntity
import com.radriver.kannada.domain.model.Language
import com.radriver.kannada.domain.model.QuizWord
import com.radriver.kannada.domain.model.displayText

/*
 * The one place a word in the language being learned is turned into text.
 * Kannada shows its script with the romanization underneath; Gujarati shows
 * its romanization where the script would be and no second line — its
 * script lives in the database only for TTS and must never reach the screen.
 * Every composable here decides from the word's OWN language (not the
 * screen's), so a frame rendered mid-language-switch can't leak a script.
 */

/** What the learner sees for this word (see [Language.showsScript]). */
val WordEntity.displayText: String get() = language.displayText(script, transliteration)

/** What the learner sees for this word (see [Language.showsScript]). */
val QuizWord.displayText: String get() = language.displayText(script, transliteration)

/** The word's main line: script, or romanization when the script is hidden. */
@Composable
fun TargetPrimaryText(
    word: WordEntity,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) = Text(text = word.displayText, style = style, color = color, modifier = modifier)

/** The word's main line: script, or romanization when the script is hidden. */
@Composable
fun TargetPrimaryText(
    word: QuizWord,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) = Text(text = word.displayText, style = style, color = color, modifier = modifier)

/** The italic romanization under a script; nothing when the primary line already is the romanization. */
@Composable
fun TargetSecondaryText(
    word: WordEntity,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) = SecondaryText(word.transliteration, word.language, style, modifier, color)

/** The italic romanization under a script; nothing when the primary line already is the romanization. */
@Composable
fun TargetSecondaryText(
    word: QuizWord,
    style: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified
) = SecondaryText(word.transliteration, word.language, style, modifier, color)

@Composable
private fun SecondaryText(
    transliteration: String,
    language: Language,
    style: TextStyle,
    modifier: Modifier,
    color: Color
) {
    if (!language.showsScript) return
    Text(
        text = transliteration,
        style = style,
        fontStyle = FontStyle.Italic,
        color = color,
        modifier = modifier
    )
}
