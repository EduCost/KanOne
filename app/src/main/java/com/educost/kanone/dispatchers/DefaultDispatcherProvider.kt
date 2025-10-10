package com.educost.kanone.dispatchers

import kotlinx.coroutines.Dispatchers

class DefaultDispatcherProvider(
) : DispatcherProvider {
    override val io = Dispatchers.IO
    override val main = Dispatchers.Main
    override val default = Dispatchers.Default
    override val unconfined = Dispatchers.Unconfined
}