package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.PrintJobEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
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
    assertEquals("High 5 Packaging", appName)
  }

  @Test
  fun `test print job entity creation and status`() {
    val job = PrintJobEntity(
      jobNumber = "JOB-5001",
      clientName = "Test Client",
      quantity = 2500,
      isCompleted = false
    )
    assertEquals("JOB-5001", job.jobNumber)
    assertEquals(2500, job.quantity)
    assertFalse(job.isCompleted)

    val completedJob = job.copy(isCompleted = true, completedDate = System.currentTimeMillis())
    assertTrue(completedJob.isCompleted)
  }

  @Test
  fun `test repeat vs new die and emboss options`() {
    // Repeat die: already available, no arrival date needed
    val repeatJob = PrintJobEntity(
      jobNumber = "JOB-REPEAT-1",
      dieType = "REPEAT",
      embossType = "REPEAT"
    )
    assertEquals("REPEAT", repeatJob.dieType)
    assertEquals("REPEAT", repeatJob.embossType)
    assertNull(repeatJob.dieArrivedDate)
    assertNull(repeatJob.embossArrivedDate)

    // New die: has arrival date and arrival toggle
    val newDieJob = PrintJobEntity(
      jobNumber = "JOB-NEW-1",
      dieType = "NEW",
      isDieArrived = true,
      dieArrivedDate = 1700000000000L,
      embossType = "NEW",
      isEmbossArrived = false
    )
    assertEquals("NEW", newDieJob.dieType)
    assertTrue(newDieJob.isDieArrived)
    assertEquals(1700000000000L, newDieJob.dieArrivedDate)
    assertEquals("NEW", newDieJob.embossType)
    assertFalse(newDieJob.isEmbossArrived)
  }
}
