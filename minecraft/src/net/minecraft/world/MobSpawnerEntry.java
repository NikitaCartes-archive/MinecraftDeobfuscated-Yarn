package net.minecraft.world;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Weighted;

public class MobSpawnerEntry extends Weighted.Absent {
	public static final int DEFAULT_WEIGHT = 1;
	public static final String DEFAULT_ENTITY_ID = "minecraft:pig";
	private final NbtCompound entityNbt;

	public MobSpawnerEntry() {
		super(1);
		this.entityNbt = new NbtCompound();
		this.entityNbt.putString("id", "minecraft:pig");
	}

	public MobSpawnerEntry(NbtCompound nbt) {
		this(nbt.contains("Weight", NbtElement.NUMBER_TYPE) ? nbt.getInt("Weight") : 1, nbt.getCompound("Entity"));
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
		nbtCompound.putInt("Weight", this.getWeight().getValue());
		return nbtCompound;
	}

	public NbtCompound getEntityNbt() {
		return this.entityNbt;
	}
}
