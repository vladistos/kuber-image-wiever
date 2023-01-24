package ru.vladik.kuber.controllers

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import retrofit2.Response
import ru.vladik.kuber.api.kuber.KuberApi
import ru.vladik.kuber.api.kuber.KuberApiProvider
import ru.vladik.kuber.api.kuber.models.Pod
import ru.vladik.kuber.models.Alert
import ru.vladik.kuber.models.LinkImage
import ru.vladik.kuber.models.PageData
import ru.vladik.kuber.utils.EnvVars
import ru.vladik.kuber.utils.getEnvVarOrEmpty
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet

@RestController("/api")
open class Controller1 {


    @GetMapping("/namespaces")
    open fun a(@RequestParam(name = "namespace", defaultValue = "all") selectedNamespace: String, model: Model): String {
        val pageData = PageData(currentNamespace = selectedNamespace)

        with (EnvVars.TOKEN) {
            if (getEnvVarOrEmpty(this.name).isEmpty()) pageData.alertList.add(Alert.forEnvVar(this))
        }

        try {
            val kuberApi = KuberApiProvider.provide()
            val namespaces = getAllowedNamespaces(kuberApi)
            val pods = if (selectedNamespace == "all") getAllowedPods(kuberApi, namespaces) else
                getPodsForNs(kuberApi, namespaces, selectedNamespace)

            namespaces.forEach { namespace ->
                pageData.namespaceList.add(namespace)
            }

            pods.forEach { pod ->
                pod.spec.containers.forEach { container ->
                    val i = LinkImage(container.image)
                    pageData.imageList.add(i)
                }
            }


            pageData.imageList = LinkedHashSet(pageData.imageList.toList().sortedWith(LinkImage.COMPARATOR))

        } catch (e: Exception) {
            e.printStackTrace()
            pageData.alertList.add(Alert(Alert.AlertType.ERROR, "Произошла ошибка во время сбора информации: ${e.message}"))
        }
        model.apply {
            addAttribute("alertList", pageData.alertList)
            addAttribute("namespaceList", pageData.namespaceList)
            addAttribute("namespace", pageData.currentNamespace)
            addAttribute("images", pageData.imageList)
        }
        return "index.html"
    }

    private fun checkResponse(response: Response<out Any>) {
        if (response.isSuccessful) return
        throw RuntimeException(response.errorBody()?.string())
    }

    private fun getAllowedNamespaces(kuberApi: KuberApi) : List<String> {
        val namespacesVar = getEnvVarOrEmpty(EnvVars.ALLOWED_NAMESPACES.name)
        val allowedNamespaces = if (namespacesVar.isNotEmpty()) namespacesVar.split(",") else ArrayList()
        if (allowedNamespaces.isEmpty()) {
            val resp = kuberApi.getNamespaces().execute()
            checkResponse(resp)
            return resp.body()?.items.orEmpty().map { it.metadata.name }
        }

        return allowedNamespaces
    }

    private fun getAllowedPods(kuberApi: KuberApi, allowedNamespaces: List<String>): List<Pod> {
        if (allowedNamespaces.isEmpty()) {
            val resp = kuberApi.getAllPods().execute()
            checkResponse(resp)
            return resp.body()?.items.orEmpty()
        }

        val podList = ArrayList<Pod>()

        for (ns in allowedNamespaces) {
            val resp = kuberApi.getNamespacePods(ns).execute()
            checkResponse(resp)
            podList.addAll(resp.body()?.items.orEmpty())
        }

        return podList
    }

    private fun getPodsForNs(kuberApi: KuberApi, allowedNamespaces: List<String>, namespace: String): List<Pod> {
        if (!allowedNamespaces.contains(namespace)) throw IllegalArgumentException("Пространство имен $namespace не существует либо не разрешено")
        val resp = kuberApi.getNamespacePods(namespace).execute()
        checkResponse(resp)

        return resp.body()?.items.orEmpty()
    }

}