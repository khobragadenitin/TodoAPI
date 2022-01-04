package com.nitin.todoAPI.repository

import com.nitin.todoAPI.dao.TodoDao
import com.nitin.todoAPI.data.Todo
import com.nitin.todoAPI.data.table.TodoTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.InsertStatement

class TodoRespository : TodoDao {

    override suspend fun createTodo(userId: Int, todoDat: String, done: Boolean): Todo? {
        var statement : InsertStatement<Number>? = null
        DatabaseFactory.dbQuery {
            statement = TodoTable.insert { todo ->
                todo[TodoTable.userId] = userId
                todo[TodoTable.done] = done
                todo[TodoTable.todo] = todoDat

            }
        }
        return rowToTodo(statement?.resultedValues?.get(0))

    }

    override suspend fun getAllTodo(userId: Int): List<Todo> =
        DatabaseFactory.dbQuery {
            TodoTable.select{
                TodoTable.userId.eq(userId)
            }
        }.mapNotNull {
            rowToTodo(it)
        }

    override suspend fun deleteTodo(id: Int): Int =TodoTable.deleteWhere {
        TodoTable.id.eq(id)
    }

    override suspend fun deleteAllTodo(userId: Int): Int =
        TodoTable.deleteWhere {
            TodoTable.userId.eq(userId)
        }

    override suspend fun updateTodo(id: Int, todoDat: String, done: Boolean): Int =
        TodoTable.update({TodoTable.id.eq(id)}) {todo ->
            todo[TodoTable.todo] = todoDat
            todo[TodoTable.done] = done
        }

    override suspend fun getTodo(int: Int): Todo? =
        DatabaseFactory.dbQuery {
            TodoTable.select { TodoTable.id.eq(int) }
                .map {
                    rowToTodo(it)
                }.singleOrNull()
        }

    private fun rowToTodo(row: ResultRow?): Todo?{
        if (row == null){
            return null
        }

        return Todo(
            id = row[TodoTable.id],
            userId = row[TodoTable.userId],
            todo = row[TodoTable.todo],
            done = row[TodoTable.done]
        )
    }
}