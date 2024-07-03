package org.hansung.oddeco.core.util.logging;

public interface FormattedLogger {
    public abstract void info(String messageFormat, Object... args);

    public abstract void warn(String messageFormat, Object... args);

    public abstract void error(String messageFormat, Object... args);
}
