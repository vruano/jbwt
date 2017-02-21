package jbwt.utils;

import com.google.caliper.Benchmark;
import com.google.caliper.api.VmOptions;
import com.google.caliper.runner.CaliperMain;

/**
 * Created by valentin on 2/20/17.
 */
@VmOptions({"-XX:-TieredCompilation"})
public class BigArrayOfIntsBenchmark {
    public static void main(String[] args) {
        CaliperMain.main( BigArrayOfIntsBenchmark.class, args);
    }

    private static int size = 10_000;

    @Benchmark
    private void bigArrayOfIntsSequentialSet(final int reps) {
        final BigArrayOfInts ints = BigArrayOfInts.newInstance(size);
        for (int i = 0; i < reps; i++)
            for (int j = 0; j < size; j++)
                ints.set(j, j);
    }

    @Benchmark
    private void arrayOfIntsSequentialSet(final int reps) {
        final int[] ints = new int[size];
        for (int i = 0; i < reps; i++)
            for (int j = 0; j < size; j++)
                ints[j] = j;
    }

}
