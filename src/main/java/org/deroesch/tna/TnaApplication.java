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

    public static void main(String[] args) throws IOException {

        ApplicationContext ctx = SpringApplication.run(TnaApplication.class, args);
        DayDB.initialize(ctx, DayDB.DB_FILE);

        List<Day> days = DayDB.getDayList();

        MovingAverage.computeAll(days, 5);
        MovingAverage.computeAll(days, 10);
        MovingAverage.computeAll(days, 20);
        MovingAverage.computeAll(days, 50);
        MovingAverage.computeAll(days, 100);

    }
}
