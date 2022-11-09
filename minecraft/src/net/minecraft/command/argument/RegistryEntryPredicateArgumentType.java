package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class RegistryEntryPredicateArgumentType<T> implements ArgumentType<RegistryEntryPredicateArgumentType.EntryPredicate<T>> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
	private static final Dynamic2CommandExceptionType NOT_FOUND_EXCEPTION = new Dynamic2CommandExceptionType(
		(tag, type) -> Text.translatable("argument.resource_tag.not_found", tag, type)
	);
	private static final Dynamic3CommandExceptionType WRONG_TYPE_EXCEPTION = new Dynamic3CommandExceptionType(
		(tag, type, expectedType) -> Text.translatable("argument.resource_tag.invalid_type", tag, type, expectedType)
	);
	private final RegistryWrapper<T> registryWrapper;
	final RegistryKey<? extends Registry<T>> registryRef;

	public RegistryEntryPredicateArgumentType(CommandRegistryAccess registryAccess, RegistryKey<? extends Registry<T>> registryRef) {
		this.registryRef = registryRef;
		this.registryWrapper = registryAccess.createWrapper(registryRef);
	}

	public static <T> RegistryEntryPredicateArgumentType<T> registryEntryPredicate(
		CommandRegistryAccess registryRef, RegistryKey<? extends Registry<T>> registryAccess
	) {
		return new RegistryEntryPredicateArgumentType<>(registryRef, registryAccess);
	}

	public static <T> RegistryEntryPredicateArgumentType.EntryPredicate<T> getRegistryEntryPredicate(
		CommandContext<ServerCommandSource> context, String name, RegistryKey<Registry<T>> registryRef
	) throws CommandSyntaxException {
		RegistryEntryPredicateArgumentType.EntryPredicate<?> entryPredicate = context.getArgument(name, RegistryEntryPredicateArgumentType.EntryPredicate.class);
		Optional<RegistryEntryPredicateArgumentType.EntryPredicate<T>> optional = entryPredicate.tryCast(registryRef);
		return (RegistryEntryPredicateArgumentType.EntryPredicate<T>)optional.orElseThrow(() -> entryPredicate.getEntry().map(entry -> {
				RegistryKey<?> registryKey2 = entry.registryKey();
				return RegistryEntryArgumentType.INVALID_TYPE_EXCEPTION.create(registryKey2.getValue(), registryKey2.getRegistry(), registryRef.getValue());
			}, entryList -> {
				TagKey<?> tagKey = entryList.getTag();
				return WRONG_TYPE_EXCEPTION.create(tagKey.id(), tagKey.registry(), registryRef.getValue());
			}));
	}

	public RegistryEntryPredicateArgumentType.EntryPredicate<T> parse(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '#') {
			int i = stringReader.getCursor();

			try {
				stringReader.skip();
				Identifier identifier = Identifier.fromCommandInput(stringReader);
				TagKey<T> tagKey = TagKey.of(this.registryRef, identifier);
				RegistryEntryList.Named<T> named = (RegistryEntryList.Named<T>)this.registryWrapper
					.getOptional(tagKey)
					.orElseThrow(() -> NOT_FOUND_EXCEPTION.create(identifier, this.registryRef.getValue()));
				return new RegistryEntryPredicateArgumentType.TagBased<>(named);
			} catch (CommandSyntaxException var6) {
				stringReader.setCursor(i);
				throw var6;
			}
		} else {
			Identifier identifier2 = Identifier.fromCommandInput(stringReader);
			RegistryKey<T> registryKey = RegistryKey.of(this.registryRef, identifier2);
			RegistryEntry.Reference<T> reference = (RegistryEntry.Reference<T>)this.registryWrapper
				.getOptional(registryKey)
				.orElseThrow(() -> RegistryEntryArgumentType.NOT_FOUND_EXCEPTION.create(identifier2, this.registryRef.getValue()));
			return new RegistryEntryPredicateArgumentType.EntryBased<>(reference);
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		CommandSource.suggestIdentifiers(this.registryWrapper.streamTagKeys().map(TagKey::id), builder, "#");
		return CommandSource.suggestIdentifiers(this.registryWrapper.streamKeys().map(RegistryKey::getValue), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	static record EntryBased<T>(RegistryEntry.Reference<T> value) implements RegistryEntryPredicateArgumentType.EntryPredicate<T> {
		@Override
		public Either<RegistryEntry.Reference<T>, RegistryEntryList.Named<T>> getEntry() {
			return Either.left(this.value);
		}

		@Override
		public <E> Optional<RegistryEntryPredicateArgumentType.EntryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
			return this.value.registryKey().isOf(registryRef) ? Optional.of(this) : Optional.empty();
		}

		public boolean test(RegistryEntry<T> registryEntry) {
			return registryEntry.equals(this.value);
		}

		@Override
		public String asString() {
			return this.value.registryKey().getValue().toString();
		}
	}

	public interface EntryPredicate<T> extends Predicate<RegistryEntry<T>> {
		Either<RegistryEntry.Reference<T>, RegistryEntryList.Named<T>> getEntry();

		<E> Optional<RegistryEntryPredicateArgumentType.EntryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef);

		String asString();
	}

	public static class Serializer<T>
		implements ArgumentSerializer<RegistryEntryPredicateArgumentType<T>, RegistryEntryPredicateArgumentType.Serializer<T>.Properties> {
		public void writePacket(RegistryEntryPredicateArgumentType.Serializer<T>.Properties properties, PacketByteBuf packetByteBuf) {
			packetByteBuf.writeIdentifier(properties.registryRef.getValue());
		}

		public RegistryEntryPredicateArgumentType.Serializer<T>.Properties fromPacket(PacketByteBuf packetByteBuf) {
			Identifier identifier = packetByteBuf.readIdentifier();
			return new RegistryEntryPredicateArgumentType.Serializer.Properties(RegistryKey.ofRegistry(identifier));
		}

		public void writeJson(RegistryEntryPredicateArgumentType.Serializer<T>.Properties properties, JsonObject jsonObject) {
			jsonObject.addProperty("registry", properties.registryRef.getValue().toString());
		}

		public RegistryEntryPredicateArgumentType.Serializer<T>.Properties getArgumentTypeProperties(
			RegistryEntryPredicateArgumentType<T> registryEntryPredicateArgumentType
		) {
			return new RegistryEntryPredicateArgumentType.Serializer.Properties(registryEntryPredicateArgumentType.registryRef);
		}

		public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<RegistryEntryPredicateArgumentType<T>> {
			final RegistryKey<? extends Registry<T>> registryRef;

			Properties(RegistryKey<? extends Registry<T>> registryRef) {
				this.registryRef = registryRef;
			}

			public RegistryEntryPredicateArgumentType<T> createType(CommandRegistryAccess commandRegistryAccess) {
				return new RegistryEntryPredicateArgumentType<>(commandRegistryAccess, this.registryRef);
			}

			@Override
			public ArgumentSerializer<RegistryEntryPredicateArgumentType<T>, ?> getSerializer() {
				return Serializer.this;
			}
		}
	}

	static record TagBased<T>(RegistryEntryList.Named<T> tag) implements RegistryEntryPredicateArgumentType.EntryPredicate<T> {
		@Override
		public Either<RegistryEntry.Reference<T>, RegistryEntryList.Named<T>> getEntry() {
			return Either.right(this.tag);
		}

		@Override
		public <E> Optional<RegistryEntryPredicateArgumentType.EntryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
			return this.tag.getTag().isOf(registryRef) ? Optional.of(this) : Optional.empty();
		}

		public boolean test(RegistryEntry<T> registryEntry) {
			return this.tag.contains(registryEntry);
		}

		@Override
		public String asString() {
			return "#" + this.tag.getTag().id();
		}
	}
}
