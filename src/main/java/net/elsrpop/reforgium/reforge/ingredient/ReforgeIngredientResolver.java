
package net.elsrpop.reforgium.reforge.ingredient;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import java.util.Comparator;
import java.util.Optional;

public final class ReforgeIngredientResolver {

    private ReforgeIngredientResolver() {
    }

    public static Optional<Item> findMainMaterial(ItemStack stack, Level level) {
        if (stack.isEmpty() || level == null) {
            return Optional.empty();
        }

        Optional<Item> recipeMaterial = findMaterialFromRecipe(stack, level);

        if (recipeMaterial.isPresent()) {
            return recipeMaterial;
        }

        return findMaterialByTags(stack);
    }

    private static Optional<Item> findMaterialFromRecipe(
            ItemStack stack,
            Level level
    ) {
        return level.getRecipeManager()
                .getAllRecipesFor(RecipeType.CRAFTING)
                .stream()
                .filter(holder -> holder.value() instanceof CraftingRecipe)
                .filter(holder -> {
                    ItemStack result = holder.value()
                            .getResultItem(level.registryAccess());

                    return !result.isEmpty()
                            && ItemStack.isSameItem(result, stack);
                })
                .map(RecipeHolder::value)
                .flatMap(recipe -> recipe.getIngredients().stream())
                .flatMap(ingredient -> {
                    ItemStack[] items = ingredient.getItems();

                    return java.util.Arrays.stream(items);
                })
                .filter(item -> !item.isEmpty())
                .map(ItemStack::getItem)
                .filter(ReforgeIngredientResolver::isMainMaterial)
                .distinct()
                .sorted(Comparator.comparing(
                        item -> BuiltInRegistries.ITEM.getKey(item).toString()
                ))
                .findFirst();
    }

    private static boolean isMainMaterial(Item item) {
        return hasMaterialTag(item)
                || hasMaterialName(item);
    }

    private static boolean hasMaterialTag(Item item) {
        return item.builtInRegistryHolder().tags()
                .map(TagKey::location)
                .map(ResourceLocation::getPath)
                .anyMatch(ReforgeIngredientResolver::isMaterialTag);
    }

    private static boolean hasMaterialName(Item item) {
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();

        return path.contains("ingot")
                || path.contains("gem")
                || path.contains("diamond")
                || path.contains("emerald")
                || path.contains("netherite")
                || path.contains("leather")
                || path.contains("plank")
                || path.contains("log")
                || path.contains("wood");
    }

    private static boolean isMaterialTag(String path) {
        return path.startsWith("ingots/")
                || path.startsWith("gems/")
                || path.startsWith("ores/")
                || path.startsWith("nuggets/")
                || path.startsWith("raw_materials/")
                || path.startsWith("storage_blocks/")
                || path.startsWith("planks/")
                || path.startsWith("logs/")
                || path.startsWith("leathers/");
    }

    public static Optional<Item> findMaterialByTags(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }

        Item item = stack.getItem();

        return BuiltInRegistries.ITEM.getTagNames()
                .filter(tag -> isMaterialTag(tag.location().getPath()))
                .filter(tag -> item.builtInRegistryHolder().is(tag))
                .map(tag -> BuiltInRegistries.ITEM.getTag(tag))
                .flatMap(Optional::stream)
                .flatMap(named -> named.stream())
                .map(Holder::value)
                .findFirst();
    }
}
