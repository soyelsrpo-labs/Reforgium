
package net.elsrpop.reforgium.quality;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.quality.attribute.QualityAttribute;
import net.elsrpop.reforgium.quality.attribute.QualityModifier;
import net.elsrpop.reforgium.quality.attribute.QualityTooltipBuilder;
import net.elsrpop.reforgium.quality.attribute.VanillaAttributeMapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.Map;

@EventBusSubscriber(modid = Reforgium.MOD_ID)
public class QualityTooltipHandler {

    public static java.util.List<Component> getQualitySummary(ItemStack stack) {
        java.util.List<Component> lines = new java.util.ArrayList<>();

        if (stack.isEmpty()) {
            return lines;
        }

        QualityDefinition quality = QualityHelper.getQuality(stack);

        if (quality == null) {
            return lines;
        }

        // Nombre de la calidad.
        lines.add(
                Component.literal("Calidad: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(
                                Component.literal(quality.name())
                                        .withStyle(getRarityColor(quality.rarity()))
                        )
        );

        // Modificadores.
        for (QualityModifier modifier : quality.modifiers()) {
            String attributeName = getAttributeName(modifier);

            if (attributeName == null) {
                continue;
            }

            String value = modifier.percentage()
                    ? formatValue(Math.abs(modifier.value())) + "%"
                    : formatValue(Math.abs(modifier.value()));

            boolean negative = modifier.value() < 0;

            String prefix = negative ? "-" : "+";

            ChatFormatting modifierColor = negative
                    ? ChatFormatting.RED
                    : ChatFormatting.BLUE;

            lines.add(
                    Component.literal(
                            "  " + prefix + value + " " + attributeName
                    ).withStyle(modifierColor)
            );
        }

        // Rareza.
        lines.add(
                Component.literal(getRarityName(quality.rarity()))
                        .withStyle(getRarityColor(quality.rarity()))
        );

        return lines;
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {

        ItemStack stack = event.getItemStack();

        if (stack.isEmpty()) return;

        QualityDefinition quality = QualityHelper.getQuality(stack);

        if (quality == null) return;

        event.getToolTip().add(Component.empty());

        // Calidad y nombre.
        event.getToolTip().add(
                Component.literal("Calidad: ")
                        .withStyle(ChatFormatting.GRAY)
                        .append(
                                Component.literal(quality.name())
                                        .withStyle(getRarityColor(quality.rarity()))
                        )
        );


        // Modificadores de calidad.
        for (QualityModifier modifier : quality.modifiers()) {

            String attributeName = getAttributeName(modifier);

            if (attributeName == null) continue;

            String value = modifier.percentage()
                    ? formatValue(Math.abs(modifier.value())) + "%"
                    : formatValue(Math.abs(modifier.value()));

            boolean negative = modifier.value() < 0;

            String prefix = negative ? "-" : "+";

            ChatFormatting modifierColor = negative
                    ? ChatFormatting.RED
                    : ChatFormatting.BLUE;

            event.getToolTip().add(
                    Component.literal("  " + prefix + value + " " + attributeName)
                            .withStyle(modifierColor)
            );
        }

        // Rareza.
        event.getToolTip().add(
                Component.literal(getRarityName(quality.rarity()))
                        .withStyle(getRarityColor(quality.rarity()))
        );

        // Valores finales y espacio de equipamiento.
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        EquipmentSlot equippedSlot =
                QualityTooltipBuilder.getEquippedSlot(player, stack);

        if (equippedSlot == null) {
            equippedSlot = QualityTooltipBuilder.getTargetSlot(stack);
        }

        if (equippedSlot != null) {

            event.getToolTip().add(Component.empty());

            event.getToolTip().add(
                    Component.literal(
                            QualityTooltipBuilder.getSlotName(equippedSlot)
                    ).withStyle(ChatFormatting.GRAY)
            );
        }

        // Estadísticas finales según el tipo de equipo.
        switch (quality.equipmentType()) {

            case ARMOR -> {
                addFinalAttribute(
                        event, player, stack,
                        QualityAttribute.ARMOR,
                        "Armadura"
                );

                addFinalAttribute(
                        event, player, stack,
                        QualityAttribute.ARMOR_TOUGHNESS,
                        "Dureza de armadura"
                );
            }

            case WEAPON -> {
                addFinalAttribute(
                        event, player, stack,
                        QualityAttribute.ATTACK_DAMAGE,
                        "Daño de ataque"
                );

                addFinalAttribute(
                        event, player, stack,
                        QualityAttribute.ATTACK_SPEED,
                        "Velocidad de ataque"
                );
            }

            case TOOL -> addFinalAttribute(
                    event, player, stack,
                    QualityAttribute.MINING_SPEED,
                    "Velocidad de minería"
            );

            case SHIELD -> {
                // Los escudos no muestran estadísticas finales.
            }
        }
    }

    private static void addFinalAttribute(
            ItemTooltipEvent event,
            Player player,
            ItemStack stack,
            QualityAttribute qualityAttribute,
            String displayName
    ) {

        Holder<Attribute> attribute =
                VanillaAttributeMapper.getVanillaAttribute(qualityAttribute);

        if (attribute == null) return;

        Map<Holder<Attribute>, Double> values =
                QualityTooltipBuilder.calculateFinalValues(player, stack);

        Double value = values.get(attribute);

        if (value == null) return;

        event.getToolTip().add(
                Component.literal(
                        "  " +displayName + ": " + formatValue(value)
                ).withStyle(ChatFormatting.BLUE)
        );
    }

    // Traducción de rarezas.
    private static String getRarityName(QualityRarity rarity) {
        return switch (rarity) {
            case BAD -> "Común";
            case COMMON -> "Poco común";
            case GOOD -> "Buena";
            case RARE -> "Rara";
            case EPIC -> "Épica";
            case EXOTIC -> "Exótica";
            case LEGENDARY -> "Legendaria";
        };
    }

    // Procesar códigos de colores y formatos.
    private static MutableComponent parseFormattedText(String text) {

        MutableComponent result = Component.empty();
        Style currentStyle = Style.EMPTY.withColor(ChatFormatting.GRAY);

        StringBuilder currentText = new StringBuilder();

        for (int i = 0; i < text.length(); i++) {

            char character = text.charAt(i);

            if (character == '§' && i + 1 < text.length()) {

                if (!currentText.isEmpty()) {
                    result.append(
                            Component.literal(currentText.toString())
                                    .withStyle(currentStyle)
                    );

                    currentText.setLength(0);
                }

                ChatFormatting formatting =
                        ChatFormatting.getByCode(text.charAt(i + 1));

                if (formatting != null) {
                    if (formatting == ChatFormatting.RESET) {
                        currentStyle = Style.EMPTY;
                    } else {
                        currentStyle = currentStyle.applyFormat(formatting);
                    }
                }

                i++;

            } else {
                currentText.append(character);
            }
        }

        if (!currentText.isEmpty()) {
            result.append(
                    Component.literal(currentText.toString())
                            .withStyle(currentStyle)
            );
        }

        return result;
    }

    // Colores de rareza.
    private static ChatFormatting getRarityColor(QualityRarity rarity) {
        return switch (rarity) {
            case BAD -> ChatFormatting.RED;
            case COMMON -> ChatFormatting.GRAY;
            case GOOD -> ChatFormatting.GREEN;
            case RARE -> ChatFormatting.BLUE;
            case EPIC -> ChatFormatting.LIGHT_PURPLE;
            case EXOTIC -> ChatFormatting.AQUA;
            case LEGENDARY -> ChatFormatting.GOLD;
        };
    }

    // Nombres de los atributos.
    private static String getAttributeName(QualityModifier modifier) {
        return switch (modifier.attribute()) {
            case ATTACK_DAMAGE -> "Daño de ataque";
            case ATTACK_SPEED -> "Velocidad de ataque";
            case ATTACK_REACH -> "Alcance de ataque";
            case MINING_SPEED -> "Velocidad de minería";
            case BLOCK_REACH -> "Alcance de bloques";
            case MAX_DURABILITY -> "Durabilidad";
            case ARMOR -> "Armadura";
            case ARMOR_TOUGHNESS -> "Dureza de armadura";
            case MAX_HEALTH -> "Vida máxima";
            case MOVEMENT_SPEED -> "Velocidad de movimiento";
            case KNOCKBACK_RESISTANCE -> "Resistencia al retroceso";
            case LIFE_STEAL -> "Robo de vida";
            case LUCK -> "Suerte";
            case JUMP_HEIGHT -> "Altura al saltar";
            case KNOCKBACK -> "Empuje";

            default -> null;
        };
    }

    private static String formatValue(double value) {
        return String.format(
                java.util.Locale.ROOT,
                "%.1f",
                value
        ).replaceAll("\\.?0+$", "");
    }
}
