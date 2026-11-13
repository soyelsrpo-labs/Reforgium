package net.elsrpop.reforgium.reforge;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.quality.EquipmentTypeHelper;
import net.elsrpop.reforgium.quality.QualityDefinition;
import net.elsrpop.reforgium.quality.QualityHelper;
import net.elsrpop.reforgium.quality.QualityManager;
import net.elsrpop.reforgium.quality.QualityRarity;
import net.elsrpop.reforgium.reforge.ingredient.ReforgeIngredientResolver;
import net.elsrpop.reforgium.reforge.item.ReforgeHammerItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import java.util.Set;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public final class ReforgeManager {

    private static final int MAX_ROLL_ATTEMPTS = 1000;

    private ReforgeManager() {
    }

    /**
     * Comprueba si el objeto es compatible con el sistema de reforja.
     */
    public static boolean canReforge(ItemStack stack, Level level) {

        if (stack.isEmpty() || level == null) {
            return false;
        }

        if (stack.getMaxDamage() <= 0) {
            return false;
        }

        return EquipmentTypeHelper.getType(stack) != null;
    }

    /**
     * Obtiene el material principal del objeto.
     */
    public static Set<Item> getMainMaterials(
            ItemStack stack,
            Level level
    ) {
        return ReforgeIngredientResolver.findMainMaterials(stack, level);
    }

    // Se mantiene por compatibilidad con el código anterior.
    public static Optional<Item> getMainMaterial(
            ItemStack stack,
            Level level
    ) {
        return getMainMaterials(stack, level)
                .stream()
                .findFirst();
    }

    /**
     * Comprueba si el material de reforja es válido.
     */
    public static boolean isValidReforgeMaterial(ItemStack material) {

        if (material.isEmpty()) {
            return false;
        }

        return ReforgeMaterialRegistry.getTier(
                BuiltInRegistries.ITEM.getKey(material.getItem())
        ).isPresent();
    }

    /**
     * Comprueba si el ingrediente principal es válido.
     * Acepta el material principal o una Estrella del Nether.
     */
    public static boolean isValidMainIngredient(
            ItemStack original,
            ItemStack ingredient,
            Level level
    ) {
        if (original.isEmpty() || ingredient.isEmpty() || level == null) {
            return false;
        }

        if (ingredient.is(Items.NETHER_STAR)) {
            return true;
        }

        Optional<Item> mainMaterial = getMainMaterial(original, level);

        return mainMaterial.isPresent()
                && ingredient.is(mainMaterial.get());
    }

    /**
     * Realiza una reforja y devuelve una copia del objeto con su nueva calidad.
     * <p>
     * No consume materiales ni durabilidad del martillo.
     */
    public static Optional<ItemStack> reforge(
            ItemStack original,
            ItemStack reforgeMaterial,
            ItemStack mainIngredient,
            ItemStack hammer,
            Level level
    ) {
        if (!canReforge(original, level)) {
            return Optional.empty();
        }

        if (!isValidReforgeMaterial(reforgeMaterial)) {
            return Optional.empty();
        }

        if (!isValidMainIngredient(original, mainIngredient, level)) {
            return Optional.empty();
        }

        // El martillo debe ser válido y tener usos disponibles.
        if (!ReforgeHammerItem.hasUses(hammer)) {
            return Optional.empty();
        }

        int tier = ReforgeMaterialRegistry.getTier(
                BuiltInRegistries.ITEM.getKey(reforgeMaterial.getItem())
        ).orElseThrow();

        QualityDefinition selectedQuality = null;

        // Sorteamos hasta encontrar una rareza compatible.
        for (int attempt = 0; attempt < MAX_ROLL_ATTEMPTS; attempt++) {

            // Primero sorteamos la rareza.
            QualityRarity rarity = ReforgeChanceManager.roll(tier);

            // Buscamos todas las calidades compatibles con esa rareza.
            List<QualityDefinition> availableQualities = QualityManager.getAll().stream()
                    .filter(quality -> quality.rarity() == rarity)
                    .filter(quality -> EquipmentTypeHelper.isCompatible(original, quality))
                    .toList();

            // Elegimos una calidad aleatoria entre las disponibles.
            if (!availableQualities.isEmpty()) {

                int randomIndex = ThreadLocalRandom.current()
                        .nextInt(availableQualities.size());

                selectedQuality = availableQualities.get(randomIndex);

                Reforgium.LOGGER.debug("Rareza: {} | Seleccionada: {}", rarity, selectedQuality.id());

                break;
            }
        }

        if (selectedQuality == null) {
            return Optional.empty();
        }

        // Trabajamos sobre una copia para preservar el objeto original.
        ItemStack result = original.copy();

        if (!QualityHelper.setQuality(result, selectedQuality.id())) {
            return Optional.empty();
        }

        return Optional.of(result);
    }

}