package com.codeplaydata.controller.provider

import com.codeplaydata.crd.provider.LlmProvider
import com.codeplaydata.crd.provider.LlmProviderState
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl
import org.slf4j.LoggerFactory

class OllamaStrategy(
    val ollamaCrdGroupName: String = "models.ollama.ayaka.io"
) : LlmProviderReconciler() {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun reconcile(resource: LlmProvider, context: Context<LlmProvider>): UpdateControl<LlmProvider> {
        logger.info("Reconciling LlmProvider for Ollama: {}", resource.metadata.name)
        val crdExists = context.client.apiextensions().v1().customResourceDefinitions()
            .withName(ollamaCrdGroupName).get() != null
        if (!crdExists) {
            logger.warn("Ollama Operator CRD not found.")
            val message = """
                Ollama Operator CRD $ollamaCrdGroupName not found in cluster. Please install Ollama Operator following the instructions
                in this link: https://ollama-operator.ayaka.io/pages/en/guide/getting-started/crd.html.
            """.trimIndent()
            return updateStatus(resource, LlmProviderState.PENDING_DEPENDENCY, message)
        }
        return updateStatus(resource, LlmProviderState.READY, "Ollama Operator dependency satisfied.")
    }
}