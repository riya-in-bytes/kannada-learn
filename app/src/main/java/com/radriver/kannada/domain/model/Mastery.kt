package com.radriver.kannada.domain.model

/**
 * Single source of truth for the mastery rule: a word is mastered once it has
 * been answered correctly this many times in quizzes. The DAO queries take it
 * as a bind parameter so the SQL never hard-codes the number.
 */
const val MASTERY_THRESHOLD = 3

/**
 * Leitner-lite review intervals, in days, indexed by box where
 * box = min(correctCount, 3). A word answered on day D is due for review on
 * D + REVIEW_INTERVAL_DAYS[box]. Boxes never demote — wrong answers instead
 * bump wrongCount, which orders the review queue (most-struggled first).
 *
 * SQL can't bind per-row constants, so the CASE expressions in
 * [com.radriver.kannada.data.local.dao.WordDao.dueForReview] and
 * [com.radriver.kannada.data.local.dao.WordDao.dueForReviewCount] hard-code
 * these same numbers — keep all three in sync.
 */
val REVIEW_INTERVAL_DAYS = intArrayOf(1, 2, 4, 7)
