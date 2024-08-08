package net.minecraft.component.type;

import com.google.common.collect.Iterables;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Stream;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.collection.DefaultedList;

/**
 * A component that stores a list of item stacks.
 */
public final class ContainerComponent {
	private static final int ALL_SLOTS_EMPTY = -1;
	private static final int MAX_SLOTS = 256;
	/**
	 * An empty container component.
	 */
	public static final ContainerComponent DEFAULT = new ContainerComponent(DefaultedList.of());
	public static final Codec<ContainerComponent> CODEC = ContainerComponent.Slot.CODEC
		.sizeLimitedListOf(256)
		.xmap(ContainerComponent::fromSlots, ContainerComponent::collectSlots);
	public static final PacketCodec<RegistryByteBuf, ContainerComponent> PACKET_CODEC = ItemStack.OPTIONAL_PACKET_CODEC
		.collect(PacketCodecs.toList(256))
		.xmap(ContainerComponent::new, component -> component.stacks);
	private final DefaultedList<ItemStack> stacks;
	private final int hashCode;

	private ContainerComponent(DefaultedList<ItemStack> stacks) {
		if (stacks.size() > 256) {
			throw new IllegalArgumentException("Got " + stacks.size() + " items, but maximum is 256");
		} else {
			this.stacks = stacks;
			this.hashCode = ItemStack.listHashCode(stacks);
		}
	}

	private ContainerComponent(int size) {
		this(DefaultedList.ofSize(size, ItemStack.EMPTY));
	}

	private ContainerComponent(List<ItemStack> stacks) {
		this(stacks.size());

		for (int i = 0; i < stacks.size(); i++) {
			this.stacks.set(i, (ItemStack)stacks.get(i));
		}
	}

	private static ContainerComponent fromSlots(List<ContainerComponent.Slot> slots) {
		OptionalInt optionalInt = slots.stream().mapToInt(ContainerComponent.Slot::index).max();
		if (optionalInt.isEmpty()) {
			return DEFAULT;
		} else {
			ContainerComponent containerComponent = new ContainerComponent(optionalInt.getAsInt() + 1);

			for (ContainerComponent.Slot slot : slots) {
				containerComponent.stacks.set(slot.index(), slot.item());
			}

			return containerComponent;
		}
	}

	/**
	 * Creates a container component from a list of item stacks.
	 * The stacks are copied into the component, which will contain copies of all stacks
	 * up to the last non-empty stack.
	 * 
	 * @return the created component
	 * 
	 * @param stacks the list of stacks to copy
	 */
	public static ContainerComponent fromStacks(List<ItemStack> stacks) {
		int i = findLastNonEmptyIndex(stacks);
		if (i == -1) {
			return DEFAULT;
		} else {
			ContainerComponent containerComponent = new ContainerComponent(i + 1);

			for (int j = 0; j <= i; j++) {
				containerComponent.stacks.set(j, ((ItemStack)stacks.get(j)).copy());
			}

			return containerComponent;
		}
	}

	private static int findLastNonEmptyIndex(List<ItemStack> stacks) {
		for (int i = stacks.size() - 1; i >= 0; i--) {
			if (!((ItemStack)stacks.get(i)).isEmpty()) {
				return i;
			}
		}

		return -1;
	}

	private List<ContainerComponent.Slot> collectSlots() {
		List<ContainerComponent.Slot> list = new ArrayList();

		for (int i = 0; i < this.stacks.size(); i++) {
			ItemStack itemStack = this.stacks.get(i);
			if (!itemStack.isEmpty()) {
				list.add(new ContainerComponent.Slot(i, itemStack));
			}
		}

		return list;
	}

	/**
	 * Copies the contents of this component to a list of item stacks.
	 * 
	 * <p>If the list has a larger size than this component, the remaining slots
	 * will be filled with empty stacks.
	 * 
	 * @param stacks the target list of item stacks
	 */
	public void copyTo(DefaultedList<ItemStack> stacks) {
		for (int i = 0; i < stacks.size(); i++) {
			ItemStack itemStack = i < this.stacks.size() ? this.stacks.get(i) : ItemStack.EMPTY;
			stacks.set(i, itemStack.copy());
		}
	}

	/**
	 * {@return a copy of the first contained stack in this component}
	 * 
	 * <p>If this component is empty, returns an empty stack.
	 */
	public ItemStack copyFirstStack() {
		return this.stacks.isEmpty() ? ItemStack.EMPTY : this.stacks.get(0).copy();
	}

	/**
	 * {@return a stream over copies of this component's stored item stacks}
	 */
	public Stream<ItemStack> stream() {
		return this.stacks.stream().map(ItemStack::copy);
	}

	/**
	 * {@return a stream over copies of this component's non-empty item stacks}
	 */
	public Stream<ItemStack> streamNonEmpty() {
		return this.stacks.stream().filter(stack -> !stack.isEmpty()).map(ItemStack::copy);
	}

	/**
	 * {@return an iterable over this component's non-empty item stacks}
	 * 
	 * <p>The stacks should not be modified to keep this component immutable.
	 * Use {@link #iterateNonEmptyCopy} or the stream methods for getting freely modifiable
	 * copies of the stacks.
	 */
	public Iterable<ItemStack> iterateNonEmpty() {
		return Iterables.filter(this.stacks, stack -> !stack.isEmpty());
	}

	/**
	 * {@return an iterable over copies of this component's non-empty item stacks}
	 */
	public Iterable<ItemStack> iterateNonEmptyCopy() {
		return Iterables.transform(this.iterateNonEmpty(), ItemStack::copy);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			if (o instanceof ContainerComponent containerComponent && ItemStack.stacksEqual(this.stacks, containerComponent.stacks)) {
				return true;
			}

			return false;
		}
	}

	public int hashCode() {
		return this.hashCode;
	}

	static record Slot(int index, ItemStack item) {
		public static final Codec<ContainerComponent.Slot> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codec.intRange(0, 255).fieldOf("slot").forGetter(ContainerComponent.Slot::index),
						ItemStack.CODEC.fieldOf("item").forGetter(ContainerComponent.Slot::item)
					)
					.apply(instance, ContainerComponent.Slot::new)
		);
	}
}
