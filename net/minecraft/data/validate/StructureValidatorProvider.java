/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.validate;

import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureValidatorProvider
implements SnbtProvider.Tweaker {
    private static final Logger field_24617 = LogManager.getLogger();

    @Override
    public CompoundTag write(String name, CompoundTag nbt) {
        if (name.startsWith("data/minecraft/structures/")) {
            return StructureValidatorProvider.method_32235(name, nbt);
        }
        return nbt;
    }

    public static CompoundTag method_32235(String string, CompoundTag compoundTag) {
        return StructureValidatorProvider.update(string, StructureValidatorProvider.addDataVersion(compoundTag));
    }

    private static CompoundTag addDataVersion(CompoundTag nbt) {
        if (!nbt.contains("DataVersion", 99)) {
            nbt.putInt("DataVersion", 500);
        }
        return nbt;
    }

    private static CompoundTag update(String string, CompoundTag compoundTag) {
        Structure structure = new Structure();
        int i = compoundTag.getInt("DataVersion");
        int j = 2678;
        if (i < 2678) {
            field_24617.warn("SNBT Too old, do not forget to update: {} < {}: {}", (Object)i, (Object)2678, (Object)string);
        }
        CompoundTag compoundTag2 = NbtHelper.update(Schemas.getFixer(), DataFixTypes.STRUCTURE, compoundTag, i);
        structure.fromTag(compoundTag2);
        return structure.toTag(new CompoundTag());
    }
}

