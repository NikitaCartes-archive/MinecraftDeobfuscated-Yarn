package net.minecraft;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.command.argument.TagKeyArgumentType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.Util;

public class class_9215 {
	private final TagKeyArgumentType field_48960;

	public class_9215(RegistryWrapper.WrapperLookup wrapperLookup) {
		this.field_48960 = new TagKeyArgumentType(wrapperLookup, true);
	}

	public Predicate<ItemStack> method_56859(StringReader stringReader) throws CommandSyntaxException {
		final List<Predicate<ItemStack>> list = new ArrayList();
		this.field_48960.method_56865(stringReader, new TagKeyArgumentType.class_9219() {
			@Override
			public void method_56853(RegistryEntry<Item> registryEntry) {
				list.add((Predicate)itemStack -> itemStack.itemMatches(registryEntry));
			}

			@Override
			public void method_56862(RegistryEntryList<Item> registryEntryList) {
				list.add((Predicate)itemStack -> itemStack.itemMatches(registryEntryList));
			}

			@Override
			public void method_56854(NbtCompound nbtCompound) {
				if (!nbtCompound.isEmpty()) {
					list.add((Predicate)itemStack -> {
						NbtCompound nbtCompound2 = itemStack.getNbt();
						return NbtHelper.matches(nbtCompound, nbtCompound2, true);
					});
				}
			}
		});
		return Util.allOf(list);
	}

	public CompletableFuture<Suggestions> method_56860(SuggestionsBuilder suggestionsBuilder) {
		return this.field_48960.method_56866(suggestionsBuilder);
	}
}
