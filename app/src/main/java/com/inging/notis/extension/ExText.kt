package com.inging.notis.extension

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.widget.TextView

/**
 * Created by annasu on 2021/05/18.
 */

// 검색 단어 하이라이트
fun TextView.searchWordHighlight(word: String) {
    if (word.isEmpty()) return

    val spannable = SpannableString(text)
    val regex = when (word.substring(0, 1)) {
        """+""", """*""", """(""", """)""", """?""",
        """{""", """}""", """[""", """|""", """^""",
        """\""", """$""", """₩""", """￦""" -> """\$word"""
        else -> word
    }
    regex.toRegex(RegexOption.IGNORE_CASE).findAll(text.toString()).forEach {
        if (it.range.first <= it.range.last) {
            spannable.setSpan(BackgroundColorSpan(Color.CYAN),
                it.range.first, it.range.last + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(ForegroundColorSpan(Color.BLACK),
                it.range.first, it.range.last + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    text = spannable
}