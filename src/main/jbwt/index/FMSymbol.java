package jbwt.index;

/**
 * Created by valentin on 12/26/16.
 */
public interface FMSymbol {
    boolean isSentinel();
    boolean isAmbiguous();
    boolean isConcrete();
}
