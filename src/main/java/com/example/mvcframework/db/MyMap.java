package com.example.mvcframework.db;

import com.example.mvcframework.spring.annotation.Autowired;
import com.example.mvcframework.spring.annotation.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@NoArgsConstructor
public class MyMap {
    @Getter
    @Setter
    private boolean isDevMode;
    @Autowired
    private ConnectionPool connectionPool;

    public SecSql genSecSql() {
        return new SecSql(connectionPool, isDevMode);
    }

    public void run(String sql, Object... bindingDatum) {
        genSecSql().append(sql, bindingDatum).update();
    }

    public void closeConnection() {
        connectionPool.closeConnection();
    }
}
