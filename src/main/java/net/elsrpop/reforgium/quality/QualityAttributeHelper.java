package net.elsrpop.reforgium.quality;

import net.elsrpop.reforgium.quality.attribute.QualityAttribute;
import net.elsrpop.reforgium.quality.attribute.QualityModifier;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class QualityAttributeHelper {

    public static List<QualityModifier> getModifiers(ItemStack stack) {

        QualityDefinition quality = QualityHelper.getQuality(stack);

        if (quality == null) {
            return List.of();
        }

        return quality.modifiers();
    }

    public static double getModifierValue(
            ItemStack stack,
            QualityAttribute attribute
    ) {
        return getModifiers(stack).stream()
                .filter(modifier -> modifier.attribute() == attribute)
                .mapToDouble(QualityModifier::value)
                .sum();
    }

    public static double getPercentageValue(
            ItemStack stack,
            QualityAttribute attribute
    ) {
        return getModifiers(stack).stream()
                .filter(modifier ->
                        modifier.attribute() == attribute
                                && modifier.percentage()
                )
                .mapToDouble(QualityModifier::value)
                .sum();
    }

    public static double getFlatValue(
            ItemStack stack,
            QualityAttribute attribute
    ) {
        return getModifiers(stack).stream()
                .filter(modifier ->
                        modifier.attribute() == attribute
                                && !modifier.percentage()
                )
                .mapToDouble(QualityModifier::value)
                .sum();
    }

    public static boolean hasModifier(
            ItemStack stack,
            QualityAttribute attribute
    ) {
        return getModifiers(stack).stream()
                .anyMatch(modifier -> modifier.attribute() == attribute);
    }
}