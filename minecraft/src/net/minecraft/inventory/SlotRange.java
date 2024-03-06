package net.minecraft.inventory;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.util.StringIdentifiable;

public interface SlotRange extends StringIdentifiable {
	IntList getSlotIds();

	default int getSlotCount() {
		return this.getSlotIds().size();
	}

	static SlotRange create(String name, IntList slotIds) {
		return new SlotRange() {
			@Override
			public IntList getSlotIds() {
				return slotIds;
			}

			@Override
			public String asString() {
				return name;
			}

			public String toString() {
				return name;
			}
		};
	}
}
