
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

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

public final class ReforgeIngredientResolver {

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
         * Buscamos recetas de transformación cuyo resultado
         * sea el objeto que queremos reforjar.
         *
         * El único ingrediente que nos interesa es el de adición.
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

            /*
             * No usamos getIngredients().
             *
             * Comprobamos directamente qué objetos acepta
             * la receta como material de adición.
             */
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

        /*
         * Si encontramos una receta de herrería para este objeto,
         * devolvemos sus materiales aunque el conjunto esté vacío.
         *
         * Así evitamos mezclar materiales de recetas de crafteo.
         */
        if (foundSmithingRecipe) {
            return materials;
        }

        /*
         * CRAFTEO
         *
         * Si no existe una receta de herrería aplicable,
         * buscamos los materiales de las recetas de crafteo.
         */
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

                    materials.add(item);
                }
            }
        }

        return materials;
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
