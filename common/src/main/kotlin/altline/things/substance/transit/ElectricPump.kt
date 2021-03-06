package altline.things.substance.transit

import altline.things.electricity.BasicElectricalDevice
import altline.things.measure.Power
import altline.things.measure.Volume
import altline.things.measure.VolumetricFlow
import altline.things.measure.divSameUnit
import io.nacular.measured.units.*
import kotlin.random.Random

class ElectricPump(
    power: Measure<Power>,
    maxFlowRate: Measure<VolumetricFlow>,
    inputCount: Int = 1,
    outputCount: Int = 1
) : BasicElectricalDevice(power) {

    private val pipe = Valve(maxFlowRate, inputCount, outputCount)

    val substanceInputs = pipe.inputs
    val substanceOutputs = pipe.outputs

    var powerSetting: Double
        get() = pipe.realFlowRate.divSameUnit(pipe.maxFlowRate)
        set(value) {
            require(value in 0.0..1.0)
            pipe.open(value * pipe.maxFlowRate)
        }

    private fun pump(amount: Measure<Volume>, timeFrame: Measure<Time>) {
        val chunk = pipe.pullFlow(amount, timeFrame, Random.nextLong())
        if (chunk != null) {
            pipe.pushFlow(chunk, timeFrame, Random.nextLong())
        }
    }

    override fun operate() {
        val requiredEnergy = (power * tickPeriod) * powerSetting
        val availableEnergy = pullEnergy(requiredEnergy, tickPeriod)?.amount
        if (availableEnergy != null) {
            val energyRatio = availableEnergy / requiredEnergy
            val pumpAmount = (pipe.realFlowRate * tickPeriod) * energyRatio
            pump(pumpAmount, tickPeriod)
        }
    }
}
