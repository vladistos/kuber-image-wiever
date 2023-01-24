package ru.vladik.kuber.utils

fun getEnvVarOrThrow(name: String) : String {
    val v = System.getenv()[name]
    if (v != null) return v
    throw IllegalArgumentException("Переменная окружения $name не задана")
}

fun getEnvVarOrEmpty(name: String) : String {
    return System.getenv()[name].orEmpty()
}

enum class EnvVars {
    BASE_URL,
    TOKEN,
    ALLOWED_PREFIXES,
    CONTEXT_PATH,
    ALLOWED_NAMESPACES,
}