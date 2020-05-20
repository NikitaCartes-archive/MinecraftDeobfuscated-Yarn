/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.BlockStateFlattening;
import net.minecraft.datafixer.fix.ChoiceFix;

public class BlockEntityBlockStateFix
extends ChoiceFix {
    public BlockEntityBlockStateFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "BlockEntityBlockStateFix", TypeReferences.BLOCK_ENTITY, "minecraft:piston");
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        Type<?> type = this.getOutputSchema().getChoiceType(TypeReferences.BLOCK_ENTITY, "minecraft:piston");
        Type<?> type2 = type.findFieldType("blockState");
        OpticFinder<?> opticFinder = DSL.fieldFinder("blockState", type2);
        Dynamic<?> dynamic = inputType.get(DSL.remainderFinder());
        int i = dynamic.get("blockId").asInt(0);
        dynamic = dynamic.remove("blockId");
        int j = dynamic.get("blockData").asInt(0) & 0xF;
        dynamic = dynamic.remove("blockData");
        Dynamic<?> dynamic2 = BlockStateFlattening.lookupState(i << 4 | j);
        Typed<?> typed = type.pointTyped(inputType.getOps()).orElseThrow(() -> new IllegalStateException("Could not create new piston block entity."));
        return typed.set(DSL.remainderFinder(), dynamic).set(opticFinder, type2.readTyped(dynamic2).result().orElseThrow(() -> new IllegalStateException("Could not parse newly created block state tag.")).getFirst());
    }
}

