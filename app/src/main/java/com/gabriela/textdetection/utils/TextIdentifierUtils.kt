package com.gabriela.textdetection.utils

import android.content.Context

import com.gabriela.textdetection.R
import com.gabriela.textdetection.dictionary.Dictionary

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

import br.com.concretesolutions.canarinho.validator.Validador

object TextIdentifierUtils {

    private val CPF_SIZE = 11
    private val DAY_MONTH_SIZE = 2
    private val YEAR_SIZE = 4

    private fun removePossibleSeparators(text: String): String {
        return text.replace("|".toRegex(), "")
                .replace("!".toRegex(), "")
                .replace("\\\\".toRegex(), "")
    }

    fun isCpfValue(text: String): Boolean {
        var text = removePossibleSeparators(text)
        text = FormatUtils.cleanFormattting(text)
        return Validador.CPF.ehValido(text)
    }

    fun getCpf(text: String): String? {
        var text = text
        text = FormatUtils.cleanFormattting(removePossibleSeparators(text))

        if (isLettersOnly(text)) {
            return null
        }

        var cpf: String? = null
        var value: String
        if (text.length >= CPF_SIZE) {
            for (i in 0..text.length - CPF_SIZE - 1) {
                if (text.length > i + CPF_SIZE) {
                    value = text.substring(i, i + CPF_SIZE)
                    if (Validador.CPF.ehValido(value)) {
                        cpf = value
                        break
                    }
                }
            }
        }

        return cpf
    }

    fun getBirthDate(text: String, context: Context): Date? {
        var text = text
        text = text.replace(" ".toRegex(), "")
        val pieces = text.split("/".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()

        if (pieces.size < 3 || pieces[0].length < DAY_MONTH_SIZE || pieces[1].length != DAY_MONTH_SIZE || pieces[2].length < YEAR_SIZE) {
            return null
        }

        val day = replaceLettersLookLikeNumbers(
                pieces[0].substring(
                        pieces[0].length - 2,
                        pieces[0].length)
                        .toLowerCase())
        val month = replaceLettersLookLikeNumbers(pieces[1].toLowerCase())
        val year = replaceLettersLookLikeNumbers(pieces[2].substring(0, 4).toLowerCase())
        return formatDate("$day/$month/$year", context)
    }

    private fun replaceLettersLookLikeNumbers(text: String): String {
        return text.replace("o".toRegex(), "0")
                .replace("b".toRegex(), "8")
                .replace("l".toRegex(), "1")
    }

    private fun isLettersOnly(str: CharSequence): Boolean {
        var cp: Int
        var i = 0
        while (i < str.length) {
            cp = Character.codePointAt(str, i)
            if (!Character.isLetter(cp) && !Character.isSpaceChar(cp)) {
                return false
            }
            i += Character.charCount(cp)
        }
        return true
    }

    fun isDate(text: String, context: Context): Boolean {
        return formatDate(text, context) != null
    }

    fun formatDate(text: String, context: Context): Date? {
        var text = text
        text = removePossibleSeparators(text)
        var date: Date? = null
        try {
            val dateFormat = SimpleDateFormat(context.getString(R.string.birth_date_format))
            dateFormat.isLenient = false
            date = dateFormat.parse(text)
        } catch (e: ParseException) {
        }

        return date
    }

    fun mightBeName(text: String): Boolean {
        var text = removePossibleSeparators(text).toLowerCase()
        return isLettersOnly(text) && text.contains(" ") && isAllowedName(text)
    }

    private fun isAllowedName(text: String): Boolean {
        val words = text.split(" ")
        for (word in words) {
            if (Dictionary.getInstance().isInForbidden(word)) {
                return false
            }
        }
        return true
    }
}
