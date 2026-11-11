
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

        if (item instanceof ArmorItem armorItem) {
            return switch (armorItem.getType()) {
                case HELMET -> EquipmentType.ARMOR_HELMET;
                case CHESTPLATE -> EquipmentType.ARMOR_CHESTPLATE;
                case LEGGINGS -> EquipmentType.ARMOR_LEGGINGS;
                case BOOTS -> EquipmentType.ARMOR_BOOTS;
                default -> EquipmentType.ARMOR;
            };
        }

        if (item instanceof SwordItem) {
            return EquipmentType.WEAPON;
        }

        if (item instanceof DiggerItem) {
            return EquipmentType.TOOL;
        }

        if (item instanceof ShieldItem) {
            return EquipmentType.SHIELD;
        }

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

        EquipmentType itemType = getType(stack);
        EquipmentType qualityType = quality.equipmentType();

        if (itemType == null) {
            return false;
        }

        if (qualityType == EquipmentType.ARMOR) {
            return itemType == EquipmentType.ARMOR
                    || itemType == EquipmentType.ARMOR_HELMET
                    || itemType == EquipmentType.ARMOR_CHESTPLATE
                    || itemType == EquipmentType.ARMOR_LEGGINGS
                    || itemType == EquipmentType.ARMOR_BOOTS;
        }

        return itemType == qualityType;
    }
}
