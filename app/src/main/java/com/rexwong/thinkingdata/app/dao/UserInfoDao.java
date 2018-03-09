package com.rexwong.thinkingdata.app.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * @author: yanglele
 * @date: 2018/3/1 13:13
 */
@Component
@Slf4j
public class UserInfoDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public String findUserFromRoom(String roomid) throws Exception{
        String sql = String.format("select id from room_live where roomid=%s", roomid);
        try{
            return jdbcTemplate.queryForObject(sql,Long.class).toString();
        }catch (Exception e){
            log.error("Bad Sql:{}",sql);
            throw new DBDataAccessException(sql,e);
        }
    }

}
