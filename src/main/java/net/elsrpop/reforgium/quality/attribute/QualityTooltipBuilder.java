
package net.elsrpop.reforgium.quality.attribute;

import net.elsrpop.reforgium.quality.QualityDefinition;
import net.elsrpop.reforgium.quality.QualityHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ItemAttributeModifiers;

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
        EquipmentSlotGroup slotGroup =
                slot.getType() == EquipmentSlot.Type.HAND
                        ? EquipmentSlotGroup.HAND
                        : EquipmentSlotGroup.bySlot(slot);

        Set<net.minecraft.resources.ResourceLocation> addedIds =
                new HashSet<>();

        // Modificadores originales del objeto.
        ItemAttributeModifiers itemModifiers = stack.getOrDefault(
                DataComponents.ATTRIBUTE_MODIFIERS,
                ItemAttributeModifiers.EMPTY
        );

        for (ItemAttributeModifiers.Entry entry : itemModifiers.modifiers()) {

            if (!entry.slot().test(slot)) {
                continue;
            }

            if (!supportedAttributes.contains(entry.attribute())) {
                continue;
            }

            result.computeIfAbsent(
                    entry.attribute(),
                    key -> new ArrayList<>()
            ).add(entry.modifier());

            addedIds.add(entry.modifier().id());
        }

        // Modificadores de calidad.
        QualityDefinition quality = QualityHelper.getQuality(stack);

        if (quality == null) {
            return;
        }

        for (QualityAttributeBuilder.AttributeEntry entry :
                QualityAttributeBuilder.build(
                        quality.id(),
                        quality.modifiers()
                )) {

            if (!supportedAttributes.contains(entry.attribute())) {
                continue;
            }

            if (!slotGroup.test(slot)) {
                continue;
            }

            // Evitar agregar dos veces el mismo modificador.
            if (!addedIds.add(entry.modifier().id())) {
                continue;
            }

            result.computeIfAbsent(
                    entry.attribute(),
                    key -> new ArrayList<>()
            ).add(entry.modifier());
        }
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
