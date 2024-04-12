package mx.tecnm.cdhidalgo.iotapp

import android.content.Context
import android.graphics.Bitmap
import android.util.LruCache
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley

// Crea una instancia de Volley para la aplicación y la mantiene viva durante toda la vida útil de la aplicación
// Esto se logra mediante el uso de un patrón Singleton
class MySingleton constructor(context: Context) {
    // El objeto companion es similar a una clase estática en Java
    companion object {
        // La instancia de MySingleton es volátil, lo que significa que los cambios realizados en una instancia de MySingleton se reflejarán en todas las demás instancias
        @Volatile
        private var INSTANCE: MySingleton? = null
        // La función getInstance devuelve la instancia de MySingleton
        fun getInstance(context: Context) =
            // Si INSTANCE no es nulo, se devuelve INSTANCE
            INSTANCE ?: synchronized(this) {
                // Si INSTANCE es nulo, se crea una nueva instancia de MySingleton
                INSTANCE ?: MySingleton(context).also {
                    /// Se asigna la nueva instancia a INSTANCE
                    INSTANCE = it
                }
            }
    }
    // ImageLoader es una clase de Volley que se utiliza para cargar imágenes de la red
    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(20)
                override fun getBitmap(url: String): Bitmap? {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }
    // RequestQueue es una cola de solicitudes de Volley que se utiliza para enviar solicitudes a la red
    val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(context.applicationContext)
    }
    // La función addToRequestQueue agrega una solicitud a la cola de solicitudes de Volley
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}