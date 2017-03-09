package jbwt.base;

import jbwt.utils.Log2;
import jbwt.utils.ParamUtils;

import java.util.*;

/**
 * Created by valentin on 12/25/16.
 */
public interface Alphabet<S> {

    int size();

    int toCode(S symbol);

    S toSymbol(int code);

    default int bits() {
        return Log2.bits(size());
    }

    List<S> symbols();

}

