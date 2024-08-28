package net.minecraft.data.validate;

import com.mojang.logging.LogUtils;
import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceType;
import net.minecraft.structure.StructureTemplate;
import org.slf4j.Logger;

public class StructureValidatorProvider implements SnbtProvider.Tweaker {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String PATH_PREFIX = ResourceType.SERVER_DATA.getDirectory() + "/minecraft/structure/";

	@Override
	public NbtCompound write(String name, NbtCompound nbt) {
		return name.startsWith(PATH_PREFIX) ? update(name, nbt) : nbt;
	}

	public static NbtCompound update(String name, NbtCompound nbt) {
		StructureTemplate structureTemplate = new StructureTemplate();
		int i = NbtHelper.getDataVersion(nbt, 500);
		int j = 4053;
		if (i < 4053) {
			LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", i, 4053, name);
		}

		NbtCompound nbtCompound = DataFixTypes.STRUCTURE.update(Schemas.getFixer(), nbt, i);
		structureTemplate.readNbt(Registries.BLOCK, nbtCompound);
		return structureTemplate.writeNbt(new NbtCompound());
	}
}
