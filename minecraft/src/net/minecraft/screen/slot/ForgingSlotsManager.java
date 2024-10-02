package net.minecraft.screen.slot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;

public class ForgingSlotsManager {
	private final List<ForgingSlotsManager.ForgingSlot> inputSlots;
	private final ForgingSlotsManager.ForgingSlot resultSlot;

	ForgingSlotsManager(List<ForgingSlotsManager.ForgingSlot> inputSlots, ForgingSlotsManager.ForgingSlot resultSlot) {
		if (!inputSlots.isEmpty() && !resultSlot.equals(ForgingSlotsManager.ForgingSlot.DEFAULT)) {
			this.inputSlots = inputSlots;
			this.resultSlot = resultSlot;
		} else {
			throw new IllegalArgumentException("Need to define both inputSlots and resultSlot");
		}
	}

	public static ForgingSlotsManager.Builder builder() {
		return new ForgingSlotsManager.Builder();
	}

	public ForgingSlotsManager.ForgingSlot getInputSlot(int index) {
		return (ForgingSlotsManager.ForgingSlot)this.inputSlots.get(index);
	}

	public ForgingSlotsManager.ForgingSlot getResultSlot() {
		return this.resultSlot;
	}

	public List<ForgingSlotsManager.ForgingSlot> getInputSlots() {
		return this.inputSlots;
	}

	public int getInputSlotCount() {
		return this.inputSlots.size();
	}

	public int getResultSlotIndex() {
		return this.getInputSlotCount();
	}

	public static class Builder {
		private final List<ForgingSlotsManager.ForgingSlot> inputs = new ArrayList();
		private ForgingSlotsManager.ForgingSlot resultSlot = ForgingSlotsManager.ForgingSlot.DEFAULT;

		public ForgingSlotsManager.Builder input(int slotId, int x, int y, Predicate<ItemStack> mayPlace) {
			this.inputs.add(new ForgingSlotsManager.ForgingSlot(slotId, x, y, mayPlace));
			return this;
		}

		public ForgingSlotsManager.Builder output(int slotId, int x, int y) {
			this.resultSlot = new ForgingSlotsManager.ForgingSlot(slotId, x, y, stack -> false);
			return this;
		}

		public ForgingSlotsManager build() {
			int i = this.inputs.size();

			for (int j = 0; j < i; j++) {
				ForgingSlotsManager.ForgingSlot forgingSlot = (ForgingSlotsManager.ForgingSlot)this.inputs.get(j);
				if (forgingSlot.slotId != j) {
					throw new IllegalArgumentException("Expected input slots to have continous indexes");
				}
			}

			if (this.resultSlot.slotId != i) {
				throw new IllegalArgumentException("Expected result slot index to follow last input slot");
			} else {
				return new ForgingSlotsManager(this.inputs, this.resultSlot);
			}
		}
	}

	public static record ForgingSlot(int slotId, int x, int y, Predicate<ItemStack> mayPlace) {
		static final ForgingSlotsManager.ForgingSlot DEFAULT = new ForgingSlotsManager.ForgingSlot(0, 0, 0, stack -> true);
	}
}
