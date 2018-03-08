package com.rexwong.thinkingdata.app.dao;

import com.rexwong.thinkingdata.app.entity.UserDetailInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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

    public String findUserFromRoom(String roomid) {
        return jdbcTemplate.queryForObject(
                String.format("select id from room_live where roomid=%s", roomid),
                Long.class).toString();
    }


}
