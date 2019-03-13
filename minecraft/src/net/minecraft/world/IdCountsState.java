package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.nbt.CompoundTag;

public class IdCountsState extends PersistentState {
	private final Object2IntMap<String> idCounts = new Object2IntOpenHashMap<>();

	public IdCountsState() {
		super("idcounts");
		this.idCounts.defaultReturnValue(-1);
	}

	@Override
	public void method_77(CompoundTag compoundTag) {
		this.idCounts.clear();

		for (String string : compoundTag.getKeys()) {
			if (compoundTag.containsKey(string, 99)) {
				this.idCounts.put(string, compoundTag.getInt(string));
			}
		}
	}

	@Override
	public CompoundTag method_75(CompoundTag compoundTag) {
		for (Entry<String> entry : this.idCounts.object2IntEntrySet()) {
			compoundTag.putInt((String)entry.getKey(), entry.getIntValue());
		}

		return compoundTag;
	}

	public int getNextMapId() {
		int i = this.idCounts.getInt("map") + 1;
		this.idCounts.put("map", i);
		this.markDirty();
		return i;
	}
}
