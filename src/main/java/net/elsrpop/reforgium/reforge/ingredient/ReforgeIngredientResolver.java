
package net.elsrpop.reforgium.reforge.ingredient;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.crafting.SmithingTransformRecipe;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public final class ReforgeIngredientResolver {

    private static final int MAX_CRAFTING_MATERIALS = 2;

    private ReforgeIngredientResolver() {
    }

    public static Set<Item> findMainMaterials(ItemStack stack, Level level) {

        Set<Item> materials = new LinkedHashSet<>();

        if (stack.isEmpty() || level == null) {
            return materials;
        }

        /*
         * HERRERÍA
         *
         * Conservamos el comportamiento anterior:
         * se aceptan todos los materiales de adición compatibles.
         */
        boolean foundSmithingRecipe = false;

        for (RecipeHolder<SmithingRecipe> holder :
                level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING)) {

            SmithingRecipe recipe = holder.value();

            if (!(recipe instanceof SmithingTransformRecipe transformRecipe)) {
                continue;
            }

            ItemStack result = transformRecipe.getResultItem(level.registryAccess());

            if (result.isEmpty() || !ItemStack.isSameItem(result, stack)) {
                continue;
            }

            foundSmithingRecipe = true;

            for (Item item : BuiltInRegistries.ITEM) {

                if (item == Items.AIR) {
                    continue;
                }

                ItemStack candidate = new ItemStack(item);

                if (transformRecipe.isAdditionIngredient(candidate)) {
                    materials.add(item);
                }
            }
        }

        if (foundSmithingRecipe) {
            return materials;
        }

        /*
         * CRAFTEO
         *
         * Reunimos los ingredientes posibles y después
         * seleccionamos como máximo dos, priorizando
         * los materiales valiosos.
         */
        Set<Item> candidates = new LinkedHashSet<>();

        for (RecipeHolder<CraftingRecipe> holder :
                level.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING)) {

            CraftingRecipe recipe = holder.value();

            ItemStack result = recipe.getResultItem(level.registryAccess());

            if (result.isEmpty() || !ItemStack.isSameItem(result, stack)) {
                continue;
            }

            for (Ingredient ingredient : recipe.getIngredients()) {

                for (ItemStack candidate : ingredient.getItems()) {

                    if (candidate.isEmpty()) {
                        continue;
                    }

                    Item item = candidate.getItem();

                    if (isStick(item)) {
                        continue;
                    }

                    if (item == stack.getItem()) {
                        continue;
                    }

                    candidates.add(item);
                }
            }
        }

        /*
         * ORDEN DE PRIORIDAD
         *
         * 0 = Lingotes y gemas.
         * 1 = Pepitas.
         * 2 = Cristales, fragmentos y materiales similares.
         * 3 = Otros ingredientes.
         */
        candidates.stream()
                .sorted(
                        Comparator
                                .comparingInt(ReforgeIngredientResolver::getMaterialPriority)
                                .thenComparing(item ->
                                        BuiltInRegistries.ITEM.getKey(item).toString()
                                )
                )
                .limit(MAX_CRAFTING_MATERIALS)
                .forEach(materials::add);

        return materials;
    }

    /*
     * PRIORIDAD DE LOS MATERIALES
     */
    private static int getMaterialPriority(Item item) {

        String path = BuiltInRegistries.ITEM.getKey(item).getPath();

        // Lingotes.
        if (path.endsWith("_ingot") || path.contains("ingot")) {
            return 0;
        }

        // Gemas y piedras preciosas.
        if (path.endsWith("_gem")
                || path.equals("diamond")
                || path.equals("emerald")
                || path.equals("amethyst_shard")
                || path.equals("prismarine_shard")
                || path.equals("quartz")) {
            return 0;
        }

        // Otros materiales valiosos.
        if (path.contains("crystal")
                || path.contains("shard")
                || path.contains("pearl")
                || path.contains("fragment")
                || path.contains("essence")) {
            return 1;
        }

        // Pepitas.
        if (path.endsWith("_nugget") || path.contains("nugget")) {
            return 2;
        }

        // Ingredientes restantes.
        return 3;
    }

    private static boolean isStick(Item item) {

        if (item == Items.STICK) {
            return true;
        }

        String path = BuiltInRegistries.ITEM.getKey(item).getPath();

        return path.contains("stick");
    }

    public static Optional<Item> findMainMaterial(
            ItemStack stack,
            Level level
    ) {
        return findMainMaterials(stack, level)
                .stream()
                .findFirst();
    }
}
