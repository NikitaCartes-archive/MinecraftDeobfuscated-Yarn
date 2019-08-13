package net.minecraft.data.validate;

import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixers.DataFixTypes;
import net.minecraft.datafixers.Schemas;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.structure.Structure;
import net.minecraft.util.TagHelper;

public class StructureValidatorProvider implements SnbtProvider.class_4460 {
	@Override
	public CompoundTag method_21674(String string, CompoundTag compoundTag) {
		return string.startsWith("data/minecraft/structures/") ? method_16878(method_16880(compoundTag)) : compoundTag;
	}

	private static CompoundTag method_16880(CompoundTag compoundTag) {
		if (!compoundTag.containsKey("DataVersion", 99)) {
			compoundTag.putInt("DataVersion", 500);
		}

		return compoundTag;
	}

	private static CompoundTag method_16878(CompoundTag compoundTag) {
		Structure structure = new Structure();
		structure.fromTag(TagHelper.update(Schemas.getFixer(), DataFixTypes.field_19217, compoundTag, compoundTag.getInt("DataVersion")));
		return structure.toTag(new CompoundTag());
	}
}
