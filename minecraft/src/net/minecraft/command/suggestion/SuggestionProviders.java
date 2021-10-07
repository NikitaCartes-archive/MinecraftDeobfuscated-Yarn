package net.minecraft.command.suggestion;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class SuggestionProviders {
	private static final Map<Identifier, SuggestionProvider<CommandSource>> REGISTRY = Maps.<Identifier, SuggestionProvider<CommandSource>>newHashMap();
	private static final Identifier ASK_SERVER_NAME = new Identifier("ask_server");
	public static final SuggestionProvider<CommandSource> ASK_SERVER = register(
		ASK_SERVER_NAME, (context, builder) -> context.getSource().getCompletions(context, builder)
	);
	public static final SuggestionProvider<ServerCommandSource> ALL_RECIPES = register(
		new Identifier("all_recipes"), (context, builder) -> CommandSource.suggestIdentifiers(context.getSource().getRecipeIds(), builder)
	);
	public static final SuggestionProvider<ServerCommandSource> AVAILABLE_SOUNDS = register(
		new Identifier("available_sounds"), (context, builder) -> CommandSource.suggestIdentifiers(context.getSource().getSoundIds(), builder)
	);
	public static final SuggestionProvider<ServerCommandSource> ALL_BIOMES = register(
		new Identifier("available_biomes"),
		(context, builder) -> CommandSource.suggestIdentifiers(context.getSource().getRegistryManager().get(Registry.BIOME_KEY).getIds(), builder)
	);
	public static final SuggestionProvider<ServerCommandSource> SUMMONABLE_ENTITIES = register(
		new Identifier("summonable_entities"),
		(context, builder) -> CommandSource.suggestFromIdentifier(
				Registry.ENTITY_TYPE.stream().filter(EntityType::isSummonable),
				builder,
				EntityType::getId,
				entityType -> new TranslatableText(Util.createTranslationKey("entity", EntityType.getId(entityType)))
			)
	);

	public static <S extends CommandSource> SuggestionProvider<S> register(Identifier name, SuggestionProvider<CommandSource> provider) {
		if (REGISTRY.containsKey(name)) {
			throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + name);
		} else {
			REGISTRY.put(name, provider);
			return new SuggestionProviders.LocalProvider(name, provider);
		}
	}

	public static SuggestionProvider<CommandSource> byId(Identifier id) {
		return (SuggestionProvider<CommandSource>)REGISTRY.getOrDefault(id, ASK_SERVER);
	}

	public static Identifier computeName(SuggestionProvider<CommandSource> provider) {
		return provider instanceof SuggestionProviders.LocalProvider ? ((SuggestionProviders.LocalProvider)provider).name : ASK_SERVER_NAME;
	}

	public static SuggestionProvider<CommandSource> getLocalProvider(SuggestionProvider<CommandSource> provider) {
		return provider instanceof SuggestionProviders.LocalProvider ? provider : ASK_SERVER;
	}

	protected static class LocalProvider implements SuggestionProvider<CommandSource> {
		private final SuggestionProvider<CommandSource> provider;
		final Identifier name;

		public LocalProvider(Identifier name, SuggestionProvider<CommandSource> provider) {
			this.provider = provider;
			this.name = name;
		}

		@Override
		public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
			return this.provider.getSuggestions(context, builder);
		}
	}
}
