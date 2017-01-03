package jbwt.index;

/**
 * Created by valentin on 12/26/16.
 */
public interface Symbol {
    boolean isSentinel();
    boolean isAmbiguous();
    boolean isConcrete();
    int toInt();
}
