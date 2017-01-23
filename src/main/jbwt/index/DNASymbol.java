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
        if (Character.isUpperCase(c)) {
            switch (c) {
                case 'A': return A;
                case 'C': return C;
                case 'G': return G;
                case 'T':
                case 'U': return T;
            }
        }
        switch (c) {
                case 'a': return A;
                case 'c': return C;
                case 'g': return G;
                case 't':
                case 'u': return T;
                case '$': return $;
        }
        switch (Character.toLowerCase(c)) {
                case 'r': return R;
                case 'y': return Y;
                case 's': return S;
                case 'w': return W;
                case 'k': return K;
                case 'm': return M;
                case 'b': return B;
                case 'd': return D;
                case 'h': return H;
                case 'v': return V;
                case 'n': return N;
                default:
                    throw new IllegalArgumentException("unknown symbol: '" + c + "'");
        }
    }
}
