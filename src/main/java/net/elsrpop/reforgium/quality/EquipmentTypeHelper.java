
package net.elsrpop.reforgium.quality;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ShieldItem;

public class EquipmentTypeHelper {

    public static final TagKey<Item> WEAPONS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("reforgium", "weapons"));

    public static final TagKey<Item> TOOLS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("reforgium", "tools"));

    public static final TagKey<Item> ARMOR =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("reforgium", "armor"));

    public static final TagKey<Item> SHIELDS =
            TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("reforgium", "shields"));

    public static EquipmentType getType(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }

        Item item = stack.getItem();

        // Detección por herencia de Minecraft.
        if (item instanceof SwordItem) {
            return EquipmentType.WEAPON;
        }

        if (item instanceof DiggerItem) {
            return EquipmentType.TOOL;
        }

        if (item instanceof ArmorItem) {
            return EquipmentType.ARMOR;
        }

        if (item instanceof ShieldItem) {
            return EquipmentType.SHIELD;
        }

        // Detección mediante tags.
        if (stack.is(WEAPONS)) {
            return EquipmentType.WEAPON;
        }

        if (stack.is(TOOLS)) {
            return EquipmentType.TOOL;
        }

        if (stack.is(ARMOR)) {
            return EquipmentType.ARMOR;
        }

        if (stack.is(SHIELDS)) {
            return EquipmentType.SHIELD;
        }

        return null;
    }

    public static boolean isCompatible(ItemStack stack, QualityDefinition quality) {
        if (quality == null) {
            return false;
        }

        return getType(stack) == quality.equipmentType();
    }
}
