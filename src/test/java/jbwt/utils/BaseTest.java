package jbwt.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by valentin on 1/9/17.
 */
public class BaseTest {

    protected class TestData {
        final List<Object[]> data;

        public TestData() {
            this.data = new ArrayList<>();
        }

        public Object[][] toArray() {
            return data.toArray(new Object[data.size()][]);
        }

        public TestData add(final Object ... d) {
            data.add(d);
            return this;
        }
    }

}
