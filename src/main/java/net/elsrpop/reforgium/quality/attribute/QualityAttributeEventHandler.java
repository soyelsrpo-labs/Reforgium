package net.elsrpop.reforgium.quality.attribute;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.quality.EquipmentType;
import net.elsrpop.reforgium.quality.EquipmentTypeHelper;
import net.elsrpop.reforgium.quality.QualityDefinition;
import net.elsrpop.reforgium.quality.QualityHelper;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;

@EventBusSubscriber(
        modid = Reforgium.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
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

        EquipmentType type = EquipmentTypeHelper.getType(stack);

        if (type != quality.equipmentType()) {
            return;
        }

        EquipmentSlotGroup slot;

        if (type == EquipmentType.ARMOR
                && stack.getItem() instanceof ArmorItem armor) {

            slot = EquipmentSlotGroup.bySlot(armor.getEquipmentSlot());

        } else {
            slot = EquipmentSlotGroup.HAND;
        }

        for (QualityAttributeBuilder.AttributeEntry entry :
                QualityAttributeBuilder.build(
                        quality.id(),
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