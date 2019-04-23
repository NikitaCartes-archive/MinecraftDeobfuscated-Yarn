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

public class BlockEntityKeepPacked
extends ChoiceFix {
    public BlockEntityKeepPacked(Schema schema, boolean bl) {
        super(schema, bl, "BlockEntityKeepPacked", TypeReferences.BLOCK_ENTITY, "DUMMY");
    }

    private static Dynamic<?> keepPacked(Dynamic<?> dynamic) {
        return dynamic.set("keepPacked", dynamic.createBoolean(true));
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), BlockEntityKeepPacked::keepPacked);
    }
}

