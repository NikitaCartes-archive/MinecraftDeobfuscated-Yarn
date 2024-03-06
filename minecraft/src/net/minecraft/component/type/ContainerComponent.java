package net.minecraft.component.type;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;

public final class ContainerComponent implements Iterable<ItemStack> {
	private static final int MAX_SLOTS = 256;
	public static final ContainerComponent DEFAULT = new ContainerComponent(DefaultedList.of());
	public static final Codec<ContainerComponent> CODEC = Codecs.list(ContainerComponent.Slot.CODEC.listOf(), 256)
		.xmap(ContainerComponent::fromSlots, ContainerComponent::collectSlots);
	public static final PacketCodec<RegistryByteBuf, ContainerComponent> PACKET_CODEC = ItemStack.OPTIONAL_PACKET_CODEC
		.collect(PacketCodecs.toList(256))
		.xmap(ContainerComponent::new, component -> component.stacks);
	private final DefaultedList<ItemStack> stacks;

	private ContainerComponent(DefaultedList<ItemStack> stacks) {
		this.stacks = stacks;
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
		int i = slots.stream().mapToInt(ContainerComponent.Slot::index).max().orElse(-1);
		ContainerComponent containerComponent = new ContainerComponent(i + 1);

		for (ContainerComponent.Slot slot : slots) {
			containerComponent.stacks.set(slot.index(), slot.item());
		}

		return containerComponent;
	}

	public static ContainerComponent fromStacks(List<ItemStack> stacks) {
		int i = getSize(stacks);
		if (i == 0) {
			return DEFAULT;
		} else {
			ContainerComponent containerComponent = new ContainerComponent(i);

			for (int j = 0; j < i; j++) {
				containerComponent.stacks.set(j, ((ItemStack)stacks.get(j)).copy());
			}

			return containerComponent;
		}
	}

	private static int getSize(List<ItemStack> size) {
		for (int i = size.size() - 1; i >= 0; i--) {
			if (!((ItemStack)size.get(i)).isEmpty()) {
				return i + 1;
			}
		}

		return 0;
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

	public void copyTo(DefaultedList<ItemStack> stacks) {
		for (int i = 0; i < stacks.size(); i++) {
			ItemStack itemStack = i < this.stacks.size() ? this.stacks.get(i) : ItemStack.EMPTY;
			stacks.set(i, itemStack.copy());
		}
	}

	public ItemStack copyFirstStack() {
		return this.stacks.isEmpty() ? ItemStack.EMPTY : this.stacks.get(0).copy();
	}

	public Stream<ItemStack> stream() {
		return this.stacks.stream().filter(stack -> !stack.isEmpty()).map(ItemStack::copy);
	}

	public Iterator<ItemStack> iterator() {
		return Iterators.transform(Iterators.filter(this.stacks.iterator(), (Predicate<? super ItemStack>)(stack -> !stack.isEmpty())), ItemStack::copy);
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
		return ItemStack.listHashCode(this.stacks);
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
