
package net.elsrpop.reforgium.client.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TexturedButton extends AbstractWidget {

    private static final ResourceLocation DISABLED_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "reforgium",
                    "textures/gui/buttons/reforge_disabled.png"
            );

    private static final ResourceLocation NORMAL_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "reforgium",
                    "textures/gui/buttons/reforge_normal.png"
            );

    private static final ResourceLocation HOVER_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "reforgium",
                    "textures/gui/buttons/reforge_hover.png"
            );

    private static final ResourceLocation PRESSED_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(
                    "reforgium",
                    "textures/gui/buttons/reforge_pressed.png"
            );

    private final Runnable onPress;

    private boolean pressed = false;

    public TexturedButton(
            int x,
            int y,
            Runnable onPress
    ) {
        super(x, y, 18, 18, Component.empty());
        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        ResourceLocation texture;

        if (!active) {
            texture = DISABLED_TEXTURE;
        } else if (pressed && isHovered) {
            texture = PRESSED_TEXTURE;
        } else if (isHovered) {
            texture = HOVER_TEXTURE;
        } else {
            texture = NORMAL_TEXTURE;
        }

        graphics.blit(
                texture,
                getX(),
                getY(),
                0,
                0,
                18,
                18,
                18,
                18
        );
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (active) {
            pressed = true;
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        boolean shouldPress = pressed && active && isHovered;

        pressed = false;

        if (shouldPress) {
            if (net.minecraft.client.Minecraft.getInstance().player != null) {
                net.minecraft.client.Minecraft.getInstance().player.playSound(
                        net.minecraft.sounds.SoundEvents.ANVIL_USE,
                        1.0F,
                        1.0F
                );
            }

            onPress.run();
        }
    }

    @Override
    protected void updateWidgetNarration(
            NarrationElementOutput narration
    ) {
        defaultButtonNarrationText(narration);
    }
}
