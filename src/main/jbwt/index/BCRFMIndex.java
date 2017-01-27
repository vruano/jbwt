package jbwt.index;

import jbwt.sequences.SymbolSequence;
import jbwt.utils.ParamUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by valentin on 1/4/17.
 */
public class BCRFMIndex<A extends Symbol, S extends SymbolSequence<A>> implements Index<A, S> {

    private final Alphabet<A> alphabet;

    private BCRSymbolSequence<A, Location<S>>[] B;

    private final long[] symbolCounts;

    private static final long locationMask = 64 - 1;

    public BCRFMIndex(final Alphabet<A> alphabet) {
        this.alphabet = ParamUtils.requiresNonNull(alphabet);
        symbolCounts = new long[alphabet.size()];
        B = composeB(alphabet);
    }

    @SuppressWarnings("unchecked")
    private static <A extends Symbol, S extends SymbolSequence<A>> BCRSymbolSequence<A, Location<S>>[] composeB(final Alphabet<A> alphabet) {
        return (BCRSymbolSequence<A, Location<S>>[]) alphabet.symbols().stream()
                .map(s -> new BCRSymbolSequence<>(alphabet))
                .toArray(BCRSymbolSequence[]::new);
    }

    @Override
    public List<Location<S>> locate(final SymbolSequence<A> query) {
        return Collections.emptyList();
    }

    public void addAll(final Collection<S> sequences) {

        final Set<InsertionSequenceTracker> remaining = sequences.stream()
                .filter(s -> s.length() > 0)
                .map(InsertionSequenceTracker::new)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        @SuppressWarnings("unchecked")
        final Set<InsertionSequenceTracker>[][] remainingPerSymbol =
                (Set<InsertionSequenceTracker>[][]) new Set[][] {
                        alphabet.symbols().stream().map((__) -> new TreeSet<>()).toArray(Set[]::new),
                        alphabet.symbols().stream().map((__) -> new TreeSet<>()).toArray(Set[]::new)};

        if (remaining.isEmpty())
            return;

        // initialize BWT to [$$$..$], [], [],.... []
        indexBuildingInitialization(remaining, remainingPerSymbol);

        final long[][] symbolAccumulateCountMatrix = new long[B.length + 1][alphabet.size()];
        final Queue<InsertionSequenceTracker> trackerToProcess = new ArrayDeque<>(sequences.size());
        long iteration = 0;
        while (!remaining.isEmpty()) {
            updateAcummulateSymbolCountMatrix(symbolAccumulateCountMatrix);
            indexBuildingIteration(remaining, iteration++, remainingPerSymbol, symbolAccumulateCountMatrix, trackerToProcess);
        }
    }

    private void indexBuildingInitialization(Set<InsertionSequenceTracker> remaining, Set<InsertionSequenceTracker>[][] remainingPerSymbol) {
        final int sentinelCode = alphabet.sentinel().toInt();
        for (final InsertionSequenceTracker tracker : remaining) {
            tracker.bIndex = sentinelCode;
            tracker.bPosition = B[sentinelCode].length();
            tracker.bSymbol = tracker.previousSymbol();
            if ((tracker.iterator.nextPosition() & locationMask) == 0)
                B[tracker.bIndex].insert(0, tracker.bSymbol, new Location<>(tracker.sequence, tracker.iterator.nextPosition()));
            else
                B[tracker.bIndex].append(tracker.bSymbol);
            remainingPerSymbol[0][tracker.bSymbol.toInt()].add(tracker);
            symbolCounts[tracker.bSymbol.toInt()]++;
        }
    }

    private void indexBuildingIteration(final Set<InsertionSequenceTracker> remaining, final long iteration, final Set<InsertionSequenceTracker>[][] remainingPerSymbol, long[][] symbolAccumulateCountMatrix, Queue<InsertionSequenceTracker> trackerToProcess) {
        final int remainingFromIndex = (iteration & 1L) == 0 ? 0 : 1;
        final int remainingToIndex = remainingFromIndex == 0 ? 1 : 0;
        for (int symbolCode = 0; symbolCode < B.length; symbolCode++) {
            trackerToProcess.addAll(remainingPerSymbol[remainingFromIndex][symbolCode]);
            remainingPerSymbol[remainingFromIndex][symbolCode].clear();
            while (!trackerToProcess.isEmpty()) {
                final InsertionSequenceTracker tracker = trackerToProcess.remove();
                //B[symbolCode].insert(position, tracker.bSymbol);
                if (!tracker.hasPrevious()) // we reached the end of the sequence.
                    remaining.remove(tracker);
                else {
                    final long position = symbolAccumulateCountMatrix[tracker.bIndex][symbolCode]
                            + B[tracker.bIndex].symbolCountTo(symbolCode, tracker.bPosition);
                    tracker.bIndex = symbolCode;
                    tracker.bPosition = position;
                    tracker.bSymbol = tracker.previousSymbol();
                    final int bSymbolCode = tracker.bSymbol.toInt();
                    if ((tracker.iterator.nextPosition() & locationMask) == 0)
                        B[symbolCode].insert(position, tracker.bSymbol, new Location<S>(tracker.sequence, tracker.iterator.nextPosition()));
                    else
                        B[symbolCode].insert(position, tracker.bSymbol);
                    symbolCounts[bSymbolCode]++;
                    remainingPerSymbol[remainingToIndex][bSymbolCode].add(tracker);
                }
            }
        }
    }

    private void updateAcummulateSymbolCountMatrix(final long[][] symbolCountsBefore) {
        B[0].copySymbolCounts(symbolCountsBefore[1]);
        for (int bIndex = 1; bIndex < B.length; bIndex++) {
            B[bIndex].copySymbolCounts(symbolCountsBefore[bIndex + 1]);
            for (int symbolCode = 0; symbolCode < symbolCountsBefore[bIndex].length; symbolCode++)
                symbolCountsBefore[bIndex + 1][symbolCode] += symbolCountsBefore[bIndex][symbolCode];
        }
    }

    public void add(final S sequence) {
        addAll(Collections.singleton(sequence));
    }

    public SymbolSequence<A> toBWTSequence() {
        final RLESymbolSequence<A> result = new RLESymbolSequence<>(alphabet);
        for (final BCRSymbolSequence<A, Location<S>> sub : B)
            result.append(sub);
        return result;
    }

    /**
     * Holds the state of sequence insertion in the index for a given
     * sequence.
     *
     *
     */
    private class InsertionSequenceTracker implements Comparable<InsertionSequenceTracker> {
        final S sequence;
        final SymbolSequence.Iterator<A> iterator;
        boolean beyondBeginning = false;

        int bIndex;

        /**
         * Would correspond to the original paper P array index.
         */
        long bPosition;

        A bSymbol;

        private InsertionSequenceTracker(final S sequence) {
            this.sequence = sequence;
            this.iterator = sequence.iterator(sequence.length());
            bIndex = -1;
            bPosition = -1;
            beyondBeginning = false;
        }

        private A previousSymbol() {
            if (iterator.hasPrevious()) {
                return iterator.previous();
            } else {
                beyondBeginning = true;
                return alphabet.sentinel();
            }
        }

        @Override
        public int compareTo(final InsertionSequenceTracker o) {
            if (o == this)
                return 0;
            else if (this.bIndex != o.bIndex)
                return Integer.compare(this.bIndex, o.bIndex);
            else if (this.bPosition != o.bPosition)
                return Long.compare(this.bPosition, o.bPosition);
            else
                return Integer.compare(Objects.hashCode(this), Objects.hashCode(o));
        }

        public boolean hasPrevious() {
            return !beyondBeginning;
        }
    }
}
