package net.elsrpop.reforgium.quality;

import net.elsrpop.reforgium.quality.attribute.QualityAttribute;
import net.elsrpop.reforgium.quality.attribute.QualityModifier;

import java.util.List;

public class QualityRegistry {

    public static void registerAll() {

        // ARMAS

        //Malas

        register("very_heavy", "Muy pesada", "",
                QualityRarity.BAD, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_SPEED, -20, true),
                modifier(QualityAttribute.MOVEMENT_SPEED,-15, true));

        register("no_sharpening", "Sin filo", "",
                QualityRarity.BAD, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, -25, true));

        register("short", "corta", "",
                QualityRarity.BAD, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_REACH, -20, true));

        register("about_to_break", "A punto de romperse", "",
                QualityRarity.BAD, EquipmentType.WEAPON,
                modifier(QualityAttribute.MAX_DURABILITY, -50, true));

        register("worn", "Gastada", "",
                QualityRarity.BAD, EquipmentType.WEAPON,
                modifier(QualityAttribute.MAX_DURABILITY, -20, true),
                modifier(QualityAttribute.ATTACK_DAMAGE, -5, true));

        register("unbalanced", "Desequilibrada", "",
                QualityRarity.BAD, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, -10, true),
                modifier(QualityAttribute.ATTACK_SPEED, -10, true));

        register("jinxed", "Impía", "",
                QualityRarity.BAD, EquipmentType.WEAPON,
                modifier(QualityAttribute.LUCK, -1, false));

        register("draining", "Drenante", "",
                QualityRarity.BAD, EquipmentType.WEAPON,
                modifier(QualityAttribute.MAX_HEALTH, -2, false));

        //Comunes

        register("light", "Ligera", "",
                QualityRarity.COMMON, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_SPEED, 10, true),
                modifier(QualityAttribute.ATTACK_DAMAGE, -1, false));

        register("small", "Pequeña", "",
                QualityRarity.COMMON, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_REACH, -5, true));

        register("unstable", "Inestable", "",
                QualityRarity.COMMON, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 0.5, false),
                modifier(QualityAttribute.MAX_DURABILITY, -20, true),
                modifier(QualityAttribute.MOVEMENT_SPEED, -5, true));

        register("heavy", "Pesada", "",
                QualityRarity.COMMON, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 1, false),
                modifier(QualityAttribute.ATTACK_SPEED, -15, true),
                modifier(QualityAttribute.MOVEMENT_SPEED, -15, true));

        register("crude", "Tosca", "",
                QualityRarity.COMMON, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 1, false),
                modifier(QualityAttribute.MAX_DURABILITY, -20, true));

        register("sturdy", "Sólida", "",
                QualityRarity.COMMON, EquipmentType.WEAPON,
                modifier(QualityAttribute.MAX_DURABILITY, 10, true),
                modifier(QualityAttribute.ATTACK_SPEED, -5, true));

        register("stiff", "Rígida", "",
                QualityRarity.COMMON, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_REACH, 5, true),
                modifier(QualityAttribute.ATTACK_SPEED, -10, true));

        register("airy", "Aérea", "",
                QualityRarity.COMMON, EquipmentType.WEAPON,
                modifier(QualityAttribute.MOVEMENT_SPEED, 5, true),
                modifier(QualityAttribute.ATTACK_DAMAGE, -1, false));

        register("rough", "Áspera", "",
                QualityRarity.COMMON, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 0.5, false),
                modifier(QualityAttribute.ATTACK_SPEED, -5, true));

        register("fortunate", "Afortunada", "",
                QualityRarity.COMMON, EquipmentType.WEAPON,
                modifier(QualityAttribute.LUCK, 1, false),
                modifier(QualityAttribute.ATTACK_DAMAGE, -1, false));

        register("lively", "Vivaz", "",
                QualityRarity.COMMON, EquipmentType.WEAPON,
                modifier(QualityAttribute.MAX_HEALTH, 1, false),
                modifier(QualityAttribute.ATTACK_SPEED, -10, true));

        //buenas

        register("sharp", "Afilada", "",
                QualityRarity.GOOD, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 1, false));

        register("balanced1", "Equilibrada i", "",
                QualityRarity.GOOD, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 0.5, false),
                modifier(QualityAttribute.MAX_DURABILITY, 5, true),
                modifier(QualityAttribute.ATTACK_SPEED, 5, true));

        register("resistant", "Resistente", "",
                QualityRarity.GOOD, EquipmentType.WEAPON,
                modifier(QualityAttribute.MAX_DURABILITY, 15, true));

        register("powered", "Poderosa", "",
                QualityRarity.GOOD, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 0.5, false),
                modifier(QualityAttribute.KNOCKBACK, 0.5, false));

        register("flexible", "Flexible", "",
                QualityRarity.GOOD, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_REACH, 0.2, false),
                modifier(QualityAttribute.ATTACK_SPEED, 15, true));

        register("extensive", "Extensa", "",
                QualityRarity.GOOD, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_REACH, 0.5, false));

        register("swift", "Rápida", "",
                QualityRarity.GOOD, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_SPEED, 20, true));

        register("precise", "Precisa", "",
                QualityRarity.GOOD, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_REACH, 0.3, false),
                modifier(QualityAttribute.ATTACK_DAMAGE, 0.5, false));

        register("forged", "Forjada", "",
                QualityRarity.GOOD, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 0.5, false),
                modifier(QualityAttribute.MAX_DURABILITY, 10, true));

        register("stalwart", "Firme", "",
                QualityRarity.GOOD, EquipmentType.WEAPON,
                modifier(QualityAttribute.KNOCKBACK, 0.3, false),
                modifier(QualityAttribute.MAX_DURABILITY, 10, true));

        register("lucky", "Suertuda I", "",
                QualityRarity.GOOD, EquipmentType.WEAPON,
                modifier(QualityAttribute.LUCK, 1, false),
                modifier(QualityAttribute.ATTACK_DAMAGE, 0.5, false));

        register("expansive", "Expansivo", "",
                QualityRarity.GOOD, EquipmentType.WEAPON,
                modifier(QualityAttribute.SWEEPING_DAMAGE, 0.10, false));

        //RARAS

        register("long", "Larga", "",
                QualityRarity.RARE, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_REACH, 0.7, false));

        register("balanced2", "Equilibrada II", "",
                QualityRarity.RARE, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 1, false),
                modifier(QualityAttribute.MAX_DURABILITY, 10, true),
                modifier(QualityAttribute.ATTACK_SPEED, 10, true));

        register("accurate", "Certera", "",
                QualityRarity.RARE, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 1.7, false));


        register("fortified", "Fortificada", "",
                QualityRarity.RARE, EquipmentType.WEAPON,
                modifier(QualityAttribute.MAX_DURABILITY, 25, true));

        register("exceptional", "Excepcional", "",
                QualityRarity.RARE, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 1.25, false),
                modifier(QualityAttribute.ATTACK_REACH, 0.5, false),
                modifier(QualityAttribute.ATTACK_SPEED, 15, true));

        register("impeccable", "Impecable", "",
                QualityRarity.RARE, EquipmentType.WEAPON,
                modifier(QualityAttribute.MAX_DURABILITY, 15, true),
                modifier(QualityAttribute.ATTACK_SPEED, 20, true));


        register("speedster", "Velosista", "",
                QualityRarity.RARE, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_SPEED, 30, true));

        register("lucky2", "Suertuda II", "",
                QualityRarity.RARE, EquipmentType.WEAPON,
                modifier(QualityAttribute.LUCK, 2, false),
                modifier(QualityAttribute.ATTACK_DAMAGE, 1, false));

        register("devastating", "Devastadora", "",
                QualityRarity.RARE, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 2, false),
                modifier(QualityAttribute.ATTACK_SPEED, -10, true));

        register("colossal", "Colosal", "",
                QualityRarity.RARE, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 1, false),
                modifier(QualityAttribute.SWEEPING_DAMAGE, 0.50, false),
                modifier(QualityAttribute.ATTACK_REACH, 0.75, false));


        //EPICAS

        register("long2", "Larga II", "",
                QualityRarity.EPIC, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_REACH, 1.5, false));

        register("balanced3", "Equilibrada III", "",
                QualityRarity.EPIC, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 2, false),
                modifier(QualityAttribute.ATTACK_SPEED, 20, true),
                modifier(QualityAttribute.ATTACK_REACH, 0.5, false),
                modifier(QualityAttribute.MAX_DURABILITY, 20, true));

        register("accurate2", "Certera II", "",
                QualityRarity.EPIC, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 2.5, false));


        register("fortified2", "Irrompible", "",
                QualityRarity.EPIC, EquipmentType.WEAPON,
                modifier(QualityAttribute.MAX_DURABILITY, 40, true));

        register("extraordinary", "Extraordinaria", "",
                QualityRarity.EPIC, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 3.5, false),
                modifier(QualityAttribute.ATTACK_SPEED, 25, true),
                modifier(QualityAttribute.ATTACK_REACH, -0.5, false),
                modifier(QualityAttribute.MAX_DURABILITY, -40, true));

        register("destructor", "Debastadora", "",
                QualityRarity.EPIC, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 1.5, false),
                modifier(QualityAttribute.SWEEPING_DAMAGE, 0.50, false),
                modifier(QualityAttribute.KNOCKBACK, 20, true));

        //EXOTICAS

        register("bloodthirsty", "Sanguinaria", "",
                QualityRarity.EXOTIC, EquipmentType.WEAPON,
                modifier(QualityAttribute.LIFE_STEAL, 1, false),
                modifier(QualityAttribute.ATTACK_DAMAGE, -2, false),
                modifier(QualityAttribute.ATTACK_SPEED, -30, true));

        register("venomous", "Venenosa", "",
                QualityRarity.EXOTIC, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, -1.5, false),
                modifier(QualityAttribute.ATTACK_SPEED, -25, true));

        //legendarias

        register("mityc", "Mitica", "",
                QualityRarity.LEGENDARY, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 3, false),
                modifier(QualityAttribute.ATTACK_SPEED, 50, true),
                modifier(QualityAttribute.MAX_DURABILITY, 70, true));

        register("divine", "Divina", "",
                QualityRarity.LEGENDARY, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 3, false),
                modifier(QualityAttribute.ATTACK_REACH, 2, false),
                modifier(QualityAttribute.SWEEPING_DAMAGE, 1, false));

        register("absolute", "Absoluta", "",
                QualityRarity.LEGENDARY, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 4, false));

        register("inmortal", "Inmortal", "",
                QualityRarity.LEGENDARY, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 2, false),
                modifier(QualityAttribute.MAX_DURABILITY, 200, true));

        register("primordial", "Primordial", "",
                QualityRarity.LEGENDARY, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 2.5, false),
                modifier(QualityAttribute.ATTACK_SPEED, 30, true),
                modifier(QualityAttribute.ATTACK_REACH, 1, false),
                modifier(QualityAttribute.MAX_DURABILITY, 40, true),
                modifier(QualityAttribute.KNOCKBACK, 1, false));

        register("Sagrada", "Sagrada", "",
                QualityRarity.LEGENDARY, EquipmentType.WEAPON,
                modifier(QualityAttribute.ATTACK_DAMAGE, 3, false),
                modifier(QualityAttribute.KNOCKBACK, 3, false));

        // HERRAMIENTAS

        register("mining", "Minera",
                "Aumenta un 100% la velocidad de minería y 5 bloques el alcance de bloque.",
                QualityRarity.COMMON, EquipmentType.TOOL,
                modifier(QualityAttribute.MINING_SPEED, 100, true),
                modifier(QualityAttribute.BLOCK_REACH, 5, false));

        register("efficient", "Eficiente", "Aumenta la velocidad de minería un 8%.",
                QualityRarity.GOOD, EquipmentType.TOOL,
                modifier(QualityAttribute.MINING_SPEED, 8, true));

        register("durable", "Resistente", "Aumenta la durabilidad un 20%.",
                QualityRarity.RARE, EquipmentType.TOOL,
                modifier(QualityAttribute.MAX_DURABILITY, 20, true));

        register("excavator", "Excavadora",
                "Aumenta la velocidad de minería un 10% y el alcance de bloque en 1.",
                QualityRarity.EPIC, EquipmentType.TOOL,
                modifier(QualityAttribute.MINING_SPEED, 10, true),
                modifier(QualityAttribute.BLOCK_REACH, 1, false));


        // ARMADURAS

        //MALAS

        register("fragile", "Fragil", "",
                QualityRarity.BAD, EquipmentType.ARMOR,
                modifier(QualityAttribute.MAX_DURABILITY, -50, true));

        register("heavy_armor", "Pesada", "",
                QualityRarity.BAD, EquipmentType.ARMOR,
                modifier(QualityAttribute.MOVEMENT_SPEED, -30, true));

        register("damaged_armor", "Dañada", "",
                QualityRarity.BAD, EquipmentType.ARMOR,
                modifier(QualityAttribute.MAX_DURABILITY, -15, true),
                modifier(QualityAttribute.ARMOR, -1, false));

        register("overwhelming", "Agobiante", "",
                QualityRarity.BAD, EquipmentType.ARMOR,
                modifier(QualityAttribute.MOVEMENT_SPEED, -10, true),
                modifier(QualityAttribute.MAX_HEALTH, -2, false));

        register("uncomfortable", "Incómoda", "",
                QualityRarity.BAD, EquipmentType.ARMOR,
                modifier(QualityAttribute.MOVEMENT_SPEED, -10, true),
                modifier(QualityAttribute.ARMOR, -0.5, false));

        //COMUNES

        register("guardian", "Liviana", "",
                QualityRarity.COMMON, EquipmentType.ARMOR,
                modifier(QualityAttribute.MOVEMENT_SPEED, 10, true),
                modifier(QualityAttribute.ARMOR, -1, false));

        register("guardian", "Inestable", "",
                QualityRarity.COMMON, EquipmentType.ARMOR,
                modifier(QualityAttribute.ARMOR, 0.5, false),
                modifier(QualityAttribute.MAX_DURABILITY, -20, true));

        register("robust", "Dura", "",
                QualityRarity.COMMON, EquipmentType.ARMOR,
                modifier(QualityAttribute.MAX_DURABILITY, 20, true),
                modifier(QualityAttribute.MOVEMENT_SPEED, -15 , true));

        register("robust", "lenta", "",
                QualityRarity.COMMON, EquipmentType.ARMOR,
                modifier(QualityAttribute.MOVEMENT_SPEED, -25 , true));

        //BUENAS

        register("fragile", "protectora", "",
                QualityRarity.GOOD, EquipmentType.ARMOR,
                modifier(QualityAttribute.ARMOR, 1, false));

        register("heavy_armor", "Reforzada", "",
                QualityRarity.GOOD, EquipmentType.ARMOR,
                modifier(QualityAttribute.MAX_DURABILITY, 25, true));

        register("overwhelming", "Ligera", "",
                QualityRarity.GOOD, EquipmentType.ARMOR,
                modifier(QualityAttribute.MOVEMENT_SPEED, 20, true));

        register("uncomfortable", "Cómoda", "",
                QualityRarity.GOOD, EquipmentType.ARMOR,
                modifier(QualityAttribute.ARMOR,  0.5, false),
                modifier(QualityAttribute.MOVEMENT_SPEED, 10, true));

        //RARAS

        register("fragile", "protectora", "",
                QualityRarity.GOOD, EquipmentType.ARMOR,
                modifier(QualityAttribute.ARMOR, 1, false));

        register("heavy_armor", "Reforzada", "",
                QualityRarity.GOOD, EquipmentType.ARMOR,
                modifier(QualityAttribute.MAX_DURABILITY, 25, true));

        register("overwhelming", "Ligera", "",
                QualityRarity.GOOD, EquipmentType.ARMOR,
                modifier(QualityAttribute.MOVEMENT_SPEED, 20, true));

        register("uncomfortable", "Cómoda", "",
                QualityRarity.GOOD, EquipmentType.ARMOR,
                modifier(QualityAttribute.ARMOR,  0.5, false),
                modifier(QualityAttribute.MOVEMENT_SPEED, 10, true));







        // ESCUDOS
        register("fortified", "Fortificado", "Aumenta la durabilidad un 20%.",
                QualityRarity.COMMON, EquipmentType.SHIELD,
                modifier(QualityAttribute.MAX_DURABILITY, 20, true));

        register("protector", "Protector", "Añade 1 punto de armadura.",
                QualityRarity.GOOD, EquipmentType.SHIELD,
                modifier(QualityAttribute.ARMOR, 1, false));

        register("light", "Ligero", "Aumenta la velocidad de movimiento un 3%.",
                QualityRarity.RARE, EquipmentType.SHIELD,
                modifier(QualityAttribute.MOVEMENT_SPEED, 3, true));

        register("reinforced", "Reforzado",
                "Añade 1 punto de armadura y un 10% de durabilidad.",
                QualityRarity.EPIC, EquipmentType.SHIELD,
                modifier(QualityAttribute.ARMOR, 1, false),
                modifier(QualityAttribute.MAX_DURABILITY, 10, true));
    }

    private static QualityModifier modifier(
            QualityAttribute attribute,
            double value,
            boolean percentage
    ) {
        return new QualityModifier(attribute, value, percentage);
    }

    private static void register(
            String id,
            String name,
            String description,
            QualityRarity rarity,
            EquipmentType type,
            QualityModifier... modifiers
    ) {
        QualityManager.register(
                new QualityDefinition(
                        id,
                        name,
                        description,
                        rarity,
                        type,
                        List.of(modifiers)
                )
        );
    }
}