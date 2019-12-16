package net.minecraft.data.validate;

import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;

public class StructureValidatorProvider implements SnbtProvider.Tweaker {
	@Override
	public CompoundTag write(String name, CompoundTag nbt) {
		return name.startsWith("data/minecraft/structures/") ? update(addDataVersion(nbt)) : nbt;
	}

	private static CompoundTag addDataVersion(CompoundTag nbt) {
		if (!nbt.contains("DataVersion", 99)) {
			nbt.putInt("DataVersion", 500);
		}

		return nbt;
	}

	private static CompoundTag update(CompoundTag nbt) {
		Structure structure = new Structure();
		structure.fromTag(NbtHelper.update(Schemas.getFixer(), DataFixTypes.STRUCTURE, nbt, nbt.getInt("DataVersion")));
		return structure.toTag(new CompoundTag());
	}
}
