/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.AbstractUuidFix;
import net.minecraft.datafixer.fix.EntityUuidFix;

public class PlayerUuidFix
extends AbstractUuidFix {
    public PlayerUuidFix(Schema outputSchema) {
        super(outputSchema, TypeReferences.PLAYER);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("PlayerUUIDFix", this.getInputSchema().getType(this.typeReference), typed2 -> {
            OpticFinder<?> opticFinder = typed2.getType().findField("RootVehicle");
            return typed2.updateTyped(opticFinder, opticFinder.type(), (Typed<?> typed) -> typed.update(DSL.remainderFinder(), dynamic -> PlayerUuidFix.updateRegularMostLeast(dynamic, "Attach", "Attach").orElse((Dynamic<?>)dynamic))).update(DSL.remainderFinder(), dynamic -> EntityUuidFix.updateSelfUuid(EntityUuidFix.updateLiving(dynamic)));
        });
    }
}

