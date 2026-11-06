
package net.elsrpop.reforgium.quality.effect;

import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.quality.attribute.QualityAttribute;
import net.elsrpop.reforgium.quality.attribute.QualityModifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.elsrpop.reforgium.quality.QualityDefinition;
import net.elsrpop.reforgium.quality.QualityHelper;

@EventBusSubscriber(modid = Reforgium.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class QualityEffectHandler {

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Post event) {
        if (!(event.getEntity() instanceof LivingEntity target)) return;
        if (!(event.getSource().getEntity() instanceof Player player)) return;

        ItemStack stack = player.getMainHandItem();
        QualityDefinition quality = QualityHelper.getQuality(stack);

        if (quality == null) return;

        // Efecto de la calidad Venenosa.
        if (quality.id().equals("venomous")) {
            target.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
        }

        // Efecto de robo de vida.
        for (QualityModifier modifier : quality.modifiers()) {
            if (modifier.attribute() != QualityAttribute.LIFE_STEAL) continue;

            float healing;

            if (modifier.percentage()) {
                healing = (float) (event.getNewDamage() * modifier.value() / 100.0);
            } else {
                healing = (float) modifier.value();
            }

            if (healing > 0.0F) {
                player.heal(healing);
            }

            break;
        }
    }
}
