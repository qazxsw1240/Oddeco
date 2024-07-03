package org.hansung.oddeco.core.sql;

import java.sql.Connection;

public interface ConnectionHolder {
    public abstract Connection getConnection();
}
