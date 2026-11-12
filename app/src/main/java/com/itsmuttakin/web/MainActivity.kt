package com.itsmuttakin.web

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.provider.Settings
import android.util.Rational
import androidx.compose.ui.layout.ContentScale
import java.net.HttpURLConnection
import android.graphics.Bitmap
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.ValueCallback
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
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
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.firebase.messaging.FirebaseMessaging
import android.content.BroadcastReceiver
import android.content.IntentFilter
import android.database.Cursor
import androidx.core.content.FileProvider
import android.os.Build
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import android.graphics.BitmapFactory
import androidx.core.content.edit
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
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import java.util.Locale
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Settings
import androidx.media3.common.Tracks
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.TrackGroup
import androidx.media3.common.Format
import androidx.activity.compose.BackHandler
import androidx.core.content.ContextCompat

data class VideoQuality(
    val name: String,
    val trackGroup: TrackGroup? = null,
    val trackIndex: Int = -1,
    val isAuto: Boolean = false
)

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

// Global states for stats
val totalUsersState = mutableLongStateOf(0L)
val newUsersState = mutableLongStateOf(0L)
val onlineUsersState = mutableLongStateOf(0L)

// Helper function to open external links / Telegram / WhatsApp / intents safely
fun openExternalLink(context: Context, url: String) {
    if (url.isBlank()) return
    val cleanUrl = url.trim()
    try {
        val intent = when {
            cleanUrl.startsWith("intent://") -> {
                Intent.parseUri(cleanUrl, Intent.URI_INTENT_SCHEME)
            }
            cleanUrl.startsWith("tg://") -> {
                Intent(Intent.ACTION_VIEW, Uri.parse(cleanUrl))
            }
            cleanUrl.contains("t.me/") || cleanUrl.contains("telegram.me/") -> {
                Intent(Intent.ACTION_VIEW, Uri.parse(cleanUrl))
            }
            else -> {
                Intent(Intent.ACTION_VIEW, Uri.parse(cleanUrl))
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val fallbackUrl = when {
                cleanUrl.contains("t.me/") -> cleanUrl
                cleanUrl.startsWith("tg:") -> "https://t.me/" + cleanUrl.substringAfter("domain=").substringAfter("resolve?domain=")
                else -> "https://t.me/mrghost_121"
            }
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(fallbackUrl))
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(browserIntent)
        } catch (ex: Exception) {
            Toast.makeText(context, "Cannot open link", Toast.LENGTH_SHORT).show()
        }
    }
}



// Gold Amber Watermark Badge for Fullscreen Video & Native Player
@Composable
fun GoldWatermarkBadge(
    text: String = "M BOX TV",
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Black.copy(alpha = 0.65f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.7f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF59E0B))
            )
            Text(
                text = text,
                color = Color(0xFFFCD34D),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}


// App Update States
val latestVersionCodeState = mutableLongStateOf(0L)
val updateUrlState = mutableStateOf("")
val updateRequiredState = mutableStateOf(false)
val updateMessageState = mutableStateOf("")

class MainActivity : ComponentActivity() {
    private var wakeLock: android.os.PowerManager.WakeLock? = null
    var filePathCallback: ValueCallback<Array<Uri>>? = null

    val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val results: Array<Uri>? = if (result.resultCode == RESULT_OK) {
            val intent = result.data
            if (intent != null) {
                val dataString = intent.dataString
                val clipData = intent.clipData
                if (clipData != null) {
                    val uriList = ArrayList<Uri>()
                    for (i in 0 until clipData.itemCount) {
                        uriList.add(clipData.getItemAt(i).uri)
                    }
                    uriList.toTypedArray()
                } else if (dataString != null) {
                    arrayOf(Uri.parse(dataString))
                } else null
            } else null
        } else null

        filePathCallback?.onReceiveValue(results)
        filePathCallback = null
    }

    @SuppressLint("InvalidWakeLockTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Background Play এর জন্য WakeLock সক্রিয় করা
        val powerManager = getSystemService(POWER_SERVICE) as android.os.PowerManager
        wakeLock = powerManager.newWakeLock(android.os.PowerManager.PARTIAL_WAKE_LOCK, "MBOX:WakeLock")
        wakeLock?.acquire(10 * 60 * 60 * 1000L) // ১০ ঘণ্টা পর অটো রিলিজ হবে (সেফটি)
        
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

        // Initialize mrghost Firebase Options
        val mrghostOptions = FirebaseOptions.Builder()
            .setApiKey("AIzaSyCkJzHEPgfS5wc8HjJf6wsH9PhjV8O0Zdw")
            .setApplicationId("1:577800510502:web:a1c6a30589521644331ae7")
            .setDatabaseUrl("https://mrghost-53f84-default-rtdb.asia-southeast1.firebasedatabase.app")
            .setProjectId("mrghost-53f84")
            .build()
        
        try {
            val mrghostApp = try {
                FirebaseApp.getInstance("mrghost")
            } catch (e: Exception) {
                FirebaseApp.initializeApp(this, mrghostOptions, "mrghost")
            }
            val mrghostDb = FirebaseDatabase.getInstance(mrghostApp).reference

            // Initialize Firebase for Stats (itsmuttakin)
            val statsOptions = FirebaseOptions.Builder()
                .setApiKey("AIzaSyB4nvhHK1f4nxyJ6fnLV9tlyUMK-dUIB3g")
                .setApplicationId("1:804285355395:web:307cfeaabac8423a1b92b6")
                .setDatabaseUrl("https://itsmuttakin-adf2b-default-rtdb.asia-southeast1.firebasedatabase.app")
                .setProjectId("itsmuttakin-adf2b")
                .build()
            
            val statsApp = try {
                FirebaseApp.getInstance("stats")
            } catch (e: Exception) {
                FirebaseApp.initializeApp(this, statsOptions, "stats")
            }
            val statsDb = FirebaseDatabase.getInstance(statsApp, "https://itsmuttakin-adf2b-default-rtdb.asia-southeast1.firebasedatabase.app").reference
            
            val prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)

            // Subscribe to a topic for mass notifications
            FirebaseMessaging.getInstance().subscribeToTopic("all_users")
            
            // Permissions for Notifications, Media & Storage
            val permsToRequest = mutableListOf<String>()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    permsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    permsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                    permsToRequest.add(Manifest.permission.READ_MEDIA_VIDEO)
                }
            } else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    permsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            if (permsToRequest.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permsToRequest.toTypedArray(), 101)
            }

            // Listen for System Notifications from mrghost RTDB
            mrghostDb.child("system_notifications").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (!snapshot.exists()) return
                    
                    val title = snapshot.child("title").getValue(String::class.java) ?: ""
                    val message = snapshot.child("message").getValue(String::class.java) ?: ""
                    val imageUrl = snapshot.child("image_url").getValue(String::class.java) ?: ""
                    val timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: 0L

                    val lastNotifTime = prefs.getLong("last_notif_timestamp", 0L)

                    // যদি নতুন কোনো নোটিফিকেশন হয় অথবা ইউজার অনেকদিন পর অনলাইনে আসে
                    if (timestamp > lastNotifTime && title.isNotEmpty()) {
                        showSystemNotification(title, message, imageUrl)
                        prefs.edit().putLong("last_notif_timestamp", timestamp).apply()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

            // Total User Counting (Using itsmuttakin) - Only counts once per device v2
            if (!prefs.getBoolean("is_total_counted_v2", false)) {
                statsDb.child("stats/total_users").runTransaction(object : com.google.firebase.database.Transaction.Handler {
                    override fun doTransaction(data: com.google.firebase.database.MutableData): com.google.firebase.database.Transaction.Result {
                        var current = data.getValue(Long::class.java)
                        if (current == null || current < 127L) current = 127L
                        data.value = current + 1
                        return com.google.firebase.database.Transaction.success(data)
                    }
                    override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                        if (committed) prefs.edit().putBoolean("is_total_counted_v2", true).apply()
                    }
                })
            }

            // Online User Tracking (Using itsmuttakin)
            val userId = prefs.getString("user_id", null) ?: java.util.UUID.randomUUID().toString().also { 
                prefs.edit().putString("user_id", it).apply()
            }
            val onlineRef = statsDb.child("stats/online_users").child(userId)
            
            // Real-time Online Tracking with fallback
            onlineRef.setValue(true) // Set immediately
            onlineRef.onDisconnect().removeValue()

            statsDb.child(".info/connected").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false
                    if (connected) {
                        onlineRef.setValue(true)
                        onlineRef.onDisconnect().removeValue()
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

            // Listen for changes to show in App Admin Screen UI (Fetching from itsmuttakin)
            statsDb.child("stats/total_users").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    totalUsersState.longValue = snapshot.getValue(Long::class.java) ?: 0L
                }
                override fun onCancelled(error: DatabaseError) {}
            })

            statsDb.child("stats/online_users").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onlineUsersState.longValue = snapshot.childrenCount
                }
                override fun onCancelled(error: DatabaseError) {}
            })

            // Listen for App Updates from mrghost RTDB
            mrghostDb.child("app_update").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    latestVersionCodeState.longValue = snapshot.child("latest_version_code").getValue(Long::class.java) ?: 0L
                    updateUrlState.value = snapshot.child("update_url").getValue(String::class.java) ?: ""
                    updateRequiredState.value = snapshot.child("update_required").getValue(Boolean::class.java) ?: false
                    updateMessageState.value = snapshot.child("update_message").getValue(String::class.java) ?: ""
                }
                override fun onCancelled(error: DatabaseError) {}
            })

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

    private fun showSystemNotification(title: String, message: String, imageUrl: String = "") {
        val channelId = "mbox_notifications"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "M BOX News", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (imageUrl.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val url = URL(imageUrl)
                    val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                    withContext(Dispatchers.Main) {
                        notificationBuilder.setLargeIcon(bitmap)
                        notificationBuilder.setStyle(NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null as android.graphics.Bitmap?))
                        notificationManager.notify(timestampId(), notificationBuilder.build())
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        notificationManager.notify(timestampId(), notificationBuilder.build())
                    }
                }
            }
        } else {
            notificationManager.notify(timestampId(), notificationBuilder.build())
        }
    }

    private fun timestampId(): Int = System.currentTimeMillis().toInt()

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (activePlayingVideoUrlState.value != null) {
                try {
                    val params = PictureInPictureParams.Builder()
                        .setAspectRatio(Rational(16, 9))
                        .build()
                    enterPictureInPictureMode(params)
                } catch (e: Exception) {}
            }
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        isInPipModeState.value = isInPictureInPictureMode
    }

    override fun onDestroy() {
        super.onDestroy()
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
    }

    companion object {
        val activePlayingVideoUrlState = mutableStateOf<String?>(null)
        val isInPipModeState = mutableStateOf(false)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("M BOX ADMIN", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F2027))
            )
        },
        containerColor = Color.Black
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatsCard("Total Users", "${totalUsersState.longValue}", Color(0xFF7A8AFF))
            StatsCard("New Users", "${newUsersState.longValue}", Color(0xFF00F5FF))
            StatsCard("Online Users", "${onlineUsersState.longValue}", Color(0xFF4CAF50))
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "Real-time analytics from Firebase",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun StatsCard(label: String, value: String, color: Color) {
    Surface(
        color = Color.White.copy(alpha = 0.05f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(label.uppercase(), color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
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

    // State to handle video player as an overlay/banner
    var playingVideoUrl by remember { mutableStateOf<String?>(null) }
    var playingVideoTitle by remember { mutableStateOf("") }
    var playingVideoIsVIP by remember { mutableStateOf(false) }
    var isPlayerLandscape by remember { mutableStateOf(false) }

    LaunchedEffect(playingVideoUrl) {
        MainActivity.activePlayingVideoUrlState.value = playingVideoUrl
    }

    // Orientation handling for player
    LaunchedEffect(isPlayerLandscape) {
        if (playingVideoUrl != null) {
            (context as? Activity)?.requestedOrientation = if (isPlayerLandscape) {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            } else {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }
        }
    }

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
                    showDownloadSheet = true // Auto open popup when completed so user can click VIEW / OPEN
                }
            }
        }
    }

    DisposableEffect(Unit) {
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        ContextCompat.registerReceiver(context, downloadReceiver, filter, ContextCompat.RECEIVER_EXPORTED)
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
    
    // Check for Updates via mrghost Realtime Database
    LaunchedEffect(latestVersionCodeState.longValue, refreshTrigger) {
        val latestVersion = latestVersionCodeState.longValue
        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val currentVersion = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            pInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            pInfo.versionCode.toLong()
        }

        // Only show update dialog if an older version is running
        if (latestVersion > currentVersion && !isUpdateDownloading) {
            updateUrl = updateUrlState.value
            isUpdateRequired = updateRequiredState.value
            showUpdateDialog = true
        } else {
            // Hide dialog if already on latest or higher version
            showUpdateDialog = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Video Player in Portrait (Integrated as a Banner)
            if (playingVideoUrl != null && !isPlayerLandscape) {
                VideoPlayerScreen(
                    url = playingVideoUrl!!,
                    title = playingVideoTitle,
                    isVIP = playingVideoIsVIP,
                    isLandscape = false,
                    onToggleRotation = { isPlayerLandscape = true },
                    onBack = { 
                        playingVideoUrl = null 
                        playingVideoTitle = ""
                        playingVideoIsVIP = false
                        isPlayerLandscape = false
                        (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    }
                )
            }

            Box(modifier = Modifier.weight(1f)) {
                NavHost(navController = navController, startDestination = "selection") {
                    composable("selection") {
                        SelectionScreen(
                            refreshTrigger = refreshTrigger,
                            onStartClick = { navController.navigate("webview/mobile") },
                            onAdminClick = { navController.navigate("admin") }
                        )
                    }
                    composable("admin") {
                        AdminScreen(onBack = { navController.popBackStack() })
                    }
                    composable("webview/{mode}") {
                        WebViewScreen(
                            isPlayerActive = playingVideoUrl != null,
                            onBackToHome = { navController.popBackStack() },
                            onDownloadStart = { id, name -> 
                                activeDownloads.add(DownloadInfo(id, name, 0f))
                                showDownloadSheet = true // Auto-open download popup when download starts
                            },
                            onShowDownloads = { showDownloadSheet = true },
                            onPlayVideo = { videoUrl, videoTitle, isVIP ->
                                playingVideoUrl = videoUrl
                                playingVideoTitle = videoTitle
                                playingVideoIsVIP = isVIP
                            },
                            onClosePlayer = {
                                playingVideoUrl = null
                                playingVideoTitle = ""
                                playingVideoIsVIP = false
                                isPlayerLandscape = false
                                (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                            }
                        )
                    }
                }

                // Video Player in Landscape (Overlay)
                if (playingVideoUrl != null && isPlayerLandscape) {
                    VideoPlayerScreen(
                        url = playingVideoUrl!!,
                        title = playingVideoTitle,
                        isVIP = playingVideoIsVIP,
                        isLandscape = true,
                        onToggleRotation = { isPlayerLandscape = false },
                        onBack = { 
                            playingVideoUrl = null 
                            playingVideoTitle = ""
                            playingVideoIsVIP = false
                            isPlayerLandscape = false
                            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        }
                    )
                }
            }
        }
        
        if (showUpdateDialog) {
            PremiumUpdateDialog(
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

        if (showDownloadSheet && activeDownloads.isNotEmpty()) {
            FloatingDownloadBar(
                downloads = activeDownloads,
                onOpen = { id -> 
                    openDownloadedFile(context, id)
                    showDownloadSheet = false
                },
                onCancel = { id -> 
                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    dm.remove(id)
                    activeDownloads.removeIf { it.id == id }
                    if (activeDownloads.isEmpty()) showDownloadSheet = false
                },
                onDismiss = { showDownloadSheet = false },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

@Composable
fun PremiumUpdateDialog(progress: Float, isDownloading: Boolean, isForceUpdate: Boolean, onStartDownload: () -> Unit, onDismiss: () -> Unit) {
    val strings = LocalStrings.current
    
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
                    text = if (isDownloading) "Please wait while we download the latest version." 
                           else if (updateMessageState.value.isNotEmpty()) updateMessageState.value 
                           else strings.updateText,
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectionScreen(refreshTrigger: Int, onStartClick: () -> Unit, onAdminClick: () -> Unit) {
    val context = LocalContext.current
    var progress by remember { mutableFloatStateOf(0f) }

    // Auto navigate after loading animation
    LaunchedEffect(Unit) {
        while (progress < 1f) {
            delay(40)
            progress += 0.02f
        }
        delay(400)
        onStartClick()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "uiAnim")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val animatedColor by infiniteTransition.animateColor(
        initialValue = Color(0xFF00F5FF),
        targetValue = Color(0xFF00A3FF),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowColor"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
    ) {
        // Force Portrait
        LaunchedEffect(Unit) {
            (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        // Top-left ambient glow
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.TopStart)
                .offset(x = (-80).dp, y = (-80).dp)
                .clip(CircleShape)
                .background(Color(0xFF00F5FF).copy(alpha = 0.12f))
        )

        // Bottom-right ambient glow
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 80.dp)
                .clip(CircleShape)
                .background(Color(0xFF0077FF).copy(alpha = 0.12f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Main Logo with Glow
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.Black)
                    .border(2.dp, animatedColor.copy(alpha = 0.8f), RoundedCornerShape(32.dp))
                    .shadow(elevation = 30.dp, shape = RoundedCornerShape(32.dp), ambientColor = animatedColor, spotColor = animatedColor),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.mbox_logo),
                    contentDescription = "M BOX Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "M",
                    color = Color(0xFF00F5FF),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "BOX",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Sub-categories text
            Text(
                text = "MOVIE   •   MOD APK   •   TOOLS   •   LIVE TV",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(42.dp))

            // Animated Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .width(180.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = Color(0xFF00F5FF),
                trackColor = Color.White.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Loading...",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Footer Credit
            Text(
                text = "───   MR GHOST STUDIO   ───",
                color = Color.White.copy(alpha = 0.35f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.5.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun PremiumNoticeBoard(refreshTrigger: Int, borderColor: Color) {
    var noticeText by remember { mutableStateOf(Firebase.remoteConfig.getString("notice_text")) }
    
    LaunchedEffect(refreshTrigger) {
        noticeText = Firebase.remoteConfig.getString("notice_text")
    }

    Surface(
        color = Color.White.copy(alpha = 0.03f),
        shape = RoundedCornerShape(28.dp),
        border = BorderStroke(1.dp, borderColor.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().height(260.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("SYSTEM NOTICE", color = borderColor, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                Row {
                    Text("Term", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp))
                    Text("Privacy", color = Color.White.copy(alpha = 0.4f), fontSize = 11.sp)
                }
            }
            
            HorizontalDivider(
                color = Color.White.copy(alpha = 0.05f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.2f))
                    .padding(14.dp)
            ) {
                val scrollState = rememberScrollState()
                Text(
                    text = noticeText,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
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
    isPlayerActive: Boolean,
    onBackToHome: () -> Unit,
    onDownloadStart: (Long, String) -> Unit,
    onShowDownloads: () -> Unit,
    onPlayVideo: (String, String, Boolean) -> Unit,
    onClosePlayer: () -> Unit
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

    val backCallback = remember(webView, customView, isPlayerActive) {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isPlayerActive) {
                    webView?.evaluateJavascript("if(window.onAndroidPlayerClosed) { window.onAndroidPlayerClosed(); }", null)
                    onClosePlayer()
                } else if (customView != null) {
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
        containerColor = Color.Black
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
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
                        settings.loadWithOverviewMode = false
                        settings.useWideViewPort = false
                        settings.databaseEnabled = true
                        settings.setSupportZoom(false)
                        settings.builtInZoomControls = false
                        settings.displayZoomControls = false
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.javaScriptCanOpenWindowsAutomatically = false
                        settings.textZoom = 100
                        setInitialScale(0)
                        
                        // Add JavaScript Interface
                        addJavascriptInterface(object {
                            @JavascriptInterface
                            fun playVideo(videoUrl: String) {
                                (context as? Activity)?.runOnUiThread {
                                    onPlayVideo(videoUrl, "", false)
                                }
                            }

                            @JavascriptInterface
                            fun playVideo(videoUrl: String, title: String) {
                                (context as? Activity)?.runOnUiThread {
                                    onPlayVideo(videoUrl, title, false)
                                }
                            }

                            @JavascriptInterface
                            fun playVideo(videoUrl: String, title: String, isVIP: Boolean) {
                                (context as? Activity)?.runOnUiThread {
                                    onPlayVideo(videoUrl, title, isVIP)
                                }
                            }

                            @JavascriptInterface
                            fun playVideoWithVIP(videoUrl: String, title: String, isVIP: Boolean) {
                                (context as? Activity)?.runOnUiThread {
                                    onPlayVideo(videoUrl, title, isVIP)
                                }
                            }

                            @JavascriptInterface
                            fun setLandscape(isLandscape: Boolean) {
                                (context as? Activity)?.runOnUiThread {
                                    val activity = context as? Activity ?: return@runOnUiThread
                                    activity.requestedOrientation = if (isLandscape) {
                                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                    } else {
                                        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                                    }
                                }
                            }

                            @JavascriptInterface
                            fun setOrientation(orientation: String) {
                                (context as? Activity)?.runOnUiThread {
                                    val activity = context as? Activity ?: return@runOnUiThread
                                    when (orientation.lowercase(Locale.ROOT)) {
                                        "landscape", "sensor_landscape" -> activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                        "portrait" -> activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                        else -> activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                                    }
                                }
                            }

                            @JavascriptInterface
                            fun openBrowser(url: String) {
                                (context as? Activity)?.runOnUiThread {
                                    openExternalLink(context, url)
                                }
                            }
                            
                            @JavascriptInterface
                            fun downloadFile(url: String, fileName: String) {
                                (context as? Activity)?.runOnUiThread {
                                    val request = DownloadManager.Request(Uri.parse(url)).apply {
                                        setTitle(fileName)
                                        setDescription("Downloading via M BOX...")
                                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                                        
                                        // Add current cookies and UA for better compatibility
                                        val cookies = CookieManager.getInstance().getCookie(url)
                                        addRequestHeader("cookie", cookies)
                                        addRequestHeader("User-Agent", settings.userAgentString)
                                    }
                                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                                    val id = dm.enqueue(request)
                                    onDownloadStart(id, fileName)
                                }
                            }

                            @JavascriptInterface
                            fun closePlayer() {
                                (context as? Activity)?.runOnUiThread {
                                    onClosePlayer()
                                }
                            }

                            @JavascriptInterface
                            fun enterPip() {
                                (context as? Activity)?.runOnUiThread {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        try {
                                            val params = PictureInPictureParams.Builder()
                                                .setAspectRatio(Rational(16, 9))
                                                .build()
                                            (context as? Activity)?.enterPictureInPictureMode(params)
                                        } catch (e: Exception) {}
                                    }
                                }
                            }
                        }, "Android")

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                val url = request?.url?.toString() ?: ""
                                val lowUrl = url.lowercase(Locale.ROOT)
                                
                                // 1. Telegram, External Intents, WhatsApp, Tel, Mailto, Market etc.
                                if (url.contains("t.me/") || url.contains("telegram.me/") || url.startsWith("tg:") || 
                                    url.startsWith("intent:") || url.startsWith("whatsapp:") || url.startsWith("mailto:") || 
                                    url.startsWith("tel:") || url.startsWith("market:") || 
                                    (!url.startsWith("http://") && !url.startsWith("https://"))) {
                                    openExternalLink(context, url)
                                    return true
                                }

                                // 2. Block ad popups & scam redirect domains
                                if (lowUrl.contains("breedsmuteexams.com") || lowUrl.contains("spin") || lowUrl.contains("cashprize") ||
                                    lowUrl.contains("bonus") || lowUrl.contains("lottery") || lowUrl.contains("reward")) {
                                    return true // Block ad popups!
                                }

                                // 3. Video streams
                                if (isVideoUrl(url) && !url.contains("savefrom") && !url.contains("y2mate")) {
                                    onPlayVideo(url, "", false)
                                    return true
                                }
                                
                                // অন্য সব ক্ষেত্রে ব্রাউজারে লোড হবে (ডাউনলোডারের জন্য জরুরি)
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
                            override fun onCreateWindow(view: WebView?, isDialog: Boolean, isUserGesture: Boolean, resultMsg: android.os.Message?): Boolean {
                                return false // Block ad popup windows completely!
                            }

                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                progress = newProgress / 100f
                            }

                            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                                customView = view
                                (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                            }

                            override fun onHideCustomView() {
                                customView = null
                                val activity = context as? Activity
                                activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                            }

                            override fun onShowFileChooser(
                                webView: WebView?,
                                filePathCallback: ValueCallback<Array<Uri>>?,
                                fileChooserParams: FileChooserParams?
                            ): Boolean {
                                val mainActivity = context as? MainActivity ?: return false
                                mainActivity.filePathCallback?.onReceiveValue(null)
                                mainActivity.filePathCallback = filePathCallback

                                try {
                                    val intent = fileChooserParams?.createIntent() ?: Intent(Intent.ACTION_GET_CONTENT).apply {
                                        addCategory(Intent.CATEGORY_OPENABLE)
                                        type = "*/*"
                                    }
                                    mainActivity.fileChooserLauncher.launch(intent)
                                } catch (e: Exception) {
                                    mainActivity.filePathCallback?.onReceiveValue(null)
                                    mainActivity.filePathCallback = null
                                    return false
                                }
                                return true
                            }
                        }

                        setDownloadListener { downloadUrl, userAgent, contentDisposition, mimetype, _ ->
                            if (isVideoUrl(downloadUrl)) {
                                onPlayVideo(downloadUrl, "", false)
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
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    AndroidView(
                        factory = {
                            FrameLayout(it).apply {
                                addView(customView)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                    GoldWatermarkBadge(
                        text = "M BOX TV",
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    )
                }
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
fun FloatingDownloadBar(
    downloads: List<DownloadInfo>,
    onOpen: (Long) -> Unit,
    onCancel: (Long) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (downloads.isEmpty()) return

    val latestDownload = downloads.last()

    Surface(
        color = Color(0xFF0F172A).copy(alpha = 0.95f),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, if (latestDownload.isFinished) Color(0xFF10B981) else Color(0xFF00F5FF)),
        shadowElevation = 16.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = if (latestDownload.isFinished) Color(0xFF10B981) else Color(0xFF00F5FF),
                        modifier = Modifier.size(20.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = latestDownload.name,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = if (latestDownload.isFinished) "Download Complete!" else "Downloading: ${(latestDownload.progress * 100).toInt()}%",
                            color = if (latestDownload.isFinished) Color(0xFF10B981) else Color.White.copy(alpha = 0.7f),
                            fontSize = 10.sp
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (latestDownload.isFinished) {
                        Button(
                            onClick = { onOpen(latestDownload.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("VIEW / OPEN", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    } else {
                        Button(
                            onClick = { onCancel(latestDownload.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0055).copy(alpha = 0.2f)),
                            border = BorderStroke(1.dp, Color(0xFFFF0055).copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("CANCEL", color = Color(0xFFFF0055), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (!latestDownload.isFinished) {
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { latestDownload.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFF00F5FF),
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }
    }
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
    
    // এপিকে বা অন্য ডাউনলোড ফাইল হলে ভিডিও হিসেবে প্লে করা যাবে না
    if (lowUrl.contains(".apk") || lowUrl.contains(".zip") || lowUrl.contains(".rar") || 
        lowUrl.contains(".pdf") || lowUrl.contains(".bin") || lowUrl.contains(".exe") ||
        lowUrl.contains(".docx") || lowUrl.contains(".txt")) {
        return false
    }

    // আইপি ভিত্তিক স্ট্রিম বা স্পেশাল ইউআরএল প্যাটার্ন ডিটেকশন
    val isIpStream = url.contains(Regex("""\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}""")) && 
                     (url.contains(":80") || url.contains(":8080") || url.contains(":8880") || url.contains("/play/"))

    return lowUrl.contains("m3u8") || 
           lowUrl.contains(".ts") || 
           lowUrl.contains(".m4s") ||
           lowUrl.contains(".mp4") ||
           lowUrl.contains(".mkv") ||
           lowUrl.contains(".webm") ||
           lowUrl.contains(".avi") ||
           lowUrl.contains(".mov") ||
           lowUrl.contains(".flv") ||
           lowUrl.contains(".3gp") ||
           lowUrl.contains("/live/") || 
           lowUrl.contains("live.php") ||
           lowUrl.contains("stream") ||
           lowUrl.contains("playlist") ||
           lowUrl.contains("player") ||
           lowUrl.contains("video") ||
           lowUrl.contains("m3u") ||
           lowUrl.contains("getstream") ||
           lowUrl.contains(".r2.dev") || // Cloudflare R2 direct links
           lowUrl.contains("drive.google") ||
           (lowUrl.contains(".js") && lowUrl.contains("as-cdn")) ||
           lowUrl.contains("premiumtvs.space") ||
           lowUrl.contains("grandpearl.top") ||
           lowUrl.contains(".mpd") ||
           isIpStream
}


@OptIn(ExperimentalMaterial3Api::class)
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun VideoPlayerScreen(
    url: String, 
    title: String = "",
    isVIP: Boolean = false,
    isLandscape: Boolean,
    onToggleRotation: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    
    // State for playback position and duration
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var isPlaying by remember { mutableStateOf(true) }
    
    // Quality Selection States
    var showQualityDialog by remember { mutableStateOf(false) }
    var showSpeedDialog by remember { mutableStateOf(false) }
    var selectedSpeed by remember { mutableFloatStateOf(1.0f) }
    val qualityOptions = remember { mutableStateListOf<VideoQuality>() }
    var selectedQualityName by remember { mutableStateOf("Auto") }
    val isInPip = MainActivity.isInPipModeState.value

    val exoPlayer = remember {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
            .build()

        val loadControl = DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                1000,
                30000,
                500,
                1000
            )
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()

        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
            .setDefaultRequestProperties(mapOf(
                "Referer" to "https://itsmuttakin.pages.dev/",
                "Origin" to "https://itsmuttakin.pages.dev"
            ))

        ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setLoadControl(loadControl)
            .setMediaSourceFactory(
                androidx.media3.exoplayer.source.DefaultMediaSourceFactory(context)
                    .setDataSourceFactory(dataSourceFactory)
                    .setLiveTargetOffsetMs(3000)
            )
            .build().apply {
                prepare()
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        if (playbackState == Player.STATE_READY) {
                            duration = this@apply.duration
                        }
                    }
                    override fun onIsPlayingChanged(isPlayingChange: Boolean) {
                        isPlaying = isPlayingChange
                    }
                    override fun onTracksChanged(tracks: Tracks) {
                        qualityOptions.clear()
                        qualityOptions.add(VideoQuality("Auto", isAuto = true))
                        
                        tracks.groups.forEach { trackGroupInfo ->
                            if (trackGroupInfo.type == C.TRACK_TYPE_VIDEO) {
                                for (i in 0 until trackGroupInfo.length) {
                                    if (trackGroupInfo.isTrackSupported(i)) {
                                        val format = trackGroupInfo.getTrackFormat(i)
                                        val qualityName = "${format.height}p"
                                        if (qualityOptions.none { it.name == qualityName }) {
                                            qualityOptions.add(VideoQuality(qualityName, trackGroupInfo.mediaTrackGroup, i))
                                        }
                                    }
                                }
                            }
                        }
                        val sorted = qualityOptions.filter { !it.isAuto }.sortedByDescending { 
                            it.name.replace("p", "").toIntOrNull() ?: 0 
                        }
                        qualityOptions.clear()
                        qualityOptions.add(VideoQuality("Auto", isAuto = true))
                        qualityOptions.addAll(sorted)
                    }
                    override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                        val currentMediaItem = this@apply.currentMediaItem
                        if (currentMediaItem != null) {
                            this@apply.setMediaItem(currentMediaItem)
                            this@apply.prepare()
                            this@apply.play()
                        }
                    }
                })
            }
    }

    // Update video source when URL changes
    LaunchedEffect(url) {
        val mediaItemBuilder = MediaItem.Builder()
            .setUri(Uri.parse(url))
            .setLiveConfiguration(
                MediaItem.LiveConfiguration.Builder()
                    .setTargetOffsetMs(3000)
                    .build()
            )
            
        if (url.contains("m3u8", ignoreCase = true) || url.contains("m3u", ignoreCase = true) || url.contains("githubusercontent", ignoreCase = true)) {
            mediaItemBuilder.setMimeType(androidx.media3.common.MimeTypes.APPLICATION_M3U8)
        } else if (url.contains(".mpd", ignoreCase = true)) {
            mediaItemBuilder.setMimeType(androidx.media3.common.MimeTypes.APPLICATION_MPD)
        }
        
        val mediaItem = mediaItemBuilder.build()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
        selectedQualityName = "Auto"
    }

    // Update position periodically
    LaunchedEffect(exoPlayer) {
        while (true) {
            currentPosition = exoPlayer.currentPosition
            delay(1000)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // Player Box with dynamic size
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isLandscape) 400.dp else 230.dp)
            .then(if (isLandscape) Modifier.fillMaxSize() else Modifier)
            .background(Color.Black)
    ) {
        var showControls by remember { mutableStateOf(true) }
        
        LaunchedEffect(showControls, isPlaying) {
            if (showControls && isPlaying) {
                delay(5000)
                showControls = false
            }
        }

        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false
                    keepScreenOn = true 
                }
            },
            modifier = Modifier.fillMaxSize().clickable { showControls = !showControls }
        )

        // Persistent Classic Amber-Gold Watermark Badge in Native Player (Hidden in PiP)
        if (!isInPip) {
            GoldWatermarkBadge(
                text = if (title.contains("Live", ignoreCase = true) || title.contains("TV", ignoreCase = true)) "M BOX TV" else "M BOX CINEMA",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            )
        }
        
        if (showControls && !isInPip) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.35f)),
                contentAlignment = Alignment.Center
            ) {
                // Center Play/Pause and 10s Seek Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Rewind 10s
                    Surface(
                        modifier = Modifier.size(44.dp).clip(CircleShape).clickable {
                            exoPlayer.seekTo((exoPlayer.currentPosition - 10000L).coerceAtLeast(0L))
                        },
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("-10s", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Play/Pause Center
                    Surface(
                        modifier = Modifier.size(56.dp).clip(CircleShape).clickable { if (isPlaying) exoPlayer.pause() else exoPlayer.play() },
                        color = Color.White.copy(alpha = 0.85f),
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(34.dp))
                        }
                    }

                    // Forward 10s
                    Surface(
                        modifier = Modifier.size(44.dp).clip(CircleShape).clickable {
                            exoPlayer.seekTo((exoPlayer.currentPosition + 10000L).coerceAtMost(duration))
                        },
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = CircleShape
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("+10s", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Bottom Controls
                Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    // Seek bar
                    if (duration > 0 && duration < 36000000) { 
                        Slider(
                            value = currentPosition.toFloat(),
                            onValueChange = { exoPlayer.seekTo(it.toLong()) },
                            valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 20.dp),
                            colors = SliderDefaults.colors(thumbColor = Color.Cyan, activeTrackColor = Color.Cyan)
                        )
                    }

                    // Quality & Speed Info
                    Text(
                        text = "Quality: $selectedQualityName  •  Speed: ${selectedSpeed}x",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Playback Speed Button
                        IconButton(
                            onClick = { showSpeedDialog = true },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape).size(36.dp)
                        ) {
                            Text("${selectedSpeed}x", color = Color.Cyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Quality Settings Button
                        IconButton(
                            onClick = { showQualityDialog = true },
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape).size(36.dp)
                        ) {
                            Icon(Icons.Default.Settings, "Quality", tint = Color.White, modifier = Modifier.size(20.dp))
                        }

                        // Picture-in-Picture Button
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            IconButton(
                                onClick = {
                                    try {
                                        val params = PictureInPictureParams.Builder()
                                            .setAspectRatio(Rational(16, 9))
                                            .build()
                                        (context as? Activity)?.enterPictureInPictureMode(params)
                                    } catch (e: Exception) {}
                                },
                                modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape).size(36.dp)
                            ) {
                                Text("PiP", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Rotate Button
                        IconButton(
                            onClick = onToggleRotation,
                            modifier = Modifier.background(Color.Black.copy(alpha = 0.4f), CircleShape).size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isLandscape) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // Close Button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.TopEnd).background(Color.Black.copy(alpha = 0.4f), CircleShape).size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }

    // Playback Speed Selection Dialog
    if (showSpeedDialog) {
        val speedOptions = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
        AlertDialog(
            onDismissRequest = { showSpeedDialog = false },
            title = { Text("Select Playback Speed", color = Color.Cyan, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn {
                    items(speedOptions) { spd ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedSpeed = spd
                                    exoPlayer.setPlaybackSpeed(spd)
                                    showSpeedDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSpeed == spd,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = Color.Cyan)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(if (spd == 1.0f) "1.0x (Normal)" else "${spd}x", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSpeedDialog = false }) {
                    Text("Cancel", color = Color.Cyan)
                }
            },
            containerColor = Color(0xFF0F2027),
            shape = RoundedCornerShape(28.dp)
        )
    }
    }

    // Quality Selection Dialog
    if (showQualityDialog) {
        AlertDialog(
            onDismissRequest = { showQualityDialog = false },
            title = { Text("Select Video Quality", color = Color.Cyan, fontWeight = FontWeight.Bold) },
            text = {
                LazyColumn {
                    items(qualityOptions) { quality ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (quality.isAuto) {
                                        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                            .buildUpon()
                                            .clearOverrides()
                                            .build()
                                        selectedQualityName = "Auto"
                                    } else {
                                        exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                                            .buildUpon()
                                            .setOverrideForType(
                                                TrackSelectionOverride(quality.trackGroup!!, quality.trackIndex)
                                            )
                                            .build()
                                        selectedQualityName = quality.name
                                    }
                                    showQualityDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedQualityName == quality.name,
                                onClick = null,
                                colors = RadioButtonDefaults.colors(selectedColor = Color.Cyan)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(quality.name, color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showQualityDialog = false }) {
                    Text("Cancel", color = Color.Cyan)
                }
            },
            containerColor = Color(0xFF0F2027),
            shape = RoundedCornerShape(28.dp)
        )
    }
}

fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600
    return if (hours > 0) String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    else String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
