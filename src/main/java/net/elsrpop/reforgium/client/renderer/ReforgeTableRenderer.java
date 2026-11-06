package net.elsrpop.reforgium.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import net.elsrpop.reforgium.client.model.ReforgeTableModel;
import net.elsrpop.reforgium.reforge.block.ReforgeTableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ReforgeTableRenderer extends GeoBlockRenderer<ReforgeTableBlockEntity> {

    public ReforgeTableRenderer(BlockEntityRendererProvider.Context context) {
        super(new ReforgeTableModel());
    }

    @Override
    public void render(
            ReforgeTableBlockEntity animatable,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource bufferSource,
            int packedLight,
            int packedOverlay
    ) {
        super.render(
                animatable,
                partialTick,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
        );
    }
}