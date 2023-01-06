package com.itmo.highload.userservice.jdbc

import com.itmo.highload.userservice.model.Role
import com.itmo.highload.userservice.model.Status
import com.itmo.highload.userservice.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class UserTemplate (@Autowired val jdbcTemplate: JdbcTemplate) {

    fun findAll(): List<User>? {
        val sql = "SELECT * FROM users JOIN user_roles ON user_roles.user_id = users.id"
        return jdbcTemplate.query(
            sql
        ) { rs, rowNum ->
            User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                Role.valueOf(rs.getString("roles")),
                Status.valueOf(rs.getString("status"))
            )
        }
    }

    fun findById(id: Long): User? {
        val sql = "SELECT * FROM users" +
                "JOIN user_roles ON user_roles.user_id = users.id " +
                "WHERE id = ?"
        return jdbcTemplate.queryForObject(sql, arrayOf<Any>(id)) { rs, rowNum ->
            User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                Role.valueOf(rs.getString("roles")),
                Status.valueOf(rs.getString("status"))
            )
        }
    }

    fun findByEmail(email: String): User? {
        val sql = "SELECT * FROM users " +
                "JOIN user_roles ON user_roles.user_id = users.id " +
                "WHERE email = ?"
        return jdbcTemplate.queryForObject(sql, arrayOf<Any>(email)) { rs, rowNum ->
            User(
                rs.getLong("id"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                Role.valueOf(rs.getString("roles")),
                Status.valueOf(rs.getString("status"))
            )
        }
    }

    fun insert(user: User) {
        val sql_user = ("INSERT INTO users (id, email, password, first_name, last_name, status) "
                + "VALUES (?, ?, ?, ?, ?, ?);")
        val sql_role = ("INSERT INTO user_roles (user_id, roles) "
                + "VALUES (?, ?);")
        jdbcTemplate.update(
            sql_user,
                user.id,
                user.email,
                user.password,
                user.firstName,
                user.lastName,
                user.status.toString()
        )
        jdbcTemplate.update(
            sql_role,
            user.id,
            user.role.toString()
        )
    }

    fun deleteById(id: Long) {
        val sql = ("DELETE FROM users " +
                "WHERE id = ?")
        jdbcTemplate.update(
            sql,
            id
        )
    }

}
