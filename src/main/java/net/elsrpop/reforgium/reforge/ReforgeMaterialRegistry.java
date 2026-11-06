
package net.elsrpop.reforgium.reforge;

import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class ReforgeMaterialRegistry {

    private static final Map<ResourceLocation, Integer> MATERIALS =
            new LinkedHashMap<>();

    private ReforgeMaterialRegistry() {
    }

    public static void register(ResourceLocation itemId, int tier) {
        if (tier < 1 || tier > 3) {
            throw new IllegalArgumentException(
                    "El nivel del material de reforja debe estar entre 1 y 3."
            );
        }

        MATERIALS.put(itemId, tier);
    }

    public static Optional<Integer> getTier(ResourceLocation itemId) {
        return Optional.ofNullable(MATERIALS.get(itemId));
    }

    public static boolean isReforgeMaterial(ResourceLocation itemId) {
        return MATERIALS.containsKey(itemId);
    }

    public static Map<ResourceLocation, Integer> getMaterials() {
        return Map.copyOf(MATERIALS);
    }
}
