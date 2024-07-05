package org.hansung.oddeco.butcher;

import java.util.List;

public class Probability {
    public List<ProbabilityField> data;

    public static class ProbabilityField {
        public int level;
        public Probabilities probabilities;

        public static class Probabilities {
            public int normal;
            public int rare;
            public int epic;
        }
    }
}
