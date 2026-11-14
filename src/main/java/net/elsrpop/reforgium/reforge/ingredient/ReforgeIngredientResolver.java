
package net.elsrpop.reforgium.reforge.ingredient;

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


// RECETAS DE HERRERÍA
        for (RecipeHolder<SmithingRecipe> holder :
                level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING)) {

            SmithingRecipe recipe = holder.value();

            // Solo recetas de transformación.
            if (!(recipe instanceof SmithingTransformRecipe)) {
                continue;
            }

            // Comprobar que el resultado sea el objeto que queremos reforjar.
            ItemStack result = recipe.getResultItem(level.registryAccess());

            if (result.isEmpty() || !ItemStack.isSameItem(result, stack)) {
                continue;
            }

            // 0 = plantilla
            // 1 = objeto base
            // 2 = material de adición
            var ingredients = recipe.getIngredients();

            if (ingredients.size() < 3) {
                continue;
            }

            Ingredient addition = ingredients.get(2);

            // Obtener todos los materiales posibles.
            for (ItemStack candidate : addition.getItems()) {
                if (!candidate.isEmpty()) {
                    materials.add(candidate.getItem());
                }
            }
        }


        if (!materials.isEmpty()) {
            return materials;
        }

        // RECETAS DE CRAFTEO
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

                    // Excluir palos de los crafteos.
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

        String path = net.minecraft.core.registries.BuiltInRegistries.ITEM
                .getKey(item)
                .getPath();

        return path.contains("stick");
    }

    public static Optional<Item> findMainMaterial(ItemStack stack, Level level) {
        return findMainMaterials(stack, level)
                .stream()
                .findFirst();
    }
}
