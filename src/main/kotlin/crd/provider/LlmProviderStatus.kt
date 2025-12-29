package com.codeplaydata.crd.provider

import io.fabric8.kubernetes.api.model.Condition

class LlmProviderStatus {
    var state: LlmProviderState? = null
    var message: String? = null
    var conditions: List<Condition> = ArrayList()
}