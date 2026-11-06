package net.elsrpop.reforgium.quality.attribute;

import net.elsrpop.reforgium.registry.ModAttributes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;

@EventBusSubscriber(
        modid = "reforgium",
        bus = EventBusSubscriber.Bus.GAME
)
public class JumpAttributeHandler {

    private static final double GRAVITY = 0.08D;

    @SubscribeEvent
    public static void onLivingJump(LivingEvent.LivingJumpEvent event) {

        LivingEntity entity = event.getEntity();

        AttributeInstance attribute =
                entity.getAttribute(ModAttributes.JUMP_HEIGHT);

        if (attribute == null) {
            return;
        }

        double flatBonus = 0.0D;
        double percentageBonus = 0.0D;

        for (AttributeModifier modifier : attribute.getModifiers()) {

            if (modifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
                flatBonus += modifier.amount();
            }

            if (modifier.operation() == AttributeModifier.Operation.ADD_MULTIPLIED_BASE) {
                percentageBonus += modifier.amount();
            }
        }

        if (flatBonus == 0.0D && percentageBonus == 0.0D) {
            return;
        }

        double jumpVelocity = entity.getDeltaMovement().y;

        double originalHeight =
                (jumpVelocity * jumpVelocity) / (2.0D * GRAVITY);

        double additionalHeight =
                flatBonus + (originalHeight * percentageBonus);

        double newVelocity = Math.sqrt(
                Math.max(
                        0.0D,
                        jumpVelocity * jumpVelocity
                                + 2.0D * GRAVITY * additionalHeight
                )
        );

        entity.setDeltaMovement(
                entity.getDeltaMovement().x,
                newVelocity,
                entity.getDeltaMovement().z
        );

        entity.hurtMarked = true;
    }
}