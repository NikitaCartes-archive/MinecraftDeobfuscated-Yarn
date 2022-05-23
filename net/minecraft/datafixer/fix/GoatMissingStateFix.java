/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class GoatMissingStateFix
extends ChoiceFix {
    public GoatMissingStateFix(Schema schema) {
        super(schema, false, "EntityGoatMissingStateFix", TypeReferences.ENTITY, "minecraft:goat");
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), dynamic -> dynamic.set("HasLeftHorn", dynamic.createBoolean(true)).set("HasRightHorn", dynamic.createBoolean(true)));
    }
}

