package net.elsrpop.reforgium.quality.attribute;

import net.elsrpop.reforgium.Reforgium;
import net.minecraft.resources.ResourceLocation;

public class QualityAttributeIds {

    public static ResourceLocation create(String qualityId, String attributeId) {
        return ResourceLocation.fromNamespaceAndPath(
                Reforgium.MOD_ID,
                qualityId + "_" + attributeId
        );
    }
}