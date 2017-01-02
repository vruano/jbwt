package jbwt.intervals;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Genomic interval expressed in 1-base indexes.
 */
public class UserCoordinate {

    public static final NumberFormat POSITION_FORMAT = NumberFormat.getNumberInstance(Locale.US);
    public final int start;

    public final int stop;

    public final String contig;

    public UserCoordinate(final String str) {
        if (!str.contains(":")) {
            contig = str.trim();
            start = stop = -1;
        } else {
            final int contigSplitIndex = str.lastIndexOf(":");
            contig = str.substring(0, contigSplitIndex).trim();
            final String startStopString = str.substring(contigSplitIndex + 1).trim();
            try {
                if (startStopString.contains("-")) {
                    start = POSITION_FORMAT.parse(startStopString.substring(0, startStopString.indexOf('-')).trim()).intValue();
                    stop = POSITION_FORMAT.parse(startStopString.substring(startStopString.indexOf('-') + 1).trim()).intValue();
                } else {
                    stop = POSITION_FORMAT.parse(startStopString.trim()).intValue();
                    start = stop;
                }
            } catch (final Exception ex) {
                throw new IllegalArgumentException("invalid coordinate string: " + str);
            }
        }

    }

    public String toString() {
        if (start == -1) {
            return contig;
        } else if (start == stop) {
            return contig + ":" + POSITION_FORMAT.format(start);
        } else {
            return contig + ":" + POSITION_FORMAT.format(start) + "-" + POSITION_FORMAT.format(stop);
        }
    }
}
