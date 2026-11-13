
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

    private static final float PREVIEW_OPACITY = 0.25F;

    private TexturedButton reforgeButton;

    public ReforgeScreen(
            ReforgeMenu menu,
            Inventory inventory,
            Component title
    ) {
        super(menu, inventory, title);

        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    private Set<Item> getRequiredMaterials() {
        ItemStack original = menu.getSlot(0).getItem();

        if (original.isEmpty() || minecraft == null || minecraft.level == null) {
            return Set.of();
        }

        return ReforgeManager.getMainMaterials(original, minecraft.level);
    }

    private void renderMainIngredientPreview(GuiGraphics graphics) {
        // No mostramos la vista previa si el slot ya tiene un objeto.
        if (menu.getSlot(2).hasItem()) {
            return;
        }

        Set<Item> materials = getRequiredMaterials();

        if (materials.isEmpty()) {
            return;
        }

        // Mostramos el primer material válido como ejemplo.
        ItemStack preview = new ItemStack(materials.iterator().next());

        // Activamos la transparencia antes de renderizar.
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
            // Restauramos el estado gráfico.
            RenderSystem.setShaderColor(
                    1.0F,
                    1.0F,
                    1.0F,
                    1.0F
            );

            RenderSystem.disableBlend();
        }
    }

    private void renderMainIngredientTooltip(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        int slotX = leftPos + 28;
        int slotY = topPos + 45;

        // Comprobamos si el mouse está sobre el slot 2.
        if (mouseX < slotX || mouseX >= slotX + 16
                || mouseY < slotY || mouseY >= slotY + 16) {
            return;
        }

        if (menu.getSlot(2).hasItem()) {
            return;
        }

        Set<Item> materials = getRequiredMaterials();

        if (materials.isEmpty()) {
            return;
        }

        List<Component> tooltip = new ArrayList<>();

        tooltip.add(Component.literal("Materiales necesarios:"));

        for (Item item : materials) {
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

    @Override
    protected void containerTick() {
        super.containerTick();

        if (reforgeButton != null) {
            reforgeButton.active = menu.canReforgeNow();
        }
    }

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

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        super.render(graphics, mouseX, mouseY, partialTick);

        renderQualityPanel(graphics, mouseX, mouseY);

        renderMainIngredientPreview(graphics);

        renderMainIngredientTooltip(graphics, mouseX, mouseY);

        renderTooltip(graphics, mouseX, mouseY);
    }
}
