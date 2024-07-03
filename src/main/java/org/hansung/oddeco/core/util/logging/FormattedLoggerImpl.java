package org.hansung.oddeco.core.util.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

class FormattedLoggerImpl implements FormattedLogger {
    private final Logger logger;

    FormattedLoggerImpl(Logger logger) {
        if (logger == null) {
            throw new IllegalArgumentException("logger is null");
        }
        this.logger = logger;
    }

    @Override
    public void info(String messageFormat, Object... args) {
        String message = String.format(messageFormat, args);
        this.logger.log(Level.INFO, message);
    }

    @Override
    public void warn(String messageFormat, Object... args) {
        String message = String.format(messageFormat, args);
        this.logger.log(Level.WARNING, message);
    }

    @Override
    public void error(String messageFormat, Object... args) {
        String message = String.format(messageFormat, args);
        this.logger.log(Level.SEVERE, message);
    }
}
