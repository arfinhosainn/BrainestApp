package com.scelio.brainest.presentation.permission

expect class PermissionController {
    suspend fun requestPermission(permission: Permission): PermissionState
}