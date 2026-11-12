package net.elsrpop.reforgium.quality.attribute;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class QualityTooltipBuilder {

    private record ModifierEntry(
            Holder<Attribute> attribute,
            AttributeModifier modifier
    ) {
    }

    public static EquipmentSlot getTargetSlot(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        if (stack.getItem() instanceof ArmorItem armor) {
            return armor.getEquipmentSlot();
        }

        return EquipmentSlot.MAINHAND;
    }

    public static String getSlotName(EquipmentSlot slot) {
        return switch (slot) {
            case MAINHAND -> "En la mano principal";
            case OFFHAND -> "En la mano secundaria";
            case HEAD -> "En el casco";
            case CHEST -> "En el pecho";
            case LEGS -> "En las piernas";
            case FEET -> "En las botas";
            default -> "Equipado";
        };
    }

    public static Map<Holder<Attribute>, Double> calculateFinalValues(
            Player player,
            ItemStack targetStack
    ) {
        Map<Holder<Attribute>, Double> result = new HashMap<>();

        if (player == null || targetStack.isEmpty()) {
            return result;
        }

        EquipmentSlot targetSlot = getTargetSlot(targetStack);

        if (targetSlot == null) {
            return result;
        }

        Map<Holder<Attribute>, List<AttributeModifier>> modifiers =
                new HashMap<>();

        // Obtener los atributos vanilla que reconoce Reforgium.
        Set<Holder<Attribute>> supportedAttributes = new HashSet<>();

        for (QualityAttribute attribute : QualityAttribute.values()) {
            Holder<Attribute> holder =
                    VanillaAttributeMapper.getVanillaAttribute(attribute);

            if (holder != null) {
                supportedAttributes.add(holder);
            }
        }

        // Recorrer el equipamiento actual, excepto el espacio simulado.
        for (EquipmentSlot slot : EquipmentSlot.values()) {

            if (slot == targetSlot) {
                continue;
            }

            ItemStack equippedStack = player.getItemBySlot(slot);

            if (equippedStack.isEmpty()) {
                continue;
            }

            addStackModifiers(
                    equippedStack,
                    slot,
                    supportedAttributes,
                    modifiers
            );
        }

        // Agregar los modificadores del objeto que estamos inspeccionando.
        addStackModifiers(
                targetStack,
                targetSlot,
                supportedAttributes,
                modifiers
        );

        // Calcular los valores finales.
        for (Holder<Attribute> attribute : supportedAttributes) {

            AttributeInstance instance = player.getAttribute(attribute);

            if (instance == null) {
                continue;
            }

            double value = instance.getBaseValue();

            List<AttributeModifier> attributeModifiers =
                    modifiers.getOrDefault(attribute, List.of());

            // ADD_VALUE
            for (AttributeModifier modifier : attributeModifiers) {
                if (modifier.operation()
                        == AttributeModifier.Operation.ADD_VALUE) {
                    value += modifier.amount();
                }
            }

            // ADD_MULTIPLIED_BASE
            double baseValue = value;

            for (AttributeModifier modifier : attributeModifiers) {
                if (modifier.operation()
                        == AttributeModifier.Operation.ADD_MULTIPLIED_BASE) {
                    value += baseValue * modifier.amount();
                }
            }

            // ADD_MULTIPLIED_TOTAL
            for (AttributeModifier modifier : attributeModifiers) {
                if (modifier.operation()
                        == AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL) {
                    value *= 1.0 + modifier.amount();
                }
            }

            result.put(attribute, value);
        }

        return result;
    }

    private static void addStackModifiers(
            ItemStack stack,
            EquipmentSlot slot,
            Set<Holder<Attribute>> supportedAttributes,
            Map<Holder<Attribute>, List<AttributeModifier>> result
    ) {
        // Usa los modificadores que el juego aplica de verdad al equipar:
        // los base del objeto (por ejemplo, los 3 de armadura de unas botas
        // de diamante) más los que agrega la calidad por ItemAttributeModifierEvent.
        stack.forEachModifier(slot, (attribute, modifier) -> {
            if (!supportedAttributes.contains(attribute)) {
                return;
            }

            result.computeIfAbsent(
                    attribute,
                    key -> new ArrayList<>()
            ).add(modifier);
        });
    }

    public static EquipmentSlot getEquippedSlot(
            Player player,
            ItemStack stack
    ) {
        if (player == null || stack.isEmpty()) {
            return null;
        }

        EquipmentSlot[] slots = {
                EquipmentSlot.MAINHAND,
                EquipmentSlot.OFFHAND,
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        };

        for (EquipmentSlot slot : slots) {
            ItemStack equippedStack = player.getItemBySlot(slot);

            if (!equippedStack.isEmpty()
                    && ItemStack.isSameItemSameComponents(stack, equippedStack)) {
                return slot;
            }
        }

        return null;
    }
}