package org.deroesch.tna.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
class DayDBTest {

    private static final int EXPECTED_RECORD_COUNT = 198;

    @Autowired
    private ApplicationContext ctx;

    @BeforeEach
    void beforeEach() throws IOException {
        DayDB.initialize(ctx, DayDB.DB_FILE);
    }

    @Test
    void testGetDayList() {
        assertEquals(EXPECTED_RECORD_COUNT, DayDB.getDayList().size());
    }

    @Test
    void testGetDayByIndex() {
        assertNotNull(DayDB.getDay(0));
    }

    @Test
    void testGetDayMap() {
        assertEquals(EXPECTED_RECORD_COUNT, DayDB.getDayMap().size());
    }

    @Test
    void testGetDayByDate() {
        assertNotNull(DayDB.getDay(LocalDateTime.of(2021, 1, 12, 0, 0)));
    }

    @Test
    void testReset() throws IOException {
        DayDB.reset();
        assertEquals(0, DayDB.getDayList().size());
    }

}
