
package net.elsrpop.reforgium.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.elsrpop.reforgium.Reforgium;
import net.elsrpop.reforgium.quality.QualityDefinition;
import net.elsrpop.reforgium.quality.QualityHelper;
import net.elsrpop.reforgium.quality.QualityManager;
import net.elsrpop.reforgium.quality.attribute.QualityModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = Reforgium.MOD_ID)
public class ModCommands {

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {

        event.getDispatcher().register(
                Commands.literal("reforgium")

                        .then(Commands.literal("test")
                                .executes(context -> {
                                    context.getSource().sendSuccess(
                                            () -> Component.literal(
                                                    "Reforgium funciona correctamente."
                                            ),
                                            false
                                    );

                                    return 1;
                                })
                        )

                        .then(Commands.literal("quality")

                                // Mostrar todas las calidades
                                .executes(context -> {

                                    var source = context.getSource();

                                    source.sendSuccess(
                                            () -> Component.literal(
                                                    "===== Calidades de Reforgium ====="
                                            ).withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD),
                                            false
                                    );

                                    for (QualityDefinition quality : QualityManager.getAll()) {

                                        ChatFormatting color = getRarityColor(quality.rarity());

                                        source.sendSuccess(
                                                () -> Component.literal(
                                                                quality.name()
                                                        ).withStyle(color, ChatFormatting.BOLD)
                                                        .append(Component.literal(
                                                                " [" + quality.id() + "]"
                                                        ).withStyle(ChatFormatting.GRAY))
                                                        .append(Component.literal(
                                                                "\n  Rareza: " + quality.rarity().name()
                                                        ).withStyle(color))
                                                        .append(Component.literal(
                                                                "\n  Tipo: " + quality.equipmentType().name()
                                                        ).withStyle(ChatFormatting.YELLOW)),
                                                false
                                        );

                                        for (QualityModifier modifier : quality.modifiers()) {

                                            String attribute = modifier.attribute()
                                                    .name()
                                                    .replace("_", " ");

                                            String value = modifier.percentage()
                                                    ? modifier.value() + "%"
                                                    : "+" + modifier.value();

                                            source.sendSuccess(
                                                    () -> Component.literal(
                                                            "  - " + attribute + ": " + value
                                                    ).withStyle(ChatFormatting.GREEN),
                                                    false
                                            );
                                        }
                                    }

                                    return 1;
                                })

                                // Asignar una calidad manualmente
                                .then(Commands.argument(
                                                        "quality",
                                                        StringArgumentType.word()
                                                )
                                                .executes(context -> {

                                                    String qualityId =
                                                            StringArgumentType.getString(
                                                                    context, "quality"
                                                            );

                                                    ItemStack stack = context.getSource()
                                                            .getPlayerOrException()
                                                            .getMainHandItem();

                                                    QualityDefinition quality =
                                                            QualityManager.getById(qualityId);

                                                    if (quality == null) {
                                                        context.getSource().sendFailure(
                                                                Component.literal(
                                                                        "Esa calidad no existe."
                                                                )
                                                        );
                                                        return 0;
                                                    }

                                                    if (!QualityHelper.setQuality(stack, qualityId)) {
                                                        context.getSource().sendFailure(
                                                                Component.literal(
                                                                        "No se pudo asignar esa calidad a este objeto."
                                                                )
                                                        );
                                                        return 0;
                                                    }

                                                    context.getSource().sendSuccess(
                                                            () -> Component.literal(
                                                                    "Calidad asignada: " + quality.name()
                                                            ).withStyle(ChatFormatting.GREEN),
                                                            false
                                                    );

                                                    return 1;
                                                })
                                )
                        )
        );
    }

    private static ChatFormatting getRarityColor(
            net.elsrpop.reforgium.quality.QualityRarity rarity
    ) {
        return switch (rarity) {
            case BAD -> ChatFormatting.RED;
            case COMMON -> ChatFormatting.WHITE;
            case GOOD -> ChatFormatting.GREEN;
            case RARE -> ChatFormatting.BLUE;
            case EPIC -> ChatFormatting.DARK_PURPLE;
            case EXOTIC -> ChatFormatting.AQUA;
            case LEGENDARY -> ChatFormatting.GOLD;
        };
    }
}
