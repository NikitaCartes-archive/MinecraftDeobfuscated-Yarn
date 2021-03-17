package net.minecraft.data.validate;

import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class StructureValidatorProvider implements SnbtProvider.Tweaker {
	private static final Logger LOGGER = LogManager.getLogger();

	@Override
	public NbtCompound write(String name, NbtCompound nbt) {
		return name.startsWith("data/minecraft/structures/") ? method_32235(name, nbt) : nbt;
	}

	public static NbtCompound method_32235(String string, NbtCompound nbtCompound) {
		return update(string, addDataVersion(nbtCompound));
	}

	private static NbtCompound addDataVersion(NbtCompound nbt) {
		if (!nbt.contains("DataVersion", NbtTypeIds.NUMBER)) {
			nbt.putInt("DataVersion", 500);
		}

		return nbt;
	}

	private static NbtCompound update(String name, NbtCompound tag) {
		Structure structure = new Structure();
		int i = tag.getInt("DataVersion");
		int j = 2678;
		if (i < 2678) {
			LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", i, 2678, name);
		}

		NbtCompound nbtCompound = NbtHelper.update(Schemas.getFixer(), DataFixTypes.STRUCTURE, tag, i);
		structure.readNbt(nbtCompound);
		return structure.writeNbt(new NbtCompound());
	}
}
