package com.darekbx.carscrap.repository.remote.scrap

import okhttp3.Request

object Common {

    val url = "https://www.otomoto.pl/graphql"

    val headers = mapOf(
        "User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:132.0) Gecko/20100101 Firefox/132.0",
        "Accept" to "application/graphql-response+json, application/graphql+json, application/json, text/event-stream, multipart/mixed",
        "Accept-Language" to "en-US,en;q=0.5",
        "content-type" to "application/json"
    )

    fun Request.Builder.addHeaders() {
        headers.forEach { (key, value) -> addHeader(key, value) }
    }
}
