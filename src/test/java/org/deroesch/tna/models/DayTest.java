package org.deroesch.tna.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DayTest {

    Day day;
    LocalDateTime now;

    @BeforeEach
    void beforeAll() {
        now = LocalDateTime.of(2020, 12, 12, 0, 0, 0);
        day = new Day(now, 2.0, 3.0, 4.0, 5.0, 6.0, 7L);
        day.setMovingAvg(10, 10.0);
    }

    @Test
    void testHashCode() {
        assertEquals(day.hashCode(), new Day(now, 2.0, 3.0, 4.0, 5.0, 6.0, 7L).hashCode());
        assertNotEquals(day.hashCode(), new Day(now, 3.0, 3.0, 4.0, 5.0, 6.0, 7L).hashCode());
    }

    @Test
    void testDayConstructor() {
        assertEquals(day, new Day(now, 2.0, 3.0, 4.0, 5.0, 6.0, 7L));
    }

    @Test
    void testGetDate() {
        assertEquals(now, day.getDate());
    }

    @Test
    void testGetHigh() {
        assertEquals(2.0, day.getHigh());
    }

    @Test
    void testGetLow() {
        assertEquals(3.0, day.getLow());
    }

    @Test
    void testGetOpen() {
        assertEquals(4.0, day.getOpen());
    }

    @Test
    void testGetClose() {
        assertEquals(6.0, day.getClose());
    }

    @Test
    void testGetAdjClose() {
        assertEquals(6.0, day.getAdjClose());
    }

    @Test
    void testGetMovingAvg() {
        assertEquals(10, day.getMovingAvg(10));
        assertThrows(IllegalArgumentException.class, () -> day.getMovingAvg(0));
    }

    @Test
    void testSetMovingAvg() {
        assertFalse(day.hasMovingAvg(15));
        day.setMovingAvg(15, 15.0);
        assertEquals(15, day.getMovingAvg(15));
        assertThrows(IllegalArgumentException.class, () -> day.setMovingAvg(0, 15.0));
    }

    @Test
    void testHasMovingAvg() {
        assertTrue(day.hasMovingAvg(10));
        assertFalse(day.hasMovingAvg(20));
        assertThrows(IllegalArgumentException.class, () -> day.hasMovingAvg(0));
    }

    @Test
    void testGetVolume() {
        assertEquals(7L, day.getVolume());
    }

    @Test
    void testEqualsObject() {
        assertEquals(day, new Day(now, 2.0, 3.0, 4.0, 5.0, 6.0, 7L));
        assertEquals(day, day);
        assertNotEquals(day, new Object());
    }

    @Test
    void testToString() {
        assertEquals("Day [date=2020-12-12T00:00, high=2.0, low=3.0, open=4.0, close=5.0, adjClose=6.0, volume=7]",
                day.toString());
    }

}
