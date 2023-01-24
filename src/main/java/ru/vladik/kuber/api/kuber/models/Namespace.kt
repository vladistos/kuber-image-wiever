package ru.vladik.kuber.api.kuber.models

import com.google.gson.annotations.SerializedName

data class Namespace(@SerializedName("metadata") val metadata: NamespaceMetadata)
