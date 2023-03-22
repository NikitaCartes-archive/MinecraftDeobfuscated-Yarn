package net.minecraft.data.validate;

import com.mojang.logging.LogUtils;
import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.structure.StructureTemplate;
import org.slf4j.Logger;

public class StructureValidatorProvider implements SnbtProvider.Tweaker {
	private static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public NbtCompound write(String name, NbtCompound nbt) {
		return name.startsWith("data/minecraft/structures/") ? update(name, nbt) : nbt;
	}

	public static NbtCompound update(String name, NbtCompound nbt) {
		StructureTemplate structureTemplate = new StructureTemplate();
		int i = NbtHelper.getDataVersion(nbt, 500);
		int j = 3437;
		if (i < 3437) {
			LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", i, 3437, name);
		}

		NbtCompound nbtCompound = DataFixTypes.STRUCTURE.update(Schemas.getFixer(), nbt, i);
		structureTemplate.readNbt(Registries.BLOCK.getReadOnlyWrapper(), nbtCompound);
		return structureTemplate.writeNbt(new NbtCompound());
	}
}
