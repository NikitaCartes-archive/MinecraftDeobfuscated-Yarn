package net.minecraft;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.PersistentState;

public class class_3978 extends PersistentState {
	private final Object2IntMap<String> field_17662 = new Object2IntOpenHashMap<>();

	public class_3978() {
		super("idcounts");
		this.field_17662.defaultReturnValue(-1);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		this.field_17662.clear();

		for (String string : compoundTag.getKeys()) {
			if (compoundTag.containsKey(string, 99)) {
				this.field_17662.put(string, compoundTag.getInt(string));
			}
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		for (Entry<String> entry : this.field_17662.object2IntEntrySet()) {
			compoundTag.putInt((String)entry.getKey(), entry.getIntValue());
		}

		return compoundTag;
	}

	public int method_17920() {
		int i = this.field_17662.getInt("map") + 1;
		this.field_17662.put("map", i);
		this.markDirty();
		return i;
	}
}
