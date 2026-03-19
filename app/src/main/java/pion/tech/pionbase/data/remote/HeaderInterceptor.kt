package pion.tech.pionbase.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor to add common headers to all requests
 * Currently adds "Accept: application/json" header
 */
class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain
            .request()
            .newBuilder()
            .header("Accept", "application/json")
            .build()
            .let(chain::proceed)
}
