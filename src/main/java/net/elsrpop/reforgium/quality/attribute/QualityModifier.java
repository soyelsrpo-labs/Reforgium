package net.elsrpop.reforgium.quality.attribute;

public record QualityModifier(
        QualityAttribute attribute,
        double value,
        boolean percentage
) {
}