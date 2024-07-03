package org.hansung.oddeco.core.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultMapper<T> {
    public abstract T map(ResultSet resultSet) throws SQLException;
}
