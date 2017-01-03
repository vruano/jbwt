package jbwt.index;

import jbwt.utils.ParamUtils;

/**
 * Created by valentin on 12/25/16.
 */
public class FMIndex<A extends Symbol> {

    private final BWTArray<A> bwt;

    private final long[] C;
    private final long[][] occurrences;
    private final long[] locations;
    private final int occurrencePeriod;
    private final int locationPeriod;

    private FMIndex(final BWTArray<A> bwt, final int occurrencePeriod, final int locationPeriod) {
        this.bwt = ParamUtils.requiresNonNull(bwt);
        this.occurrencePeriod = ParamUtils.requiresGreaterThanZero(occurrencePeriod);
        this.locationPeriod = ParamUtils.requiresGreaterThanZero(locationPeriod);
        this.C = bwt.getCounts();
        final long length = bwt.length();
        final int alphabetSize = bwt.getAlphabet().size();
        occurrences = new long[alphabetSize][(int) (length + occurrencePeriod + 1) / occurrencePeriod];
        locations = new long[(int) (length + locationPeriod + 1) / locationPeriod];
        fillOccurrenceAndLocationsArrays(occurrences, locations, bwt);
    }

    private void fillOccurrenceAndLocationsArrays(final long[][] occurrences, final long[] locations, final BWTArray<A> bwt) {

    }
}
