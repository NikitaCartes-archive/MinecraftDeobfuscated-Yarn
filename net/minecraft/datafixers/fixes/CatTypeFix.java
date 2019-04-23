/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixers.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixers.TypeReferences;
import net.minecraft.datafixers.fixes.ChoiceFix;

public class CatTypeFix
extends ChoiceFix {
    public CatTypeFix(Schema schema, boolean bl) {
        super(schema, bl, "CatTypeFix", TypeReferences.ENTITY, "minecraft:cat");
    }

    public Dynamic<?> fixCatTypeData(Dynamic<?> dynamic) {
        if (dynamic.get("CatType").asInt(0) == 9) {
            return dynamic.set("CatType", dynamic.createInt(10));
        }
        return dynamic;
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixCatTypeData);
    }
}

