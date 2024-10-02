package net.minecraft.recipe.display;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.FuelRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.equipment.trim.ArmorTrim;
import net.minecraft.item.equipment.trim.ArmorTrimMaterial;
import net.minecraft.item.equipment.trim.ArmorTrimMaterials;
import net.minecraft.item.equipment.trim.ArmorTrimPattern;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableObject;

public interface SlotDisplay {
	Codec<SlotDisplay> CODEC = Registries.SLOT_DISPLAY.getCodec().dispatch(SlotDisplay::serializer, SlotDisplay.Serializer::codec);
	PacketCodec<RegistryByteBuf, SlotDisplay> PACKET_CODEC = PacketCodecs.registryValue(RegistryKeys.SLOT_DISPLAY)
		.dispatch(SlotDisplay::serializer, SlotDisplay.Serializer::streamCodec);

	void appendStacks(SlotDisplay.Context context, SlotDisplay.StackConsumer consumer);

	SlotDisplay.Serializer<? extends SlotDisplay> serializer();

	default boolean isEnabled(FeatureSet features) {
		return true;
	}

	default void appendStacks(SlotDisplay.Context context, Consumer<ItemStack> consumer) {
		this.appendStacks(context, new SlotDisplay.StackConsumer() {
			@Override
			public void append(RegistryEntry<Item> item) {
				consumer.accept(new ItemStack(item));
			}

			@Override
			public void append(Item item) {
				consumer.accept(new ItemStack(item));
			}

			@Override
			public void append(ItemStack stack) {
				consumer.accept(stack);
			}
		});
	}

	default List<ItemStack> getStacks(SlotDisplay.Context context) {
		List<ItemStack> list = new ArrayList();
		this.appendStacks(context, list::add);
		return list;
	}

	default ItemStack getFirst(SlotDisplay.Context context) {
		MutableObject<ItemStack> mutableObject = new MutableObject<>(ItemStack.EMPTY);
		this.appendStacks(context, stack -> {
			if (!stack.isEmpty() && mutableObject.getValue().isEmpty()) {
				mutableObject.setValue(stack);
			}
		});
		return mutableObject.getValue();
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
		public void appendStacks(SlotDisplay.Context context, SlotDisplay.StackConsumer consumer) {
			context.fuelRegistry().getFuelItems().forEach(consumer::append);
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
		public void appendStacks(SlotDisplay.Context context, SlotDisplay.StackConsumer consumer) {
			this.contents.forEach(display -> display.appendStacks(context, consumer));
		}

		@Override
		public boolean isEnabled(FeatureSet features) {
			return this.contents.stream().allMatch(child -> child.isEnabled(features));
		}
	}

	public interface Context {
		FuelRegistry fuelRegistry();

		RegistryWrapper.WrapperLookup registries();

		static SlotDisplay.Context create(World world) {
			return new SlotDisplay.Context() {
				@Override
				public FuelRegistry fuelRegistry() {
					return world.getFuelRegistry();
				}

				@Override
				public RegistryWrapper.WrapperLookup registries() {
					return world.getRegistryManager();
				}
			};
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
		public void appendStacks(SlotDisplay.Context context, SlotDisplay.StackConsumer consumer) {
		}
	}

	public static record ItemSlotDisplay(RegistryEntry<Item> item) implements SlotDisplay {
		public static final MapCodec<SlotDisplay.ItemSlotDisplay> CODEC = RecordCodecBuilder.mapCodec(
			instance -> instance.group(RegistryFixedCodec.of(RegistryKeys.ITEM).fieldOf("item").forGetter(SlotDisplay.ItemSlotDisplay::item))
					.apply(instance, SlotDisplay.ItemSlotDisplay::new)
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
		public void appendStacks(SlotDisplay.Context context, SlotDisplay.StackConsumer consumer) {
			consumer.append(this.item);
		}

		@Override
		public boolean isEnabled(FeatureSet features) {
			return this.item.value().isEnabled(features);
		}
	}

	public static record Serializer<T extends SlotDisplay>(MapCodec<T> codec, PacketCodec<RegistryByteBuf, T> streamCodec) {
	}

	public static class SmithingTrimSlotDisplay implements SlotDisplay {
		public static final SlotDisplay.SmithingTrimSlotDisplay INSTANCE = new SlotDisplay.SmithingTrimSlotDisplay();
		public static final MapCodec<SlotDisplay.SmithingTrimSlotDisplay> CODEC = MapCodec.unit(INSTANCE);
		public static final PacketCodec<RegistryByteBuf, SlotDisplay.SmithingTrimSlotDisplay> PACKET_CODEC = PacketCodec.unit(INSTANCE);
		public static final SlotDisplay.Serializer<SlotDisplay.SmithingTrimSlotDisplay> SERIALIZER = new SlotDisplay.Serializer<>(CODEC, PACKET_CODEC);

		private SmithingTrimSlotDisplay() {
		}

		@Override
		public SlotDisplay.Serializer<SlotDisplay.SmithingTrimSlotDisplay> serializer() {
			return SERIALIZER;
		}

		public String toString() {
			return "<smithing trim demo>";
		}

		@Override
		public void appendStacks(SlotDisplay.Context context, SlotDisplay.StackConsumer consumer) {
			Optional<RegistryEntry.Reference<ArmorTrimPattern>> optional = context.registries().getOrThrow(RegistryKeys.TRIM_PATTERN).streamEntries().findFirst();
			Optional<RegistryEntry.Reference<ArmorTrimMaterial>> optional2 = context.registries()
				.getOrThrow(RegistryKeys.TRIM_MATERIAL)
				.getOptional(ArmorTrimMaterials.REDSTONE);
			if (optional.isPresent() && optional2.isPresent()) {
				ItemStack itemStack = new ItemStack(Items.IRON_CHESTPLATE);
				itemStack.set(DataComponentTypes.TRIM, new ArmorTrim((RegistryEntry<ArmorTrimMaterial>)optional2.get(), (RegistryEntry<ArmorTrimPattern>)optional.get()));
				consumer.append(itemStack);
			}
		}
	}

	public interface StackConsumer {
		void append(RegistryEntry<Item> item);

		void append(Item item);

		void append(ItemStack stack);
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
		public void appendStacks(SlotDisplay.Context context, SlotDisplay.StackConsumer consumer) {
			consumer.append(this.stack);
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
		public void appendStacks(SlotDisplay.Context context, SlotDisplay.StackConsumer consumer) {
			context.registries().getOrThrow(RegistryKeys.ITEM).getOptional(this.tag).ifPresent(tag -> tag.forEach(consumer::append));
		}
	}
}
