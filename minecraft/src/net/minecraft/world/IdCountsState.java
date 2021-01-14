package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.nbt.NbtCompound;

public class IdCountsState extends PersistentState {
	private final Object2IntMap<String> idCounts = new Object2IntOpenHashMap<>();

	public IdCountsState() {
		super("idcounts");
		this.idCounts.defaultReturnValue(-1);
	}

	@Override
	public void fromTag(NbtCompound tag) {
		this.idCounts.clear();

		for (String string : tag.getKeys()) {
			if (tag.contains(string, 99)) {
				this.idCounts.put(string, tag.getInt(string));
			}
		}
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
