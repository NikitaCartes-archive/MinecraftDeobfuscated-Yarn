/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.serialization.Dynamic;
import java.util.Optional;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.util.math.MathHelper;

public class VillagerXpRebuildFix
extends DataFix {
    private static final int field_29914 = 2;
    private static final int[] LEVEL_TO_XP = new int[]{0, 10, 50, 100, 150};

    public static int levelToXp(int level) {
        return LEVEL_TO_XP[MathHelper.clamp(level - 1, 0, LEVEL_TO_XP.length - 1)];
    }

    public VillagerXpRebuildFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType);
    }

    @Override
    public TypeRewriteRule makeRule() {
        Type<?> type = this.getInputSchema().getChoiceType(TypeReferences.ENTITY, "minecraft:villager");
        OpticFinder<?> opticFinder = DSL.namedChoice("minecraft:villager", type);
        OpticFinder<?> opticFinder2 = type.findField("Offers");
        Type<?> type2 = opticFinder2.type();
        OpticFinder<?> opticFinder3 = type2.findField("Recipes");
        List.ListType listType = (List.ListType)opticFinder3.type();
        OpticFinder opticFinder4 = listType.getElement().finder();
        return this.fixTypeEverywhereTyped("Villager level and xp rebuild", this.getInputSchema().getType(TypeReferences.ENTITY), typed -> typed.updateTyped(opticFinder, type, typed2 -> {
            Optional<Number> optional;
            int j;
            Dynamic<?> dynamic = typed2.get(DSL.remainderFinder());
            int i = dynamic.get("VillagerData").get("level").asInt(0);
            Typed<?> typed22 = typed2;
            if ((i == 0 || i == 1) && (i = MathHelper.clamp((j = typed2.getOptionalTyped(opticFinder2).flatMap(typed -> typed.getOptionalTyped(opticFinder3)).map(typed -> typed.getAllTyped(opticFinder4).size()).orElse(0).intValue()) / 2, 1, 5)) > 1) {
                typed22 = VillagerXpRebuildFix.method_20487(typed22, i);
            }
            if (!(optional = dynamic.get("Xp").asNumber().result()).isPresent()) {
                typed22 = VillagerXpRebuildFix.method_20490(typed22, i);
            }
            return typed22;
        }));
    }

    private static Typed<?> method_20487(Typed<?> typed, int i) {
        return typed.update(DSL.remainderFinder(), dynamic2 -> dynamic2.update("VillagerData", dynamic -> dynamic.set("level", dynamic.createInt(i))));
    }

    private static Typed<?> method_20490(Typed<?> typed, int i) {
        int j = VillagerXpRebuildFix.levelToXp(i);
        return typed.update(DSL.remainderFinder(), dynamic -> dynamic.set("Xp", dynamic.createInt(j)));
    }
}

