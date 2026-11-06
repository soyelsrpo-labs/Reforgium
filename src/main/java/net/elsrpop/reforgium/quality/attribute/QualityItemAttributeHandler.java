package net.elsrpop.reforgium.quality.attribute;

import net.elsrpop.reforgium.quality.QualityHelper;
import net.elsrpop.reforgium.quality.QualityDefinition;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;

public class QualityItemAttributeHandler {

    public static ItemAttributeModifiers apply(
            ItemStack stack,
            ItemAttributeModifiers original,
            EquipmentSlotGroup slot
    ) {
        if (stack.isEmpty()) {
            return original;
        }

        QualityDefinition quality = QualityHelper.getQuality(stack);

        if (quality == null) {
            return original;
        }

        ItemAttributeModifiers.Builder builder =
                ItemAttributeModifiers.builder();

        // Conservar todos los modificadores originales.
        for (ItemAttributeModifiers.Entry entry : original.modifiers()) {
            builder.add(
                    entry.attribute(),
                    entry.modifier(),
                    entry.slot()
            );
        }

        // Agregar los modificadores de calidad.
        for (QualityAttributeBuilder.AttributeEntry entry :
                QualityAttributeBuilder.build(
                        quality.id(),
                        quality.modifiers()
                )) {

            builder.add(
                    entry.attribute(),
                    entry.modifier(),
                    slot
            );
        }

        return builder.build().withTooltip(original.showInTooltip());
    }
}