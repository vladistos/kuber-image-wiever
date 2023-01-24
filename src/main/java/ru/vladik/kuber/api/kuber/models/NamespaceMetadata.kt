package ru.vladik.kuber.api.kuber.models

import com.google.gson.annotations.SerializedName

data class NamespaceMetadata(@SerializedName("name") val name: String)
