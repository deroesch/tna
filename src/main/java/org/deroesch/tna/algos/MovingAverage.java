package org.deroesch.tna.algos;

import java.util.List;

import org.deroesch.tna.db.DayDB;
import org.deroesch.tna.models.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MovingAverage {

    public static void computeAll(final List<Day> days, final Integer value) {

        Double sum = 0.0;
        Double avg = 0.0;
        Integer count = 0;

        for (final Day day : DayDB.getDayList()) {
            count++;

            // Add this day's value to the accumulator
            sum += day.getClose();

            if (count >= value) {
                avg = sum / value;

                // Save it!
                day.setMovingAvg(value, avg);

                if (VERBOSE) {
                    final String format = "Count: %s, Date: %s, Sum: %5.2f, Avg: %5.2f, Close: %5.2f";
                    logger.info(String.format(format, count, day.getDate(), sum, avg, day.getClose()));
                }

                // Now remove the LAST day's value ahead of the next loop
                sum -= DayDB.getDayList().get(count - value).getClose();
            }
        }

    }

    /*************************************************************
     * Miscellaneous
     */
    private static final Logger logger = LoggerFactory.getLogger(DayDB.class);

    /*
     * Print all the averages while computing?
     */
    private static final boolean VERBOSE = false;

}
