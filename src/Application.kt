package com.nitin.todoAPI

import com.nitin.todoAPI.auth.JwtService
import com.nitin.todoAPI.auth.MySession
import com.nitin.todoAPI.repository.DatabaseFactory
import com.nitin.todoAPI.repository.TodoRespository
import com.nitin.todoAPI.repository.UserRepository
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.gson.*
import io.ktor.features.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    DatabaseFactory.init()

    val  userDb = UserRepository()
    val jwt = JwtService()
    val todoDb = TodoRespository();

    val hashFunction = {s:String -> hash(s)}

    install(Locations) {
    }

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    install(Authentication) {
        jwt("jwt"){
            verifier(jwt.verfier)
            realm = "Todo Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("userId")
                val claimInt = claim.asInt()
                val user = userDb.findUserById(claimInt)
                user
            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        userRoute(userDb,todoDb,jwt,hashFunction)
    }
}




