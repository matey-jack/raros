package util;

import java.util.List;

public class GermanList {
    public static String join(List<String> list) {
        if (list.isEmpty()) {
            return "";
        }
        if (list.size() == 1) {
            return list.getFirst();
        }
        String last = list.removeLast();
        return String.join(", ", list) + " und " + last;
    }
}
