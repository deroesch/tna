package org.deroesch.tna.db;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.deroesch.tna.models.Day;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * 
 */
public class DayDB {

    /**
     * @return the dayList
     */
    @NonNull
    public static List<Day> getDayList() {
        return dayList;
    }

    /**
     * @return the dayMap
     */
    @NonNull
    public static Map<Date, Day> getDayMap() {
        return dayMap;
    }

    /**
     * Figure out the correct pathname for the input file, then load it.
     * 
     * @param context
     * @param location
     * @throws IOException
     */
    public static void initialize(@NonNull final ApplicationContext context, @NonNull final String location)
            throws IOException {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(location);

        // Only run once
        if (dayList.size() > 0)
            return;

        URI uri = context.getResource(String.format("classpath:%s", location)).getURI();
        if (uri.getScheme().equals("jar")) {

            try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap());) {
                load(fs.getPath("/BOOT-INF/classes/" + location));
            }
        } else
            load(Paths.get(uri));

    }

    /**
     * Load the Day data from a disk file in xlsx format
     * 
     * @param path
     * @throws IOException
     */
    public static void load(@NonNull final Path path) throws IOException {
        Preconditions.checkNotNull(path);

        FileInputStream fis = new FileInputStream(path.toFile());
        try (XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            XSSFSheet sheet = workbook.getSheetAt(0);
            Iterator<Row> ri = sheet.iterator();
            Cell c;

            // Skip header row, or fail if emtpy
            if (ri.hasNext())
                ri.next();
            else
                return;

            // Load row cells into a Day object and save it.
            while (ri.hasNext()) {
                Row row = ri.next();
                Iterator<Cell> ci = row.cellIterator();

                c = ci.next();
                Date date = c.getDateCellValue();

                c = ci.next();
                Double open = c.getNumericCellValue();

                c = ci.next();
                Double high = c.getNumericCellValue();

                c = ci.next();
                Double low = c.getNumericCellValue();

                c = ci.next();
                Double close = c.getNumericCellValue();

                c = ci.next();
                Double adjClose = c.getNumericCellValue();

                c = ci.next();
                Long volume = (long) c.getNumericCellValue();

                // Create the day
                Day day = new Day(date, open, high, low, close, adjClose, volume);

                // Save to linear list
                dayList.add(day);

                // Save to map
                dayMap.put(date, day);

                if (VERBOSE)
                    logger.info(String.format("Added %s", day.toString()));
            }

            // dayList.add assembles these in the wrong order.
            Lists.reverse(dayList);

        }
    }

    /*
     * Print all the Days while loading?
     */
    private static final boolean VERBOSE = false;

    /*
     * Data as a linear list.
     */
    private static final List<Day> dayList = new ArrayList<>();

    /*
     * Data as a map indexed by date.
     */
    private static final Map<Date, Day> dayMap = new HashMap<>();

    /*************************************************************
     * Miscellaneous
     */
    private static final Logger logger = LoggerFactory.getLogger(DayDB.class);

    public static final String DB_FILE = "data/tna.xlsx";

}
