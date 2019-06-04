package com.example.wing;


import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
//@Transactional
//@Rollback
@ActiveProfiles(profiles = "local")
/**
 * 单元测试继承该类即可
 * @author dell
 */
public abstract class AbstractTest {

    @Autowired
    private WebApplicationContext wac;

    protected MockMvc mockMvc;


    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

}



