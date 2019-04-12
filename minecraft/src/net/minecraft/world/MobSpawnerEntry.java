package net.minecraft.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.WeightedPicker;

public class MobSpawnerEntry extends WeightedPicker.Entry {
	private final CompoundTag entityTag;

	public MobSpawnerEntry() {
		super(1);
		this.entityTag = new CompoundTag();
		this.entityTag.putString("id", "minecraft:pig");
	}

	public MobSpawnerEntry(CompoundTag compoundTag) {
		this(compoundTag.containsKey("Weight", 99) ? compoundTag.getInt("Weight") : 1, compoundTag.getCompound("Entity"));
	}

	public MobSpawnerEntry(int i, CompoundTag compoundTag) {
		super(i);
		this.entityTag = compoundTag;
	}

	public CompoundTag serialize() {
		CompoundTag compoundTag = new CompoundTag();
		if (!this.entityTag.containsKey("id", 8)) {
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
