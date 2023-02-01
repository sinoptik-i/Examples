package ru.stream.smarthome.features.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val PERMISSION_PREFIX = "Permission: "

class PermissionManager  constructor(
    private val preferences: SharedPreferences,
    private val context: Context
) : PermissionBinder, PermissionRequester, LifecycleObserver {

    private val permissionResults = MutableSharedFlow<RequestPermissionResult>()
    private var activityRef: WeakReference<AppCompatActivity>? = null
    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private val activity: AppCompatActivity?
        get() = activityRef?.get()?.takeIf { !it.isFinishing }

    override fun bind(activity: AppCompatActivity) {
        permissionLauncher =
            activity.registerForActivityResult(RequestPermissionContract()) { result ->
                activity.lifecycleScope.launch { permissionResults.emit(result) }
            }
        activityRef = WeakReference(activity)
        activity.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                activityRef?.clear()
                permissionLauncher = null
            }
        })
    }

    override fun checkPermission(permission: String) = isPermissionGranted(permission)

    override suspend fun requestPermissionResult(permission: String): PermissionResult? =
        suspendCoroutine { continuation ->
            val activity = activity
            val launcher = permissionLauncher
            if (activity == null || launcher == null) {
                continuation.resume(null)
                return@suspendCoroutine
            }
            if (isPermissionGranted(permission)) {
                continuation.resume(PermissionResult.GRANTED)
            } else if (activity.shouldShowRequestPermissionRationale(permission) && isPermissionRequested(
                    permission
                )
            ) {
                continuation.resume(PermissionResult.NEVER_ASK_AGAIN)
            } else {
                activity.lifecycleScope.launch {
                    permissionResults.firstOrNull { it.permission == permission }
                        ?.let {
                            putRequestedPermission(permission)
                            continuation.resume(PermissionResult.create(it.isGranted))
                        } ?: continuation.resume(PermissionResult.DECLINED)
                }
                launcher.launch(permission)
            }
        }

    override fun shouldShowRationale(permission: String) =
        activity?.shouldShowRequestPermissionRationale(permission) == true

    override fun isPermissionRequested(permission: String) =
        preferences.getBoolean(PERMISSION_PREFIX + permission, false)

    private fun putRequestedPermission(permission: String) =
        preferences.edit { putBoolean(PERMISSION_PREFIX + permission, false) }

    private fun isPermissionGranted(permission: String) =
        context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}


class RequestPermissionContract : ActivityResultContract<String, RequestPermissionResult>() {
    override fun createIntent(context: Context, input: String): Intent {
        return ActivityResultContracts.RequestMultiplePermissions()
            .createIntent(context, arrayOf(input))
    }

    override fun parseResult(resultCode: Int, intent: Intent?): RequestPermissionResult {
        return ActivityResultContracts.RequestMultiplePermissions()
            .parseResult(resultCode, intent).entries
            .first().let { RequestPermissionResult(it.key, it.value) }
    }
}


class RequestPermissionResult(
    val permission: String,
    val isGranted: Boolean
)


enum class PermissionResult {
    GRANTED, DECLINED, NEVER_ASK_AGAIN;

    companion object {
        fun create(isGranted: Boolean, shouldShowRationale: Boolean = true): PermissionResult =
            when {
                isGranted -> GRANTED
                shouldShowRationale -> DECLINED
                else -> NEVER_ASK_AGAIN
            }
    }
}

interface PermissionBinder {

    fun bind(activity: AppCompatActivity)
}

interface PermissionChecker {
    fun checkPermission(permission: String): Boolean
    fun isPermissionRequested(permission: String): Boolean
    fun shouldShowRationale(permission: String): Boolean
    fun isReadExternalStorage() = checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun isWriteExternalStorage() = checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    fun isCamera() = checkPermission(Manifest.permission.CAMERA)
}

interface PermissionRequester : PermissionChecker {
    suspend fun requestPermissionResult(permission: String): PermissionResult?
    /** @return is permission granted*/
    suspend fun requestPermission(permission: String): Boolean = requestPermissionResult(permission) == PermissionResult.GRANTED
}