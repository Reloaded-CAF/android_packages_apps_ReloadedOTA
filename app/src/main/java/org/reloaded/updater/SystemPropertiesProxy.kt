package org.reloaded.updater

import android.annotation.SuppressLint
import android.content.Context

object SystemPropertiesProxy {

    /**
     * Get the value for the given key.
     * @return an empty string if the key isn't found
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    operator fun get(context: Context, key: String): String {

        try {

            val cl = context.classLoader
            val systemProperties = cl.loadClass("android.os.systemProperties")

            //Parameters Types
            val paramTypes = arrayOfNulls<Class<*>>(1)
            paramTypes[0] = String::class.java

            val get = systemProperties.getMethod("get", *paramTypes)

            //Parameters
            val params = arrayOfNulls<Any>(1)
            params[0] = key

            return get.invoke(systemProperties, *params) as String

        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""

    }

    /**
     * Get the value for the given key.
     * @return if the key isn't found, return def if it isn't null, or an empty string otherwise
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    operator fun get(context: Context, key: String, def: String): String {

        try {

            val cl = context.classLoader
            val systemProperties = cl.loadClass("android.os.systemProperties")

            //Parameters Types
            val paramTypes = arrayOfNulls<Class<*>>(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = String::class.java

            val get = systemProperties.getMethod("get", *paramTypes)

            //Parameters
            val params = arrayOfNulls<Any>(2)
            params[0] = key
            params[1] = def

            return get.invoke(systemProperties, *params) as String

        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return def
    }

    /**
     * Get the value for the given key, and return as an integer.
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as an integer, or def if the key isn't found or
     * cannot be parsed
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    fun getInt(context: Context, key: String, def: Int): Int? {

        try {

            val cl = context.classLoader
            val systemProperties = cl.loadClass("android.os.systemProperties")

            //Parameters Types
            val paramTypes = arrayOfNulls<Class<*>>(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = Int::class.javaPrimitiveType

            val getInt = systemProperties.getMethod("getInt", *paramTypes)

            //Parameters
            val params = arrayOfNulls<Any>(2)
            params[0] = key
            params[1] = def

            return getInt.invoke(systemProperties, *params) as Int

        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return def

    }

    /**
     * Get the value for the given key, and return as a long.
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a long, or def if the key isn't found or
     * cannot be parsed
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    fun getLong(context: Context, key: String, def: Long): Long? {

        try {

            val cl = context.classLoader
            val systemProperties = cl.loadClass("android.os.systemProperties")

            //Parameters Types
            val paramTypes = arrayOfNulls<Class<*>>(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = Long::class.javaPrimitiveType

            val getLong = systemProperties.getMethod("getLong", *paramTypes)

            //Parameters
            val params = arrayOfNulls<Any>(2)
            params[0] = key
            params[1] = def

            return getLong.invoke(systemProperties, *params) as Long

        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return def

    }

    /**
     * Get the value for the given key, returned as a boolean.
     * Values 'n', 'no', '0', 'false' or 'off' are considered false.
     * Values 'y', 'yes', '1', 'true' or 'on' are considered true.
     * (case insensitive).
     * If the key does not exist, or has any other value, then the default
     * result is returned.
     * @param key the key to lookup
     * @param def a default value to return
     * @return the key parsed as a boolean, or def if the key isn't found or is
     * not able to be parsed as a boolean.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     */
    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    fun getBoolean(context: Context, key: String, def: Boolean): Boolean? {

        try {

            val cl = context.classLoader
            val systemProperties = cl.loadClass("android.os.systemProperties")

            //Parameters Types
            val paramTypes = arrayOfNulls<Class<*>>(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = Boolean::class.javaPrimitiveType

            val getBoolean = systemProperties.getMethod("getBoolean", *paramTypes)

            //Parameters
            val params = arrayOfNulls<Any>(2)
            params[0] = key
            params[1] = def

            return getBoolean.invoke(systemProperties, *params) as Boolean

        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return def

    }

    /**
     * Set the value for the given key.
     * @throws IllegalArgumentException if the key exceeds 32 characters
     * @throws IllegalArgumentException if the value exceeds 92 characters
     */
    @SuppressLint("PrivateApi")
    @Throws(IllegalArgumentException::class)
    operator fun set(key: String, `val`: String) {

        try {

            val systemProperties = Class.forName("android.os.systemProperties")

            //Parameters Types
            val paramTypes = arrayOfNulls<Class<*>>(2)
            paramTypes[0] = String::class.java
            paramTypes[1] = String::class.java

            val set = systemProperties.getMethod("set", *paramTypes)

            //Parameters
            val params = arrayOfNulls<Any>(2)
            params[0] = key
            params[1] = `val`

            set.invoke(systemProperties, *params)

        } catch (iAE: IllegalArgumentException) {
            throw iAE
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
}