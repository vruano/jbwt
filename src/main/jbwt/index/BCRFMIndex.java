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

    private BCRSymbolSequence<A>[] B;

    public BCRFMIndex(final Alphabet<A> alphabet) {
        this.alphabet = ParamUtils.requiresNonNull(alphabet);
        B = composeB(alphabet);
    }

    @SuppressWarnings("unchecked")
    private static <A extends Symbol> BCRSymbolSequence<A>[] composeB(Alphabet<A> alphabet) {
        return (BCRSymbolSequence<A>[]) alphabet.symbols().stream()
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
                .collect(Collectors.toSet());

        @SuppressWarnings("unchecked")
        final Set<InsertionSequenceTracker>[] remainingPerSymbol =
                (Set<InsertionSequenceTracker>[]) alphabet.symbols().stream()
                .map((__) -> new TreeSet<>())
                .toArray(Set[]::new);

        if (remaining.isEmpty())
            return;

        // initialize BWT to [$$$..$], [], [],.... []
        indexBuildingInitialization(remaining, remainingPerSymbol);

        final long[][] symbolAccumulateCountMatrix = new long[B.length + 1][alphabet.size()];
        final Queue<InsertionSequenceTracker> trackerToProcess = new ArrayDeque<>(sequences.size());
        while (!remaining.isEmpty()) {
            updateAcummulateSymbolCountMatrix(symbolAccumulateCountMatrix);
            indexBuildingIteration(remaining, remainingPerSymbol, symbolAccumulateCountMatrix, trackerToProcess);
        }
    }

    private void indexBuildingInitialization(Set<InsertionSequenceTracker> remaining, Set<InsertionSequenceTracker>[] remainingPerSymbol) {
        for (final InsertionSequenceTracker tracker : remaining) {
            tracker.bIndex = alphabet.sentinel().toInt();
            tracker.bPosition = B[0].length();
            tracker.bSymbol = tracker.previousSymbol();
            B[tracker.bIndex].append(tracker.bSymbol);
            remainingPerSymbol[tracker.bSymbol.toInt()].add(tracker);
        }
    }

    private void indexBuildingIteration(Set<InsertionSequenceTracker> remaining, Set<InsertionSequenceTracker>[] remainingPerSymbol, long[][] symbolAccumulateCountMatrix, Queue<InsertionSequenceTracker> trackerToProcess) {
        for (int symbolCode = 0; symbolCode < B.length; symbolCode++) {
            trackerToProcess.addAll(remainingPerSymbol[symbolCode]);
            remainingPerSymbol[symbolCode].clear();
            while (!trackerToProcess.isEmpty()) {
                final InsertionSequenceTracker tracker = trackerToProcess.remove();
                final long position = symbolAccumulateCountMatrix[tracker.bIndex][symbolCode]
                        + B[tracker.bIndex].symbolCountTo(symbolCode, tracker.bPosition);
                B[symbolCode].insert(position, tracker.bSymbol);
                if (tracker.bSymbol.isSentinel()) // we reached the end of the sequence.
                    remaining.remove(tracker);
                else {
                    tracker.bIndex = symbolCode;
                    tracker.bPosition = position;
                    tracker.bSymbol = tracker.previousSymbol();
                    remainingPerSymbol[tracker.bSymbol.toInt()].add(tracker);
                }
            }
        }
    }

    private void updateAcummulateSymbolCountMatrix(long[][] symbolCountsBefore) {
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
        for (final BCRSymbolSequence<A> sub : B)
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

        int bIndex;

        /**
         * Would correspond to the original paper P array index.
         */
        long bPosition;

        A bSymbol;

        int bSymbolCode;

        private InsertionSequenceTracker(final S sequence) {
            this.sequence = sequence;
            this.iterator = sequence.iterator(sequence.length());
            bIndex = -1;
            bPosition = -1;
        }

        private A previousSymbol() {
            if (iterator.hasPrevious()) {
                return iterator.previous();
            } else {
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
    }
}
