
package net.elsrpop.reforgium.client.model;

import net.elsrpop.reforgium.reforge.item.ReforgeTableItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ReforgeTableItemModel extends GeoModel<ReforgeTableItem> {

    @Override
    public ResourceLocation getModelResource(ReforgeTableItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                "reforgium",
                "geo/reforging_station.geo.json"
        );
    }

    @Override
    public ResourceLocation getTextureResource(ReforgeTableItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                "reforgium",
                "textures/block/reforging_station.png"
        );
    }

    @Override
    public ResourceLocation getAnimationResource(ReforgeTableItem animatable) {
        return ResourceLocation.fromNamespaceAndPath(
                "reforgium",
                "animations/reforging_station.animation.json"
        );
    }
}
