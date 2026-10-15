package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.PrintJobEntity
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
    assertEquals("PrintJob Tracker", appName)
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
}
