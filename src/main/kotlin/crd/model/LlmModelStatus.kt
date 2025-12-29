package com.codeplaydata.crd.model

import com.codeplaydata.crd.provider.LlmProviderState
import io.fabric8.kubernetes.api.model.Condition

class LlmModelStatus {
    var state: LlmProviderState? = null
    var message: String? = null
    var conditions: List<Condition> = ArrayList()
}