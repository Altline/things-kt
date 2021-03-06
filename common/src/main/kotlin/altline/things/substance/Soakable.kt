package altline.things.substance

import altline.things.measure.Volume
import io.nacular.measured.units.*

interface Soakable {
    val soakedSubstance: Substance
    var freshness: Double

    fun soak(substance: MutableSubstance)
    fun resoakWith(substance: MutableSubstance, amount: Measure<Volume>)
    fun dry(amount: Measure<Volume>): MutableSubstance
}