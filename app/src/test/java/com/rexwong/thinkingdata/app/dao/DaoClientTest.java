package com.rexwong.thinkingdata.app.dao;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-dao.xml")
@Ignore
public class DaoClientTest {
    @Resource
    private DaoClient daoClient;
    @Test
    public void httpTest(){
        daoClient.httpClient("MSL");
    }
}
