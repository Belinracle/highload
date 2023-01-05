package com.example.fileservice.config

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration
import io.r2dbc.postgresql.PostgresqlConnectionFactory
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories


@Configuration
@EnableR2dbcRepositories
class R2DBCConfig : AbstractR2dbcConfiguration() {
    @Value("\${spring.r2dbc.database:file-service}")
    lateinit var postgresDatabase: String

    @Value("\${spring.r2dbc.host:localhost}")
    lateinit var postgresHost: String

    @Value("\${spring.r2dbc.port:5433}")
    var postgresPort: Int = 0

    @Value("\${spring.r2dbc.username}")
    lateinit var postgresUsername: String

    @Value("\${spring.r2dbc.password}")
    lateinit var postgresPassword: String

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        return PostgresqlConnectionFactory(
            PostgresqlConnectionConfiguration.builder()
                .host(postgresHost)
                .port(postgresPort)
                .username(postgresUsername)
                .password(postgresPassword)
                .database(postgresDatabase)
                .build()
        )
    }
}