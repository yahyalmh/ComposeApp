package com.example.common.test

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.mockito.stubbing.OngoingStubbing

fun <T : Any> OngoingStubbing<Flow<T>>.thenEmitError(e: Throwable) {
    thenReturn(flow { throw e })
}

fun <T : Any> OngoingStubbing<Flow<T>>.thenEmitNothing() {
    thenReturn(flow {})
}