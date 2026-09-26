package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.HsnDatabase
import com.example.ui.components.Formatters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("BillNova", appName)
  }

  @Test
  fun `currency and number to words formatting`() {
    val formatted = Formatters.formatCurrency(18500.0, showDecimals = false)
    assertTrue(formatted.contains("18,500"))
    val words = Formatters.numberToWordsIndian(18500.0)
    assertTrue(words.contains("Eighteen Thousand Five Hundred"))
  }

  @Test
  fun `test hsn rate finder database search`() {
    val results7219 = HsnDatabase.search("7219")
    assertFalse(results7219.isEmpty())
    assertTrue(results7219.any { it.code.startsWith("7219") })

    val resultsLaser = HsnDatabase.search("Laser")
    assertFalse(resultsLaser.isEmpty())
    assertTrue(resultsLaser.any { it.code == "9988" })
  }
}
