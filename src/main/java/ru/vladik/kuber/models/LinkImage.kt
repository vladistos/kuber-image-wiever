package ru.vladik.kuber.models

import ru.vladik.kuber.utils.EnvVars
import ru.vladik.kuber.utils.getEnvVarOrEmpty

data class LinkImage(val image: String) {
    val link: String
    val isAllowed: Boolean
    val imageShow: String

    init {
        val i0 = image.indexOfLast { c: Char ->  c.toString() == "/"}
        val i1 = image.indexOfLast { c: Char -> c.toString() == ":" }
        link = if (i0 != -1) "${image.substring(i0, if (i1 != -1 && i1>i0) i1 else image.length)}/" else "/"

        val allowedPrefixes = getAllowedPrefixes()
        isAllowed = if (allowedPrefixes.isEmpty()) {
            true
        } else if (i0 == -1) {
            false
        } else {
            val s = image.substring(0, i0)
            val i2 = s.indexOfLast { c: Char -> c.toString() == "/" }
            val prefix = if (i2 == -1) s else s.substring(i2 + 1, s.length)
            if (allowedPrefixes.isNotEmpty()) allowedPrefixes.contains(prefix) else true
        }
        imageShow = if (i0 != -1 && i0 < image.length) image.substring(i0+1, image.length) else image
    }

    private fun getAllowedPrefixes(): Array<String> {
        val ap = getEnvVarOrEmpty(EnvVars.ALLOWED_PREFIXES.name)
        return if (ap.isNotEmpty()) ap.split(",").toTypedArray() else emptyArray()
    }

    companion object {
        val COMPARATOR = Comparator<LinkImage> { o1, o2 -> return@Comparator o1.imageShow.compareTo(o2.imageShow)}
    }
}