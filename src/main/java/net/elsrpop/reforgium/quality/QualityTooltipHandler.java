package net.elsrpop.reforgium.quality;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.quality.attribute.QualityAttribute;
import net.elsrpop.reforgium.quality.attribute.QualityModifier;
import net.elsrpop.reforgium.quality.attribute.QualityTooltipBuilder;
import net.elsrpop.reforgium.quality.attribute.VanillaAttributeMapper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
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

    // Prioridad mínima: corre después de los demás.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onItemTooltip(ItemTooltipEvent event) {

        ItemStack stack = event.getItemStack();

        if (stack.isEmpty()) return;

        if (QualityHelper.getQuality(stack) == null) return;

        // Líneas del modo avanzado (F3+H): durabilidad, id del objeto y
        // cantidad de componentes. Vanilla las pone justo debajo del nombre;
        // se sacan y se vuelven a poner al final de todo el tooltip.
        java.util.List<Component> advancedLines = new java.util.ArrayList<>();
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

        event.getToolTip().removeIf(line -> {
            boolean advanced = line.getString().equals(itemId)
                    || (line.getContents() instanceof TranslatableContents translatable
                    && (translatable.getKey().equals("item.components")
                    || translatable.getKey().equals("item.durability")));

            if (advanced) {
                advancedLines.add(line);
            }

            return advanced;
        });

        appendQualityTooltip(event);

        event.getToolTip().addAll(advancedLines);
    }

    private static void appendQualityTooltip(ItemTooltipEvent event) {

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

            case ARMOR, ARMOR_HELMET, ARMOR_CHESTPLATE, ARMOR_LEGGINGS, ARMOR_BOOTS -> {
                EquipmentSlot armorSlot =
                        QualityTooltipBuilder.getTargetSlot(stack);

                addArmorLine(
                        event, stack, armorSlot,
                        Attributes.ARMOR,
                        "Armadura"
                );

                addArmorLine(
                        event, stack, armorSlot,
                        Attributes.ARMOR_TOUGHNESS,
                        "Dureza de armadura"
                );
            }

            case WEAPON -> {
                addWeaponLine(
                        event, player, stack,
                        Attributes.ATTACK_DAMAGE,
                        "Daño de ataque"
                );

                addWeaponLine(
                        event, player, stack,
                        Attributes.ATTACK_SPEED,
                        "Velocidad de ataque"
                );
            }

            case TOOL -> addFinalAttribute(
                    event, player, stack,
                    QualityAttribute.MINING_SPEED,
                    "Velocidad de minería",
                    ChatFormatting.BLUE
            );

            case SHIELD -> {
                // Los escudos no muestran estadísticas finales.
            }
        }
    }

    // Línea de armadura al estilo vanilla:
    //   +4 Armadura [+3 base +1 calidad]
    // El total va en azul (rojo si es negativo) y lo que está entre
    // corchetes va en gris. Si la pieza no tiene ese atributo (ni base ni
    // de la calidad), la línea no se muestra.
    private static void addArmorLine(
            ItemTooltipEvent event,
            ItemStack stack,
            EquipmentSlot slot,
            Holder<Attribute> attribute,
            String displayName
    ) {
        if (slot == null) return;

        // 0 = valor base de la pieza, 1 = suma plana de la calidad,
        // 2 = porcentaje de la calidad.
        double[] sums = new double[3];

        stack.forEachModifier(slot, (modifierAttribute, modifier) -> {
            if (!modifierAttribute.equals(attribute)) return;

            boolean fromQuality =
                    modifier.id().getNamespace().equals(Reforgium.MOD_ID);

            switch (modifier.operation()) {
                case ADD_VALUE -> sums[fromQuality ? 1 : 0] += modifier.amount();
                case ADD_MULTIPLIED_BASE -> {
                    if (fromQuality) {
                        sums[2] += modifier.amount();
                    }
                }
                default -> {
                }
            }
        });

        double base = sums[0];
        double quality = sums[1] + base * sums[2];
        double total = base + quality;

        if (base == 0.0 && quality == 0.0) return;

        java.util.List<String> parts = new java.util.ArrayList<>();

        if (base != 0.0) {
            parts.add(signed(base));
        }

        if (quality != 0.0) {
            parts.add(signed(quality));
        }

        MutableComponent line = Component.literal(
                "  " + signed(total) + " " + displayName
        ).withStyle(total < 0 ? ChatFormatting.RED : ChatFormatting.BLUE);

        line.append(
                Component.literal(" [" + String.join(" ", parts) + "]")
                        .withStyle(ChatFormatting.GRAY)
        );

        event.getToolTip().add(line);
    }

    // Línea de arma al estilo vanilla, con el desglose solo en números:
    //   +8 Daño de ataque [1 + 6 + 1]
    //   +1.6 Velocidad de ataque [4 - 2.4 + 0.32]
    // Dentro de los corchetes: valor base del jugador, modificador del arma
    // y aporte de la calidad, cada uno con su signo. Texto en verde oscuro
    // (rojo si el total es negativo) y corchetes en gris.
    private static void addWeaponLine(
            ItemTooltipEvent event,
            Player player,
            ItemStack stack,
            Holder<Attribute> attribute,
            String displayName
    ) {
        EquipmentSlot slot = QualityTooltipBuilder.getTargetSlot(stack);

        if (slot == null) return;

        double playerBase = player.getAttributeBaseValue(attribute);

        // 0 = planos del arma, 1 = planos de la calidad,
        // 2 = porcentaje de la calidad.
        double[] sums = new double[3];

        stack.forEachModifier(slot, (modifierAttribute, modifier) -> {
            if (!modifierAttribute.equals(attribute)) return;

            boolean fromQuality =
                    modifier.id().getNamespace().equals(Reforgium.MOD_ID);

            switch (modifier.operation()) {
                case ADD_VALUE -> sums[fromQuality ? 1 : 0] += modifier.amount();
                case ADD_MULTIPLIED_BASE -> {
                    if (fromQuality) {
                        sums[2] += modifier.amount();
                    }
                }
                default -> {
                }
            }
        });

        double weapon = sums[0];

        // El porcentaje se calcula sobre base + planos, como hace el juego.
        double quality = sums[1]
                + (playerBase + weapon + sums[1]) * sums[2];

        if (weapon == 0.0 && quality == 0.0) return;

        double total = playerBase + weapon + quality;

        StringBuilder detail = new StringBuilder(formatPrecise(playerBase));

        if (weapon != 0.0) {
            detail.append(weapon < 0 ? " - " : " + ")
                    .append(formatPrecise(Math.abs(weapon)));
        }

        if (quality != 0.0) {
            detail.append(quality < 0 ? " - " : " + ")
                    .append(formatPrecise(Math.abs(quality)));
        }

        MutableComponent line = Component.literal(
                "  " + (total < 0 ? "-" : "+")
                        + formatPrecise(Math.abs(total)) + " " + displayName
        ).withStyle(total < 0 ? ChatFormatting.RED : ChatFormatting.DARK_GREEN);

        line.append(
                Component.literal(" [" + detail + "]")
                        .withStyle(ChatFormatting.GRAY)
        );

        event.getToolTip().add(line);
    }

    // Hasta 2 decimales, sin ceros de más.
    private static String formatPrecise(double value) {
        return String.format(
                java.util.Locale.ROOT,
                "%.2f",
                value
        ).replaceAll("\\.?0+$", "");
    }

    private static String signed(double value) {
        return (value < 0 ? "-" : "+") + formatValue(Math.abs(value));
    }

    private static void addFinalAttribute(
            ItemTooltipEvent event,
            Player player,
            ItemStack stack,
            QualityAttribute qualityAttribute,
            String displayName,
            ChatFormatting color
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
                ).withStyle(color)
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