package net.minecraft.data.validate;

import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureValidatorProvider implements SnbtProvider.Tweaker {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public CompoundTag write(String name, CompoundTag nbt) {
		return name.startsWith("data/minecraft/structures/") ? method_32235(name, nbt) : nbt;
	}

	public static CompoundTag method_32235(String string, CompoundTag compoundTag) {
		return update(string, addDataVersion(compoundTag));
	}

	private static CompoundTag addDataVersion(CompoundTag nbt) {
		if (!nbt.contains("DataVersion", 99)) {
			nbt.putInt("DataVersion", 500);
		}

		return nbt;
	}

	private static CompoundTag update(String name, CompoundTag tag) {
		Structure structure = new Structure();
		int i = tag.getInt("DataVersion");
		int j = 2678;
		if (i < 2678) {
			LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", i, 2678, name);
		}

		CompoundTag compoundTag = NbtHelper.update(Schemas.getFixer(), DataFixTypes.STRUCTURE, tag, i);
		structure.fromTag(compoundTag);
		return structure.toTag(new CompoundTag());
	}
}
