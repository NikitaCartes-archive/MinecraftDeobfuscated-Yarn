package net.minecraft.component.type;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.screen.slot.Slot;
import org.apache.commons.lang3.math.Fraction;

public final class BundleContentsComponent implements TooltipData {
	public static final BundleContentsComponent DEFAULT = new BundleContentsComponent(List.of());
	public static final Codec<BundleContentsComponent> CODEC = ItemStack.CODEC.listOf().xmap(BundleContentsComponent::new, component -> component.stacks);
	public static final PacketCodec<RegistryByteBuf, BundleContentsComponent> PACKET_CODEC = ItemStack.PACKET_CODEC
		.collect(PacketCodecs.toList())
		.xmap(BundleContentsComponent::new, component -> component.stacks);
	private static final Fraction NESTED_BUNDLE_OCCUPANCY = Fraction.getFraction(1, 16);
	private static final int ADD_TO_NEW_SLOT = -1;
	public static final int field_52591 = -1;
	final List<ItemStack> stacks;
	final Fraction occupancy;
	final int selectedStackIndex;

	BundleContentsComponent(List<ItemStack> stacks, Fraction occupancy, int selectedStackIndex) {
		this.stacks = stacks;
		this.occupancy = occupancy;
		this.selectedStackIndex = selectedStackIndex;
	}

	public BundleContentsComponent(List<ItemStack> stacks) {
		this(stacks, calculateOccupancy(stacks), -1);
	}

	private static Fraction calculateOccupancy(List<ItemStack> stacks) {
		Fraction fraction = Fraction.ZERO;

		for (ItemStack itemStack : stacks) {
			fraction = fraction.add(getOccupancy(itemStack).multiplyBy(Fraction.getFraction(itemStack.getCount(), 1)));
		}

		return fraction;
	}

	static Fraction getOccupancy(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
		if (bundleContentsComponent != null) {
			return NESTED_BUNDLE_OCCUPANCY.add(bundleContentsComponent.getOccupancy());
		} else {
			List<BeehiveBlockEntity.BeeData> list = stack.getOrDefault(DataComponentTypes.BEES, List.of());
			return !list.isEmpty() ? Fraction.ONE : Fraction.getFraction(1, stack.getMaxCount());
		}
	}

	public static boolean canBeBundled(ItemStack stack) {
		return !stack.isEmpty() && stack.getItem().canBeNested();
	}

	public int getNumberOfStacksShown() {
		int i = this.size();
		int j = i > 12 ? 11 : 12;
		int k = i % 4;
		int l = k == 0 ? 0 : 4 - k;
		return Math.min(i, j - l);
	}

	public ItemStack get(int index) {
		return (ItemStack)this.stacks.get(index);
	}

	public Stream<ItemStack> stream() {
		return this.stacks.stream().map(ItemStack::copy);
	}

	public Iterable<ItemStack> iterate() {
		return this.stacks;
	}

	public Iterable<ItemStack> iterateCopy() {
		return Lists.<ItemStack, ItemStack>transform(this.stacks, ItemStack::copy);
	}

	public int size() {
		return this.stacks.size();
	}

	public Fraction getOccupancy() {
		return this.occupancy;
	}

	public boolean isEmpty() {
		return this.stacks.isEmpty();
	}

	public int getSelectedStackIndex() {
		return this.selectedStackIndex;
	}

	public boolean hasSelectedStack() {
		return this.selectedStackIndex != -1;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof BundleContentsComponent bundleContentsComponent)
				? false
				: this.occupancy.equals(bundleContentsComponent.occupancy) && ItemStack.stacksEqual(this.stacks, bundleContentsComponent.stacks);
		}
	}

	public int hashCode() {
		return ItemStack.listHashCode(this.stacks);
	}

	public String toString() {
		return "BundleContents" + this.stacks;
	}

	public static class Builder {
		private final List<ItemStack> stacks;
		private Fraction occupancy;
		private int selectedStackIndex;

		public Builder(BundleContentsComponent base) {
			this.stacks = new ArrayList(base.stacks);
			this.occupancy = base.occupancy;
			this.selectedStackIndex = base.selectedStackIndex;
		}

		public BundleContentsComponent.Builder clear() {
			this.stacks.clear();
			this.occupancy = Fraction.ZERO;
			this.selectedStackIndex = -1;
			return this;
		}

		private int getInsertionIndex(ItemStack stack) {
			if (!stack.isStackable()) {
				return -1;
			} else {
				for (int i = 0; i < this.stacks.size(); i++) {
					if (ItemStack.areItemsAndComponentsEqual((ItemStack)this.stacks.get(i), stack)) {
						return i;
					}
				}

				return -1;
			}
		}

		private int getMaxAllowed(ItemStack stack) {
			Fraction fraction = Fraction.ONE.subtract(this.occupancy);
			return Math.max(fraction.divideBy(BundleContentsComponent.getOccupancy(stack)).intValue(), 0);
		}

		public int add(ItemStack stack) {
			if (!BundleContentsComponent.canBeBundled(stack)) {
				return 0;
			} else {
				int i = Math.min(stack.getCount(), this.getMaxAllowed(stack));
				if (i == 0) {
					return 0;
				} else {
					this.occupancy = this.occupancy.add(BundleContentsComponent.getOccupancy(stack).multiplyBy(Fraction.getFraction(i, 1)));
					int j = this.getInsertionIndex(stack);
					if (j != -1) {
						ItemStack itemStack = (ItemStack)this.stacks.remove(j);
						ItemStack itemStack2 = itemStack.copyWithCount(itemStack.getCount() + i);
						stack.decrement(i);
						this.stacks.add(0, itemStack2);
					} else {
						this.stacks.add(0, stack.split(i));
					}

					return i;
				}
			}
		}

		public int add(Slot slot, PlayerEntity player) {
			ItemStack itemStack = slot.getStack();
			int i = this.getMaxAllowed(itemStack);
			return BundleContentsComponent.canBeBundled(itemStack) ? this.add(slot.takeStackRange(itemStack.getCount(), i, player)) : 0;
		}

		public void setSelectedStackIndex(int selectedStackIndex) {
			this.selectedStackIndex = this.selectedStackIndex != selectedStackIndex && selectedStackIndex < this.stacks.size() ? selectedStackIndex : -1;
		}

		@Nullable
		public ItemStack removeSelected() {
			if (this.stacks.isEmpty()) {
				return null;
			} else {
				int i = this.selectedStackIndex != -1 && this.selectedStackIndex < this.stacks.size() ? this.selectedStackIndex : 0;
				ItemStack itemStack = ((ItemStack)this.stacks.remove(i)).copy();
				this.occupancy = this.occupancy.subtract(BundleContentsComponent.getOccupancy(itemStack).multiplyBy(Fraction.getFraction(itemStack.getCount(), 1)));
				this.setSelectedStackIndex(-1);
				return itemStack;
			}
		}

		public Fraction getOccupancy() {
			return this.occupancy;
		}

		public BundleContentsComponent build() {
			return new BundleContentsComponent(List.copyOf(this.stacks), this.occupancy, this.selectedStackIndex);
		}
	}
}
