package com.lucidsoftworksllc.taxidi.utils

object Constants {
    // URL constants, Sabot is a pet-project of mine, I'll be using the existing domain for this project.
    const val BASE_URL = "https://www.sabotcommunity.com/"
    private const val APP_VERSION_FINAL = "v0.0.1a"
    private const val APP_VERSION_URL = "$APP_VERSION_FINAL/"
    const val ROOT_URL = BASE_URL + "taxidi/" + APP_VERSION_URL

    // TODO Use this installed version to cross-reference with newest version uploaded to notify user a newer version is available.
    const val APP_GRADLE_VERSION = 1
}