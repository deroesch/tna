package org.deroesch.tna;

import java.io.IOException;
import java.util.List;

import org.deroesch.tna.algos.MovingAverage;
import org.deroesch.tna.db.DayDB;
import org.deroesch.tna.models.Day;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class TnaApplication {

    /**
     * Main entry point
     *
     * @param args
     * @throws IOException
     */
    public static void main(final String[] args) throws IOException {
        final ApplicationContext ctx = SpringApplication.run(TnaApplication.class, args);

        // Load the data from disk
        DayDB.initialize(ctx, DayDB.DB_FILE);
        final List<Day> days = DayDB.getDayList();

        // Compute these moving averages and attach the values to each Day object.
        MovingAverage.computeAll(days, 5);
        MovingAverage.computeAll(days, 10);
        MovingAverage.computeAll(days, 20);
        MovingAverage.computeAll(days, 50);
        MovingAverage.computeAll(days, 100);

    }
}
