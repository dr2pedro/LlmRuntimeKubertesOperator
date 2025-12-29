package com.codeplaydata.controller.provider

import com.codeplaydata.crd.provider.LlmProvider
import com.codeplaydata.crd.common.ProviderType
import com.codeplaydata.crd.provider.LlmProviderState
import com.codeplaydata.crd.provider.LlmProviderStatus
import io.javaoperatorsdk.operator.api.reconciler.Context
import io.javaoperatorsdk.operator.api.reconciler.Reconciler
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl

open class LlmProviderReconciler() : Reconciler<LlmProvider> {
    override fun reconcile(resource: LlmProvider, context: Context<LlmProvider>): UpdateControl<LlmProvider> {
        return when(resource.spec.type) {
            ProviderType.OLLAMA -> OllamaStrategy().reconcile(resource, context)
            ProviderType.GOOGLE_GEMINI, ProviderType.OPENAI -> UpdateControl.noUpdate()
        }
    }

    fun updateStatus(resource: LlmProvider, state: LlmProviderState, message: String): UpdateControl<LlmProvider> {
        if (resource.status == null) {
            resource.status = LlmProviderStatus()
        }
        if (resource.status.state != state || resource.status.message != message) {
            resource.status.state = state
            resource.status.message = message
            return UpdateControl.patchStatus(resource)
        }
        return UpdateControl.noUpdate()
    }
}