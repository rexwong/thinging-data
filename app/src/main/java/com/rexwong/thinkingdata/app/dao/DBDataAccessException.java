package com.rexwong.thinkingdata.app.dao;

import org.springframework.dao.DataAccessException;

public class DBDataAccessException extends DataAccessException {

    public DBDataAccessException(String msg) {
        super(msg);
    }

    public DBDataAccessException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
