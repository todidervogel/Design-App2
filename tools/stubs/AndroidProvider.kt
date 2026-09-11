package android.provider

import android.content.ContentResolver

/** Stub. Nur fuer die lokale Typpruefung. */
object Settings {
    object Global {
        const val ANIMATOR_DURATION_SCALE: String = "animator_duration_scale"

        @JvmStatic
        fun getFloat(resolver: ContentResolver, name: String, def: Float): Float = def
    }
}
