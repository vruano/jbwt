package jbwt.index;

import jbwt.base.Alphabet;
import jbwt.base.Text;
import jbwt.utils.ParamUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by valentin on 12/27/16.
 */
public class BCRFMIndexBuilder<A extends Alphabet<?>> {

    private final A alphabet;


    private BCRSuffixArray sa;
    public BCRFMIndexBuilder(final A alphabet) {
        this.alphabet = ParamUtils.requiresNonNull(alphabet);
    }

    public void add(final Collection<Text<?>> input) {
        final Set<Text<?>> pending = new LinkedHashSet<>(input);
        int i = 0;



    }
}
