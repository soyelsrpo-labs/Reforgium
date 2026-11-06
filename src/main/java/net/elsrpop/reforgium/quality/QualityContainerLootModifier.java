
package net.elsrpop.reforgium.quality;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.LootModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

public class QualityContainerLootModifier extends LootModifier {

    public static final MapCodec<QualityContainerLootModifier> CODEC =
            RecordCodecBuilder.mapCodec(instance ->
                    LootModifier.codecStart(instance).apply(
                            instance,
                            QualityContainerLootModifier::new
                    )
            );

    public QualityContainerLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(
            ObjectArrayList<ItemStack> generatedLoot,
            LootContext context
    ) {
        for (ItemStack stack : generatedLoot) {
            if (stack.isEmpty()) continue;
            if (QualityHelper.hasQuality(stack)) continue;

            EquipmentType type = EquipmentTypeHelper.getType(stack);

            if (type == null) continue;

            QualityDefinition quality =
                    QualityRandomizer.getRandomQuality(type);

            if (quality == null) continue;

            QualityHelper.setQuality(stack, quality.id());
        }

        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
