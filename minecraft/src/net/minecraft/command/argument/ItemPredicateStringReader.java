package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Util;

public class ItemPredicateStringReader {
	private final ItemPredicateReader predicateReader;

	public ItemPredicateStringReader(RegistryWrapper.WrapperLookup registryLookup) {
		this.predicateReader = new ItemPredicateReader(registryLookup, true);
	}

	public Predicate<ItemStack> read(StringReader reader) throws CommandSyntaxException {
		final List<Predicate<ItemStack>> list = new ArrayList();
		this.predicateReader.read(reader, new ItemPredicateReader.Callbacks() {
			@Override
			public void onItem(RegistryEntry<Item> item) {
				list.add((Predicate)stack -> stack.itemMatches(item));
			}

			@Override
			public void onTag(RegistryEntryList<Item> tag) {
				list.add((Predicate)stack -> stack.itemMatches(tag));
			}

			@Override
			public void setNbt(NbtCompound nbt) {
				if (!nbt.isEmpty()) {
					list.add((Predicate)stack -> {
						NbtCompound nbtCompound2 = stack.getNbt();
						return NbtHelper.matches(nbt, nbtCompound2, true);
					});
				}
			}
		});
		return Util.allOf(list);
	}

	public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder) {
		return this.predicateReader.getSuggestions(builder);
	}
}
