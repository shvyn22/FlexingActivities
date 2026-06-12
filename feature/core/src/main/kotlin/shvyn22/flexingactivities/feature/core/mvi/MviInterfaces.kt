package shvyn22.flexingactivities.feature.core.mvi

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MviIntent
interface MviEvent
interface MviState

fun interface MviStateReducer<TState : MviState> {
    fun reduce(state: TState): TState
}

interface MviStateDelegate<TState : MviState> {
    val state: StateFlow<TState>
    fun reduceState(vararg reducers: MviStateReducer<TState>)
}

interface MviEventDelegate<TEvent : MviEvent> {
    val event: Flow<TEvent>
    fun sendEvent(event: TEvent)
}

interface MviIntentDelegate<TIntent : MviIntent> {
    fun handleIntent(intent: TIntent)
}

interface MviDelegate<TState : MviState, TIntent : MviIntent, TEvent : MviEvent>
    : MviStateDelegate<TState>, MviIntentDelegate<TIntent>, MviEventDelegate<TEvent>
