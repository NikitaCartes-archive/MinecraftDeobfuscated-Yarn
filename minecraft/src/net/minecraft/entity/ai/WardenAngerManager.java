package net.minecraft.entity.ai;

import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.function.Supplier;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

public class WardenAngerManager {
	private final Map<UUID, Integer> suspects = Maps.<UUID, Integer>newHashMap();
	private static final String SUSPECT_KEY = "Suspect";
	private static final String UUID_KEY = "UUID";
	private static final String ANGER_KEY = "Anger";
	private final int maxAnger;
	private final int angerDecreasePerTick;
	private final Supplier<Boolean> field_36737;

	public WardenAngerManager(int angerDecreasePerTick, int maxAnger, Supplier<Boolean> supplier) {
		this.angerDecreasePerTick = angerDecreasePerTick;
		this.maxAnger = maxAnger;
		this.field_36737 = supplier;
	}

	public void tick() {
		if ((Boolean)this.field_36737.get()) {
			this.suspects.replaceAll((uuid, anger) -> Math.max(0, anger - this.angerDecreasePerTick));
			this.suspects.entrySet().removeIf(suspect -> (Integer)suspect.getValue() <= 0);
		}
	}

	private Optional<Entry<UUID, Integer>> getPrimeSuspect() {
		return this.suspects.entrySet().stream().max(Entry.comparingByValue());
	}

	public void increaseAngerAt(UUID uuid, int level) {
		Integer integer = (Integer)this.suspects.putIfAbsent(uuid, level);
		this.suspects.replace(uuid, Math.min(this.maxAnger, (integer != null ? integer : 0) + level));
	}

	public void removeSuspect(UUID uuid) {
		this.suspects.remove(uuid);
	}

	public int getAnger(UUID uuid) {
		return !this.suspects.containsKey(uuid) ? 0 : (Integer)this.suspects.get(uuid);
	}

	public int getPrimeSuspectAnger() {
		return (Integer)this.getPrimeSuspect().map(Entry::getValue).orElse(0);
	}

	public Optional<UUID> getPrimeSuspectUuid() {
		return this.getPrimeSuspect().map(Entry::getKey);
	}

	public void writeToNbt(NbtCompound nbt) {
		NbtList nbtList = new NbtList();

		for (Entry<UUID, Integer> entry : this.suspects.entrySet()) {
			UUID uUID = (UUID)entry.getKey();
			int i = (Integer)entry.getValue();
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.putUuid("UUID", uUID);
			nbtCompound.putInt("Anger", i);
			nbtList.add(nbtCompound);
		}

		nbt.put("Suspect", nbtList);
	}

	public void readNbt(NbtCompound nbt) {
		if (nbt.contains("Suspect", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbt.getList("Suspect", NbtElement.COMPOUND_TYPE);

			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				UUID uUID = nbtCompound.getUuid("UUID");
				int j = nbtCompound.getInt("Anger");
				this.suspects.put(uUID, j);
			}
		}
	}
}
