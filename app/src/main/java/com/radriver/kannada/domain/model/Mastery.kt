package com.radriver.kannada.domain.model

/**
 * Single source of truth for the mastery rule: a word is mastered once it has
 * been answered correctly this many times in quizzes. The DAO queries take it
 * as a bind parameter so the SQL never hard-codes the number.
 */
const val MASTERY_THRESHOLD = 3
