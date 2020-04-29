/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class StriderGravityFix
extends ChoiceFix {
    public StriderGravityFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "StriderGravityFix", TypeReferences.ENTITY, "minecraft:strider");
    }

    public Dynamic<?> updateNoGravityTag(Dynamic<?> data) {
        if (data.get("NoGravity").asBoolean(false)) {
            return data.set("NoGravity", data.createBoolean(false));
        }
        return data;
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), this::updateNoGravityTag);
    }
}

