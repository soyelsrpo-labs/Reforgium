package net.elsrpop.reforgium.quality;

import net.elsrpop.reforgium.quality.attribute.QualityModifier;

import java.util.List;

public record QualityDefinition(
        String id,
        String name,
        String description,
        QualityRarity rarity,
        EquipmentType equipmentType,
        List<QualityModifier> modifiers
) {
    public QualityDefinition {
        modifiers = List.copyOf(modifiers);
    }
}