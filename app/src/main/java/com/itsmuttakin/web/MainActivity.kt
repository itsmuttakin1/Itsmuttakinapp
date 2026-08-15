package com.itsmuttakin.web

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.Canvas
import android.webkit.WebChromeClient
import android.webkit.DownloadListener
import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import android.webkit.CookieManager
import android.webkit.URLUtil
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableFloatStateOf
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.itsmuttakin.web.ui.theme.ItsMuttakinTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.database.Cursor
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.media3.common.MediaItem
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.ui.PlayerView
import java.net.URLDecoder
import androidx.media3.common.MimeTypes
import android.webkit.JavascriptInterface
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import android.webkit.MimeTypeMap
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Movie

data class AppStrings(
    val selectMode: String,
    val mobile: String,
    val tv: String,
    val updateTitle: String,
    val updateText: String,
    val updateNow: String,
    val updateLater: String,
    val mobileMode: String,
    val errorLoading: String,
    val downloadStarted: String
)

data class DownloadInfo(
    val id: Long,
    val name: String,
    val progress: Float,
    val isFinished: Boolean = false
)

val translations = mapOf(
    "English" to AppStrings("Select Mode", "Mobile", "Android TV", "Update Available", "A new version of Its Muttakin Studio is available. Please update for better experience.", "Update Now", "Later", "Mobile Mode", "Error loading page", "Download Started"),
    "বাংলা" to AppStrings("মোড নির্বাচন করুন", "মোবাইল", "অ্যান্ড্রয়েড টিভি", "আপডেট উপলব্ধ", "ইটস মুত্তাকিন স্টুডিওর একটি নতুন সংস্করণ উপলব্ধ। আরও ভালো অভিজ্ঞতার জন্য আপডেট করুন।", "এখনই আপডেট করুন", "পরে", "মোবাইল মোড", "পৃষ্ঠা লোড করতে ত্রুটি", "ডাউনলোড শুরু হয়েছে"),
    "Indonesian" to AppStrings("Pilih Mode", "Seluler", "Android TV", "Pembaruan Tersedia", "Versi baru Its Muttakin Studio tersedia. Silakan perbarui untuk pengalaman yang lebih baik.", "Perbarui Sekarang", "Nanti", "Mode Seluler", "Kesalahan memuat halaman", "Unduhan Dimulai"),
    "Português" to AppStrings("Selecionar Modo", "Móvel", "Android TV", "Atualização Disponível", "Uma nova versão do Its Muttakin Studio está disponível. Atualize para uma melhor experiência.", "Atualizar Agora", "Depois", "Modo Móvel", "Erro ao carregar a página", "Download Iniciado"),
    "Español" to AppStrings("Seleccionar Modo", "Móvil", "Android TV", "Actualización Disponible", "Una nueva versión de Its Muttakin Studio está disponible. Atualice para una mejor experiencia.", "Actualizar Ahora", "Luego", "Modo Móvel", "Error al cargar la página", "Descarga Iniciada"),
    "Français" to AppStrings("Sélectionner le Mode", "Mobile", "Android TV", "Mise à Jour Disponible", "Une nouvelle version d'Its Muttakin Studio est disponible. Veuillez mettre à jour pour une meilleure expérience.", "Mettre à jour maintenant", "Plus tard", "Mode Mobile", "Erreur de chargement", "Téléchargement Commencé")
)

val LocalStrings = staticCompositionLocalOf { translations["English"]!! }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Primary Firebase
        val primaryRemoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 0
        }
        primaryRemoteConfig.setConfigSettingsAsync(configSettings)
        primaryRemoteConfig.setDefaultsAsync(mapOf(
            "notice_text" to "Welcome to M BOX!",
            "latest_version_code" to 1L,
            "update_url" to "https://itsmuttakin.pages.dev/",
            "update_required" to false
        ))

        // Initialize Firebase for Stats
        val secondaryOptions = FirebaseOptions.Builder()
            .setApiKey("AIzaSyCkJzHEPgfS5wc8HjJf6wsH9PhjV8O0Zdw")
            .setApplicationId("1:577800510502:web:a1c6a30589521644331ae7")
            .setDatabaseUrl("https://mrghost-53f84-default-rtdb.asia-southeast1.firebasedatabase.app")
            .setProjectId("mrghost-53f84")
            .build()
        
        try {
            val statsApp = FirebaseApp.initializeApp(this, secondaryOptions, "stats")
            val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            if (!prefs.getBoolean("is_user_counted", false)) {
                val db = FirebaseDatabase.getInstance(statsApp).reference
                db.child("stats/total_users").runTransaction(object : com.google.firebase.database.Transaction.Handler {
                    override fun doTransaction(data: com.google.firebase.database.MutableData): com.google.firebase.database.Transaction.Result {
                        val current = data.getValue(Long::class.java) ?: 0L
                        data.value = current + 1
                        return com.google.firebase.database.Transaction.success(data)
                    }
                    override fun onComplete(error: com.google.firebase.database.DatabaseError?, committed: Boolean, snapshot: com.google.firebase.database.DataSnapshot?) {
                        if (committed) prefs.edit().putBoolean("is_user_counted", true).apply()
                    }
                })
            }
        } catch (e: Exception) {}

        setContent {
            // Live state for remote config
            val remoteConfigState = remember { mutableStateOf(0) }
            
            // Re-fetch whenever app comes to foreground
            val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
            DisposableEffect(lifecycleOwner) {
                val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                    if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener {
                            remoteConfigState.value += 1 // Trigger UI refresh
                        }
                    }
                }
                lifecycleOwner.lifecycle.addObserver(observer)
                onDispose {
                    lifecycleOwner.lifecycle.removeObserver(observer)
                }
            }

            val stringsState = remember { mutableStateOf("English") }
            val strings = translations[stringsState.value] ?: translations["English"]!!
            
            CompositionLocalProvider(LocalStrings provides strings) {
                ItsMuttakinTheme {
                    AppNavigation(stringsState.value, remoteConfigState.value) { stringsState.value = it }
                }
            }
        }
    }
}

@Composable
fun AppNavigation(currentLang: String, refreshTrigger: Int, onLangChange: (String) -> Unit) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val strings = LocalStrings.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showUpdateDialog by remember { mutableStateOf(false) }
    var updateUrl by remember { mutableStateOf("") }
    var updateDownloadId by remember { mutableLongStateOf(-1L) }
    var updateProgress by remember { mutableFloatStateOf(0f) }
    var isUpdateDownloading by remember { mutableStateOf(false) }
    var isUpdateRequired by remember { mutableStateOf(false) }

    // Receiver to handle all downloads
    val activeDownloads = remember<SnapshotStateList<DownloadInfo>> { mutableStateListOf() }
    var showDownloadSheet by remember { mutableStateOf(false) }

    // Receiver to handle all downloads
    val downloadReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                if (id == updateDownloadId) {
                    // Handle Update APK install
                    installApk(context, id)
                    isUpdateDownloading = false
                    showUpdateDialog = false
                }
                // Mark as finished instead of removing
                val index = activeDownloads.indexOfFirst { it.id == id }
                if (index != -1) {
                    activeDownloads[index] = activeDownloads[index].copy(isFinished = true, progress = 1f)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        context.registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED)
        onDispose {
            context.unregisterReceiver(downloadReceiver)
        }
    }

    // Periodic progress tracker for all active downloads
    LaunchedEffect(isUpdateDownloading, activeDownloads.size) {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        while (isUpdateDownloading || activeDownloads.isNotEmpty()) {
            // Update app update progress
            if (isUpdateDownloading) {
                val progress = getDownloadProgress(dm, updateDownloadId)
                if (progress >= 0) updateProgress = progress
            }

            // Update general downloads progress
            activeDownloads.forEachIndexed { index, info ->
                if (!info.isFinished) {
                    val progress = getDownloadProgress(dm, info.id)
                    if (progress >= 0) {
                        activeDownloads[index] = info.copy(progress = progress)
                    }
                }
            }
            
            delay(1000)
        }
    }
    
    // Check for Updates via Remote Config
    LaunchedEffect(refreshTrigger) {
        val remoteConfig = Firebase.remoteConfig
        val latestVersion = remoteConfig.getLong("latest_version_code")
        val updateRequired = remoteConfig.getBoolean("update_required")
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val currentVersion = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            pInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            pInfo.versionCode.toLong()
        }

        if (latestVersion > currentVersion && !isUpdateDownloading) {
            updateUrl = remoteConfig.getString("update_url")
            isUpdateRequired = updateRequired
            showUpdateDialog = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(navController = navController, startDestination = "selection") {
            composable("selection") {
                SelectionScreen(
                    currentLang = currentLang,
                    refreshTrigger = refreshTrigger,
                    onLangChange = onLangChange,
                    onStartClick = { navController.navigate("webview/mobile") }
                )
            }
            composable("webview/{mode}") { backStackEntry ->
                val mode = backStackEntry.arguments?.getString("mode") ?: "mobile"
                WebViewScreen(
                    mode = mode,
                    onBackToHome = { navController.popBackStack() },
                    onDownloadStart = { id, name -> 
                        activeDownloads.add(DownloadInfo(id, name, 0f))
                        scope.launch {
                            val result = snackbarHostState.showSnackbar(
                                message = "Downloading: $name",
                                actionLabel = "VIEW",
                                duration = SnackbarDuration.Short
                            )
                            if (result == SnackbarResult.ActionPerformed) {
                                showDownloadSheet = true
                            }
                        }
                    },
                    onShowDownloads = { showDownloadSheet = true },
                    onPlayVideo = { videoUrl ->
                        val encodedUrl = Uri.encode(videoUrl)
                        navController.navigate("player/$encodedUrl")
                    }
                )
            }
            composable("player/{url}") { backStackEntry ->
                val videoUrl = backStackEntry.arguments?.getString("url")?.let { URLDecoder.decode(it, "UTF-8") } ?: ""
                VideoPlayerScreen(url = videoUrl, onBack = { navController.popBackStack() })
            }
        }
        
        if (showUpdateDialog) {
            PremiumUpdateDialog(
                url = updateUrl,
                progress = updateProgress,
                isDownloading = isUpdateDownloading,
                isForceUpdate = isUpdateRequired,
                onStartDownload = {
                    val request = DownloadManager.Request(Uri.parse(updateUrl)).apply {
                        setMimeType("application/vnd.android.package-archive")
                        setTitle("Updating M BOX Studio")
                        setDescription("Downloading new version...")
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "update.apk")
                    }
                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    updateDownloadId = dm.enqueue(request)
                    isUpdateDownloading = true
                },
                onDismiss = { 
                    if (!isUpdateDownloading && !isUpdateRequired) showUpdateDialog = false 
                }
            )
        }

        if (showDownloadSheet) {
            DownloadStatusDialog(
                downloads = activeDownloads,
                onOpen = { id -> openDownloadedFile(context, id) },
                onCancel = { id -> 
                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    dm.remove(id) // এটি ডাউনলোড বন্ধ করবে এবং ফাইল ডিলিট করবে
                    activeDownloads.removeIf { it.id == id }
                },
                onDelete = { id ->
                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    dm.remove(id) // এটি সেভ করা ফাইলটি ফোন থেকে ডিলিট করে দেবে
                    activeDownloads.removeIf { it.id == id }
                },
                onDismiss = { showDownloadSheet = false }
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

@Composable
fun PremiumUpdateDialog(url: String, progress: Float, isDownloading: Boolean, isForceUpdate: Boolean, onStartDownload: () -> Unit, onDismiss: () -> Unit) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    
    AlertDialog(
        onDismissRequest = { if (!isForceUpdate) onDismiss() },
        modifier = Modifier
            .shadow(24.dp, RoundedCornerShape(28.dp), spotColor = Color.Cyan)
            .border(1.dp, Color.Cyan.copy(alpha = 0.3f), RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        containerColor = Color(0xFF0F2027),
        title = {
            Text(
                text = if (isDownloading) "Downloading Update..." else strings.updateTitle,
                color = Color.Cyan,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                Text(
                    text = if (isDownloading) "Please wait while we download the latest version." else strings.updateText,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 15.sp
                )
                if (isDownloading) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = Color.Cyan,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        color = Color.Cyan,
                        modifier = Modifier.align(Alignment.End),
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            if (!isDownloading) {
                Button(
                    onClick = onStartDownload,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(strings.updateNow, color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            if (!isDownloading && !isForceUpdate) {
                TextButton(onClick = { onDismiss() }) {
                    Text(strings.updateLater, color = Color.White.copy(alpha = 0.6f))
                }
            }
        }
    )
}

@Composable
fun SelectionScreen(currentLang: String, refreshTrigger: Int, onLangChange: (String) -> Unit, onStartClick: () -> Unit) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    var progress by remember { mutableFloatStateOf(0f) }
    
    // Auto navigate after loading animation
    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(50)
            progress += 0.02f
        }
        delay(500)
        onStartClick()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0A0A0A))) {
        // Force Portrait
        LaunchedEffect(Unit) {
            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // M BOX Logo
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .background(Color.Black)
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mbox_logo),
                    contentDescription = "M BOX Logo",
                    modifier = Modifier.fillMaxSize().padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "LOADING..",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 12.sp,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(200.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = Color(0xFF7A8AFF),
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(80.dp))

            // Notice Board matching the screenshot
            PremiumNoticeBoard(refreshTrigger)
        }
    }
}

@Composable
fun PremiumNoticeBoard(refreshTrigger: Int) {
    var noticeText by remember { mutableStateOf(Firebase.remoteConfig.getString("notice_text")) }
    
    LaunchedEffect(refreshTrigger) {
        noticeText = Firebase.remoteConfig.getString("notice_text")
    }

    Surface(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(24.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
        modifier = Modifier.fillMaxWidth().height(280.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("NOTICE BOARD", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row {
                    Text("Term", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp))
                    Text("Privacy", color = Color.White.copy(alpha = 0.5f), fontSize = 11.sp)
                }
            }
            
            Text(
                "© 2024 M BOX. All rights reserved.",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 10.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.3f))
                    .padding(12.dp)
            ) {
                val scrollState = rememberScrollState()
                Text(
                    text = noticeText,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    modifier = Modifier.verticalScroll(scrollState)
                )
            }
        }
    }
}

@Composable
fun AnimatedStudioText() {
    val infiniteTransition = rememberInfiniteTransition(label = "colorTransition")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = Color.Red,
        targetValue = Color(0xFFFF5252), // Bright Red/Pink
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )

    Text(
        text = "M BOX",
        color = Color.White,
        fontSize = 28.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        letterSpacing = 2.sp,
        style = androidx.compose.ui.text.TextStyle(
            shadow = androidx.compose.ui.graphics.Shadow(
                color = animatedColor,
                offset = androidx.compose.ui.geometry.Offset(0f, 0f),
                blurRadius = 25f
            )
        ),
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    )
}

@Composable
fun ModeCard(title: String, icon: ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .height(220.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .border(2.dp, Color.Cyan, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(60.dp),
                    tint = Color.Cyan
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewScreen(
    mode: String, 
    onBackToHome: () -> Unit,
    onDownloadStart: (Long, String) -> Unit,
    onShowDownloads: () -> Unit,
    onPlayVideo: (String) -> Unit
) {
    val context = LocalContext.current
    val strings = LocalStrings.current
    val url = "https://itsmuttakin.pages.dev/"
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableFloatStateOf(0f) }
    var webView: WebView? by remember { mutableStateOf(null) }
    
    // Full screen support
    var customView by remember { mutableStateOf<View?>(null) }
    val window = (context as? Activity)?.window
    val insetsController = window?.let { WindowCompat.getInsetsController(it, it.decorView) }

    LaunchedEffect(customView) {
        if (customView != null) {
            insetsController?.apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            insetsController?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    // Fixed to mobile orientation or unspecified for general web
    LaunchedEffect(Unit) {
        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    val backCallback = remember(webView, customView) {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (customView != null) {
                    webView?.webChromeClient?.onHideCustomView()
                } else if (webView?.canGoBack() == true) {
                    webView?.goBack()
                } else {
                    onBackToHome()
                }
            }
        }
    }

    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    DisposableEffect(backDispatcher, backCallback) {
        backDispatcher?.addCallback(backCallback)
        onDispose {
            backCallback.remove()
        }
    }

    Scaffold(
        topBar = {
            if (customView == null) {
                TopAppBar(
                    title = { 
                        val infiniteTransition = rememberInfiniteTransition(label = "titleGlow")
                        val animatedColor by infiniteTransition.animateColor(
                            initialValue = Color.Cyan,
                            targetValue = Color(0xFF00FF9D), // Light Greenish Blue
                            animationSpec = infiniteRepeatable(
                                animation = tween(2000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "color"
                        )
                        Text(
                            text = "M BOX",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp,
                            style = androidx.compose.ui.text.TextStyle(
                                shadow = androidx.compose.ui.graphics.Shadow(
                                    color = animatedColor,
                                    blurRadius = 15f
                                )
                            )
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F2027)),
                    actions = {
                        IconButton(onClick = onShowDownloads) {
                            Icon(Icons.Default.Download, contentDescription = "Downloads", tint = Color.White)
                        }
                        IconButton(onClick = { webView?.reload() }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(if (customView == null) padding else PaddingValues(0.dp))
            .background(Color.Black)) {
            AndroidView(
                factory = {
                    WebView(it).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.databaseEnabled = true
                        settings.setSupportZoom(true)
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        
                        // Add JavaScript Interface
                        addJavascriptInterface(object {
                            @JavascriptInterface
                            fun playVideo(videoUrl: String) {
                                (context as? Activity)?.runOnUiThread {
                                    onPlayVideo(videoUrl)
                                }
                            }
                        }, "Android")

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val url = request?.url?.toString() ?: ""
                                if (isVideoUrl(url)) {
                                    onPlayVideo(url)
                                    return true
                                }
                                return false
                            }

                            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                isLoading = true
                                progress = 0.1f
                            }

                            override fun onPageFinished(view: WebView?, url: String?) {
                                isLoading = false
                                progress = 1f
                            }

                            override fun onReceivedError(
                                view: WebView?,
                                request: WebResourceRequest?,
                                error: WebResourceError?
                            ) {
                                // Only show error for main frame to avoid noise
                                if (request?.isForMainFrame == true) {
                                    isLoading = false
                                    Toast.makeText(context, strings.errorLoading, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                progress = newProgress / 100f
                            }

                            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                                customView = view
                                (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                            }

                            override fun onHideCustomView() {
                                customView = null
                                val activity = context as? Activity
                                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                            }
                        }

                        setDownloadListener { downloadUrl, userAgent, contentDisposition, mimetype, contentLength ->
                            if (isVideoUrl(downloadUrl)) {
                                onPlayVideo(downloadUrl)
                            } else {
                                // উন্নত ফাইলের নাম ডিটেকশন
                                var fileName = URLUtil.guessFileName(downloadUrl, contentDisposition, mimetype)
                                
                                // যদি এক্সটেনশন .bin হয়, তবে ইউআরএল থেকে আসল এক্সটেনশন খোঁজার চেষ্টা করা
                                if (fileName.endsWith(".bin")) {
                                    val extension = MimeTypeMap.getFileExtensionFromUrl(downloadUrl)
                                    if (extension.isNotEmpty()) {
                                        fileName = fileName.substringBeforeLast(".") + "." + extension
                                    }
                                }

                                val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
                                    // সঠিক MimeType সেট করা
                                    val finalMimeType = if (mimetype == "application/octet-stream") {
                                        MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                                            MimeTypeMap.getFileExtensionFromUrl(downloadUrl)
                                        ) ?: mimetype
                                    } else mimetype
                                    
                                    setMimeType(finalMimeType)
                                    val cookies = CookieManager.getInstance().getCookie(downloadUrl)
                                    addRequestHeader("cookie", cookies)
                                    addRequestHeader("User-Agent", userAgent)
                                    setDescription("Downloading file...")
                                    setTitle(fileName)
                                    setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                                }
                                val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                val id = dm.enqueue(request)
                                onDownloadStart(id, fileName)
                            }
                        }
                        
                        // Focus handling for TV remote
                        isFocusable = true
                        isFocusableInTouchMode = true
                        
                        loadUrl(url)
                        webView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Full screen overlay
            if (customView != null) {
                AndroidView(
                    factory = {
                        FrameLayout(it).apply {
                            addView(customView)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (isLoading && customView == null) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp),
                    color = Color.Cyan,
                    trackColor = Color.Transparent
                )
            }
        }
    }
}

@Composable
fun DownloadStatusDialog(
    downloads: List<DownloadInfo>, 
    onOpen: (Long) -> Unit, 
    onCancel: (Long) -> Unit, 
    onDelete: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Downloads", color = Color.Cyan, fontWeight = FontWeight.Bold) },
        text = {
            if (downloads.isEmpty()) {
                Text("No downloads yet", color = Color.White)
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                    items(downloads) { download ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(download.name, color = Color.White, fontSize = 14.sp, maxLines = 1)
                                Spacer(modifier = Modifier.height(4.dp))
                                if (!download.isFinished) {
                                    LinearProgressIndicator(
                                        progress = { download.progress },
                                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                        color = Color.Cyan,
                                        trackColor = Color.White.copy(alpha = 0.1f)
                                    )
                                } else {
                                    Text("Download Completed", color = Color.Green.copy(alpha = 0.7f), fontSize = 12.sp)
                                }
                            }
                            if (download.isFinished) {
                                Row {
                                    TextButton(onClick = { onOpen(download.id) }) {
                                        Text("Open", color = Color.Cyan, fontWeight = FontWeight.Bold)
                                    }
                                    IconButton(onClick = { onDelete(download.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f))
                                    }
                                }
                            } else {
                                IconButton(onClick = { onCancel(download.id) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Cancel", tint = Color.Red.copy(alpha = 0.7f))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color.Cyan)
            }
        },
        containerColor = Color(0xFF0F2027),
        shape = RoundedCornerShape(28.dp)
    )
}

@SuppressLint("Range")
fun getDownloadProgress(dm: DownloadManager, id: Long): Float {
    val query = DownloadManager.Query().setFilterById(id)
    val cursor = dm.query(query)
    if (cursor != null && cursor.moveToFirst()) {
        val downloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
        val total = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
        cursor.close()
        return if (total > 0) downloaded.toFloat() / total.toFloat() else 0f
    }
    cursor?.close()
    return -1f
}

fun installApk(context: Context, downloadId: Long) {
    openDownloadedFile(context, downloadId)
}

fun openDownloadedFile(context: Context, downloadId: Long) {
    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    val uri = dm.getUriForDownloadedFile(downloadId)
    val mimeType = dm.getMimeTypeForDownloadedFile(downloadId)
    if (uri != null) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show()
        }
    }
}

fun isVideoUrl(url: String): Boolean {
    val lowUrl = url.lowercase()
    return lowUrl.endsWith(".ts") || 
           lowUrl.contains(".m3u8") || 
           lowUrl.contains("/live/") || 
           (lowUrl.contains(".js") && lowUrl.contains("as-cdn")) ||
           lowUrl.contains("premiumtvs.space")
}


@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayerScreen(url: String, onBack: () -> Unit) {
    val context = LocalContext.current
    val exoPlayer = remember {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        // উন্নত বাফারিং কন্ট্রোল
        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                30000, // Min buffer to start
                60000, // Max buffer
                2500,  // Buffer to start playback
                5000   // Buffer after rebuffering
            )
            .build()

        ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setLoadControl(loadControl)
            .setWakeMode(C.WAKE_MODE_NETWORK)
            .build().apply {
            val isHls = url.contains(".m3u8") || url.contains(".js") || url.contains("as-cdn")
            
            val dataSourceFactory = DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)
                .setConnectTimeoutMs(15000) // টাইমআউট বাড়ানো হয়েছে
                .setReadTimeoutMs(15000)
                .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36")
            
            if (isHls) {
                val mediaSource = androidx.media3.exoplayer.hls.HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(url))
                setMediaSource(mediaSource)
            } else {
                setMediaItem(MediaItem.fromUri(url))
            }
            
            repeatMode = Player.REPEAT_MODE_ALL // এরর হলেও লুপ করার চেষ্টা করবে
            prepare()
            playWhenReady = true
        }
    }

    // Full screen handling
    val activity = context as? Activity
    DisposableEffect(Unit) {
        val originalOrientation = activity?.requestedOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        
        val window = activity?.window
        val insetsController = window?.let { WindowCompat.getInsetsController(it, it.decorView) }
        insetsController?.apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            exoPlayer.release()
            activity?.requestedOrientation = originalOrientation
            insetsController?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = true
                    keepScreenOn = true // এটি ভিডিও চলাকালীন স্ক্রিন বন্ধ হতে দেবে না
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Back", tint = Color.White)
        }
    }
}
