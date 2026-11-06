
package net.elsrpop.reforgium.client.model;

import net.elsrpop.reforgium.reforge.block.ReforgeTableBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ReforgeTableModel extends GeoModel<ReforgeTableBlockEntity> {

    @Override
    public ResourceLocation getModelResource(ReforgeTableBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                "reforgium",
                "geo/reforging_station.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(ReforgeTableBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                "reforgium",
                "textures/block/reforging_station.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(ReforgeTableBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                "reforgium",
                "animations/reforging_station.animation.json"
        );
    }
}
