package net.minecraft.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.SmithingTrimRecipe;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.util.Util;
import net.minecraft.util.context.ContextParameterMap;
import net.minecraft.util.math.random.Random;

public interface SlotDisplay {
	Codec<SlotDisplay> CODEC = Registries.SLOT_DISPLAY.getCodec().dispatch(SlotDisplay::serializer, SlotDisplay.Serializer::codec);
	PacketCodec<RegistryByteBuf, SlotDisplay> PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.SLOT_DISPLAY)
		.dispatch(SlotDisplay::serializer, SlotDisplay.Serializer::streamCodec);

	<T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory);

	SlotDisplay.Serializer<? extends SlotDisplay> serializer();

	default boolean isEnabled(FeatureSet features) {
		return true;
	}

	default List<ItemStack> getStacks(ContextParameterMap parameters) {
		return this.appendStacks(parameters, SlotDisplay.NoopDisplayedItemFactory.INSTANCE).toList();
	}

	default ItemStack getFirst(ContextParameterMap context) {
		return (ItemStack)this.appendStacks(context, SlotDisplay.NoopDisplayedItemFactory.INSTANCE).findFirst().orElse(ItemStack.EMPTY);
	}

	public static class AnyFuelSlotDisplay implements SlotDisplay {
		public static final SlotDisplay.AnyFuelSlotDisplay INSTANCE = new SlotDisplay.AnyFuelSlotDisplay();
		public static final MapCodec<SlotDisplay.AnyFuelSlotDisplay> CODEC = MapCodec.unit(INSTANCE);
		public static final PacketCodec<RegistryByteBuf, SlotDisplay.AnyFuelSlotDisplay> PACKET_CODEC = PacketCodec.unit(INSTANCE);
		public static final SlotDisplay.Serializer<SlotDisplay.AnyFuelSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<>(CODEC, PACKET_CODEC);

		private AnyFuelSlotDisplay() {
		}

		@Override
		public SlotDisplay.Serializer<SlotDisplay.AnyFuelSlotDisplay> serializer() {
			return SERIALIZER;
		}

		public String toString() {
			return "<any fuel>";
		}

		@Override
		public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
			if (factory instanceof DisplayedItemFactory.FromStack<T> fromStack) {
				FuelRegistry fuelRegistry = parameters.getNullable(SlotDisplayContexts.FUEL_REGISTRY);
				if (fuelRegistry != null) {
					return fuelRegistry.getFuelItems().stream().map(fromStack::toDisplayed);
				}
			}

			return Stream.empty();
		}
	}

	public static record CompositeSlotDisplay(List<SlotDisplay> contents) implements SlotDisplay {
		public static final MapCodec<SlotDisplay.CompositeSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(SlotDisplay.CODEC.listOf().fieldOf("contents").forGetter(SlotDisplay.CompositeSlotDisplay::contents))
					.apply(instance, SlotDisplay.CompositeSlotDisplay::new)
		);
		public static final PacketCodec<RegistryByteBuf, SlotDisplay.CompositeSlotDisplay> PACKET_CODEC = PacketCodec.tuple(
			SlotDisplay.PACKET_CODEC.collect(PacketCodecs.toList()), SlotDisplay.CompositeSlotDisplay::contents, SlotDisplay.CompositeSlotDisplay::new
		);
		public static final SlotDisplay.Serializer<SlotDisplay.CompositeSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<>(CODEC, PACKET_CODEC);

		@Override
		public SlotDisplay.Serializer<SlotDisplay.CompositeSlotDisplay> serializer() {
			return SERIALIZER;
		}

		@Override
		public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
			return this.contents.stream().flatMap(display -> display.appendStacks(parameters, factory));
		}

		@Override
		public boolean isEnabled(FeatureSet features) {
			return this.contents.stream().allMatch(child -> child.isEnabled(features));
		}
	}

	public static class EmptySlotDisplay implements SlotDisplay {
		public static final SlotDisplay.EmptySlotDisplay INSTANCE = new SlotDisplay.EmptySlotDisplay();
		public static final MapCodec<SlotDisplay.EmptySlotDisplay> CODEC = MapCodec.unit(INSTANCE);
		public static final PacketCodec<RegistryByteBuf, SlotDisplay.EmptySlotDisplay> PACKET_CODEC = PacketCodec.unit(INSTANCE);
		public static final SlotDisplay.Serializer<SlotDisplay.EmptySlotDisplay> SERIALIZER = new SlotDisplay.Serializer<>(CODEC, PACKET_CODEC);

		private EmptySlotDisplay() {
		}

		@Override
		public SlotDisplay.Serializer<SlotDisplay.EmptySlotDisplay> serializer() {
			return SERIALIZER;
		}

		public String toString() {
			return "<empty>";
		}

		@Override
		public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
			return Stream.empty();
		}
	}

	public static record ItemSlotDisplay(RegistryEntry<Item> item) implements SlotDisplay {
		public static final MapCodec<SlotDisplay.ItemSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(Item.ENTRY_CODEC.fieldOf("item").forGetter(SlotDisplay.ItemSlotDisplay::item)).apply(instance, SlotDisplay.ItemSlotDisplay::new)
		);
		public static final PacketCodec<RegistryByteBuf, SlotDisplay.ItemSlotDisplay> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.registryEntry(RegistryKeys.ITEM), SlotDisplay.ItemSlotDisplay::item, SlotDisplay.ItemSlotDisplay::new
		);
		public static final SlotDisplay.Serializer<SlotDisplay.ItemSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<>(CODEC, PACKET_CODEC);

		public ItemSlotDisplay(Item item) {
			this(item.getRegistryEntry());
		}

		@Override
		public SlotDisplay.Serializer<SlotDisplay.ItemSlotDisplay> serializer() {
			return SERIALIZER;
		}

		@Override
		public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
			return factory instanceof DisplayedItemFactory.FromStack<T> fromStack ? Stream.of(fromStack.toDisplayed(this.item)) : Stream.empty();
		}

		@Override
		public boolean isEnabled(FeatureSet features) {
			return this.item.value().isEnabled(features);
		}
	}

	public static class NoopDisplayedItemFactory implements DisplayedItemFactory.FromStack<ItemStack> {
		public static final SlotDisplay.NoopDisplayedItemFactory INSTANCE = new SlotDisplay.NoopDisplayedItemFactory();

		public ItemStack toDisplayed(ItemStack itemStack) {
			return itemStack;
		}
	}

	public static record Serializer<T extends SlotDisplay>(MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> streamCodec) {
	}

	public static record SmithingTrimSlotDisplay(SlotDisplay base, SlotDisplay material, SlotDisplay pattern) implements SlotDisplay {
		public static final MapCodec<SlotDisplay.SmithingTrimSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						SlotDisplay.CODEC.fieldOf("base").forGetter(SlotDisplay.SmithingTrimSlotDisplay::base),
						SlotDisplay.CODEC.fieldOf("material").forGetter(SlotDisplay.SmithingTrimSlotDisplay::material),
						SlotDisplay.CODEC.fieldOf("pattern").forGetter(SlotDisplay.SmithingTrimSlotDisplay::pattern)
					)
					.apply(instance, SlotDisplay.SmithingTrimSlotDisplay::new)
		);
		public static final PacketCodec<RegistryByteBuf, SlotDisplay.SmithingTrimSlotDisplay> PACKET_CODEC = PacketCodec.tuple(
			SlotDisplay.PACKET_CODEC,
			SlotDisplay.SmithingTrimSlotDisplay::base,
			SlotDisplay.PACKET_CODEC,
			SlotDisplay.SmithingTrimSlotDisplay::material,
			SlotDisplay.PACKET_CODEC,
			SlotDisplay.SmithingTrimSlotDisplay::pattern,
			SlotDisplay.SmithingTrimSlotDisplay::new
		);
		public static final SlotDisplay.Serializer<SlotDisplay.SmithingTrimSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<>(CODEC, PACKET_CODEC);

		@Override
		public SlotDisplay.Serializer<SlotDisplay.SmithingTrimSlotDisplay> serializer() {
			return SERIALIZER;
		}

		@Override
		public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
			if (factory instanceof DisplayedItemFactory.FromStack<T> fromStack) {
				RegistryWrapper.WrapperLookup wrapperLookup = parameters.getNullable(SlotDisplayContexts.REGISTRIES);
				if (wrapperLookup != null) {
					Random random = Random.create((long)System.identityHashCode(this));
					List<ItemStack> list = this.base.getStacks(parameters);
					if (list.isEmpty()) {
						return Stream.empty();
					}

					List<ItemStack> list2 = this.material.getStacks(parameters);
					if (list2.isEmpty()) {
						return Stream.empty();
					}

					List<ItemStack> list3 = this.pattern.getStacks(parameters);
					if (list3.isEmpty()) {
						return Stream.empty();
					}

					return Stream.generate(() -> {
						ItemStack itemStack = Util.getRandom(list, random);
						ItemStack itemStack2 = Util.getRandom(list2, random);
						ItemStack itemStack3 = Util.getRandom(list3, random);
						return SmithingTrimRecipe.craft(wrapperLookup, itemStack, itemStack2, itemStack3);
					}).limit(256L).filter(stack -> !stack.isEmpty()).limit(16L).map(fromStack::toDisplayed);
				}
			}

			return Stream.empty();
		}
	}

	public static record StackSlotDisplay(ItemStack stack) implements SlotDisplay {
		public static final MapCodec<SlotDisplay.StackSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(ItemStack.VALIDATED_CODEC.fieldOf("item").forGetter(SlotDisplay.StackSlotDisplay::stack))
					.apply(instance, SlotDisplay.StackSlotDisplay::new)
		);
		public static final PacketCodec<RegistryByteBuf, SlotDisplay.StackSlotDisplay> PACKET_CODEC = PacketCodec.tuple(
			ItemStack.PACKET_CODEC, SlotDisplay.StackSlotDisplay::stack, SlotDisplay.StackSlotDisplay::new
		);
		public static final SlotDisplay.Serializer<SlotDisplay.StackSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<>(CODEC, PACKET_CODEC);

		@Override
		public SlotDisplay.Serializer<SlotDisplay.StackSlotDisplay> serializer() {
			return SERIALIZER;
		}

		@Override
		public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
			return factory instanceof DisplayedItemFactory.FromStack<T> fromStack ? Stream.of(fromStack.toDisplayed(this.stack)) : Stream.empty();
		}

		public boolean equals(Object o) {
			if (this == o) {
				return true;
			} else {
				if (o instanceof SlotDisplay.StackSlotDisplay stackSlotDisplay && ItemStack.areEqual(this.stack, stackSlotDisplay.stack)) {
					return true;
				}

				return false;
			}
		}

		@Override
		public boolean isEnabled(FeatureSet features) {
			return this.stack.getItem().isEnabled(features);
		}
	}

	public static record TagSlotDisplay(TagKey<Item> tag) implements SlotDisplay {
		public static final MapCodec<SlotDisplay.TagSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(TagKey.unprefixedCodec(RegistryKeys.ITEM).fieldOf("tag").forGetter(SlotDisplay.TagSlotDisplay::tag))
					.apply(instance, SlotDisplay.TagSlotDisplay::new)
		);
		public static final PacketCodec<RegistryByteBuf, SlotDisplay.TagSlotDisplay> PACKET_CODEC = PacketCodec.tuple(
			TagKey.packetCodec(RegistryKeys.ITEM), SlotDisplay.TagSlotDisplay::tag, SlotDisplay.TagSlotDisplay::new
		);
		public static final SlotDisplay.Serializer<SlotDisplay.TagSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<>(CODEC, PACKET_CODEC);

		@Override
		public SlotDisplay.Serializer<SlotDisplay.TagSlotDisplay> serializer() {
			return SERIALIZER;
		}

		@Override
		public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
			if (factory instanceof DisplayedItemFactory.FromStack<T> fromStack) {
				RegistryWrapper.WrapperLookup wrapperLookup = parameters.getNullable(SlotDisplayContexts.REGISTRIES);
				if (wrapperLookup != null) {
					return wrapperLookup.getOrThrow(RegistryKeys.ITEM)
						.getOptional(this.tag)
						.map(tag -> tag.stream().map(fromStack::toDisplayed))
						.stream()
						.flatMap(values -> values);
				}
			}

			return Stream.empty();
		}
	}

	public static record WithRemainderSlotDisplay(SlotDisplay input, SlotDisplay remainder) implements SlotDisplay {
		public static final MapCodec<SlotDisplay.WithRemainderSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(
						SlotDisplay.CODEC.fieldOf("input").forGetter(SlotDisplay.WithRemainderSlotDisplay::input),
						SlotDisplay.CODEC.fieldOf("remainder").forGetter(SlotDisplay.WithRemainderSlotDisplay::remainder)
					)
					.apply(instance, SlotDisplay.WithRemainderSlotDisplay::new)
		);
		public static final PacketCodec<RegistryByteBuf, SlotDisplay.WithRemainderSlotDisplay> PACKET_CODEC = PacketCodec.tuple(
			SlotDisplay.PACKET_CODEC,
			SlotDisplay.WithRemainderSlotDisplay::input,
			SlotDisplay.PACKET_CODEC,
			SlotDisplay.WithRemainderSlotDisplay::remainder,
			SlotDisplay.WithRemainderSlotDisplay::new
		);
		public static final SlotDisplay.Serializer<SlotDisplay.WithRemainderSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<>(CODEC, PACKET_CODEC);

		@Override
		public SlotDisplay.Serializer<SlotDisplay.WithRemainderSlotDisplay> serializer() {
			return SERIALIZER;
		}

		@Override
		public <T> Stream<T> appendStacks(ContextParameterMap parameters, DisplayedItemFactory<T> factory) {
			if (factory instanceof DisplayedItemFactory.FromRemainder<T> fromRemainder) {
				List<T> list = this.remainder.appendStacks(parameters, factory).toList();
				return this.input.appendStacks(parameters, factory).map(input -> fromRemainder.toDisplayed((T)input, list));
			} else {
				return this.input.appendStacks(parameters, factory);
			}
		}

		@Override
		public boolean isEnabled(FeatureSet features) {
			return this.input.isEnabled(features) && this.remainder.isEnabled(features);
		}
	}
}
