
package net.elsrpop.reforgium.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.elsrpop.reforgium.quality.QualityTooltipHandler;
import net.elsrpop.reforgium.reforge.ReforgeManager;
import net.elsrpop.reforgium.reforge.menu.ReforgeMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ReforgeScreen extends AbstractContainerScreen<ReforgeMenu> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "reforgium",
                    "textures/gui/reforging_station.png"
            );

    private static final float PREVIEW_OPACITY = 0.25F;
    private static final int PREVIEW_CHANGE_INTERVAL = 20;

    private TexturedButton reforgeButton;

    private List<Item> requiredMaterials = List.of();
    private ItemStack lastPreviewSource = ItemStack.EMPTY;

    private int previewIndex = 0;
    private int previewTicks = 0;

    public ReforgeScreen(
            ReforgeMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    /*
     * ACTUALIZACIÓN DE MATERIALES
     */
    private void updateRequiredMaterials() {

        ItemStack original = menu.getSlot(0).getItem();

        if (original.isEmpty()
                || minecraft == null
                || minecraft.level == null) {

            requiredMaterials = List.of();
            lastPreviewSource = ItemStack.EMPTY;
            previewIndex = 0;
            previewTicks = 0;
            return;
        }

        if (ItemStack.matches(lastPreviewSource, original)) {
            return;
        }

        lastPreviewSource = original.copy();

        Set<Item> materials = ReforgeManager.getMainMaterials(
                original,
                minecraft.level
        );

        requiredMaterials = new ArrayList<>(materials);

        previewIndex = 0;
        previewTicks = 0;
    }

    /*
     * ANIMACIÓN
     */
    private void tickPreviewAnimation() {

        if (requiredMaterials.size() <= 1) {
            return;
        }

        previewTicks++;

        if (previewTicks >= PREVIEW_CHANGE_INTERVAL) {

            previewTicks = 0;
            previewIndex++;

            if (previewIndex >= requiredMaterials.size()) {
                previewIndex = 0;
            }
        }
    }

    /*
     * MATERIAL ACTUAL
     */
    private Item getCurrentPreviewMaterial() {

        if (requiredMaterials.isEmpty()) {
            return null;
        }

        if (previewIndex >= requiredMaterials.size()) {
            previewIndex = 0;
        }

        return requiredMaterials.get(previewIndex);
    }

    /*
     * VISTA PREVIA DEL MATERIAL
     */
    private void renderMainIngredientPreview(GuiGraphics graphics) {

        if (menu.getSlot(2).hasItem()) {
            return;
        }

        Item currentMaterial = getCurrentPreviewMaterial();

        if (currentMaterial == null) {
            return;
        }

        ItemStack preview = new ItemStack(currentMaterial);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderColor(
                1.0F,
                1.0F,
                1.0F,
                PREVIEW_OPACITY
        );

        try {

            // BLOQUES: usamos una capa translúcida.
            if (currentMaterial instanceof BlockItem) {

                renderTranslucentBlockPreview(graphics, preview);

            } else {

                // OBJETOS NORMALES: conservamos el renderizado anterior.
                graphics.renderItem(
                        preview,
                        leftPos + 28,
                        topPos + 45
                );

                graphics.flush();
            }

        } finally {

            RenderSystem.setShaderColor(
                    1.0F,
                    1.0F,
                    1.0F,
                    1.0F
            );

            RenderSystem.disableBlend();
        }
    }

    /*
     * RENDERIZADO TRANSLÚCIDO DE BLOQUES
     */
    private void renderTranslucentBlockPreview(
            GuiGraphics graphics,
            ItemStack stack
    ) {

        Minecraft mc = Minecraft.getInstance();

        if (mc.level == null) {
            return;
        }

        PoseStack pose = graphics.pose();

        pose.pushPose();

        try {

            pose.translate(
                    leftPos + 36,
                    topPos + 53,
                    150.0F
            );

            pose.scale(16.0F, 16.0F, 16.0F);

            /*
             * Redirigimos los buffers del modelo del bloque
             * a una capa que permite transparencia.
             */
            MultiBufferSource translucentBuffers = renderType ->
                    graphics.bufferSource().getBuffer(
                            RenderType.entityTranslucent(
                                    TextureAtlas.LOCATION_BLOCKS
                            )
                    );

            ItemRenderer itemRenderer = mc.getItemRenderer();

            itemRenderer.renderStatic(
                    stack,
                    ItemDisplayContext.GUI,
                    15728880,
                    OverlayTexture.NO_OVERLAY,
                    pose,
                    translucentBuffers,
                    mc.level,
                    0
            );

        } finally {

            pose.popPose();

            // Dibujamos antes de restaurar la opacidad.
            graphics.flush();
        }
    }

    /*
     * TOOLTIP DE LOS MATERIALES
     */
    private void renderMainIngredientTooltip(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {

        int slotX = leftPos + 28;
        int slotY = topPos + 45;

        if (mouseX < slotX || mouseX >= slotX + 16
                || mouseY < slotY || mouseY >= slotY + 16) {
            return;
        }

        if (menu.getSlot(2).hasItem()) {
            return;
        }

        if (requiredMaterials.isEmpty()) {
            return;
        }

        List<Component> tooltip = new ArrayList<>();

        tooltip.add(Component.literal("Materiales necesarios:"));

        for (Item item : requiredMaterials) {

            ItemStack stack = new ItemStack(item);

            tooltip.add(
                    Component.literal("• ")
                            .append(stack.getHoverName())
            );
        }

        graphics.renderTooltip(
                font,
                tooltip,
                Optional.empty(),
                mouseX,
                mouseY
        );
    }

    /*
     * INICIALIZACIÓN
     */
    @Override
    protected void init() {

        super.init();

        reforgeButton = new TexturedButton(
                leftPos + 130,
                topPos + 34,
                () -> {

                    if (minecraft != null && minecraft.gameMode != null) {

                        minecraft.gameMode.handleInventoryButtonClick(
                                menu.containerId,
                                ReforgeMenu.BUTTON_REFORGE
                        );
                    }
                }
        );

        addRenderableWidget(reforgeButton);
    }

    /*
     * ACTUALIZACIÓN POR TICK
     */
    @Override
    protected void containerTick() {

        super.containerTick();

        updateRequiredMaterials();
        tickPreviewAnimation();

        if (reforgeButton != null) {
            reforgeButton.active = menu.canReforgeNow();
        }
    }

    /*
     * FONDO
     */
    @Override
    protected void renderBg(
            GuiGraphics graphics,
            float partialTick,
            int mouseX,
            int mouseY
    ) {

        graphics.blit(
                TEXTURE,
                leftPos,
                topPos,
                0,
                0,
                176,
                166,
                176,
                166
        );
    }

    /*
     * TEXTOS
     */
    @Override
    protected void renderLabels(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {

        graphics.drawString(
                font,
                title,
                8,
                6,
                0x404040,
                false
        );

        graphics.drawString(
                font,
                playerInventoryTitle,
                8,
                72,
                0x404040,
                false
        );
    }

    /*
     * PANEL DE INFORMACIÓN DE CALIDAD
     */
    private void renderQualityPanel(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {

        if (reforgeButton == null || !reforgeButton.active) {
            return;
        }

        boolean hovering =
                mouseX >= reforgeButton.getX()
                        && mouseX < reforgeButton.getX() + reforgeButton.getWidth()
                        && mouseY >= reforgeButton.getY()
                        && mouseY < reforgeButton.getY() + reforgeButton.getHeight();

        if (!hovering) {
            return;
        }

        ItemStack stack = menu.getSlot(0).getItem();

        if (stack.isEmpty()) {
            return;
        }

        List<Component> lines =
                QualityTooltipHandler.getQualitySummary(stack);

        if (lines.isEmpty()) {
            return;
        }

        int panelX = leftPos + imageWidth + 8;
        int panelY = topPos + 20;
        int padding = 5;
        int lineHeight = 10;
        int panelWidth = 120;
        int textWidth = panelWidth - padding * 2;

        List<net.minecraft.util.FormattedCharSequence> wrappedLines =
                new ArrayList<>();

        for (Component line : lines) {
            wrappedLines.addAll(font.split(line, textWidth));
        }

        int panelHeight = padding * 2 + wrappedLines.size() * lineHeight;

        graphics.fill(
                panelX,
                panelY,
                panelX + panelWidth,
                panelY + panelHeight,
                0xE0101010
        );

        graphics.renderOutline(
                panelX,
                panelY,
                panelWidth,
                panelHeight,
                0xFF606060
        );

        int textY = panelY + padding;

        for (net.minecraft.util.FormattedCharSequence line : wrappedLines) {

            graphics.drawString(
                    font,
                    line,
                    panelX + padding,
                    textY,
                    0xFFFFFF,
                    false
            );

            textY += lineHeight;
        }
    }

    /*
     * RENDERIZADO PRINCIPAL
     */
    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        updateRequiredMaterials();

        super.render(graphics, mouseX, mouseY, partialTick);

        renderQualityPanel(graphics, mouseX, mouseY);

        renderMainIngredientPreview(graphics);

        renderMainIngredientTooltip(graphics, mouseX, mouseY);

        renderTooltip(graphics, mouseX, mouseY);
    }
}
