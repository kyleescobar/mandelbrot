package dev.kyleescobar.mandelbrot

import java.math.BigDecimal

operator fun BigDecimal.plus(value: BigDecimal): BigDecimal = this.add(value)
operator fun BigDecimal.minus(value: BigDecimal): BigDecimal = this.subtract(value)
operator fun BigDecimal.times(value: BigDecimal) = this.multiply(value)
operator fun BigDecimal.div(value: BigDecimal) = this.divide(value)