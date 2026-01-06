package com.crowncode.util

import android.util.Patterns

/**
 * Utility object for form validation.
 */
object ValidationUtils {

    /**
     * Validates an email address format.
     *
     * @param email The email address to validate
     * @return true if the email format is valid, false otherwise
     */
    fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Validates a password against minimum requirements.
     *
     * @param password The password to validate
     * @param minLength Minimum required length (default: 6)
     * @return true if the password meets requirements, false otherwise
     */
    fun isValidPassword(password: String, minLength: Int = 6): Boolean {
        return password.length >= minLength
    }

    /**
     * Validates a name field (not blank and has reasonable length).
     *
     * @param name The name to validate
     * @param minLength Minimum required length (default: 2)
     * @return true if the name is valid, false otherwise
     */
    fun isValidName(name: String, minLength: Int = 2): Boolean {
        return name.isNotBlank() && name.trim().length >= minLength
    }

    /**
     * Returns an error message for email validation.
     *
     * @param email The email to check
     * @return Error message if invalid, null if valid
     */
    fun getEmailError(email: String): String? {
        return when {
            email.isBlank() -> "Lütfen email adresinizi girin"
            !isValidEmail(email) -> "Geçersiz email formatı"
            else -> null
        }
    }

    /**
     * Returns an error message for password validation.
     *
     * @param password The password to check
     * @param minLength Minimum required length
     * @return Error message if invalid, null if valid
     */
    fun getPasswordError(password: String, minLength: Int = 6): String? {
        return when {
            password.isBlank() -> "Lütfen şifrenizi girin"
            password.length < minLength -> "Şifre en az $minLength karakter olmalıdır"
            else -> null
        }
    }

    /**
     * Returns an error message for name validation.
     *
     * @param name The name to check
     * @return Error message if invalid, null if valid
     */
    fun getNameError(name: String): String? {
        return when {
            name.isBlank() -> "Lütfen adınızı girin"
            name.trim().length < 2 -> "İsim en az 2 karakter olmalıdır"
            else -> null
        }
    }
}
