package com.codeplaydata.crd.provider

import com.codeplaydata.crd.common.ProviderType

class LlmProviderSpec {
    lateinit var type: ProviderType
    var options: Map<String, String>? = null
}