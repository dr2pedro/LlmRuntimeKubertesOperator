package com.codeplaydata

import com.codeplaydata.controller.provider.LlmProviderReconciler
import io.javaoperatorsdk.operator.Operator
import org.slf4j.LoggerFactory

fun main() {
    val logger = LoggerFactory.getLogger("Main")
    val operator = Operator()

    operator.register(LlmProviderReconciler())

    //operator.register(LlmModelReconciler(client))
    
    logger.info("Starting LlmRuntime Operator")
    operator.start()

    Thread.currentThread().join()
}