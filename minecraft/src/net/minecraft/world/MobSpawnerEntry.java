package net.minecraft.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.WeightedPicker;

public class MobSpawnerEntry extends WeightedPicker.Entry {
	private final NbtCompound entityNbt;

	public MobSpawnerEntry() {
		super(1);
		this.entityNbt = new NbtCompound();
		this.entityNbt.putString("id", "minecraft:pig");
	}

	public MobSpawnerEntry(NbtCompound nbt) {
		this(nbt.contains("Weight", 99) ? nbt.getInt("Weight") : 1, nbt.getCompound("Entity"));
	}

	public MobSpawnerEntry(int weight, NbtCompound entityNbt) {
		super(weight);
		this.entityNbt = entityNbt;
		Identifier identifier = Identifier.tryParse(entityNbt.getString("id"));
		if (identifier != null) {
			entityNbt.putString("id", identifier.toString());
		} else {
			entityNbt.putString("id", "minecraft:pig");
		}
	}

	public NbtCompound toNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.put("Entity", this.entityNbt);
		nbtCompound.putInt("Weight", this.weight);
		return nbtCompound;
	}

	public NbtCompound getEntityNbt() {
		return this.entityNbt;
	}
}
