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
        return String.join(", ", list.subList(0, list.size() - 1)) + " und " + list.getLast();
    }
}
