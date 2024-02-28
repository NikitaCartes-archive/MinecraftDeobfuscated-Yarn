package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import org.apache.commons.lang3.mutable.MutableObject;

public class ItemStringReader {
	private final ItemPredicateReader predicateReader;

	public ItemStringReader(RegistryWrapper.WrapperLookup registryLookup) {
		this.predicateReader = new ItemPredicateReader(registryLookup, false);
	}

	public ItemStringReader.ItemResult consume(StringReader reader) throws CommandSyntaxException {
		final MutableObject<RegistryEntry<Item>> mutableObject = new MutableObject<>();
		final ComponentMap.Builder builder = ComponentMap.builder();
		this.predicateReader.read(reader, new ItemPredicateReader.Callbacks() {
			@Override
			public void onItem(RegistryEntry<Item> item) {
				mutableObject.setValue(item);
			}

			@Override
			public <T> void onComponent(DataComponentType<T> type, T value) {
				builder.add(type, value);
			}

			@Override
			public void setNbt(NbtCompound nbt) {
				builder.add(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
			}
		});
		return new ItemStringReader.ItemResult((RegistryEntry<Item>)Objects.requireNonNull(mutableObject.getValue(), "Parser gave no item"), builder.build());
	}

	public CompletableFuture<Suggestions> getSuggestions(SuggestionsBuilder builder) {
		return this.predicateReader.getSuggestions(builder);
	}

	public static record ItemResult(RegistryEntry<Item> item, ComponentMap components) {
	}
}
