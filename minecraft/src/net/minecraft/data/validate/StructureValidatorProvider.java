package net.minecraft.data.validate;

import com.mojang.logging.LogUtils;
import net.minecraft.data.SnbtProvider;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.datafixer.Schemas;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.structure.Structure;
import org.slf4j.Logger;

public class StructureValidatorProvider implements SnbtProvider.Tweaker {
	private static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public NbtCompound write(String name, NbtCompound nbt) {
		return name.startsWith("data/minecraft/structures/") ? update(name, nbt) : nbt;
	}

	public static NbtCompound update(String name, NbtCompound nbt) {
		return internalUpdate(name, addDataVersion(nbt));
	}

	private static NbtCompound addDataVersion(NbtCompound nbt) {
		if (!nbt.contains("DataVersion", NbtElement.NUMBER_TYPE)) {
			nbt.putInt("DataVersion", 500);
		}

		return nbt;
	}

	private static NbtCompound internalUpdate(String name, NbtCompound nbt) {
		Structure structure = new Structure();
		int i = nbt.getInt("DataVersion");
		int j = 2965;
		if (i < 2965) {
			LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", i, 2965, name);
		}

		NbtCompound nbtCompound = NbtHelper.update(Schemas.getFixer(), DataFixTypes.STRUCTURE, nbt, i);
		structure.readNbt(nbtCompound);
		return structure.writeNbt(new NbtCompound());
	}
}
