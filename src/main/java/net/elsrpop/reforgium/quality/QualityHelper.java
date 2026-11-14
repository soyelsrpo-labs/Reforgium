
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

        // Comprobamos si el objeto tiene durabilidad.
        int currentMaxDamage = stack.getMaxDamage();

        if (currentMaxDamage > 0) {

            // Recuperamos la durabilidad original si ya estaba guardada.
            Integer originalMaxDamage =
                    stack.get(ModDataComponents.ORIGINAL_MAX_DAMAGE.get());

            if (originalMaxDamage == null || originalMaxDamage <= 0) {
                originalMaxDamage = currentMaxDamage;

                stack.set(
                        ModDataComponents.ORIGINAL_MAX_DAMAGE.get(),
                        originalMaxDamage
                );
            }

            // Restauramos la durabilidad original antes de aplicar la calidad.
            stack.set(DataComponents.MAX_DAMAGE, originalMaxDamage);

            // Aplicamos el modificador de durabilidad de la calidad.
            for (QualityModifier modifier : quality.modifiers()) {

                if (modifier.attribute() == QualityAttribute.MAX_DURABILITY) {

                    int newMaxDamage;

                    if (modifier.percentage()) {
                        newMaxDamage = (int) Math.round(
                                originalMaxDamage *
                                        (1.0 + modifier.value() / 100.0)
                        );
                    } else {
                        newMaxDamage = (int) Math.round(
                                originalMaxDamage + modifier.value()
                        );
                    }

                    // MAX_DAMAGE siempre debe ser mayor que cero.
                    stack.set(
                            DataComponents.MAX_DAMAGE,
                            Math.max(1, newMaxDamage)
                    );

                    break;
                }
            }

        } else {

            // El objeto no tiene durabilidad.
            // No debemos asignarle MAX_DAMAGE ni guardar un valor cero.
            stack.remove(DataComponents.MAX_DAMAGE);
            stack.remove(ModDataComponents.ORIGINAL_MAX_DAMAGE.get());
        }

        // La calidad se aplica incluso si el objeto no tiene durabilidad.
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

        Integer originalMaxDamage =
                stack.get(ModDataComponents.ORIGINAL_MAX_DAMAGE.get());

        if (originalMaxDamage != null) {

            if (originalMaxDamage > 0) {
                stack.set(DataComponents.MAX_DAMAGE, originalMaxDamage);
            } else {
                stack.remove(DataComponents.MAX_DAMAGE);
            }

            stack.remove(ModDataComponents.ORIGINAL_MAX_DAMAGE.get());
        }

        stack.remove(ModDataComponents.QUALITY.get());
    }
}
