package com.test.sample

import com.test.sample.modals.formatNumber
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

	@Test
	fun addition_isCorrect() {
		assertEquals(4, 2 + 2)
	}

	@Test
	fun testAmountFormatting() {
		var amount1 = 9.7275
		assertEquals("9.7275", amount1.formatNumber())
		System.out.println("input: $amount1 output: ${amount1.formatNumber()}")

		amount1 = 9.7270
		assertEquals("9.727", amount1.formatNumber())
		System.out.println("input: 9.7270 output: ${amount1.formatNumber()}")

		amount1 = 9.7200
		assertEquals("9.72", amount1.formatNumber())
		System.out.println("input: 9.7200 output: ${amount1.formatNumber()}")

		amount1 = 9.7000
		assertEquals("9.70", amount1.formatNumber())
		System.out.println("input: 9.7000 output: ${amount1.formatNumber()}")

		amount1 = 9.0000
		assertEquals("9", amount1.formatNumber())
		System.out.println("input: 9.0000 output: ${amount1.formatNumber()}")

		amount1 = 9.000
		assertEquals("9", amount1.formatNumber())
		System.out.println("input: 9.000 output: ${amount1.formatNumber()}")

		amount1 = 9.00
		assertEquals("9", amount1.formatNumber())
		System.out.println("input: 9.00 output: ${amount1.formatNumber()}")

		amount1 = 9.0
		assertEquals("9", amount1.formatNumber())
		System.out.println("input: 9.0 output: ${amount1.formatNumber()}")

		amount1 = 9.1
		assertEquals("9.10", amount1.formatNumber())
		System.out.println("input: 9.1 output: ${amount1.formatNumber()}")

		amount1 = 9.99999
		assertEquals("9.9999", amount1.formatNumber())
		System.out.println("input: 9.99999 output: ${amount1.formatNumber()}")

		amount1 = 9.00009
		assertEquals("9.00", amount1.formatNumber())
		System.out.println("input: 9.00009 output: ${amount1.formatNumber()}")

		amount1 = 9.00001
		assertEquals("9.00", amount1.formatNumber())
		System.out.println("input: 9.00001 output: ${amount1.formatNumber()}")

		amount1 = 9.000009
		assertEquals("9.00", amount1.formatNumber())
		System.out.println("input: 9.000009 output: ${amount1.formatNumber()}")

		amount1 = 9.0000009
		assertEquals("9", amount1.formatNumber())
		System.out.println("input: 9.0000009 output: ${amount1.formatNumber()}")
	}
}
