package com.cruisely.config;

import javax.annotation.sql.DataSourceDefinition;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;

@DataSourceDefinition(
        name = "java:app/jdbc/ssbd03adminDS",
        className = "org.postgresql.ds.PGSimpleDataSource",
        user = "ssbd03admin",
        password = "adminpasswd",
        serverName = "studdev.it.p.lodz.pl",
        portNumber = 5432,
        databaseName = "ssbd03",
        minPoolSize = 0,
        maxPoolSize = 50,
        maxIdleTime = 10,
        isolationLevel = Connection.TRANSACTION_READ_COMMITTED
)

@DataSourceDefinition(
        name = "java:app/jdbc/ssbd03glassfishDS",
        className = "org.postgresql.ds.PGSimpleDataSource",
        user = "ssbd03glassfish",
        password = "glassfishpasswd",
        serverName = "studdev.it.p.lodz.pl",
        portNumber = 5432,
        databaseName = "ssbd03",
        isolationLevel = Connection.TRANSACTION_READ_COMMITTED,
        minPoolSize = 0,
        maxPoolSize = 50,
        maxIdleTime = 10
)

@DataSourceDefinition(
        name = "java:app/jdbc/ssbd03mokDS",
        className = "org.postgresql.ds.PGSimpleDataSource",
        user = "ssbd03mok",
        password = "mokpasswd",
        serverName = "studdev.it.p.lodz.pl",
        portNumber = 5432,
        databaseName = "ssbd03",
        isolationLevel = Connection.TRANSACTION_READ_COMMITTED,
        minPoolSize = 0,
        maxPoolSize = 50,
        maxIdleTime = 10
)

@DataSourceDefinition(
        name = "java:app/jdbc/ssbd03mowDS",
        className = "org.postgresql.ds.PGSimpleDataSource",
        user = "ssbd03mow",
        password = "mowpasswd",
        serverName = "studdev.it.p.lodz.pl",
        portNumber = 5432,
        databaseName = "ssbd03",
        isolationLevel = Connection.TRANSACTION_READ_COMMITTED,
        minPoolSize = 0,
        maxPoolSize = 50,
        maxIdleTime = 10
)

public class JDBCConfig {
    @PersistenceContext(unitName = "ssbd03adminPU")
    private EntityManager em;
}
