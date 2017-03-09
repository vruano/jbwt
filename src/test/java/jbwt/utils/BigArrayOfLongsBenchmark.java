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

    private static final LongArray longs = LongArray.newInstance(size);

    private static final LongArray longMatrix = new MatrixLongArray(size);

    private static final long[] longArray = new long[size];

    @Benchmark
    private void bigArrayOfLongsSequentialSet(final int reps) {
        for (int i = 0; i < reps; i++)
            for (int j = 0; j < size; j++)
                longs.set(j, j);
    }

    @Benchmark
    private void bigArrayOfLongsSequentialForceMatrixSet(final int reps) {
        for (int i = 0; i < reps; i++)
            for (int j = 0; j < size; j++)
                longMatrix.set(j, j);
    }

    @Benchmark
    private void arrayOfLongsSequentialSet(final int reps) {
        for (int i = 0; i < reps; i++)
            for (int j = 0; j < size; j++)
                longArray[j] = j;
    }

}
