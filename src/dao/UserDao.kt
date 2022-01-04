package com.nitin.todoAPI.dao

import com.nitin.todoAPI.data.User

interface UserDao {

    suspend fun createUser(
        name: String,
        email: String,
        password : String
    ):User?

    suspend fun findUserById(
        userId:Int,
    ):User?

    suspend fun deleteUser(
        userId: Int
    ):Int

    suspend fun findUserByEmail(
        email: String
    ): User?

    suspend fun updateUser(
        userId: Int,
        name: String,
        email: String,
        password: String
    ):Int




}