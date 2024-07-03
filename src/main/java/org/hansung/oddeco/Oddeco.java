package org.hansung.oddeco;

import org.bukkit.plugin.java.JavaPlugin;
import org.hansung.oddeco.core.Plugin;
import org.hansung.oddeco.core.util.logging.FormattedLogger;
import org.hansung.oddeco.core.util.logging.PluginLoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

@Plugin
public final class Oddeco extends JavaPlugin {
    private static final String DB_URL = "jdbc:sqlite:plugins/Oddeco/database.db";

    private final FormattedLogger logger;
    private final AtomicReference<Connection> connection;

    public Oddeco() throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        this.logger = PluginLoggerFactory.getLogger(this);
        this.connection = new AtomicReference<>();
    }

    @Override
    public void onDisable() {
        try {
            Connection connection = this.connection.get();
            if (connection != null) {
                connection.close();
            }
            this.logger.info("DB Connection successfully closed");
            this.logger.info("Disabled Plugin [Oddeco]");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onEnable() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL);
            this.connection.set(connection);
            this.logger.info("DB Connection successfully established");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.logger.info("Enabled Plugin [Oddeco]");
    }
}
