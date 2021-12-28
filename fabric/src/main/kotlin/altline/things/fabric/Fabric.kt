package altline.things.fabric

import altline.things.common.Body
import altline.things.substances.Soakable
import altline.things.measure.*
import altline.things.substances.MutableSubstance
import altline.things.substances.Substance
import io.nacular.measured.units.Measure

abstract class Fabric(
    override val volume: Measure<Volume>
) : Body, Soakable {

    override var freshness: Double = 0.0

    private val _stainSubstance = MutableSubstance()
    override val stainSubstance = _stainSubstance as Substance

    private val _soakedSubstance = MutableSubstance()
    override val soakedSubstance = _soakedSubstance as Substance

    val totalVolume: Measure<Volume>
        get() = volume + stainSubstance.totalAmount + soakedSubstance.totalAmount

    override fun stain(substance: Substance) {
        _stainSubstance.add(substance)
    }

    override fun clearStain(amount: Measure<Volume>): MutableSubstance {
        return _stainSubstance.separate(amount)
    }

    override fun soak(substance: MutableSubstance) {
        val soakableAmount = volume - soakedSubstance.totalAmount
        val soakableSubstance = substance.separate(soakableAmount)
        _soakedSubstance.add(soakableSubstance)
    }

    override fun dry(amount: Measure<Volume>): MutableSubstance {
        return _soakedSubstance.separate(amount)
    }
}