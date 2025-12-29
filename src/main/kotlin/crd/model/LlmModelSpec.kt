package com.codeplaydata.crd.model

class LlmModelSpec {
    lateinit var model: String
    lateinit var provider: String
    var replicas: Int = 1
}