package altline.things.electricity

import altline.things.electricity.transit.ElectricalDrain
import altline.things.electricity.transit.ElectricalDrainPort
import altline.things.electricity.transit.ElectricalSource
import altline.things.measure.Energy
import altline.things.measure.Power
import altline.things.util.CoroutineManager
import io.nacular.measured.units.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

abstract class BasicElectricalDevice(
    final override val power: Measure<Power>
) : ElectricalDevice {

    private val inletDrain = object : ElectricalDrain {
        override val maxInputFlowRate = power
        override val realInputFlowRate = power
        override val inputs = arrayOf(ElectricalDrainPort(this))
        override val inputCount = inputs.size

        override fun pushFlow(
            flowable: MutableElectricalEnergy,
            timeFrame: Measure<Time>,
            flowId: Long
        ): Measure<Energy> {
            return flowable.amount
        }
    }

    final override val powerInlet = inletDrain.inputs[0]
    protected val powerSource: ElectricalSource? = powerInlet.connectedPort?.owner

    val running: Boolean
        get() = coroutineManager.active

    private val coroutineManager = CoroutineManager(CoroutineScope(Dispatchers.Default)) {
        operate()
    }

    protected abstract fun operate()

    fun start() {
        coroutineManager.active = true
    }

    fun stop() {
        coroutineManager.active = false
    }
}