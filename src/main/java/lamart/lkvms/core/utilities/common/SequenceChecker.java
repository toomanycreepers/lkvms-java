package lamart.lkvms.core.utilities.common;

import java.util.Collection;

public class SequenceChecker {

    private SequenceChecker(){}

    public static boolean isSequence(Object obj) {
        return obj != null && 
               !(obj instanceof String) && 
               (obj.getClass().isArray() || obj instanceof Collection);
    }
}
