package org.deroesch.tna.algos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.deroesch.tna.db.DayDB;
import org.deroesch.tna.models.Day;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MovingAverageTest {

    @BeforeEach
    void setUp() {
        // Reset the database before each test
        DayDB.reset();
    }

    @Test
    void testComputeAllWithNullDays() {
        assertThrows(NullPointerException.class, () -> {
            MovingAverage.computeAll(null, 5);
        });
    }

    @Test
    void testComputeAllWithNullValue() {
        List<Day> days = new ArrayList<>();
        assertThrows(NullPointerException.class, () -> {
            MovingAverage.computeAll(days, null);
        });
    }

    @Test
    void testComputeAllWithZeroValue() {
        List<Day> days = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            MovingAverage.computeAll(days, 0);
        });
    }

    @Test
    void testComputeAllWithNegativeValue() {
        List<Day> days = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, () -> {
            MovingAverage.computeAll(days, -1);
        });
    }

    @Test
    void testComputeAllWithEmptyDatabase() {
        // DayDB is already reset (empty)
        List<Day> days = new ArrayList<>();
        
        // Should not throw exception with empty database
        MovingAverage.computeAll(days, 5);
        
        // Verify database is still empty
        assertEquals(0, DayDB.getDayList().size());
    }

    @Test
    void testComputeAllWithInsufficientData() {
        // Add only 3 days to DayDB but request 5-day moving average
        addTestDaysToDatabase(3);
        
        List<Day> days = new ArrayList<>();
        MovingAverage.computeAll(days, 5);
        
        // All days should have no moving average since we don't have enough data
        for (Day day : DayDB.getDayList()) {
            assertNull(day.getMovingAvg(5));
        }
    }

    @Test
    void testComputeAllWithExactData() {
        // Add exactly 5 days and request 5-day moving average
        addTestDaysToDatabase(5);
        
        List<Day> days = new ArrayList<>();
        MovingAverage.computeAll(days, 5);
        
        // First 4 days should have no moving average
        for (int i = 0; i < 4; i++) {
            assertNull(DayDB.getDay(i).getMovingAvg(5));
        }
        
        // 5th day should have moving average of (10+20+30+40+50)/5 = 30.0
        assertEquals(30.0, DayDB.getDay(4).getMovingAvg(5), 0.001);
    }

    @Test
    void testComputeAllWithSufficientData() {
        // Add 6 days and request 3-day moving average
        addTestDaysToDatabase(6);
        
        List<Day> days = new ArrayList<>();
        MovingAverage.computeAll(days, 3);
        
        // First 2 days should have no moving average
        assertNull(DayDB.getDay(0).getMovingAvg(3));
        assertNull(DayDB.getDay(1).getMovingAvg(3));
        
        // 3rd day: (10+20+30)/3 = 20.0
        assertEquals(20.0, DayDB.getDay(2).getMovingAvg(3), 0.001);
        
        // 4th day: (20+30+40)/3 = 30.0  
        assertEquals(30.0, DayDB.getDay(3).getMovingAvg(3), 0.001);
        
        // 5th day: (30+40+50)/3 = 40.0
        assertEquals(40.0, DayDB.getDay(4).getMovingAvg(3), 0.001);
        
        // 6th day: (40+50+60)/3 = 50.0
        assertEquals(50.0, DayDB.getDay(5).getMovingAvg(3), 0.001);
    }

    @Test
    void testComputeAllWithSinglePeriod() {
        // Test with 1-day moving average (should equal closing price)
        addTestDaysToDatabase(3);
        
        List<Day> days = new ArrayList<>();
        MovingAverage.computeAll(days, 1);
        
        // Each day's 1-day moving average should equal its closing price
        assertEquals(10.0, DayDB.getDay(0).getMovingAvg(1), 0.001);
        assertEquals(20.0, DayDB.getDay(1).getMovingAvg(1), 0.001);
        assertEquals(30.0, DayDB.getDay(2).getMovingAvg(1), 0.001);
    }

    @Test
    void testComputeAllWithDifferentPeriods() {
        // Test that different moving average periods can coexist
        addTestDaysToDatabase(5);
        
        List<Day> days = new ArrayList<>();
        
        // Compute 3-day moving average
        MovingAverage.computeAll(days, 3);
        
        // Compute 5-day moving average  
        MovingAverage.computeAll(days, 5);
        
        // Verify both moving averages exist on the same day
        Day fifthDay = DayDB.getDay(4);
        assertEquals(40.0, fifthDay.getMovingAvg(3), 0.001); // (30+40+50)/3
        assertEquals(30.0, fifthDay.getMovingAvg(5), 0.001); // (10+20+30+40+50)/5
    }

    @Test
    void testComputeAllWithRealWorldScenario() {
        // Test with more realistic data
        DayDB.reset();
        
        // Create test data with varying prices
        double[] prices = {100.0, 102.5, 98.0, 105.0, 110.0, 108.0, 112.0};
        
        for (int i = 0; i < prices.length; i++) {
            LocalDateTime date = LocalDateTime.of(2021, 1, i + 1, 0, 0);
            Day day = new Day(date, prices[i] + 2.0, prices[i] - 2.0, prices[i], prices[i], prices[i], 1000L);
            
            // Add directly to database (simulating what DayDB.loadFromSpreadsheet would do)
            DayDB.getDayList().add(day);
            DayDB.getDayMap().put(date, day);
        }
        
        List<Day> days = new ArrayList<>();
        MovingAverage.computeAll(days, 3);
        
        // Manual calculation for verification:
        // Day 3: (100.0 + 102.5 + 98.0) / 3 = 100.1667
        assertEquals(100.1667, DayDB.getDay(2).getMovingAvg(3), 0.001);
        
        // Day 4: (102.5 + 98.0 + 105.0) / 3 = 101.8333
        assertEquals(101.8333, DayDB.getDay(3).getMovingAvg(3), 0.001);
    }

    private void addTestDaysToDatabase(int numDays) {
        for (int i = 1; i <= numDays; i++) {
            LocalDateTime date = LocalDateTime.of(2021, 1, i, 0, 0);
            double close = i * 10.0;
            Day day = new Day(date, close + 5.0, close - 5.0, close - 2.0, close, close, 1000L);
            
            // Add directly to database lists (simulating what DayDB.loadFromSpreadsheet would do)
            DayDB.getDayList().add(day);
            DayDB.getDayMap().put(date, day);
        }
    }
}