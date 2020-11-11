package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.nbt.CompoundTag;

public class IdCountsState extends PersistentState {
	private final Object2IntMap<String> idCounts = new Object2IntOpenHashMap<>();

	public IdCountsState() {
		this.idCounts.defaultReturnValue(-1);
	}

	public static IdCountsState method_32360(CompoundTag compoundTag) {
		IdCountsState idCountsState = new IdCountsState();

		for (String string : compoundTag.getKeys()) {
			if (compoundTag.contains(string, 99)) {
				idCountsState.idCounts.put(string, compoundTag.getInt(string));
			}
		}

		return idCountsState;
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		for (Entry<String> entry : this.idCounts.object2IntEntrySet()) {
			tag.putInt((String)entry.getKey(), entry.getIntValue());
		}

		return tag;
	}

	public int getNextMapId() {
		int i = this.idCounts.getInt("map") + 1;
		this.idCounts.put("map", i);
		this.markDirty();
		return i;
	}
}
