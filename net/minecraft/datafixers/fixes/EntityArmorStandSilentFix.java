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

public class EntityArmorStandSilentFix
extends ChoiceFix {
    public EntityArmorStandSilentFix(Schema schema, boolean bl) {
        super(schema, bl, "EntityArmorStandSilentFix", TypeReferences.ENTITY, "ArmorStand");
    }

    public Dynamic<?> method_15679(Dynamic<?> dynamic) {
        if (dynamic.get("Silent").asBoolean(false) && !dynamic.get("Marker").asBoolean(false)) {
            return dynamic.remove("Silent");
        }
        return dynamic;
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::method_15679);
    }
}

