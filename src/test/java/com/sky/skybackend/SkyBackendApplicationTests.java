package com.sky.skybackend;

import com.sky.skybackend.schedul.HotRank;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class SkyBackendApplicationTests {
    @Test
    void contextLoads() {
        HotRank hotRank = new HotRank();
        hotRank.hotRank();
    }

}
