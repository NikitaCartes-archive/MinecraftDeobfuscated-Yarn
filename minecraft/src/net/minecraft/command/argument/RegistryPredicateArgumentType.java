package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.datafixers.util.Either;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.TagKey;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredStructureFeature;

public class RegistryPredicateArgumentType<T> implements ArgumentType<RegistryPredicateArgumentType.RegistryPredicate<T>> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
	private static final DynamicCommandExceptionType INVALID_BIOME_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("commands.locatebiome.invalid", id)
	);
	private static final DynamicCommandExceptionType INVALID_CONFIGURED_STRUCTURE_FEATURE_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("commands.locate.invalid", id)
	);
	final RegistryKey<? extends Registry<T>> registryRef;

	public RegistryPredicateArgumentType(RegistryKey<? extends Registry<T>> registryRef) {
		this.registryRef = registryRef;
	}

	public static <T> RegistryPredicateArgumentType<T> registryPredicate(RegistryKey<? extends Registry<T>> registryRef) {
		return new RegistryPredicateArgumentType<>(registryRef);
	}

	private static <T> RegistryPredicateArgumentType.RegistryPredicate<T> getPredicate(
		CommandContext<ServerCommandSource> context, String name, RegistryKey<Registry<T>> registryRef, DynamicCommandExceptionType invalidException
	) throws CommandSyntaxException {
		RegistryPredicateArgumentType.RegistryPredicate<?> registryPredicate = context.getArgument(name, RegistryPredicateArgumentType.RegistryPredicate.class);
		Optional<RegistryPredicateArgumentType.RegistryPredicate<T>> optional = registryPredicate.tryCast(registryRef);
		return (RegistryPredicateArgumentType.RegistryPredicate<T>)optional.orElseThrow(() -> invalidException.create(registryPredicate));
	}

	public static RegistryPredicateArgumentType.RegistryPredicate<Biome> getBiomePredicate(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getPredicate(context, name, Registry.BIOME_KEY, INVALID_BIOME_EXCEPTION);
	}

	public static RegistryPredicateArgumentType.RegistryPredicate<ConfiguredStructureFeature<?, ?>> getConfiguredStructureFeaturePredicate(
		CommandContext<ServerCommandSource> context, String name
	) throws CommandSyntaxException {
		return getPredicate(context, name, Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, INVALID_CONFIGURED_STRUCTURE_FEATURE_EXCEPTION);
	}

	public RegistryPredicateArgumentType.RegistryPredicate<T> parse(StringReader stringReader) throws CommandSyntaxException {
		if (stringReader.canRead() && stringReader.peek() == '#') {
			int i = stringReader.getCursor();

			try {
				stringReader.skip();
				Identifier identifier = Identifier.fromCommandInput(stringReader);
				return new RegistryPredicateArgumentType.TagBased<>(TagKey.of(this.registryRef, identifier));
			} catch (CommandSyntaxException var4) {
				stringReader.setCursor(i);
				throw var4;
			}
		} else {
			Identifier identifier2 = Identifier.fromCommandInput(stringReader);
			return new RegistryPredicateArgumentType.RegistryKeyBased<>(RegistryKey.of(this.registryRef, identifier2));
		}
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return context.getSource() instanceof CommandSource commandSource
			? commandSource.listIdSuggestions(this.registryRef, CommandSource.SuggestedIdType.ALL, builder, context)
			: builder.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	static record RegistryKeyBased<T>(RegistryKey<T> key) implements RegistryPredicateArgumentType.RegistryPredicate<T> {
		@Override
		public Either<RegistryKey<T>, TagKey<T>> getKey() {
			return Either.left(this.key);
		}

		@Override
		public <E> Optional<RegistryPredicateArgumentType.RegistryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
			return this.key.tryCast(registryRef).map(RegistryPredicateArgumentType.RegistryKeyBased::new);
		}

		public boolean test(RegistryEntry<T> registryEntry) {
			return registryEntry.matchesKey(this.key);
		}

		@Override
		public String asString() {
			return this.key.getValue().toString();
		}
	}

	public interface RegistryPredicate<T> extends Predicate<RegistryEntry<T>> {
		Either<RegistryKey<T>, TagKey<T>> getKey();

		<E> Optional<RegistryPredicateArgumentType.RegistryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef);

		String asString();
	}

	public static class Serializer implements ArgumentSerializer<RegistryPredicateArgumentType<?>> {
		public void toPacket(RegistryPredicateArgumentType<?> registryPredicateArgumentType, PacketByteBuf packetByteBuf) {
			packetByteBuf.writeIdentifier(registryPredicateArgumentType.registryRef.getValue());
		}

		public RegistryPredicateArgumentType<?> fromPacket(PacketByteBuf packetByteBuf) {
			Identifier identifier = packetByteBuf.readIdentifier();
			return new RegistryPredicateArgumentType(RegistryKey.ofRegistry(identifier));
		}

		public void toJson(RegistryPredicateArgumentType<?> registryPredicateArgumentType, JsonObject jsonObject) {
			jsonObject.addProperty("registry", registryPredicateArgumentType.registryRef.getValue().toString());
		}
	}

	static record TagBased<T>(TagKey<T> key) implements RegistryPredicateArgumentType.RegistryPredicate<T> {
		@Override
		public Either<RegistryKey<T>, TagKey<T>> getKey() {
			return Either.right(this.key);
		}

		@Override
		public <E> Optional<RegistryPredicateArgumentType.RegistryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
			return this.key.tryCast(registryRef).map(RegistryPredicateArgumentType.TagBased::new);
		}

		public boolean test(RegistryEntry<T> registryEntry) {
			return registryEntry.isIn(this.key);
		}

		@Override
		public String asString() {
			return "#" + this.key.id();
		}
	}
}
