package ru.vladik.kuber.models

import ru.vladik.kuber.utils.EnvVars

class Alert(var type: AlertType, var message: String) {
    enum class AlertType(val classname: String) {
        ERROR("err"),
        WARNING("warn"),
        SUCCESS("success")
    }

    companion object {
        fun forEnvVar(envVar: EnvVars) : Alert {
            return Alert(AlertType.WARNING, "Переменная окружения ${envVar.name} не задана")
        }
    }
}