/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.AbstractUuidFix;

public class BlockEntityUuidFix
extends AbstractUuidFix {
    public BlockEntityUuidFix(Schema outputSchema) {
        super(outputSchema, TypeReferences.BLOCK_ENTITY);
    }

    @Override
    protected TypeRewriteRule makeRule() {
        return this.fixTypeEverywhereTyped("BlockEntityUUIDFix", this.getInputSchema().getType(this.typeReference), typed -> {
            typed = this.updateTyped((Typed<?>)typed, "minecraft:conduit", this::updateConduit);
            typed = this.updateTyped((Typed<?>)typed, "minecraft:skull", this::updateSkull);
            return typed;
        });
    }

    private Dynamic<?> updateSkull(Dynamic<?> dynamic3) {
        return dynamic3.get("Owner").get().map(dynamic -> BlockEntityUuidFix.updateStringUuid(dynamic, "Id", "Id").orElse((Dynamic<?>)dynamic)).map(dynamic2 -> dynamic3.remove("Owner").set("SkullOwner", (Dynamic<?>)dynamic2)).result().orElse(dynamic3);
    }

    private Dynamic<?> updateConduit(Dynamic<?> dynamic) {
        return BlockEntityUuidFix.updateCompoundUuid(dynamic, "target_uuid", "Target").orElse(dynamic);
    }
}

