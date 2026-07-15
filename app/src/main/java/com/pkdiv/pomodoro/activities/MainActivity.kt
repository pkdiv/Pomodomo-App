package com.pkdiv.pomodoro.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebChromeClient.FileChooserParams
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.pkdiv.pomodoro.R
import com.pkdiv.pomodoro.databinding.ActivityMainBinding
import com.pkdiv.pomodoro.network.NetworkMonitor
import com.pkdiv.pomodoro.notifications.NotificationHelper
import com.pkdiv.pomodoro.permissions.PermissionHelper
import com.pkdiv.pomodoro.utils.Constants
import com.pkdiv.pomodoro.utils.performHaptic
import com.pkdiv.pomodoro.webview.PomoWebChromeClient
import com.pkdiv.pomodoro.webview.PomoWebViewClient
import com.pkdiv.pomodoro.webview.configurePomodoroWebView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var webChromeClient: PomoWebChromeClient
    private lateinit var networkMonitor: NetworkMonitor
    private lateinit var permissionHelper: PermissionHelper

    private lateinit var fileChooserLauncher: ActivityResultLauncher<Intent>
    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<String>

    private var isOffline = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash theme is set in the manifest; switch to the full theme here so
        // the brand-colored window shows instantly, then the WebView paints over it.
        setTheme(R.style.Theme_Pomodoro)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        NotificationHelper(this).ensureChannel()
        permissionHelper = PermissionHelper(this)

        registerActivityResults()
        setupWebView()
        setupSwipeRefresh()
        setupOfflineUi()
        setupBackPressed()
        setupNetworkMonitor()

        handleIntent(intent ?: intent)
        if (savedInstanceState == null) {
            binding.webView.loadUrl(Constants.TARGET_URL)
        }
    }

    private fun enableEdgeToEdge() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Apply insets to the content root so the WebView sits under the system bars.
        ViewCompat.setOnApplyWindowInsetsListener(binding.rootLayout) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.updatePadding(left = bars.left, right = bars.right, top = bars.top, bottom = bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupWebView() {
        webChromeClient = PomoWebChromeClient(
            onShowFileChooser = { params -> launchFileChooser(params) }
        )
        binding.webView.configurePomodoroWebView()
        binding.webView.webViewClient = PomoWebViewClient(
            onOffline = { showOffline() },
            onOnline = { /* page started; caller decides recovery */ }
        )
        binding.webView.webChromeClient = webChromeClient
        binding.webView.setDownloadListener { url, _, _, _, _ ->
            // Defer downloads to the system; keep the wrapper minimal.
            val uri = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            if (intent.resolveActivity(packageManager) != null) startActivity(intent)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            binding.root.performHaptic()
            binding.webView.reload()
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun setupOfflineUi() {
        binding.retryButton.setOnClickListener {
            it.performHaptic()
            if (networkMonitor.isCurrentlyConnected()) {
                hideOffline()
                binding.webView.reload()
            } else {
                // Still offline — keep the message up.
            }
        }
    }

    private fun setupBackPressed() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                binding.root.performHaptic()
                if (binding.webView.canGoBack()) {
                    binding.webView.goBack()
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    private fun setupNetworkMonitor() {
        networkMonitor = NetworkMonitor(this)
        networkMonitor.onChange = { connected ->
            runOnUiThread {
                if (connected && isOffline) {
                    hideOffline()
                    binding.webView.reload()
                } else if (!connected && binding.webView.url != null) {
                    // Only surface offline if we actually lost the page.
                    showOffline()
                }
            }
        }
    }

    private fun registerActivityResults() {
        fileChooserLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val data = result.data
            val results = WebChromeClientFileResult.toUris(data)
            webChromeClient.deliverFileResult(results)
        }

        notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { /* result handled by system; notifications post when enabled */ }
    }

    private fun launchFileChooser(params: FileChooserParams) {
        val intent = params.createIntent()
        runCatching { fileChooserLauncher.launch(intent) }
            .onFailure { webChromeClient.deliverFileResult(null) }
    }

    private fun showOffline() {
        isOffline = true
        binding.offlineLayout.visibility = android.view.View.VISIBLE
        binding.webView.visibility = android.view.View.GONE
    }

    private fun hideOffline() {
        isOffline = false
        binding.offlineLayout.visibility = android.view.View.GONE
        binding.webView.visibility = android.view.View.VISIBLE
    }

    private fun handleIntent(intent: Intent) {
        val data: Uri? = intent.data
        if (data != null && data.host?.equals(Constants.ALLOWED_HOST, ignoreCase = true) == true &&
            data.scheme.equals(Constants.ALLOWED_SCHEME, ignoreCase = true)
        ) {
            binding.webView.loadUrl(data.toString())
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        networkMonitor.register()
        askNotificationPermissionIfNeeded()
    }

    override fun onStop() {
        super.onStop()
        networkMonitor.unregister()
    }

    override fun onResume() {
        super.onResume()
        binding.webView.onResume()
    }

    override fun onPause() {
        binding.webView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.webView.destroy()
        super.onDestroy()
    }

    private fun askNotificationPermissionIfNeeded() {
        permissionHelper.requestNotificationPermission(notificationPermissionLauncher)
    }

    // Helper to parse file-chooser result Uris.
    private object WebChromeClientFileResult {
        fun toUris(data: Intent?): Array<Uri>? {
            if (data == null) return null
            val clip = data.clipData
            if (clip != null) {
                return Array(clip.itemCount) { i -> clip.getItemAt(i).uri }
            }
            val uri = data.data
            return if (uri != null) arrayOf(uri) else null
        }
    }
}
