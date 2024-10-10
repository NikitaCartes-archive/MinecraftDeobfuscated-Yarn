package net.minecraft.recipe;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.display.SlotDisplay;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryEntryListCodec;
import net.minecraft.util.dynamic.Codecs;

public final class Ingredient implements Predicate<ItemStack> {
	public static final PacketCodec<RegistryByteBuf, Ingredient> PACKET_CODEC = PacketCodecs.registryEntryList(RegistryKeys.ITEM)
		.xmap(Ingredient::new, ingredient -> ingredient.entries);
	public static final PacketCodec<RegistryByteBuf, Optional<Ingredient>> OPTIONAL_PACKET_CODEC = PacketCodecs.registryEntryList(RegistryKeys.ITEM)
		.xmap(
			entries -> entries.size() == 0 ? Optional.empty() : Optional.of(new Ingredient(entries)),
			optional -> (RegistryEntryList)optional.map(ingredient -> ingredient.entries).orElse(RegistryEntryList.of())
		);
	public static final Codec<RegistryEntryList<Item>> ENTRIES_CODEC = RegistryEntryListCodec.create(RegistryKeys.ITEM, Item.ENTRY_CODEC, false);
	public static final Codec<Ingredient> CODEC = Codecs.nonEmptyEntryList(ENTRIES_CODEC).xmap(Ingredient::new, ingredient -> ingredient.entries);
	private final RegistryEntryList<Item> entries;
	@Nullable
	private List<RegistryEntry<Item>> matchingItems;

	private Ingredient(RegistryEntryList<Item> entries) {
		entries.getStorage().ifRight(list -> {
			if (list.isEmpty()) {
				throw new UnsupportedOperationException("Ingredients can't be empty");
			} else if (list.contains(Items.AIR.getRegistryEntry())) {
				throw new UnsupportedOperationException("Ingredient can't contain air");
			}
		});
		this.entries = entries;
	}

	public static boolean matches(Optional<Ingredient> ingredient, ItemStack stack) {
		return (Boolean)ingredient.map(ingredient2 -> ingredient2.test(stack)).orElseGet(stack::isEmpty);
	}

	public List<RegistryEntry<Item>> getMatchingItems() {
		if (this.matchingItems == null) {
			this.matchingItems = ImmutableList.copyOf(this.entries);
		}

		return this.matchingItems;
	}

	public boolean test(ItemStack itemStack) {
		List<RegistryEntry<Item>> list = this.getMatchingItems();

		for (int i = 0; i < list.size(); i++) {
			if (itemStack.itemMatches((RegistryEntry<Item>)list.get(i))) {
				return true;
			}
		}

		return false;
	}

	public boolean equals(Object o) {
		return o instanceof Ingredient ingredient ? Objects.equals(this.entries, ingredient.entries) : false;
	}

	public static Ingredient ofItem(ItemConvertible item) {
		return new Ingredient(RegistryEntryList.of(item.asItem().getRegistryEntry()));
	}

	public static Ingredient ofItems(ItemConvertible... items) {
		return ofItems(Arrays.stream(items));
	}

	public static Ingredient ofItems(Stream<? extends ItemConvertible> stacks) {
		return new Ingredient(RegistryEntryList.of(stacks.map(item -> item.asItem().getRegistryEntry()).toList()));
	}

	public static Ingredient fromTag(RegistryEntryList<Item> tag) {
		return new Ingredient(tag);
	}

	public SlotDisplay toDisplay() {
		return this.entries
			.getStorage()
			.map(SlotDisplay.TagSlotDisplay::new, items -> new SlotDisplay.CompositeSlotDisplay(items.stream().map(Ingredient::createDisplayWithRemainder).toList()));
	}

	public static SlotDisplay toDisplay(Optional<Ingredient> ingredient) {
		return (SlotDisplay)ingredient.map(Ingredient::toDisplay).orElse(SlotDisplay.EmptySlotDisplay.INSTANCE);
	}

	private static SlotDisplay createDisplayWithRemainder(RegistryEntry<Item> displayedItem) {
		SlotDisplay slotDisplay = new SlotDisplay.ItemSlotDisplay(displayedItem);
		ItemStack itemStack = displayedItem.value().getRecipeRemainder();
		if (!itemStack.isEmpty()) {
			SlotDisplay slotDisplay2 = new SlotDisplay.StackSlotDisplay(itemStack);
			return new SlotDisplay.WithRemainderSlotDisplay(slotDisplay, slotDisplay2);
		} else {
			return slotDisplay;
		}
	}
}
