package com.matrix.qdrop.core

sealed class Router(val route: String) {
    object Auth : Router("auth")
    object Home : Router("home")
    object Update : Router("update")
}