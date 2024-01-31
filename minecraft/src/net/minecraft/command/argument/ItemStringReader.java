package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.commons.lang3.mutable.MutableObject;

public class ItemStringReader {
	private final TagKeyArgumentType field_48955;

	public ItemStringReader(RegistryWrapper.WrapperLookup wrapperLookup) {
		this.field_48955 = new TagKeyArgumentType(wrapperLookup, false);
	}

	public ItemStringReader.ItemResult consume(StringReader stringReader) throws CommandSyntaxException {
		final MutableObject<RegistryEntry<Item>> mutableObject = new MutableObject<>();
		final MutableObject<NbtCompound> mutableObject2 = new MutableObject<>();
		this.field_48955.method_56865(stringReader, new TagKeyArgumentType.class_9219() {
			@Override
			public void method_56853(RegistryEntry<Item> registryEntry) {
				mutableObject.setValue(registryEntry);
			}

			@Override
			public void method_56854(NbtCompound nbtCompound) {
				mutableObject2.setValue(nbtCompound);
			}
		});
		return new ItemStringReader.ItemResult(
			(RegistryEntry<Item>)Objects.requireNonNull(mutableObject.getValue(), "Parser gave no item"), mutableObject2.getValue()
		);
	}

	public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder suggestionsBuilder) {
		return this.field_48955.method_56866(suggestionsBuilder);
	}

	public static record ItemResult(RegistryEntry<Item> item, @Nullable NbtCompound nbt) {
	}
}
