package jbwt.index;

/**
 * Created by valentin on 12/26/16.
 */
public enum DNASymbol implements Symbol {
    $() {
        public boolean isSentinel() {
            return true;
        }
    }, A, C, G, T, R, Y, S, W, K, M, B, D, H, V, N;


    private static final DNASymbol[] fromByte = new DNASymbol[256];

    static {
        for (final DNASymbol symbol : values()) {
            final int upperCase = symbol.name().charAt(0);
            assert !Character.isLowerCase(upperCase);
            assert upperCase < 256;
            final int lowerCase = Character.toLowerCase(upperCase);
            assert lowerCase < 256;
            fromByte[(byte) lowerCase] = fromByte[(byte) upperCase] = symbol;
        }
    }

    public static final Alphabet<DNASymbol> ALPHABET = new Alphabet<DNASymbol>(DNASymbol.values());

    public int toInt() {
        return ordinal();
    }

    public boolean isConcrete() {
        return this == $ || this == A || this == C || this == G || this == T;
    }

    public boolean isAmbiguous() {
        return this != $ && !this.isConcrete();
    }

    public boolean isSentinel() {
        return false;
    }

    public static DNASymbol valueOf(final byte b) {
        return valueOf((int) b);
    }

    public static DNASymbol valueOf(final char c) {
        return valueOf((int) c);
    }

    public static DNASymbol valueOf(final int c) {
        final DNASymbol result = fromByte[c];
        if (result == null)
            throw new IllegalArgumentException("unknown symbol " + c);
        else
            return result;
    }
}
