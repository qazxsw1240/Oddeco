package org.hansung.oddeco.core.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStatementExecutor implements ConnectionHolder, StatementExecutor {
    protected final Connection connection;

    protected AbstractStatementExecutor(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Connection getConnection() {
        return this.connection;
    }

    @Override
    public boolean execute(String sql) {
        try (Statement s = this.connection.createStatement()) {
            return s.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> List<T> executeQuery(String sql, ResultMapper<T> mapper) {
        try (Statement statement = this.connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery(sql)) {
                List<T> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(mapper.map(resultSet));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int[] executeBatch(String... sql) {
        try {
            boolean autoCommit = this.connection.getAutoCommit();
            this.connection.setAutoCommit(false);
            int[] results;
            try (Statement statement = this.connection.createStatement()) {
                for (String s : sql) {
                    statement.addBatch(s);
                }
                results = statement.executeBatch();
            }
            this.connection.setAutoCommit(autoCommit);
            return results;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
