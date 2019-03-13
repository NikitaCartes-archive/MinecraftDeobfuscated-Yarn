package net.minecraft.sortme;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.WeightedPicker;

public class MobSpawnerEntry extends WeightedPicker.Entry {
	private final CompoundTag field_9307;

	public MobSpawnerEntry() {
		super(1);
		this.field_9307 = new CompoundTag();
		this.field_9307.putString("id", "minecraft:pig");
	}

	public MobSpawnerEntry(CompoundTag compoundTag) {
		this(compoundTag.containsKey("Weight", 99) ? compoundTag.getInt("Weight") : 1, compoundTag.getCompound("Entity"));
	}

	public MobSpawnerEntry(int i, CompoundTag compoundTag) {
		super(i);
		this.field_9307 = compoundTag;
	}

	public CompoundTag method_8679() {
		CompoundTag compoundTag = new CompoundTag();
		if (!this.field_9307.containsKey("id", 8)) {
			this.field_9307.putString("id", "minecraft:pig");
		} else if (!this.field_9307.getString("id").contains(":")) {
			this.field_9307.putString("id", new Identifier(this.field_9307.getString("id")).toString());
		}

		compoundTag.method_10566("Entity", this.field_9307);
		compoundTag.putInt("Weight", this.weight);
		return compoundTag;
	}

	public CompoundTag method_8678() {
		return this.field_9307;
	}
}
