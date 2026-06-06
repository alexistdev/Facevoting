package com.berkatfaatulohalawa1711010164.facevoting.API

import java.io.IOException

class NoConnectivityException : IOException() {
    override val message: String
        get() = "No Internet Connection"
}
