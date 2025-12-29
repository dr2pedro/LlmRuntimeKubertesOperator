package com.codeplaydata.controller.model

import com.codeplaydata.crd.common.ProviderType
import com.codeplaydata.crd.model.LlmModel
import com.codeplaydata.crd.model.LlmModelStatus
import com.codeplaydata.crd.provider.LlmProvider
import com.codeplaydata.crd.provider.LlmProviderState
import io.fabric8.kubernetes.client.KubernetesClient
import io.fabric8.kubernetes.client.dsl.CreateOrReplaceable
import io.fabric8.kubernetes.client.dsl.base.ResourceDefinitionContext
import io.fabric8.kubernetes.client.utils.internal.CreateOrReplaceHelper
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import org.slf4j.LoggerFactory

class LlmModelReconciler() : Reconciler<LlmModel> {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun reconcile(resource: LlmModel, context: Context<LlmModel>): UpdateControl<LlmModel> {
        val providerName = resource.spec.provider
        val provider = context.client.resources(LlmProvider::class.java)
            .inNamespace(resource.metadata.namespace)
            .withName(providerName)
            .get()

        if (provider == null) {
            return updateStatus(resource, LlmProviderState.ERROR, "Provider '$providerName' not found")
        }

        if (provider.status?.state != LlmProviderState.READY) {
            return updateStatus(resource, LlmProviderState.PENDING, "Provider '$providerName' is not READY")
        }

        if (provider.spec.type == ProviderType.OLLAMA) {
            return reconcileOllamaModel(resource, context.client)
        }

        return UpdateControl.noUpdate()
    }

    private fun reconcileOllamaModel(resource: LlmModel, client: KubernetesClient): UpdateControl<LlmModel> {
        val ollamaCrdContext = ResourceDefinitionContext.Builder()
            .withGroup("ollama.ayaka.io")
            .withVersion("v1")
            .withKind("Model")
            .withPlural("models")
            .withNamespaced(true)
            .build()

        val modelName = resource.metadata.name
        val modelImage = resource.spec.model
        val replicas = resource.spec.replicas

        val modelManifest = mapOf(
            "apiVersion" to "ollama.ayaka.io/v1",
            "kind" to "Model",
            "metadata" to mapOf(
                "name" to modelName,
                "namespace" to resource.metadata.namespace
            ),
            "spec" to mapOf(
                "image" to modelImage,
                "replicas" to replicas
            )
        )

        try {
            val genericClient = client.genericKubernetesResources(ollamaCrdContext)
                .inNamespace(resource.metadata.namespace)

            // Usando load() para carregar o mapa e serverSideApply() para criar/atualizar
            //val created = genericClient.load(modelManifest).serverSideApply()
            
            //logger.info("Ollama Model reconciled: ${created.metadata.name}")
            return updateStatus(resource, LlmProviderState.READY, "Ollama Model '$modelName' created/updated")

        } catch (e: Exception) {
            logger.error("Failed to reconcile Ollama Model", e)
            return updateStatus(resource, LlmProviderState.ERROR, "Failed to create Ollama Model: ${e.message}")
        }
    }

    private fun updateStatus(resource: LlmModel, state: LlmProviderState, message: String): UpdateControl<LlmModel> {
        if (resource.status == null) resource.status = LlmModelStatus()
        
        if (resource.status.state != state || resource.status.message != message) {
            resource.status.state = state
            resource.status.message = message
            return UpdateControl.patchStatus(resource)
        }
        return UpdateControl.noUpdate()
    }
}