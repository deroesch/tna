package org.deroesch.tna;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.deroesch.tna.algos.MovingAverage;
import org.deroesch.tna.db.DayDB;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 */
@SpringBootTest
class TnaApplicationTests {

    // The number of records we expect to find in the database
    private static final int EXPECTED_SIZE = 198;

    @BeforeAll
    static void boot() throws IOException {

        // Load things up.
        final String[] args = {};
        TnaApplication.main(args);
    }

    /**
     * Nothing of interest here yet.
     */
    @Test
    void contextLoads() {
    }

    /**
     * Load the database and check its size.
     * 
     * @throws IOException
     */
    @Test
    void testLoad() throws IOException {

        assertEquals(EXPECTED_SIZE, DayDB.getDayList().size());
        assertEquals(EXPECTED_SIZE, DayDB.getDayMap().size());
    }

    /**
     * @throws IOException
     * 
     */
    @Test
    void testMAs() throws IOException {

        MovingAverage.computeAll(DayDB.getDayList(), 5);

        assertNull(DayDB.getDay(0).getMovingAvg(5));
        assertTrue(DayDB.getDay(20).getMovingAvg(5) > 0);
    }

}
