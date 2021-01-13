package org.deroesch.tna.algos;

import java.util.List;

import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.deroesch.tna.db.DayDB;
import org.deroesch.tna.models.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * Moving average math.
 */
public class MovingAverage {

    /**
     * Computes and saves moving averages for each day in the days list.
     *
     * @param days  The subject days
     * @param value The moving average value to find (i.e., 5, 10, 20, 50, 100)
     */
    public static void computeAll(@NonNull final List<Day> days, @Positive final Integer value) {
        Preconditions.checkNotNull(days);
        Preconditions.checkNotNull(value);
        Preconditions.checkArgument(value > 0);

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
