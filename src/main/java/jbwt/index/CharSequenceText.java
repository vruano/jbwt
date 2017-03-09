package jbwt.index;

import jbwt.base.Alphabet;
import jbwt.base.CharAlphabet;
import jbwt.common.SimpleLiterate;
import jbwt.base.Text;
import jbwt.utils.ParamUtils;

/**
 * Created by valentin on 2/25/17.
 */
public class CharSequenceText<S> extends SimpleLiterate<Alphabet<S>> implements Text<S> {

    private final int from;
    private final int to;
    private final CharSequence sequence;

    public CharSequenceText(final CharAlphabet<S> alphabet, final CharSequence sequence) {
        this(alphabet, ParamUtils.requiresNonNull(sequence), 0, sequence.length());
    }
    public CharSequenceText(final CharAlphabet<S> alphabet, final CharSequence sequence, final int from, final int to) {
        super(alphabet);
        ParamUtils.requiresValidIndexInterval(from, to, sequence.length());
        this.sequence = ParamUtils.requiresNonNull(sequence);
        this.from = from;
        this.to = to;
    }

    @Override
    public CharAlphabet<S> alphabet() {
        return (CharAlphabet<S>) super.alphabet();
    }

    @Override
    public long length() {
        return to - from;
    }

    @Override
    public int codeAt(final long position) {
        ParamUtils.validIndex(position, 0, sequence.length());
        return alphabet().toCode(sequence.charAt((int)position));
    }

    @Override
    public S symbolAt(final long position) {
        ParamUtils.validIndex(position, 0, sequence.length());
        return alphabet.toSymbol(sequence.charAt((int)position));
    }

    @Override
    public CharSequenceText<S> subtext(final long from, final long to) {
        return new CharSequenceText<>(alphabet(), sequence, (int)(this.from + from), (int)(this.from + to));
    }
}
