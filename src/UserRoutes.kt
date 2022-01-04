package com.nitin.todoAPI

import com.nitin.todoAPI.auth.JwtService
import com.nitin.todoAPI.auth.MySession
import com.nitin.todoAPI.repository.TodoRespository
import com.nitin.todoAPI.repository.UserRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

fun Route.userRoute(
    userdb: UserRepository,
    todoDb: TodoRespository,
    jwtService: JwtService,
    hash: (String) -> String){

    post("/v1/create") {
        val parameter = call.receive<Parameters>()

        val name = parameter["name"] ?: return@post call.respondText(
            "Missing Data",
            status = HttpStatusCode.Unauthorized)

        val email = parameter["email"] ?: return@post call.respondText(
            "Missing Data",
            status = HttpStatusCode.Unauthorized)

        val password = parameter["password"] ?: return@post call.respondText(
            "Missing Data",
            status = HttpStatusCode.Unauthorized)


        val hashpassword = hash(password)

        try {
            val curruntUser = userdb.createUser(name, email, hashpassword)
            curruntUser?.userId?.let {
                call.sessions.set(MySession(it))
                call.respondText (jwtService.generateToken(curruntUser),status = HttpStatusCode.Created)
            }

        }catch (e: Throwable){
            call.respondText("Problem in Creating user ${e.message}",status = HttpStatusCode.Unauthorized)
        }
    }


    post("/v1/login"){
        val parameter = call.receive<Parameters>()

        val email = parameter["email"] ?: return@post call.respondText(
            "Missing Data",
            status = HttpStatusCode.Unauthorized)

        val password = parameter["password"] ?: return@post call.respondText(
            "Missing Data",
            status = HttpStatusCode.Unauthorized)


        val hashpassword = hash(password)

        try {
            val currentUser = userdb.findUserByEmail(email)
            currentUser?.userId?.let {
                if (currentUser.password == hashpassword){
                    call.sessions.set(MySession(it))
                    call.respondText(jwtService.generateToken(currentUser),status = HttpStatusCode.OK)
                }
            }

        }catch (e: Throwable){
            call.respondText("Problem in Creating user ${e.message}",status = HttpStatusCode.Unauthorized)
        }


    }

    delete("/v1/user") {
        val user = call.sessions.get<MySession>()?.let {
            userdb.findUserById(it.userId)
        }

        if (user == null) {
            call.respondText("Problem getting user", status = HttpStatusCode.BadRequest)
        }

        try {
            user?.userId?.let {
                todoDb.deleteAllTodo(it)
            }
            val status = user?.userId?.let {
                userdb.deleteUser(it)
            }
            if (status == 1) {
                call.respondText("User Deleted", status = HttpStatusCode.OK)
            } else {
                call.respondText("Getting problem in delete", status = HttpStatusCode.OK)
            }

        } catch (e: Throwable) {
            call.respondText("Problem in Delete user", status = HttpStatusCode.Unauthorized)
        }
    }

    put("/v1/user") {
        val parameter = call.receive<Parameters>()

        val name = parameter["name"] ?: return@put call.respondText(
            "Missing Data",
            status = HttpStatusCode.Unauthorized)

        val email = parameter["email"] ?: return@put call.respondText(
            "Missing Data",
            status = HttpStatusCode.Unauthorized)

        val password = parameter["password"] ?: return@put call.respondText(
            "Missing Data",
            status = HttpStatusCode.Unauthorized)

        val user = call.sessions.get<MySession>()?.let {
            userdb.findUserById(it.userId)
        }

        val hashPassword = hash(password)

        try {
            val currentUser = user?.userId?.let {
                userdb.updateUser(it,name, email,hashPassword)
            }

            if (currentUser == 1){
                call.respondText("updated sucessfully..",status = HttpStatusCode.OK)
            }else{
                call.respondText("updated sucessfully..",status = HttpStatusCode.OK)
            }
        }catch (e: Throwable) {
            call.respondText("Problem in update user", status = HttpStatusCode.Unauthorized)
        }


    }

}
