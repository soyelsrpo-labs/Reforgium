package net.elsrpop.reforgium.quality.attribute;

import net.elsrpop.reforgium.quality.QualityHelper;
import net.elsrpop.reforgium.quality.QualityDefinition;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class QualityItemAttributes {

    public static List<QualityAttributeBuilder.AttributeEntry> getModifiers(
            ItemStack stack
    ) {
        if (stack.isEmpty()) {
            return List.of();
        }

        QualityDefinition quality = QualityHelper.getQuality(stack);

        if (quality == null) {
            return List.of();
        }

        return QualityAttributeBuilder.build(
                quality.id(),
                quality.modifiers()
        );
    }
}