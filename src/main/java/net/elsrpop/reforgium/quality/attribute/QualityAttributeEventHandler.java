package net.elsrpop.reforgium.quality.attribute;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.quality.EquipmentTypeHelper;
import net.elsrpop.reforgium.quality.QualityDefinition;
import net.elsrpop.reforgium.quality.QualityHelper;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

@EventBusSubscriber(modid = Reforgium.MOD_ID)
public class QualityAttributeEventHandler {

    @SubscribeEvent
    public static void onItemAttributeModifier(
            ItemAttributeModifierEvent event
    ) {
        ItemStack stack = event.getItemStack();

        if (stack.isEmpty()) {
            return;
        }

        QualityDefinition quality = QualityHelper.getQuality(stack);

        if (quality == null) {
            return;
        }

        if (!EquipmentTypeHelper.isCompatible(stack, quality)) {
            return;
        }

        EquipmentSlotGroup slot;
        String slotKey;

        if (stack.getItem() instanceof ArmorItem armor) {
            slot = EquipmentSlotGroup.bySlot(armor.getEquipmentSlot());
            slotKey = armor.getEquipmentSlot().getName();
        } else {
            slot = EquipmentSlotGroup.HAND;
            slotKey = "hand";
        }

        // El id incluye la pieza para que dos piezas con la misma calidad
        // no se pisen los modificadores entre sí.
        String idKey = quality.id() + "_" + slotKey;

        for (QualityAttributeBuilder.AttributeEntry entry :
                QualityAttributeBuilder.build(
                        idKey,
                        quality.modifiers()
                )) {

            event.addModifier(
                    entry.attribute(),
                    entry.modifier(),
                    slot
            );
        }
    }
}