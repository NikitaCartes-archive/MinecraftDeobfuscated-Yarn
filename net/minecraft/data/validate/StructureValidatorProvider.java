/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.validate;

import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.datafixers.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;

public class StructureValidatorProvider
implements SnbtProvider.Tweaker {
    @Override
    public CompoundTag write(String string, CompoundTag compoundTag) {
        if (string.startsWith("data/minecraft/structures/")) {
            return StructureValidatorProvider.update(StructureValidatorProvider.addDataVersion(compoundTag));
        }
        return compoundTag;
    }

    private static CompoundTag addDataVersion(CompoundTag compoundTag) {
        if (!compoundTag.contains("DataVersion", 99)) {
            compoundTag.putInt("DataVersion", 500);
        }
        return compoundTag;
    }

    private static CompoundTag update(CompoundTag compoundTag) {
        Structure structure = new Structure();
        structure.fromTag(NbtHelper.update(Schemas.getFixer(), DataFixTypes.STRUCTURE, compoundTag, compoundTag.getInt("DataVersion")));
        return structure.toTag(new CompoundTag());
    }
}

