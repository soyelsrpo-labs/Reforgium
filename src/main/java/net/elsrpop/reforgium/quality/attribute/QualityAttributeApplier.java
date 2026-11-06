package net.elsrpop.reforgium.quality.attribute;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class QualityAttributeApplier {

    public static AttributeModifier.Operation getOperation(
            QualityModifier modifier
    ) {
        if (modifier.percentage()) {
            return AttributeModifier.Operation.ADD_MULTIPLIED_BASE;
        }

        return AttributeModifier.Operation.ADD_VALUE;
    }

    public static double getAmount(QualityModifier modifier) {
        if (modifier.percentage()) {
            return modifier.value() / 100.0;
        }

        return modifier.value();
    }

    public static AttributeModifier createModifier(
            String qualityId,
            QualityModifier modifier
    ) {
        ResourceLocation id = QualityAttributeIds.create(
                qualityId,
                modifier.attribute().name().toLowerCase()
        );

        return new AttributeModifier(
                id,
                getAmount(modifier),
                getOperation(modifier)
        );
    }
}