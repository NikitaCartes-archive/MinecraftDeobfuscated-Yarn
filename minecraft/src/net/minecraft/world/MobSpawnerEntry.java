package net.minecraft.world;

import net.fabricmc.yarn.constants.NbtTypeIds;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.WeightedPicker;

public class MobSpawnerEntry extends WeightedPicker.Entry {
	private final NbtCompound entityTag;

	public MobSpawnerEntry() {
		super(1);
		this.entityTag = new NbtCompound();
		this.entityTag.putString("id", "minecraft:pig");
	}

	public MobSpawnerEntry(NbtCompound tag) {
		this(tag.contains("Weight", NbtTypeIds.NUMBER) ? tag.getInt("Weight") : 1, tag.getCompound("Entity"));
	}

	public MobSpawnerEntry(int weight, NbtCompound entityTag) {
		super(weight);
		this.entityTag = entityTag;
		Identifier identifier = Identifier.tryParse(entityTag.getString("id"));
		if (identifier != null) {
			entityTag.putString("id", identifier.toString());
		} else {
			entityTag.putString("id", "minecraft:pig");
		}
	}

	public NbtCompound serialize() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.put("Entity", this.entityTag);
		nbtCompound.putInt("Weight", this.weight);
		return nbtCompound;
	}

	public NbtCompound getEntityNbt() {
		return this.entityTag;
	}
}
