
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
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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

        // Recetas de crafteo.
        level.getRecipeManager()
                .getAllRecipesFor(RecipeType.CRAFTING)
                .stream()
                .filter(holder -> holder.value() instanceof CraftingRecipe)
                .filter(holder -> {
                    ItemStack result = holder.value()
                            .getResultItem(level.registryAccess());

                    return !result.isEmpty()
                            && ItemStack.isSameItem(result, stack);
                })
                .forEach(holder -> {
                    Map<Item, Integer> candidates = new HashMap<>();

                    holder.value().getIngredients().forEach(ingredient ->
                            collectCandidates(ingredient, candidates, stack)
                    );

                    addBestCandidates(candidates, materials);
                });

        // Recetas de la mesa de herrería.
        level.getRecipeManager()
                .getAllRecipesFor(RecipeType.SMITHING)
                .stream()
                .map(RecipeHolder::value)
                .filter(recipe -> recipe instanceof SmithingRecipe)
                .filter(recipe -> {
                    ItemStack result = recipe.getResultItem(level.registryAccess());

                    return !result.isEmpty()
                            && ItemStack.isSameItem(result, stack);
                })
                .forEach(recipe -> {
                    SmithingRecipe smithing = (SmithingRecipe) recipe;
                    Map<Item, Integer> candidates = new HashMap<>();

                    for (Ingredient ingredient : smithing.getIngredients()) {
                        for (ItemStack candidate : ingredient.getItems()) {
                            if (candidate.isEmpty()
                                    || !smithing.isAdditionIngredient(candidate)) {
                                continue;
                            }

                            addCandidate(candidates, candidate.getItem(), stack);
                        }
                    }

                    addBestCandidates(candidates, materials);
                });

        // Alternativa por etiquetas si no encontramos recetas.
        if (materials.isEmpty()) {
            findMaterialByTags(stack).ifPresent(materials::add);
        }

        return materials;
    }

    // Se mantiene para compatibilidad con código que todavía espera un solo material.
    public static Optional<Item> findMainMaterial(ItemStack stack, Level level) {
        return findMainMaterials(stack, level)
                .stream()
                .findFirst();
    }

    private static void collectCandidates(
            Ingredient ingredient,
            Map<Item, Integer> candidates,
            ItemStack original
    ) {
        for (ItemStack candidate : ingredient.getItems()) {
            if (candidate.isEmpty()) {
                continue;
            }

            addCandidate(candidates, candidate.getItem(), original);
        }
    }

    private static void addCandidate(
            Map<Item, Integer> candidates,
            Item item,
            ItemStack original
    ) {
        if (item == original.getItem() || isWoodMaterial(item)) {
            return;
        }

        candidates.merge(item, 1, Integer::sum);
    }

    private static void addBestCandidates(
            Map<Item, Integer> candidates,
            Set<Item> result
    ) {
        if (candidates.isEmpty()) {
            return;
        }

        boolean hasKnownMaterial = candidates.keySet()
                .stream()
                .anyMatch(ReforgeIngredientResolver::isKnownMaterial);

        int maxCount = candidates.entrySet()
                .stream()
                .filter(entry ->
                        !hasKnownMaterial || isKnownMaterial(entry.getKey())
                )
                .mapToInt(Map.Entry::getValue)
                .max()
                .orElse(0);

        candidates.entrySet()
                .stream()
                .filter(entry ->
                        (!hasKnownMaterial || isKnownMaterial(entry.getKey()))
                                && entry.getValue() == maxCount
                )
                .sorted((a, b) ->
                        BuiltInRegistries.ITEM.getKey(a.getKey()).toString()
                                .compareTo(
                                        BuiltInRegistries.ITEM.getKey(b.getKey()).toString()
                                )
                )
                .map(Map.Entry::getKey)
                .forEach(result::add);
    }

    private static boolean isKnownMaterial(Item item) {
        return hasMaterialTag(item) || hasMaterialName(item);
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
                || path.contains("leather");
    }

    private static boolean isMaterialTag(String path) {
        return path.startsWith("ingots/")
                || path.startsWith("gems/")
                || path.startsWith("ores/")
                || path.startsWith("nuggets/")
                || path.startsWith("raw_materials/")
                || path.startsWith("storage_blocks/")
                || path.startsWith("leathers/");
    }

    private static boolean isWoodMaterial(Item item) {
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();

        if (path.contains("wood")
                || path.contains("plank")
                || path.contains("log")
                || path.contains("stick")
                || path.contains("bamboo")) {
            return true;
        }

        return item.builtInRegistryHolder().tags()
                .map(TagKey::location)
                .map(ResourceLocation::getPath)
                .anyMatch(tag ->
                        tag.startsWith("planks/")
                                || tag.startsWith("logs/")
                                || tag.contains("wooden_rods")
                );
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
                .filter(material -> !isWoodMaterial(material))
                .findFirst();
    }
}
