package ru.vladik.kuber.api.kuber.models

import com.google.gson.annotations.SerializedName

data class NamespaceList(@SerializedName("items") val items: List<Namespace>)