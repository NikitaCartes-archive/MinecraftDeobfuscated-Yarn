package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.ServerRecipeManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.structure.Structure;

public class RegistryKeyArgumentType<T> implements ArgumentType<RegistryKey<T>> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
	private static final DynamicCommandExceptionType INVALID_FEATURE_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("commands.place.feature.invalid", id)
	);
	private static final DynamicCommandExceptionType INVALID_STRUCTURE_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("commands.place.structure.invalid", id)
	);
	private static final DynamicCommandExceptionType INVALID_JIGSAW_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("commands.place.jigsaw.invalid", id)
	);
	private static final DynamicCommandExceptionType RECIPE_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("recipe.notFound", id)
	);
	private static final DynamicCommandExceptionType ADVANCEMENT_NOT_FOUND_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("advancement.advancementNotFound", id)
	);
	final RegistryKey<? extends Registry<T>> registryRef;

	public RegistryKeyArgumentType(RegistryKey<? extends Registry<T>> registryRef) {
		this.registryRef = registryRef;
	}

	public static <T> RegistryKeyArgumentType<T> registryKey(RegistryKey<? extends Registry<T>> registryRef) {
		return new RegistryKeyArgumentType<>(registryRef);
	}

	private static <T> RegistryKey<T> getKey(
		CommandContext<ServerCommandSource> context, String name, RegistryKey<Registry<T>> registryRef, DynamicCommandExceptionType invalidException
	) throws CommandSyntaxException {
		RegistryKey<?> registryKey = context.getArgument(name, RegistryKey.class);
		Optional<RegistryKey<T>> optional = registryKey.tryCast(registryRef);
		return (RegistryKey<T>)optional.orElseThrow(() -> invalidException.create(registryKey.getValue()));
	}

	private static <T> Registry<T> getRegistry(CommandContext<ServerCommandSource> context, RegistryKey<? extends Registry<T>> registryRef) {
		return context.getSource().getServer().getRegistryManager().getOrThrow(registryRef);
	}

	private static <T> RegistryEntry.Reference<T> getRegistryEntry(
		CommandContext<ServerCommandSource> context, String name, RegistryKey<Registry<T>> registryRef, DynamicCommandExceptionType invalidException
	) throws CommandSyntaxException {
		RegistryKey<T> registryKey = getKey(context, name, registryRef, invalidException);
		return (RegistryEntry.Reference<T>)getRegistry(context, registryRef)
			.getOptional(registryKey)
			.orElseThrow(() -> invalidException.create(registryKey.getValue()));
	}

	public static RegistryEntry.Reference<ConfiguredFeature<?, ?>> getConfiguredFeatureEntry(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getRegistryEntry(context, name, RegistryKeys.CONFIGURED_FEATURE, INVALID_FEATURE_EXCEPTION);
	}

	public static RegistryEntry.Reference<Structure> getStructureEntry(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getRegistryEntry(context, name, RegistryKeys.STRUCTURE, INVALID_STRUCTURE_EXCEPTION);
	}

	public static RegistryEntry.Reference<StructurePool> getStructurePoolEntry(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getRegistryEntry(context, name, RegistryKeys.TEMPLATE_POOL, INVALID_JIGSAW_EXCEPTION);
	}

	public static RecipeEntry<?> getRecipeEntry(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		ServerRecipeManager serverRecipeManager = context.getSource().getServer().getRecipeManager();
		RegistryKey<Recipe<?>> registryKey = getKey(context, name, RegistryKeys.RECIPE, RECIPE_NOT_FOUND_EXCEPTION);
		return (RecipeEntry<?>)serverRecipeManager.get(registryKey).orElseThrow(() -> RECIPE_NOT_FOUND_EXCEPTION.create(registryKey.getValue()));
	}

	public static AdvancementEntry getAdvancementEntry(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		RegistryKey<Advancement> registryKey = getKey(context, name, RegistryKeys.ADVANCEMENT, ADVANCEMENT_NOT_FOUND_EXCEPTION);
		AdvancementEntry advancementEntry = context.getSource().getServer().getAdvancementLoader().get(registryKey.getValue());
		if (advancementEntry == null) {
			throw ADVANCEMENT_NOT_FOUND_EXCEPTION.create(registryKey.getValue());
		} else {
			return advancementEntry;
		}
	}

	public RegistryKey<T> parse(StringReader stringReader) throws CommandSyntaxException {
		Identifier identifier = Identifier.fromCommandInput(stringReader);
		return RegistryKey.of(this.registryRef, identifier);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return context.getSource() instanceof CommandSource commandSource
			? commandSource.listIdSuggestions(this.registryRef, CommandSource.SuggestedIdType.ELEMENTS, builder, context)
			: builder.buildFuture();
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static class Serializer<T> implements ArgumentSerializer<RegistryKeyArgumentType<T>, RegistryKeyArgumentType.Serializer<T>.Properties> {
		public void writePacket(RegistryKeyArgumentType.Serializer<T>.Properties properties, PacketByteBuf packetByteBuf) {
			packetByteBuf.writeRegistryKey(properties.registryRef);
		}

		public RegistryKeyArgumentType.Serializer<T>.Properties fromPacket(PacketByteBuf packetByteBuf) {
			return new RegistryKeyArgumentType.Serializer.Properties(packetByteBuf.readRegistryRefKey());
		}

		public void writeJson(RegistryKeyArgumentType.Serializer<T>.Properties properties, JsonObject jsonObject) {
			jsonObject.addProperty("registry", properties.registryRef.getValue().toString());
		}

		public RegistryKeyArgumentType.Serializer<T>.Properties getArgumentTypeProperties(RegistryKeyArgumentType<T> registryKeyArgumentType) {
			return new RegistryKeyArgumentType.Serializer.Properties(registryKeyArgumentType.registryRef);
		}

		public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<RegistryKeyArgumentType<T>> {
			final RegistryKey<? extends Registry<T>> registryRef;

			Properties(final RegistryKey<? extends Registry<T>> registryRef) {
				this.registryRef = registryRef;
			}

			public RegistryKeyArgumentType<T> createType(CommandRegistryAccess commandRegistryAccess) {
				return new RegistryKeyArgumentType<>(this.registryRef);
			}

			@Override
			public ArgumentSerializer<RegistryKeyArgumentType<T>, ?> getSerializer() {
				return Serializer.this;
			}
		}
	}
}
