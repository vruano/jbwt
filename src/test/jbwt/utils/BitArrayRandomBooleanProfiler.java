package jbwt.utils;

import com.google.caliper.BeforeExperiment;
import com.google.caliper.Param;
import com.google.caliper.runner.CaliperMain;

import java.util.Random;

/**
 * Created by valentin on 2/18/17.
 */
public class BitArrayRandomBooleanProfiler {

    public static void main(String[] args) {
        final BitArrayRandomBooleanProfiler profiler = new BitArrayRandomBooleanProfiler();
        profiler.array.setLength(profiler.size);
        profiler.calculateAccessArray();
        profiler.bitArrayRandomSet(1000000000);
    }

    long size = 1000;

    int access = 1000;

    int[] positions;

    final BitArray array = new BitArray(size);


    @BeforeExperiment
    public void calculateAccessArray() {
        final Random rdn = new Random(133);
        positions = new int[access];
        for (int i = 0; i < access; i++)
            positions[i] = rdn.nextInt((int) size);
    }

    //  @Benchmark
    public void bitArrayRandomSet(int repeats) {
        for (int i = 0; i < repeats; i++) {
            for (int j = 0; j < access; j++) {
                array.set(positions[j], true);
            }
        }
    }
}
