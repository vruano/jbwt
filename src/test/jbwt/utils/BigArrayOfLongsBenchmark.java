package jbwt.utils;

import com.google.caliper.Benchmark;
import com.google.caliper.api.VmOptions;
import com.google.caliper.runner.CaliperMain;

/**
 * Created by valentin on 2/20/17.
 */
@VmOptions({"-XX:-TieredCompilation"})
public class BigArrayOfLongsBenchmark {
    public static void main(String[] args) {
        CaliperMain.main( BigArrayOfLongsBenchmark.class, args);
    }

    private static int size = 10_000;

    @Benchmark
    private void bigArrayOfLongsSequentialSet(final int reps) {
        final BigArrayOfLongs longs = BigArrayOfLongs.newInstance(size);
        for (int i = 0; i < reps; i++)
            for (int j = 0; j < size; j++)
                longs.set(j, j);
    }

    @Benchmark
    private void arrayOfLongsSequentialSet(final int reps) {
        final long[] longs = new long[size];
        for (int i = 0; i < reps; i++)
            for (int j = 0; j < size; j++)
                longs[j] = j;
    }

}
