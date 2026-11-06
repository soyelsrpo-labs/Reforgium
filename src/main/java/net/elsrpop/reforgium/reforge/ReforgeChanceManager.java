
package net.elsrpop.reforgium.reforge;

import net.elsrpop.reforgium.quality.QualityRarity;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

public final class ReforgeChanceManager {

    private static final Map<Integer, Map<QualityRarity, Integer>> CHANCES =
            new LinkedHashMap<>();

    static {
        setChances(1, List.of(
                Map.entry(QualityRarity.BAD, 50),
                Map.entry(QualityRarity.COMMON, 30),
                Map.entry(QualityRarity.GOOD, 15),
                Map.entry(QualityRarity.RARE, 5)
        ));

        setChances(2, List.of(
                Map.entry(QualityRarity.COMMON, 50),
                Map.entry(QualityRarity.GOOD, 30),
                Map.entry(QualityRarity.RARE, 10),
                Map.entry(QualityRarity.EPIC, 7),
                Map.entry(QualityRarity.EXOTIC, 3)
        ));

        setChances(3, List.of(
                Map.entry(QualityRarity.GOOD, 40),
                Map.entry(QualityRarity.RARE, 30),
                Map.entry(QualityRarity.EPIC, 20),
                Map.entry(QualityRarity.EXOTIC, 5),
                Map.entry(QualityRarity.LEGENDARY, 5)
        ));
    }

    private ReforgeChanceManager() {
    }

    private static void setChances(
            int tier,
            List<Map.Entry<QualityRarity, Integer>> entries
    ) {
        LinkedHashMap<QualityRarity, Integer> chances =
                new LinkedHashMap<>();

        int total = 0;

        for (Map.Entry<QualityRarity, Integer> entry : entries) {
            chances.put(entry.getKey(), entry.getValue());
            total += entry.getValue();
        }

        if (total != 100) {
            throw new IllegalArgumentException(
                    "Las probabilidades del nivel " + tier
                            + " deben sumar 100, pero suman " + total
            );
        }

        CHANCES.put(
                tier,
                Collections.unmodifiableMap(chances)
        );
    }

    public static QualityRarity roll(int tier) {
        Map<QualityRarity, Integer> chances = CHANCES.get(tier);

        if (chances == null) {
            throw new IllegalArgumentException(
                    "Nivel de material de reforja inválido: " + tier
            );
        }

        int roll = RandomGenerator.getDefault().nextInt(100);
        int cumulative = 0;

        for (Map.Entry<QualityRarity, Integer> entry : chances.entrySet()) {
            cumulative += entry.getValue();

            if (roll < cumulative) {
                return entry.getKey();
            }
        }

        throw new IllegalStateException(
                "No se pudo determinar la calidad para el nivel " + tier
        );
    }

    public static Map<QualityRarity, Integer> getChances(int tier) {
        return CHANCES.getOrDefault(tier, Map.of());
    }
}
