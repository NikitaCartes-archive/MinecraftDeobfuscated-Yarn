package net.minecraft.recipe;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.RegistryByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.dynamic.Codecs;

public final class Ingredient implements Predicate<ItemStack> {
	public static final Ingredient EMPTY = new Ingredient(Stream.empty());
	public static final PacketCodec<RegistryByteBuf, Ingredient> PACKET_CODEC = ItemStack.LIST_PACKET_CODEC
		.xmap(list -> ofEntries(list.stream().map(Ingredient.StackEntry::new)), ingredient -> Arrays.asList(ingredient.getMatchingStacks()));
	private final Ingredient.Entry[] entries;
	@Nullable
	private ItemStack[] matchingStacks;
	@Nullable
	private IntList ids;
	public static final Codec<Ingredient> ALLOW_EMPTY_CODEC = createCodec(true);
	public static final Codec<Ingredient> DISALLOW_EMPTY_CODEC = createCodec(false);

	private Ingredient(Stream<? extends Ingredient.Entry> entries) {
		this.entries = (Ingredient.Entry[])entries.toArray(Ingredient.Entry[]::new);
	}

	private Ingredient(Ingredient.Entry[] entries) {
		this.entries = entries;
	}

	public ItemStack[] getMatchingStacks() {
		if (this.matchingStacks == null) {
			this.matchingStacks = (ItemStack[])Arrays.stream(this.entries).flatMap(entry -> entry.getStacks().stream()).distinct().toArray(ItemStack[]::new);
		}

		return this.matchingStacks;
	}

	public boolean test(@Nullable ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		} else if (this.isEmpty()) {
			return itemStack.isEmpty();
		} else {
			for (ItemStack itemStack2 : this.getMatchingStacks()) {
				if (itemStack2.isOf(itemStack.getItem())) {
					return true;
				}
			}

			return false;
		}
	}

	public IntList getMatchingItemIds() {
		if (this.ids == null) {
			ItemStack[] itemStacks = this.getMatchingStacks();
			this.ids = new IntArrayList(itemStacks.length);

			for (ItemStack itemStack : itemStacks) {
				this.ids.add(RecipeMatcher.getItemId(itemStack));
			}

			this.ids.sort(IntComparators.NATURAL_COMPARATOR);
		}

		return this.ids;
	}

	public boolean isEmpty() {
		return this.entries.length == 0;
	}

	public boolean equals(Object o) {
		return o instanceof Ingredient ingredient ? Arrays.equals(this.entries, ingredient.entries) : false;
	}

	private static Ingredient ofEntries(Stream<? extends Ingredient.Entry> entries) {
		Ingredient ingredient = new Ingredient(entries);
		return ingredient.isEmpty() ? EMPTY : ingredient;
	}

	public static Ingredient empty() {
		return EMPTY;
	}

	public static Ingredient ofItems(ItemConvertible... items) {
		return ofStacks(Arrays.stream(items).map(ItemStack::new));
	}

	public static Ingredient ofStacks(ItemStack... stacks) {
		return ofStacks(Arrays.stream(stacks));
	}

	public static Ingredient ofStacks(Stream<ItemStack> stacks) {
		return ofEntries(stacks.filter(stack -> !stack.isEmpty()).map(Ingredient.StackEntry::new));
	}

	public static Ingredient fromTag(TagKey<Item> tag) {
		return ofEntries(Stream.of(new Ingredient.TagEntry(tag)));
	}

	private static Codec<Ingredient> createCodec(boolean allowEmpty) {
		Codec<Ingredient.Entry[]> codec = Codec.list(Ingredient.Entry.CODEC)
			.comapFlatMap(
				entries -> !allowEmpty && entries.size() < 1
						? DataResult.error(() -> "Item array cannot be empty, at least one item must be defined")
						: DataResult.success((Ingredient.Entry[])entries.toArray(new Ingredient.Entry[0])),
				List::of
			);
		return Codecs.either(codec, Ingredient.Entry.CODEC)
			.flatComapMap(
				either -> either.map(Ingredient::new, entry -> new Ingredient(new Ingredient.Entry[]{entry})),
				ingredient -> {
					if (ingredient.entries.length == 1) {
						return DataResult.success(Either.right(ingredient.entries[0]));
					} else {
						return ingredient.entries.length == 0 && !allowEmpty
							? DataResult.error(() -> "Item array cannot be empty, at least one item must be defined")
							: DataResult.success(Either.left(ingredient.entries));
					}
				}
			);
	}

	interface Entry {
		Codec<Ingredient.Entry> CODEC = Codecs.xor(Ingredient.StackEntry.CODEC, Ingredient.TagEntry.CODEC)
			.xmap(either -> either.map(stackEntry -> stackEntry, tagEntry -> tagEntry), entry -> {
				if (entry instanceof Ingredient.TagEntry tagEntry) {
					return Either.right(tagEntry);
				} else if (entry instanceof Ingredient.StackEntry stackEntry) {
					return Either.left(stackEntry);
				} else {
					throw new UnsupportedOperationException("This is neither an item value nor a tag value.");
				}
			});

		Collection<ItemStack> getStacks();
	}

	static record StackEntry(ItemStack stack) implements Ingredient.Entry {
		static final Codec<Ingredient.StackEntry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(ItemStack.INGREDIENT_ENTRY_CODEC.fieldOf("item").forGetter(entry -> entry.stack)).apply(instance, Ingredient.StackEntry::new)
		);

		public boolean equals(Object o) {
			return !(o instanceof Ingredient.StackEntry stackEntry)
				? false
				: stackEntry.stack.getItem().equals(this.stack.getItem()) && stackEntry.stack.getCount() == this.stack.getCount();
		}

		@Override
		public Collection<ItemStack> getStacks() {
			return Collections.singleton(this.stack);
		}
	}

	static record TagEntry(TagKey<Item> tag) implements Ingredient.Entry {
		static final Codec<Ingredient.TagEntry> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(TagKey.unprefixedCodec(RegistryKeys.ITEM).fieldOf("tag").forGetter(entry -> entry.tag)).apply(instance, Ingredient.TagEntry::new)
		);

		public boolean equals(Object o) {
			return o instanceof Ingredient.TagEntry tagEntry ? tagEntry.tag.id().equals(this.tag.id()) : false;
		}

		@Override
		public Collection<ItemStack> getStacks() {
			List<ItemStack> list = Lists.<ItemStack>newArrayList();

			for (RegistryEntry<Item> registryEntry : Registries.ITEM.iterateEntries(this.tag)) {
				list.add(new ItemStack(registryEntry));
			}

			return list;
		}
	}
}
