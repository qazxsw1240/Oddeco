package org.hansung.oddeco.core.sql;

import java.util.List;
import java.util.Optional;

public interface StatementExecutor {
    public abstract boolean execute(String sql);

    public abstract <T> Optional<T> executeSingleQuery(String sql, ResultMapper<T> mapper);

    public abstract <T> List<T> executeQuery(String sql, ResultMapper<T> mapper);

    public abstract int[] executeBatch(String... sql);
}
