package net.minecraft.data.validate;

import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;

public class StructureValidatorProvider implements SnbtProvider.class_4460 {
	@Override
	public CompoundTag method_21674(String string, CompoundTag compoundTag) {
		return string.startsWith("data/minecraft/structures/") ? method_16878(method_16880(compoundTag)) : compoundTag;
	}

	private static CompoundTag method_16880(CompoundTag compoundTag) {
		if (!compoundTag.contains("DataVersion", 99)) {
			compoundTag.putInt("DataVersion", 500);
		}

		return compoundTag;
	}

	private static CompoundTag method_16878(CompoundTag compoundTag) {
		Structure structure = new Structure();
		structure.fromTag(NbtHelper.update(Schemas.getFixer(), DataFixTypes.STRUCTURE, compoundTag, compoundTag.getInt("DataVersion")));
		return structure.toTag(new CompoundTag());
	}
}
