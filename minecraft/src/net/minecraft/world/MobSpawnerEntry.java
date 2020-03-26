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
	}

	public CompoundTag serialize() {
		CompoundTag compoundTag = new CompoundTag();
		if (!this.entityTag.contains("id", 8)) {
			this.entityTag.putString("id", "minecraft:pig");
		} else if (!this.entityTag.getString("id").contains(":")) {
			this.entityTag.putString("id", new Identifier(this.entityTag.getString("id")).toString());
		}

		compoundTag.put("Entity", this.entityTag);
		compoundTag.putInt("Weight", this.weight);
		return compoundTag;
	}

	public CompoundTag getEntityTag() {
		return this.entityTag;
	}
}
