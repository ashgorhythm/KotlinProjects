package ktor

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.json.Json


@OptIn(InternalAPI::class)
suspend fun main() {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json = Json {
                ignoreUnknownKeys = true
            })
        }
        defaultRequest {
            url("https://jsonplaceholder.typicode.com/")
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }
    }

//    val posts = client.get {
//        url {
//            path("posts")
//        }
//    }.body<List<Post>>()
//   val status = client.post {
//        url {
//            path("posts")
//        }
//        contentType(ContentType.Application.Json)
//        setBody(
//            Post(
//                id = 1,
//                title = "Title",
//                body = "Body",
//                userId = 101
//            )
//        )
//    }.status
//    println(status)
    val code =client.delete {
        url{
            path("posts/1")
        }
    }.status
    println(code)
}


