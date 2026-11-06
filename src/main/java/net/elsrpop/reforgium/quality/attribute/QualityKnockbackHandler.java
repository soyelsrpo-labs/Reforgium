
package net.elsrpop.reforgium.quality.attribute;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.quality.QualityDefinition;
import net.elsrpop.reforgium.quality.QualityHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;

import java.util.Map;
import java.util.WeakHashMap;

@EventBusSubscriber(
        modid = Reforgium.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME
)
public class QualityKnockbackHandler {

    private record PendingKnockback(
            double flatBonus,
            double percentageBonus,
            long tick
    ) {}

    private static final Map<LivingEntity, PendingKnockback> PENDING =
            new WeakHashMap<>();

    @SubscribeEvent
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {

        LivingEntity target = event.getEntity();

        if (target.level().isClientSide()) return;

        if (!(event.getSource().getEntity() instanceof Player player)) {
            return;
        }

        ItemStack stack = player.getMainHandItem();

        QualityDefinition quality = QualityHelper.getQuality(stack);

        if (quality == null) return;

        double flatBonus = 0.0D;
        double percentageBonus = 0.0D;

        for (QualityModifier modifier : quality.modifiers()) {

            if (modifier.attribute() != QualityAttribute.KNOCKBACK) {
                continue;
            }

            if (modifier.percentage()) {
                percentageBonus += modifier.value() / 100.0D;
            } else {
                flatBonus += modifier.value();
            }
        }

        if (flatBonus == 0.0D && percentageBonus == 0.0D) {
            return;
        }

        PENDING.put(
                target,
                new PendingKnockback(
                        flatBonus,
                        percentageBonus,
                        target.level().getGameTime()
                )
        );
    }

    @SubscribeEvent
    public static void onKnockback(LivingKnockBackEvent event) {

        LivingEntity target = event.getEntity();

        if (target.level().isClientSide()) return;

        PendingKnockback pending = PENDING.remove(target);

        if (pending == null) return;

        if (pending.tick() != target.level().getGameTime()) {
            return;
        }

        float originalStrength = event.getStrength();

        float newStrength = (float) Math.max(
                0.0D,
                originalStrength
                        + pending.flatBonus()
                        + originalStrength * pending.percentageBonus()
        );

        event.setStrength(newStrength);
    }
}
