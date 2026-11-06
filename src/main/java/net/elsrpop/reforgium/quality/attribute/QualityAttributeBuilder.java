package net.elsrpop.reforgium.quality.attribute;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;

public class QualityAttributeBuilder {

    public record AttributeEntry(
            Holder<Attribute> attribute,
            AttributeModifier modifier
    ) {
    }

    public static List<AttributeEntry> build(
            String qualityId,
            List<QualityModifier> qualityModifiers
    ) {
        List<AttributeEntry> result = new ArrayList<>();

        for (QualityModifier modifier : qualityModifiers) {

            Holder<Attribute> attribute =
                    VanillaAttributeMapper.getVanillaAttribute(
                            modifier.attribute()
                    );

            if (attribute == null) {
                continue;
            }

            AttributeModifier attributeModifier =
                    QualityAttributeApplier.createModifier(
                            qualityId,
                            modifier
                    );

            result.add(new AttributeEntry(
                    attribute,
                    attributeModifier
            ));
        }

        return List.copyOf(result);
    }
}