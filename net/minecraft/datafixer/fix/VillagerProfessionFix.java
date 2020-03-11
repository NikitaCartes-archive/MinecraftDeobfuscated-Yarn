/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class VillagerProfessionFix
extends ChoiceFix {
    public VillagerProfessionFix(Schema outputSchema, String entity) {
        super(outputSchema, false, "Villager profession data fix (" + entity + ")", TypeReferences.ENTITY, entity);
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        Dynamic dynamic = inputType.get(DSL.remainderFinder());
        return inputType.set(DSL.remainderFinder(), dynamic.remove("Profession").remove("Career").remove("CareerLevel").set("VillagerData", dynamic.createMap(ImmutableMap.of(dynamic.createString("type"), dynamic.createString("minecraft:plains"), dynamic.createString("profession"), dynamic.createString(VillagerProfessionFix.convertProfessionId(dynamic.get("Profession").asInt(0), dynamic.get("Career").asInt(0))), dynamic.createString("level"), DataFixUtils.orElse(dynamic.get("CareerLevel").get(), dynamic.createInt(1))))));
    }

    private static String convertProfessionId(int professionId, int careerId) {
        if (professionId == 0) {
            if (careerId == 2) {
                return "minecraft:fisherman";
            }
            if (careerId == 3) {
                return "minecraft:shepherd";
            }
            if (careerId == 4) {
                return "minecraft:fletcher";
            }
            return "minecraft:farmer";
        }
        if (professionId == 1) {
            if (careerId == 2) {
                return "minecraft:cartographer";
            }
            return "minecraft:librarian";
        }
        if (professionId == 2) {
            return "minecraft:cleric";
        }
        if (professionId == 3) {
            if (careerId == 2) {
                return "minecraft:weaponsmith";
            }
            if (careerId == 3) {
                return "minecraft:toolsmith";
            }
            return "minecraft:armorer";
        }
        if (professionId == 4) {
            if (careerId == 2) {
                return "minecraft:leatherworker";
            }
            return "minecraft:butcher";
        }
        if (professionId == 5) {
            return "minecraft:nitwit";
        }
        return "minecraft:none";
    }
}

