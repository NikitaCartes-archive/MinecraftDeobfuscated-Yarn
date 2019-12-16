/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class BlockEntityBannerColorFix
extends ChoiceFix {
    public BlockEntityBannerColorFix(Schema outputSchema, boolean changesType) {
        super(outputSchema, changesType, "BlockEntityBannerColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
    }

    public Dynamic<?> fixBannerColor(Dynamic<?> tag2) {
        tag2 = tag2.update("Base", tag -> tag.createInt(15 - tag.asInt(0)));
        tag2 = tag2.update("Patterns", dynamic -> DataFixUtils.orElse(dynamic.asStreamOpt().map(stream -> stream.map(dynamic -> dynamic.update("Color", tag -> tag.createInt(15 - tag.asInt(0))))).map(dynamic::createList), dynamic));
        return tag2;
    }

    @Override
    protected Typed<?> transform(Typed<?> typed) {
        return typed.update(DSL.remainderFinder(), this::fixBannerColor);
    }
}

