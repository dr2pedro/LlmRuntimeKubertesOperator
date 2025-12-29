package com.codeplaydata.crd.provider

import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Kind
import io.fabric8.kubernetes.model.annotation.Version

@Group("llmruntimeop.codeplaydata.com")
@Version("v1alpha1")
@Kind("LlmProvider")
class LlmProvider : CustomResource<LlmProviderSpec, LlmProviderStatus>(), Namespaced {
}