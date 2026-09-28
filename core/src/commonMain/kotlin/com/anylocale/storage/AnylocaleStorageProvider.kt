package com.anylocale.storage

interface AnylocaleStorageProvider {
    fun put(name: String, data: ByteArray)
    fun get(name: String): ByteArray?
}