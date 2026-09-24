package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.SearchEngine
import com.example.security.WebShieldEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Azi Browser", appName)
  }

  @Test
  fun `web shield flags malicious domain`() {
    val threat = WebShieldEngine.evaluateUrl("https://testsafebrowsing.appspot.com/malware.html")
    assertNotNull(threat)
  }

  @Test
  fun `web shield passes clean search engine`() {
    val threat = WebShieldEngine.evaluateUrl("https://duckduckgo.com")
    assertNull(threat)
  }

  @Test
  fun `sanitize url accurately resolves domains and queries`() {
    val domainUrl = WebShieldEngine.sanitizeUrl("google.com")
    assertEquals("https://google.com", domainUrl)

    val explicitUrl = WebShieldEngine.sanitizeUrl("https://en.wikipedia.org")
    assertEquals("https://en.wikipedia.org", explicitUrl)

    val braveQuery = WebShieldEngine.sanitizeUrl("news today", searchEngine = SearchEngine.BRAVE)
    assertEquals("https://search.brave.com/search?q=news%20today", braveQuery)

    val googleQuery = WebShieldEngine.sanitizeUrl("news today", searchEngine = SearchEngine.GOOGLE)
    assertEquals("https://www.google.com/search?q=news%20today", googleQuery)
  }

  @Test
  fun `tracker and ad blocking detects known tracking hosts`() {
    val isDoubleclickTracker = WebShieldEngine.isTrackerOrAd("https://ad.doubleclick.net/pixel.gif")
    assertEquals(true, isDoubleclickTracker)

    val isNormalSite = WebShieldEngine.isTrackerOrAd("https://wikipedia.org/logo.png")
    assertEquals(false, isNormalSite)
  }
}
