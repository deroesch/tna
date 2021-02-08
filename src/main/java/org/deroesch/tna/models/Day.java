package org.deroesch.tna.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.checkerframework.checker.index.qual.Positive;
import org.checkerframework.checker.nullness.qual.NonNull;

import com.google.common.base.Preconditions;

/**
 * A day in the market.
 */
public class Day {

    /**
     * Field constructor
     *
     * @param date
     * @param high
     * @param low
     * @param open
     * @param close
     * @param adjClose
     * @param volume
     */
    public Day(@NonNull final LocalDateTime date, @NonNull final Double high, @NonNull final Double low,
            @NonNull final Double open, @NonNull final Double close, @NonNull final Double adjClose,
            @NonNull final Long volume) {

        Preconditions.checkNotNull(date);
        Preconditions.checkNotNull(high);
        Preconditions.checkNotNull(low);
        Preconditions.checkNotNull(open);
        Preconditions.checkNotNull(close);
        Preconditions.checkNotNull(adjClose);
        Preconditions.checkNotNull(volume);

        this.date = date;
        this.high = high;
        this.low = low;
        this.open = open;
        this.close = close;
        this.adjClose = adjClose;
        this.volume = volume;
    }

    /**
     * @return the date
     */
    @NonNull
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * @return the high
     */
    @NonNull
    public Double getHigh() {
        return high;
    }

    /**
     * @return the low
     */
    @NonNull
    public Double getLow() {
        return low;
    }

    /**
     * @return the open
     */
    @NonNull
    public Double getOpen() {
        return open;
    }

    /**
     * @return the adjClose
     */
    @NonNull
    public Double getClose() {
        return getAdjClose();
    }

    /**
     * @return the adjClose
     */
    @NonNull
    public Double getAdjClose() {
        return adjClose;
    }

    /**
     *
     * @param period
     * @return Might return null
     */
    public Double getMovingAvg(@Positive final Integer period) {
        Preconditions.checkNotNull(period);
        Preconditions.checkArgument(period > 0);
        return movingAvgs.get(period);
    }

    /**
     *
     * @param period
     * @param value
     */
    public void setMovingAvg(@Positive final Integer period, @NonNull final Double value) {
        Preconditions.checkNotNull(period);
        Preconditions.checkNotNull(value);
        Preconditions.checkArgument(period > 0);

        movingAvgs.put(period, value);
    }

    /**
     *
     * @param period
     * @return
     */
    public boolean hasMovingAvg(@Positive final Integer period) {
        Preconditions.checkNotNull(period);
        Preconditions.checkArgument(period > 0);
        return null != getMovingAvg(period);
    }

    /**
     * @return the volume
     */
    @NonNull
    public Long getVolume() {
        return volume;
    }

    @Override
    public int hashCode() {
        return Objects.hash(adjClose, close, date, high, low, open, volume);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Day)) {
            return false;
        }
        final Day other = (Day) obj;
        return Objects.equals(adjClose, other.adjClose) && Objects.equals(close, other.close)
                && Objects.equals(date, other.date) && Objects.equals(high, other.high)
                && Objects.equals(low, other.low) && Objects.equals(open, other.open)
                && Objects.equals(volume, other.volume);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Day [date=");
        builder.append(date);
        builder.append(", high=");
        builder.append(high);
        builder.append(", low=");
        builder.append(low);
        builder.append(", open=");
        builder.append(open);
        builder.append(", close=");
        builder.append(close);
        builder.append(", adjClose=");
        builder.append(adjClose);
        builder.append(", volume=");
        builder.append(volume);
        builder.append("]");
        return builder.toString();
    }

    /*
     * Fields
     */
    private LocalDateTime date;
    private Double high;
    private Double low;
    private Double open;
    private Double close;
    private Double adjClose;
    private Long volume;

    /*
     * A map of moving averages computer for this Day. Key is the period, value is
     * the average for that period.
     */
    private final Map<Integer, Double> movingAvgs = new HashMap<>();

}
