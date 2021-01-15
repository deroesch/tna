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
 * A database of market days for ONE security.
 */
public class DayDB {

    /**
     * A linear list of days ordered by date.
     * 
     * @return the dayList
     */
    @NonNull
    public static List<Day> getDayList() {
        return dayList;
    }

    /**
     * An dictionary of days indexed by date.
     * 
     * @return the dayMap
     */
    @NonNull
    public static Map<Date, Day> getDayMap() {
        return dayMap;
    }

    /**
     * Empty the dayList to allow reloading.
     */
    public static void reset() {
        dayList.clear();
    }

    /**
     * Figure out the correct pathname for the input file, then load it.
     *
     * @param context  We need the context to figure out where to look for files.
     * @param location The target file path (relative).
     * @throws IOException
     */
    public static void initialize(@NonNull final ApplicationContext context, @NonNull final String location)
            throws IOException {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(location);

        // Only only once
        if (dayList.size() > 0)
            return;

        // Find the path
        Path path;
        final URI uri = context.getResource(String.format("classpath:%s", location)).getURI();
        if (uri.getScheme().equals("jar")) {
            try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap());) {
                path = fs.getPath("/BOOT-INF/classes/" + location);
            }
        } else
            path = Paths.get(uri);

        // Load and log
        loadFromSpreadsheet(path);
        if (VERBOSE)
            logger.info(String.format("Loaded %s records.", dayList.size()));

    }

    /**
     * Load the Day data from a disk file in xlsx format. I use Apache POI.
     *
     * @param path The target file path (relative).
     * @throws IOException
     */
    public static void loadFromSpreadsheet(@NonNull final Path path) throws IOException {
        Preconditions.checkNotNull(path);

        // Loop through each row in the spreadsheet
        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(path.toFile()))) {
            final XSSFSheet sheet = workbook.getSheetAt(0);
            final Iterator<Row> ri = sheet.iterator();

            // Skip header row, or fail if emtpy
            if (ri.hasNext())
                ri.next();
            else
                return;

            // Load row cells into a Day object and save it.
            while (ri.hasNext()) {
                final Row row = ri.next();
                final Iterator<Cell> ci = row.cellIterator();
                Cell c;

                // The day's calendar date
                c = ci.next();
                final Date date = c.getDateCellValue();

                // The day's opening price
                c = ci.next();
                final Double open = c.getNumericCellValue();

                // The day's high price
                c = ci.next();
                final Double high = c.getNumericCellValue();

                // The day's low price
                c = ci.next();
                final Double low = c.getNumericCellValue();

                // The day's closing price
                c = ci.next();
                final Double close = c.getNumericCellValue();

                // The day's adjusted closing price
                c = ci.next();
                final Double adjClose = c.getNumericCellValue();

                // The day's volume
                c = ci.next();
                final Long volume = (long) c.getNumericCellValue();

                // Create the day
                final Day day = new Day(date, open, high, low, close, adjClose, volume);

                // Save to ordered list
                dayList.add(day);

                // Save to map
                dayMap.put(date, day);

                if (VERBOSE)
                    logger.info(String.format("Added %s", day.toString()));
            }

            // dayList.add assembles these days in the wrong order. The data file is date
            // descending order. We want the other direction, with earliest date first..
            Lists.reverse(dayList);

        }
    }

    /*
     * Print all the Days while loading?
     */
    private static final boolean VERBOSE = false;

    /*
     * Data stored as a linear list.
     */
    private static final List<Day> dayList = new ArrayList<>();

    /*
     * Data stored as a map indexed by date.
     */
    private static final Map<Date, Day> dayMap = new HashMap<>();

    /*************************************************************
     * Miscellaneous
     */
    private static final Logger logger = LoggerFactory.getLogger(DayDB.class);

    public static final String DB_FILE = "data/tna.xlsx";

}
