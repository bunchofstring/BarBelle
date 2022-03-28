package com.bunchofstring.experimental.barbelle

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView
import com.tinder.StateMachine
import java.util.*

class MotionInteractionDetector(private val observer: MotionInteractionObserver) {

    private val mTimer = Timer()
    private var mTimerTask = getTimerTask()
    private val stateMachine = StateMachine.create<State, Event, SideEffect> {
        initialState(State.Stopped)
        state<State.Stopped> {
            on<Event.OnInteractionStart> {
                transitionTo(State.InteractionStarted, SideEffect.DoNothing)
            }
        }
        state<State.InteractionStarted> {
            on<Event.OnInteractionEnd> {
                transitionTo(State.Stopped, SideEffect.DoEnd)
            }
            on<Event.OnMotion> {
                transitionTo(State.Scrolling, SideEffect.DoStart)
            }
        }
        state<State.Scrolling> {
            on<Event.OnInteractionEnd> {
                transitionTo(State.Decelerating, SideEffect.DoNothing)
            }
            on<Event.OnStopMotion> {
                transitionTo(State.Paused, SideEffect.DoNothing)
            }
        }
        state<State.Decelerating> {
            on<Event.OnStopMotion> {
                transitionTo(State.Stopped, SideEffect.DoEnd)
            }
        }
        state<State.Paused> {
            on<Event.OnMotion> {
                transitionTo(State.Scrolling, SideEffect.DoNothing)
            }
            on<Event.OnInteractionEnd> {
                transitionTo(State.Stopped, SideEffect.DoEnd)
            }
        }
        onTransition {
            val validTransition = it as? StateMachine.Transition.Valid ?: return@onTransition
            when (validTransition.sideEffect) {
                SideEffect.DoStart -> observer.onStart()
                SideEffect.DoEnd -> observer.onEnd()
                else -> {}
            }
        }
    }

    fun onMotionY(){
        stateMachine.transition(Event.OnMotion)
        mTimer.schedule(getHysteresisTimerTask(),100L)
    }

    private fun getHysteresisTimerTask(): TimerTask{
        synchronized(mTimer) {
            mTimerTask.cancel()
            mTimerTask = getTimerTask()
            return mTimerTask
        }
    }

    private fun getTimerTask(): TimerTask{
        return object: TimerTask() {
            override fun run() {
                stateMachine.transition(Event.OnStopMotion)
            }
        }
    }
    fun onInteractionStart(){
        stateMachine.transition(Event.OnInteractionStart)
    }
    fun onInteractionEnd(){
        stateMachine.transition(Event.OnInteractionEnd)
    }

    interface MotionInteractionObserver {
        fun onStart()
        fun onEnd()
    }
}

sealed class State {
    object InteractionStarted : State()
    object Scrolling : State()
    object Paused : State()
    object Decelerating : State()
    object Stopped : State()
}

sealed class Event {
    object OnMotion : Event()
    object OnStopMotion : Event()
    object OnInteractionStart : Event()
    object OnInteractionEnd : Event()
}

sealed class SideEffect {
    object DoStart : SideEffect()
    object DoEnd : SideEffect()
    object DoNothing : SideEffect()
}
