
package net.elsrpop.reforgium.reforge.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ReforgeHammerItem extends Item {

    public static final int MAX_USES = 128;

    public ReforgeHammerItem() {
        super(new Item.Properties()
                .durability(MAX_USES)
        );
    }

    /**
     * Comprueba si el objeto es un martillo de reforja.
     */
    public static boolean isHammer(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() instanceof ReforgeHammerItem;
    }

    /**
     * Comprueba si el martillo todavía tiene usos disponibles.
     */
    public static boolean hasUses(ItemStack stack) {
        return isHammer(stack)
                && stack.getDamageValue() < stack.getMaxDamage();
    }

    /**
     * Consume un uso del martillo.
     *
     * Si alcanza su durabilidad máxima, se rompe y desaparece.
     *
     * @return true si se consumió un uso correctamente.
     */
    public static boolean consumeUse(ItemStack stack) {

        if (!hasUses(stack)) {
            return false;
        }

        int newDamage = stack.getDamageValue() + 1;

        if (newDamage >= stack.getMaxDamage()) {
            stack.shrink(1);
            return true;
        }

        stack.setDamageValue(newDamage);
        return true;
    }
}
