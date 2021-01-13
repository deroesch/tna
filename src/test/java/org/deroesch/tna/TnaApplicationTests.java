package org.deroesch.tna;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.deroesch.tna.algos.MovingAverage;
import org.deroesch.tna.db.DayDB;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 */
@SpringBootTest
class TnaApplicationTests {

    // The number of records we expect to find in the database
    private static final int EXPECTED_SIZE = 198;

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
    void run() throws IOException {

        // Load things up.
        final String[] args = {};
        TnaApplication.main(args);

        assertEquals(EXPECTED_SIZE, DayDB.getDayList().size());
        assertEquals(EXPECTED_SIZE, DayDB.getDayMap().size());
    }

    /**
     * 
     */
    @Test
    void computMA() {
        MovingAverage.computeAll(DayDB.getDayList(), 5);
    }

}
