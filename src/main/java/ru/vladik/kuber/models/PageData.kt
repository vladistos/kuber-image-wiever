package ru.vladik.kuber.models

import ru.vladik.kuber.api.kuber.models.Namespace

class PageData(var imageList: HashSet<LinkImage> = LinkedHashSet(), var namespaceList: MutableList<String> = ArrayList(),
               var alertList: MutableList<Alert> = ArrayList(), var currentNamespace: String)
