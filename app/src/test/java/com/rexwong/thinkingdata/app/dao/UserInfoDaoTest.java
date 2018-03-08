package com.rexwong.thinkingdata.app.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-dao.xml")
public class UserInfoDaoTest {

    @Resource
    private UserInfoDao userInfoDao;

    @Test
    public void findUserFromRoomTest() {
        userInfoDao.findUserFromRoom("111");
    }
}
