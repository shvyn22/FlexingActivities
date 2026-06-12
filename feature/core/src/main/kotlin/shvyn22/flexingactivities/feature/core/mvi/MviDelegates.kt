package shvyn22.flexingactivities.feature.core.mvi

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class MviStateDelegateImpl<TState : MviState>(
    initial: TState,
) : MviStateDelegate<TState> {
    private val _state = MutableStateFlow(initial)
    override val state = _state.asStateFlow()

    override fun reduceState(vararg reducers: MviStateReducer<TState>) {
        _state.update { current ->
            reducers.fold(current) { s, r -> r.reduce(s) }
        }
    }
}

class MviEventDelegateImpl<TEvent : MviEvent> : MviEventDelegate<TEvent> {
    private val _event = Channel<TEvent>(Channel.CONFLATED)
    override val event = _event.receiveAsFlow()

    override fun sendEvent(event: TEvent) {
        val result = _event.trySend(event)
        if (result.isFailure) result.exceptionOrNull()?.printStackTrace()
    }
}

class MviDelegateImpl<TState : MviState, TIntent : MviIntent, TEvent : MviEvent>(
    initial: TState,
    private val stateDelegate: MviStateDelegate<TState> = MviStateDelegateImpl(initial),
    private val eventDelegate: MviEventDelegate<TEvent> = MviEventDelegateImpl(),
) : MviDelegate<TState, TIntent, TEvent>,
    MviStateDelegate<TState> by stateDelegate,
    MviEventDelegate<TEvent> by eventDelegate {

    override fun handleIntent(intent: TIntent) {}
}
