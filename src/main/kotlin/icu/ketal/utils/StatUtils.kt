package icu.ketal.utils

import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.sqrt

object StatUtils {


    /**
     * Return the value of [p] in the inverse cdf for the normal distribution
     * with mean [mean] and standard deviation [std].
     */
    fun inverseNormalCumulative(p: Double, mean: Double, std: Double): Double {
        return -sqrt(2.0) * erfcInv(2.0 * p) * std + mean
    }

    /**
     * Returns the inverse of the complementary error function
     */
    fun erfcInv(p: Double): Double {
        var j = 0
        if (p >= 2) {
            return -100.0
        }
        if (p <= 0) {
            return 100.0
        }
        val pp = if (p < 1) p else 2 - p
        val t = sqrt(-2 * ln(pp / 2))
        var x = -0.70711 * ((2.30753 + t * 0.27061) / (1 + t * (0.99229 + t * 0.04481)) - t)
        while (j < 2) {
            val err = erfc(x) - pp
            x += err / (1.12837916709551257 * Math.exp(-x * x) - x * err)
            j++
        }
        return if (p < 1) x else -x
    }

    /**
     * Returns the complmentary error function erfc(x)
     */
    fun erfc(x: Double): Double {
        return 1 - erf(x)
    }

    /**
     * Returns the error function erf(x)
     */
    fun erf(x: Double): Double {
        val cof = arrayOf(
            -1.3026537197817094,
            0.6419697923564902,
            1.9476473204185836e-2,
            -0.00956151478680863,
            -9.46595344482036e-4,
            3.66839497852761e-4,
            4.2523324806907e-5,
            -2.0278578112534e-5,
            -1.624290004647e-6,
            1.303655835580e-6,
            1.5626441722e-8,
            -8.5238095915e-8,
            6.529054439e-9,
            5.059343495e-9,
            -9.91364156e-10,
            -2.27365122e-10,
            9.6467911e-11,
            2.394038e-12,
            -6.886027e-12,
            8.94487e-13,
            3.13092e-13,
            -1.12708e-13,
            3.81e-16,
            7.106e-15,
            -1.523e-15,
            -9.4e-17,
            1.21e-16,
            -2.8e-17
        )
        var j = cof.size - 1
        val isneg = x < 0
        val x1 = abs(x)
        var d = 0.0
        var dd = 0.0

        val t = 2 / (2 + x1)
        val ty = 4 * t - 2

        while (j > 0) {
            val tmp = d
            d = ty * d - dd + cof[j]
            dd = tmp
            j--
        }

        val res = t * exp(-x1 * x1 + 0.5 * (cof[0] + ty * d) - dd)
        return if (isneg) res - 1 else 1 - res
    }
}
