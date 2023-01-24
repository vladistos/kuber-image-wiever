package ru.vladik.kuber.api.kuber

import org.springframework.stereotype.Component
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import ru.vladik.kuber.api.kuber.models.NamespaceList
import ru.vladik.kuber.api.kuber.models.PodList

@Component
interface KuberApi {
    @GET("/api/v1/namespaces")
    fun getNamespaces() : Call<NamespaceList>

    @GET("/api/v1/pods")
    fun getAllPods() : Call<PodList>

    @GET("/api/v1/namespaces/{name}/pods")
    fun getNamespacePods(@Path(value = "name", encoded = true) namespaceName: String) : Call<PodList>
}
