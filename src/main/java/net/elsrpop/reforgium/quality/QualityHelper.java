package net.elsrpop.reforgium.quality;

import net.elsrpop.reforgium.quality.attribute.QualityAttribute;
import net.elsrpop.reforgium.quality.attribute.QualityModifier;
import net.elsrpop.reforgium.registry.ModDataComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public class QualityHelper {

    public static boolean setQuality(ItemStack stack, String qualityId) {
        if (stack.isEmpty()) return false;

        QualityDefinition quality = QualityManager.getById(qualityId);
        if (quality == null) return false;

        if (!EquipmentTypeHelper.isCompatible(stack, quality)) return false;

        // Guardamos la durabilidad original la primera vez.
        Integer originalMaxDamage = stack.get(ModDataComponents.ORIGINAL_MAX_DAMAGE.get());

        if (originalMaxDamage == null) {
            originalMaxDamage = stack.getMaxDamage();
            stack.set(ModDataComponents.ORIGINAL_MAX_DAMAGE.get(), originalMaxDamage);
        }

        // Restauramos temporalmente la durabilidad original.
        stack.set(DataComponents.MAX_DAMAGE, originalMaxDamage);

        // Buscamos el modificador de durabilidad.
        for (QualityModifier modifier : quality.modifiers()) {
            if (modifier.attribute() == QualityAttribute.MAX_DURABILITY) {
                int newMaxDamage;

                if (modifier.percentage()) {
                    newMaxDamage = (int) Math.round(
                            originalMaxDamage * (1.0 + modifier.value() / 100.0)
                    );
                } else {
                    newMaxDamage = (int) Math.round(
                            originalMaxDamage + modifier.value()
                    );
                }

                stack.set(DataComponents.MAX_DAMAGE, Math.max(1, newMaxDamage));
                break;
            }
        }

        stack.set(ModDataComponents.QUALITY.get(), qualityId);
        return true;
    }

    public static String getQualityId(ItemStack stack) {
        if (stack.isEmpty()) return null;
        return stack.get(ModDataComponents.QUALITY.get());
    }

    public static QualityDefinition getQuality(ItemStack stack) {
        String qualityId = getQualityId(stack);
        if (qualityId == null) return null;
        return QualityManager.getById(qualityId);
    }

    public static boolean hasQuality(ItemStack stack) {
        return getQualityId(stack) != null;
    }

    public static void removeQuality(ItemStack stack) {
        if (stack.isEmpty()) return;

        Integer originalMaxDamage = stack.get(ModDataComponents.ORIGINAL_MAX_DAMAGE.get());

        if (originalMaxDamage != null) {
            stack.set(DataComponents.MAX_DAMAGE, originalMaxDamage);
            stack.remove(ModDataComponents.ORIGINAL_MAX_DAMAGE.get());
        }

        stack.remove(ModDataComponents.QUALITY.get());
    }
}