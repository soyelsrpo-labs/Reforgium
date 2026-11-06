package net.elsrpop.reforgium.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.elsrpop.reforgium.client.model.ReforgeTableItemModel;
import net.elsrpop.reforgium.reforge.item.ReforgeTableItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class ReforgeTableItemRenderer extends GeoItemRenderer<ReforgeTableItem> {

    public ReforgeTableItemRenderer() {
        super(new ReforgeTableItemModel());
        withScale(0.62f);
    }

    @Override
    public void preRender(
            PoseStack poseStack,
            ReforgeTableItem animatable,
            BakedGeoModel model,
            @Nullable MultiBufferSource bufferSource,
            @Nullable VertexConsumer buffer,
            boolean isReRender,
            float partialTick,
            int packedLight,
            int packedOverlay,
            int colour
    ) {
        super.preRender(
                poseStack, animatable, model, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, colour
        );

        if (isReRender) return;

        ItemDisplayContext perspective = this.renderPerspective;

        if (perspective == ItemDisplayContext.GUI) {
            poseStack.translate(0.3f, -0.15f, 0f);
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(30));
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(45));
        }

        if (perspective == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || perspective == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {

            // Reduce la altura sin achicar tanto el ancho.
            poseStack.scale(0.7f, 0.7f, 0.7f);

            // Ajuste de centrado y altura en la mano.
            poseStack.translate(0f, 0.2f, 0.4f);

        }
        if (perspective == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                || perspective == ItemDisplayContext.THIRD_PERSON_LEFT_HAND) {

            // Rotación en tercera persona
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(0));
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(0));
            poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(0));

            // Tamaño en tercera persona
            poseStack.scale(0.7f, 0.7f, 0.7f);

            // Posición en la mano
            poseStack.translate(0.0f, 0.0f, 0.0f);
        }
    }
}