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

    public UserDetailInfo findUserDetailInfo(String userid) {
        return null;
    }

    public UserDetailInfo loadUserDetailInfo(String userid) {
        return jdbcTemplate.queryForObject(String.format(
                "select countryCode,gender,qualityAuth from user_detail_info where uid=%s", userid),
                new BeanPropertyRowMapper<>(UserDetailInfo.class));
    }

    public String findUserFromRoom(String roomid) {
        return jdbcTemplate.queryForObject(
                String.format("select id from room_live where roomid=%s", roomid),
                Long.class).toString();
    }


}
