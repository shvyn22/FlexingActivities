package shvyn22.flexingactivities.feature.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MviStateDelegateImpl<TState : MviState>(
    initial: TState,
) : MviStateDelegate<TState> {
    private val mutex = Mutex()
    private val _state = MutableStateFlow(initial)
    override val state = _state.asStateFlow()

    override suspend fun reduceState(vararg reducers: MviStateReducer<TState>) {
        mutex.withLock {
            _state.update { current -> reducers.fold(current) { s, r -> r.reduce(s) } }
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

fun <TState, TViewModel> TViewModel.updateState(
    vararg reducers: MviStateReducer<TState>,
) where TState : MviState, TViewModel : ViewModel, TViewModel : MviStateDelegate<TState> {
    viewModelScope.launch { reduceState(*reducers) }
}
