/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.data.validate;

import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureValidatorProvider
implements SnbtProvider.Tweaker {
    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public NbtCompound write(String name, NbtCompound nbt) {
        if (name.startsWith("data/minecraft/structures/")) {
            return StructureValidatorProvider.update(name, nbt);
        }
        return nbt;
    }

    public static NbtCompound update(String name, NbtCompound nbt) {
        return StructureValidatorProvider.internalUpdate(name, StructureValidatorProvider.addDataVersion(nbt));
    }

    private static NbtCompound addDataVersion(NbtCompound nbt) {
        if (!nbt.contains("DataVersion", 99)) {
            nbt.putInt("DataVersion", 500);
        }
        return nbt;
    }

    private static NbtCompound internalUpdate(String name, NbtCompound nbt) {
        Structure structure = new Structure();
        int i = nbt.getInt("DataVersion");
        int j = 2678;
        if (i < 2678) {
            LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", (Object)i, (Object)2678, (Object)name);
        }
        NbtCompound nbtCompound = NbtHelper.update(Schemas.getFixer(), DataFixTypes.STRUCTURE, nbt, i);
        structure.readNbt(nbtCompound);
        return structure.writeNbt(new NbtCompound());
    }
}

