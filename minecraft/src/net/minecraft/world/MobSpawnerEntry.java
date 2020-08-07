package net.minecraft.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.WeightedPicker;

public class MobSpawnerEntry extends WeightedPicker.Entry {
	private final CompoundTag entityTag;

	public MobSpawnerEntry() {
		super(1);
		this.entityTag = new CompoundTag();
		this.entityTag.putString("id", "minecraft:pig");
	}

	public MobSpawnerEntry(CompoundTag tag) {
		this(tag.contains("Weight", 99) ? tag.getInt("Weight") : 1, tag.getCompound("Entity"));
	}

	public MobSpawnerEntry(int weight, CompoundTag entityTag) {
		super(weight);
		this.entityTag = entityTag;
		Identifier identifier = Identifier.tryParse(entityTag.getString("id"));
		if (identifier != null) {
			entityTag.putString("id", identifier.toString());
		} else {
			entityTag.putString("id", "minecraft:pig");
		}
	}

	public CompoundTag serialize() {
		CompoundTag compoundTag = new CompoundTag();
		compoundTag.put("Entity", this.entityTag);
		compoundTag.putInt("Weight", this.weight);
		return compoundTag;
	}

	public CompoundTag getEntityTag() {
		return this.entityTag;
	}
}
