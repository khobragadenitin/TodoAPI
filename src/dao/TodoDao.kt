package com.nitin.todoAPI.dao

import com.nitin.todoAPI.data.Todo

interface TodoDao {

    suspend fun createTodo(
        userId :Int,
        todo:String,
        done:Boolean
    ):Todo?

    suspend fun getAllTodo(
        userId: Int,
    ):List<Todo>

    suspend fun deleteTodo(
        id:Int
    ):Int

    suspend fun deleteAllTodo(
        userId: Int
    ):Int

    suspend fun updateTodo(
        id:Int,
        todo: String,
        done: Boolean
    ):Int

    suspend fun getTodo(
        int: Int
    ):Todo?


}