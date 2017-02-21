package jbwt.utils;

import com.google.caliper.BeforeExperiment;
import com.google.caliper.Benchmark;
import com.google.caliper.Param;
import com.google.caliper.api.VmOptions;
import com.google.caliper.runner.BenchmarkClassModule;
import com.google.caliper.runner.CaliperMain;
import com.google.caliper.runner.Running;

import java.util.Random;
import java.util.Scanner;

/**
 * Created by valentin on 2/17/17.
 */
@VmOptions({"-XX:-TieredCompilation", "-XX:MaxInlineSize=1024", "-XX:FreqInlineSize=1024"})
public class BitArrayRandomBooleanBenchmark {



    static long size = 1000;

    int access = 1000;

    int[] positions;

    static BitArray bitArray = new BitArray(size);

    boolean[] boolArray = new boolean[(int)size];

    static {
        bitArray.setLength(size);
    }


    @BeforeExperiment
    public void calculateAccessArray() {
        final Random rdn = new Random(133);
        positions = new int[access];
        for (int i = 0; i < access; i++)
            positions[i] = rdn.nextInt((int) size);
    }

    @Benchmark
    public void bitArrayRandomSet(int repeats) {
        for (int i = 0; i < repeats; i++) {
            //final BitArray array = new BitArray(size);
            //array.setLength(size);
            for (int j = 0; j < access; j++) {
                bitArray.set(positions[j], true);
            }
        }
    }

//    @Benchmark
    public void boolArrayRandomSet(int repeats) {
        for (int i = 0; i < repeats; i++) {
            final boolean[] array = new boolean[(int) size];
            for (int j = 0; j < access; j++) {
                array[positions[j]] =  true;
            }
        }
    }

 //   @Benchmark
    public void byteArrayRandomSet(int repeats) {
        for (int i = 0; i < repeats; i++) {
            final byte[] array = new byte[(int) size];
            for (int j = 0; j < access; j++) {
                array[positions[j]] =  1;
            }
        }
    }

//    @Benchmark
    public void longArrayRandomSet(int repeats) {
        for (int i = 0; i < repeats; i++) {
            final long[] array = new long[(int) size];
            for (int j = 0; j < access; j++) {
                array[positions[j]] =  1;
            }
        }
    }


    @Benchmark
    public void bitArraySequentialSet(int repeats) {
        for (int i = 0; i < repeats; i++) {
            //final BitArray array = new BitArray(size);
            //array.setLength(size);
            for (int j = 0; j < size; j++) {
                bitArray.set(j, true);
            }
        }
    }

    @Benchmark
    public void bitArrayIteratorSet(int repeats) {
        for (int i = 0; i < repeats; i++) {
            final BitArray.Iterator it = bitArray.iterator();
            for (int j = 0; j < size; j++) {
                it.set(true);
            }
        }
    }

    @Benchmark
    public void bitArrayBooleanIteratorSet(int repeats) {
        for (int i = 0; i < repeats; i++) {
            final BitArray.BooleanIterator it = bitArray.booleanIterator();
            for (int j = 0; j < size; j++) {
                it.set(true);
            }
        }
    }


    @Benchmark
    public void boolArraySequentialSet(int repeats) {
        for (int i = 0; i < repeats; i++) {
            for (int j = 0; j < size; j++) {
                boolArray[j] =  true;
            }
        }
    }
}
