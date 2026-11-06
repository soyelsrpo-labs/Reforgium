package net.elsrpop.reforgium.quality;

import java.util.ArrayList;
import java.util.List;

public class QualityManager {

    private static final List<QualityDefinition> QUALITIES = new ArrayList<>();

    public static void register(QualityDefinition quality) {
        QUALITIES.add(quality);
    }

    public static List<QualityDefinition> getAll() {
        return List.copyOf(QUALITIES);
    }

    public static QualityDefinition getById(String id) {
        return QUALITIES.stream()
                .filter(quality -> quality.id().equals(id))
                .findFirst()
                .orElse(null);
    }

}