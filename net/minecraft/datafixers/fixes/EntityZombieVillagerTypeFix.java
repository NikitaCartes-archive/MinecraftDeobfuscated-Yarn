/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Random;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.fixes.ChoiceFix;

public class EntityZombieVillagerTypeFix
extends ChoiceFix {
    private static final Random RANDOM = new Random();

    public EntityZombieVillagerTypeFix(Schema schema, boolean bl) {
        super(schema, bl, "EntityZombieVillagerTypeFix", TypeReferences.ENTITY, "Zombie");
    }

    public Dynamic<?> method_4990(Dynamic<?> dynamic) {
        if (dynamic.get("IsVillager").asBoolean(false)) {
            if (!dynamic.get("ZombieType").get().isPresent()) {
                int i = this.clampType(dynamic.get("VillagerProfession").asInt(-1));
                if (i == -1) {
                    i = this.clampType(RANDOM.nextInt(6));
                }
                dynamic = dynamic.set("ZombieType", dynamic.createInt(i));
            }
            dynamic = dynamic.remove("IsVillager");
        }
        return dynamic;
    }

    private int clampType(int i) {
        if (i < 0 || i >= 6) {
            return -1;
        }
        return i;
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::method_4990);
    }
}

