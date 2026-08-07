package edu.jhu.cobra.commons.value

// Characters that carry special meaning in regex syntax; each is escaped so pattern text matches literally.
private val REGEX_METACHARACTERS = ".^$*+?-()[]{}\\|".toSet()

// Escapes every regex metacharacter so the text matches itself literally inside a pattern.
private fun escapeRegexChars(text: String): String =
    buildString(text.length) {
        text.forEach {
            if (it in REGEX_METACHARACTERS) append('\\')
            append(it)
        }
    }

// Regex fragment each Unsure placeholder stands for.
private val Unsure.regexPattern: String
    get() =
        when (this) {
            Unsure.ANY -> ".*"
            Unsure.STR -> ".*"
            Unsure.NUM -> "\\d+"
            Unsure.BOOL -> "(true|false)"
        }

/**
 * Converts this [StrVal] to a [Regex] pattern, with special character escaping and pattern substitution.
 *
 * This function:
 * 1. Escapes special regex characters
 * 2. Replaces COBRA-specific placeholders — both the bare core spelling (e.g. `__NumVal__`) and the
 *    `Unsure{...}` rendering produced by [Unsure.toString] — with their regex equivalents:
 *    - `Unsure.ANY` → `.*`
 *    - `Unsure.STR` → `.*`
 *    - `Unsure.NUM` → `\d+`
 *    - `Unsure.BOOL` → `(true|false)`
 *
 * Example:
 * ```kotlin
 * val strVal = StrVal("Hello.*")
 * val regex = strVal.toRegex() // Creates Regex("Hello\\.\\*")
 *
 * val pattern = Unsure.ANY.strVal
 * val numRegex = pattern.toRegex() // Creates Regex(".*")
 * ```
 *
 * @param doCaseIgnore Whether to make the regex case-insensitive
 * @return A [Regex] object based on this [StrVal]'s content
 */
public fun StrVal.toRegex(doCaseIgnore: Boolean = false): Regex {
    // Substitution runs on the escaped text, so each placeholder is matched in its escaped rendering.
    // The "Unsure{...}" spelling is replaced before the bare core it contains as a substring.
    val substituted =
        Unsure.entries.fold(escapeRegexChars(core)) { pattern, unsure ->
            pattern
                .replace(escapeRegexChars(unsure.toString()), unsure.regexPattern)
                .replace(unsure.core, unsure.regexPattern)
        }
    return substituted.toRegex(if (doCaseIgnore) setOf(RegexOption.IGNORE_CASE) else setOf())
}

/**
 * Converts the current [Unsure] instance to its corresponding regular expression pattern as a string.
 *
 * - [Unsure.ANY] and [Unsure.STR] are represented as `.*`, allowing matching of any string.
 * - [Unsure.NUM] is represented as `\\d+`, allowing matching of one or more numeric digits.
 * - [Unsure.BOOL] is represented as `(true|false)`, allowing matching of boolean values `true` or `false`.
 *
 * @return A string containing the regular expression pattern corresponding to the current [Unsure] type.
 */
public fun Unsure.toRegex(doCaseIgnore: Boolean = false): Regex =
    regexPattern.toRegex(if (doCaseIgnore) setOf(RegexOption.IGNORE_CASE) else setOf())
