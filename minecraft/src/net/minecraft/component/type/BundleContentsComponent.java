package net.minecraft.component.type;

import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.client.item.TooltipData;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.screen.slot.Slot;

public final class BundleContentsComponent implements TooltipData {
	public static final int MAX_SIZE = 64;
	public static final BundleContentsComponent DEFAULT = new BundleContentsComponent(List.of());
	public static final Codec<BundleContentsComponent> CODEC = ItemStack.CODEC
		.sizeLimitedListOf(64)
		.xmap(BundleContentsComponent::new, component -> component.stacks);
	public static final PacketCodec<RegistryByteBuf, BundleContentsComponent> PACKET_CODEC = ItemStack.PACKET_CODEC
		.collect(PacketCodecs.toList(64))
		.xmap(BundleContentsComponent::new, component -> component.stacks);
	private static final int NESTED_BUNDLE_OCCUPANCY = 4;
	private static final int ADD_TO_NEW_SLOT = -1;
	final List<ItemStack> stacks;
	final int occupancy;

	BundleContentsComponent(List<ItemStack> stacks, int occupancy) {
		this.stacks = stacks;
		this.occupancy = occupancy;
	}

	public BundleContentsComponent(List<ItemStack> stacks) {
		this(stacks, calculateOccupancy(stacks));
	}

	private static int calculateOccupancy(List<ItemStack> stacks) {
		int i = 0;

		for (ItemStack itemStack : stacks) {
			i += getOccupancy(itemStack) * itemStack.getCount();
		}

		return i;
	}

	static int getOccupancy(ItemStack stack) {
		BundleContentsComponent bundleContentsComponent = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
		if (bundleContentsComponent != null) {
			return 4 + bundleContentsComponent.getOccupancy();
		} else {
			List<BeehiveBlockEntity.BeeData> list = stack.getOrDefault(DataComponentTypes.BEES, List.of());
			return !list.isEmpty() ? 64 : 64 / stack.getMaxCount();
		}
	}

	public ItemStack get(int index) {
		return (ItemStack)this.stacks.get(index);
	}

	public Stream<ItemStack> stream() {
		return this.stacks.stream().map(ItemStack::copy);
	}

	public int size() {
		return this.stacks.size();
	}

	public int getOccupancy() {
		return this.occupancy;
	}

	public boolean isEmpty() {
		return this.stacks.isEmpty();
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof BundleContentsComponent bundleContentsComponent)
				? false
				: this.occupancy == bundleContentsComponent.occupancy && ItemStack.stacksEqual(this.stacks, bundleContentsComponent.stacks);
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
		private int occupancy;

		public Builder(BundleContentsComponent base) {
			this.stacks = new ArrayList(base.stacks);
			this.occupancy = base.occupancy;
		}

		private int addInternal(ItemStack stack) {
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
			return Math.max(64 - this.occupancy, 0) / BundleContentsComponent.getOccupancy(stack);
		}

		public int add(ItemStack stack) {
			if (!stack.isEmpty() && stack.getItem().canBeNested()) {
				int i = Math.min(stack.getCount(), this.getMaxAllowed(stack));
				if (i == 0) {
					return 0;
				} else {
					this.occupancy = this.occupancy + BundleContentsComponent.getOccupancy(stack) * i;
					int j = this.addInternal(stack);
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
			} else {
				return 0;
			}
		}

		public int add(Slot slot, PlayerEntity player) {
			ItemStack itemStack = slot.getStack();
			int i = this.getMaxAllowed(itemStack);
			return this.add(slot.takeStackRange(itemStack.getCount(), i, player));
		}

		@Nullable
		public ItemStack removeFirst() {
			if (this.stacks.isEmpty()) {
				return null;
			} else {
				ItemStack itemStack = ((ItemStack)this.stacks.remove(0)).copy();
				this.occupancy = this.occupancy - BundleContentsComponent.getOccupancy(itemStack) * itemStack.getCount();
				return itemStack;
			}
		}

		public int getOccupancy() {
			return this.occupancy;
		}

		public BundleContentsComponent build() {
			return new BundleContentsComponent(List.copyOf(this.stacks), this.occupancy);
		}
	}
}
