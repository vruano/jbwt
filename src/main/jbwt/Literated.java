package jbwt;

import jbwt.index.Alphabet;
import jbwt.index.Symbol;

/**
 * Created by valentin on 2/6/17.
 */
public interface Literated<S extends Symbol> {
    Alphabet<S> alphabet();
}
