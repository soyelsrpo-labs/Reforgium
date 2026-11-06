
package net.elsrpop.reforgium.reforge.menu;

import net.elsrpop.reforgium.reforge.block.ReforgeTableBlockEntity;
import net.elsrpop.reforgium.reforge.ReforgeManager;
import net.elsrpop.reforgium.registry.ModItems;
import net.elsrpop.reforgium.registry.ModMenus;
import net.elsrpop.reforgium.registry.ModBlocks;
import net.elsrpop.reforgium.reforge.item.ReforgeHammerItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ReforgeMenu extends AbstractContainerMenu {

    public static final int BUTTON_REFORGE = 0;

    private final ReforgeTableBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public ReforgeMenu(
            int containerId,
            Inventory playerInventory,
            ReforgeTableBlockEntity blockEntity
    ) {
        super(ModMenus.REFORGE_MENU.get(), containerId);

        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(
                blockEntity.getLevel(),
                blockEntity.getBlockPos()
        );

        // Slot 0: objeto que queremos reforjar.
        addSlot(new Slot(blockEntity, 0, 95, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ReforgeManager.canReforge(
                        stack,
                        blockEntity.getLevel()
                );
            }
        });

        // Slot 1: material de reforja.
        addSlot(new Slot(blockEntity, 1, 28, 25) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ReforgeManager.isValidReforgeMaterial(stack);
            }
        });

        // Slot 2: material principal o Estrella del Nether.
        addSlot(new Slot(blockEntity, 2, 28, 45) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return ReforgeManager.isValidMainIngredient(
                        blockEntity.getItem(0),
                        stack,
                        blockEntity.getLevel()
                );
            }
        });

        // Slot 3: martillo de reforja.
        addSlot(new Slot(blockEntity, 3, 59, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return !stack.isEmpty()
                        && stack.is(ModItems.REFORGING_HAMMER.get())
                        && ReforgeHammerItem.hasUses(stack);
            }
        });

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }

    public ReforgeMenu(
            int containerId,
            Inventory playerInventory,
            FriendlyByteBuf buffer
    ) {
        this(
                containerId,
                playerInventory,
                getBlockEntity(playerInventory, buffer)
        );
    }

    private static ReforgeTableBlockEntity getBlockEntity(
            Inventory playerInventory,
            FriendlyByteBuf buffer
    ) {
        var pos = buffer.readBlockPos();

        var blockEntity = playerInventory.player
                .level()
                .getBlockEntity(pos);

        if (!(blockEntity instanceof ReforgeTableBlockEntity table)) {
            throw new IllegalStateException(
                    "No se encontró la mesa de reforja en " + pos
            );
        }

        return table;
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(
                        inventory,
                        column + row * 9 + 9,
                        8 + column * 18,
                        84 + row * 18
                ));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(
                    inventory,
                    column,
                    8 + column * 18,
                    142
            ));
        }
    }
    public boolean canReforgeNow() {
        var level = blockEntity.getLevel();

        if (level == null) {
            return false;
        }

        ItemStack original = blockEntity.getItem(0);
        ItemStack reforgeMaterial = blockEntity.getItem(1);
        ItemStack mainIngredient = blockEntity.getItem(2);
        ItemStack hammer = blockEntity.getItem(3);

        return ReforgeManager.canReforge(original, level)
                && ReforgeManager.isValidReforgeMaterial(reforgeMaterial)
                && ReforgeManager.isValidMainIngredient(
                original,
                mainIngredient,
                level
        )
                && !hammer.isEmpty()
                && hammer.is(ModItems.REFORGING_HAMMER.get())
                && ReforgeHammerItem.hasUses(hammer);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id != BUTTON_REFORGE) {
            return false;
        }

        if (player.level().isClientSide()) {
            return false;
        }

        if (!stillValid(player) || !canReforgeNow()) {
            return false;
        }

        ItemStack original = blockEntity.getItem(0);
        ItemStack reforgeMaterial = blockEntity.getItem(1);
        ItemStack mainIngredient = blockEntity.getItem(2);
        ItemStack hammer = blockEntity.getItem(3);

        // Primero intentamos realizar la reforja.
        var result = ReforgeManager.reforge(
                original,
                reforgeMaterial,
                mainIngredient,
                hammer,
                player.level()
        );

        // Si falla, no consumimos absolutamente nada.
        if (result.isEmpty()) {
            return false;
        }

        // Aplicamos el objeto reforjado.
        blockEntity.setItem(0, result.get());

        // Consumimos un material de reforja.
        reforgeMaterial.shrink(1);
        blockEntity.setItem(1, reforgeMaterial);

        // Consumimos un ingrediente principal o una Estrella del Nether.
        mainIngredient.shrink(1);
        blockEntity.setItem(2, mainIngredient);

        // Comprobamos si este uso va a romper el martillo.
        boolean willBreak =
                hammer.getDamageValue() + 1 >= hammer.getMaxDamage();

        // Consumimos un uso del martillo.
        ReforgeHammerItem.consumeUse(hammer);
        blockEntity.setItem(3, hammer);

        // Reproducimos el sonido si se rompió.
        if (willBreak) {
            player.level().playSound(
                    null,
                    player.blockPosition(),
                    net.minecraft.sounds.SoundEvents.ITEM_BREAK,
                    net.minecraft.sounds.SoundSource.PLAYERS,
                    1.0F,
                    1.0F
            );
        }

        return true;
    }


    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack original = slot.getItem();
            result = original.copy();

            if (index < ReforgeTableBlockEntity.SLOT_COUNT) {
                if (!moveItemStackTo(
                        original,
                        ReforgeTableBlockEntity.SLOT_COUNT,
                        slots.size(),
                        true
                )) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(
                    original,
                    0,
                    ReforgeTableBlockEntity.SLOT_COUNT,
                    false
            )) {
                return ItemStack.EMPTY;
            }

            if (original.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.REFORGE_TABLE.get());
    }

}
