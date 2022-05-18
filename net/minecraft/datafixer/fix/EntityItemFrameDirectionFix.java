/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class EntityItemFrameDirectionFix
extends ChoiceFix {
    public EntityItemFrameDirectionFix(Schema schema, boolean bl) {
        super(schema, bl, "EntityItemFrameDirectionFix", TypeReferences.ENTITY, "minecraft:item_frame");
    }

    public Dynamic<?> fixDirection(Dynamic<?> dynamic) {
        return dynamic.set("Facing", dynamic.createByte(EntityItemFrameDirectionFix.updateDirection(dynamic.get("Facing").asByte((byte)0))));
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), this::fixDirection);
    }

    private static byte updateDirection(byte b) {
        switch (b) {
            default: {
                return 2;
            }
            case 0: {
                return 3;
            }
            case 1: {
                return 4;
            }
            case 3: 
        }
        return 5;
    }
}

