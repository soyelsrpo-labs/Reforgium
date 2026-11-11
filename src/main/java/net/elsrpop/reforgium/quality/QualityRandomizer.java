
package net.elsrpop.reforgium.quality;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QualityRandomizer {

    private static final Random RANDOM = new Random();

    public static QualityDefinition getRandomQuality(EquipmentType equipmentType) {
        if (equipmentType == null) {
            return null;
        }

        List<QualityDefinition> available = new ArrayList<>();
        List<Integer> weights = new ArrayList<>();

        int totalWeight = 0;

        for (QualityDefinition quality : QualityManager.getAll()) {

            if (!isCompatible(equipmentType, quality.equipmentType())) {
                continue;
            }

            int weight = getRarityWeight(quality.rarity());

            if (weight <= 0) {
                continue;
            }

            available.add(quality);
            weights.add(weight);
            totalWeight += weight;
        }

        if (available.isEmpty()) {
            return null;
        }

        int roll = RANDOM.nextInt(totalWeight);

        for (int i = 0; i < available.size(); i++) {
            roll -= weights.get(i);

            if (roll < 0) {
                return available.get(i);
            }
        }

        return null;
    }

    private static boolean isCompatible(
            EquipmentType itemType,
            EquipmentType qualityType
    ) {
        if (itemType == qualityType) {
            return true;
        }

        return switch (itemType) {
            case ARMOR_HELMET,
                 ARMOR_CHESTPLATE,
                 ARMOR_LEGGINGS,
                 ARMOR_BOOTS -> qualityType == EquipmentType.ARMOR;

            default -> false;
        };
    }

    private static int getRarityWeight(QualityRarity rarity) {
        return switch (rarity) {
            case BAD -> 40;
            case COMMON -> 30;
            case GOOD -> 20;
            case RARE -> 10;
            case EPIC, EXOTIC, LEGENDARY -> 0;
        };
    }
}
