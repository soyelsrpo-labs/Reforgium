package net.elsrpop.reforgium.quality.attribute;

import net.elsrpop.reforgium.registry.ModAttributes;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class VanillaAttributeMapper {

    public static Holder<Attribute> getVanillaAttribute(
            QualityAttribute attribute
    ) {
        return switch (attribute) {

            case ATTACK_DAMAGE -> Attributes.ATTACK_DAMAGE;
            case ATTACK_SPEED -> Attributes.ATTACK_SPEED;
            case ATTACK_REACH -> Attributes.ENTITY_INTERACTION_RANGE;
            case BLOCK_REACH -> Attributes.BLOCK_INTERACTION_RANGE;

            case ARMOR -> Attributes.ARMOR;
            case ARMOR_TOUGHNESS -> Attributes.ARMOR_TOUGHNESS;
            case MAX_HEALTH -> Attributes.MAX_HEALTH;
            case MOVEMENT_SPEED -> Attributes.MOVEMENT_SPEED;
            case KNOCKBACK_RESISTANCE -> Attributes.KNOCKBACK_RESISTANCE;

            case LUCK -> Attributes.LUCK;
            case JUMP_HEIGHT -> ModAttributes.JUMP_HEIGHT;
            case KNOCKBACK -> Attributes.ATTACK_KNOCKBACK;

            default -> null;
        };
    }

    public static boolean hasVanillaAttribute(
            QualityAttribute attribute
    ) {
        return getVanillaAttribute(attribute) != null;
    }
}