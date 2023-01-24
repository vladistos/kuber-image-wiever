package ru.vladik.kuber.api.kuber.models

data class Pod(val metadata: PodMetadata, val spec: PodSpec)
