/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.datafixer.fix;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.datafixer.fix.ChoiceFix;

public class BlockEntityBannerColorFix
extends ChoiceFix {
    public BlockEntityBannerColorFix(Schema schema, boolean bl) {
        super(schema, bl, "BlockEntityBannerColorFix", TypeReferences.BLOCK_ENTITY, "minecraft:banner");
    }

    public Dynamic<?> fixBannerColor(Dynamic<?> dynamic2) {
        dynamic2 = dynamic2.update("Base", dynamic -> dynamic.createInt(15 - dynamic.asInt(0)));
        dynamic2 = dynamic2.update("Patterns", dynamic -> DataFixUtils.orElse(dynamic.asStreamOpt().map(stream -> stream.map(dynamic2 -> dynamic2.update("Color", dynamic -> dynamic.createInt(15 - dynamic.asInt(0))))).map(dynamic::createList).result(), dynamic));
        return dynamic2;
    }

    @Override
    protected Typed<?> transform(Typed<?> inputType) {
        return inputType.update(DSL.remainderFinder(), this::fixBannerColor);
    }
}

