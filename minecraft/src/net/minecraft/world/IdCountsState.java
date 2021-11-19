package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class IdCountsState extends PersistentState {
	public static final String IDCOUNTS_KEY = "idcounts";
	private final Object2IntMap<String> idCounts = new Object2IntOpenHashMap<>();

	public IdCountsState() {
		this.idCounts.defaultReturnValue(-1);
	}

	public static IdCountsState fromNbt(NbtCompound nbt) {
		IdCountsState idCountsState = new IdCountsState();

		for (String string : nbt.getKeys()) {
			if (nbt.contains(string, NbtElement.NUMBER_TYPE)) {
				idCountsState.idCounts.put(string, nbt.getInt(string));
			}
		}

		return idCountsState;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		for (Entry<String> entry : this.idCounts.object2IntEntrySet()) {
			nbt.putInt((String)entry.getKey(), entry.getIntValue());
		}

		return nbt;
	}

	public int getNextMapId() {
		int i = this.idCounts.getInt("map") + 1;
		this.idCounts.put("map", i);
		this.markDirty();
		return i;
	}
}
