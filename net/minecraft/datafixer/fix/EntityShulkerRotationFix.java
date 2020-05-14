/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.List;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class EntityShulkerRotationFix
extends ChoiceFix {
    public EntityShulkerRotationFix(Schema outputSchema) {
        super(outputSchema, false, "EntityShulkerRotationFix", TypeReferences.ENTITY, "minecraft:shulker");
    }

    public Dynamic<?> fixShulkerRotation(Dynamic<?> dynamic2) {
        List<Double> list = dynamic2.get("Rotation").asList(dynamic -> dynamic.asDouble(180.0));
        if (!list.isEmpty()) {
            list.set(0, list.get(0) - 180.0);
            return dynamic2.set("Rotation", dynamic2.createList(list.stream().map(dynamic2::createDouble)));
        }
        return dynamic2;
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), this::fixShulkerRotation);
    }
}

