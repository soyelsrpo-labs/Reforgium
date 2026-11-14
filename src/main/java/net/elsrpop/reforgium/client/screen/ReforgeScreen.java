
package net.elsrpop.reforgium.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.elsrpop.reforgium.quality.QualityTooltipHandler;
import net.elsrpop.reforgium.reforge.ReforgeManager;
import net.elsrpop.reforgium.reforge.menu.ReforgeMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
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

    // Transparencia del objeto de vista previa.
    private static final float PREVIEW_OPACITY = 0.25F;

    // Tiempo entre cambios: 20 ticks = aproximadamente 1 segundo.
    private static final int PREVIEW_CHANGE_INTERVAL = 20;

    private TexturedButton reforgeButton;

    // Materiales compatibles con el objeto actual.
    private List<Item> requiredMaterials = List.of();

    // Copia del objeto para detectar cuándo cambia el slot principal.
    private ItemStack lastPreviewSource = ItemStack.EMPTY;

    // Índice del material que se está mostrando.
    private int previewIndex = 0;

    // Contador para controlar la animación.
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
     *
     * Solo volvemos a buscar los materiales cuando cambia
     * el objeto colocado en el slot principal.
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

        // Si el objeto no cambió, no volvemos a calcular los materiales.
        if (ItemStack.matches(lastPreviewSource, original)) {
            return;
        }

        lastPreviewSource = original.copy();

        Set<Item> materials = ReforgeManager.getMainMaterials(
                original,
                minecraft.level
        );

        requiredMaterials = new ArrayList<>(materials);

        // Reiniciamos la animación al cambiar el objeto.
        previewIndex = 0;
        previewTicks = 0;
    }

    /*
     * ANIMACIÓN
     *
     * Cambia el material mostrado cada 20 ticks.
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
     * OBTENER EL MATERIAL ACTUAL DE LA ANIMACIÓN
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

        // No mostramos la vista previa si el slot ya tiene un objeto.
        if (menu.getSlot(2).hasItem()) {
            return;
        }

        Item currentMaterial = getCurrentPreviewMaterial();

        if (currentMaterial == null) {
            return;
        }

        ItemStack preview = new ItemStack(currentMaterial);

        // Activamos la transparencia.
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderColor(
                1.0F,
                1.0F,
                1.0F,
                PREVIEW_OPACITY
        );

        try {

            graphics.renderItem(
                    preview,
                    leftPos + 28,
                    topPos + 45
            );

        } finally {

            // Restauramos el color y la transparencia.
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
     * TOOLTIP DE LOS MATERIALES
     */
    private void renderMainIngredientTooltip(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {

        int slotX = leftPos + 28;
        int slotY = topPos + 45;

        // Comprobamos si el mouse está sobre el slot.
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

        // Detectamos cambios en el objeto principal.
        updateRequiredMaterials();

        // Avanzamos la animación.
        tickPreviewAnimation();

        if (reforgeButton != null) {
            reforgeButton.active = menu.canReforgeNow();
        }
    }

    /*
     * FONDO DE LA INTERFAZ
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
     * TEXTOS DE LA INTERFAZ
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

        // Actualizamos por si el objeto cambió antes del siguiente tick.
        updateRequiredMaterials();

        super.render(graphics, mouseX, mouseY, partialTick);

        renderQualityPanel(graphics, mouseX, mouseY);

        renderMainIngredientPreview(graphics);

        renderMainIngredientTooltip(graphics, mouseX, mouseY);

        renderTooltip(graphics, mouseX, mouseY);
    }
}
