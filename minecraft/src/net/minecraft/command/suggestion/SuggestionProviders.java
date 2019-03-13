package net.minecraft.command.suggestion;

import com.google.common.collect.Maps;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;

public class SuggestionProviders {
	private static final Map<Identifier, SuggestionProvider<CommandSource>> REGISTRY = Maps.<Identifier, SuggestionProvider<CommandSource>>newHashMap();
	private static final Identifier field_10930 = new Identifier("ask_server");
	public static final SuggestionProvider<CommandSource> ASK_SERVER = method_10022(
		field_10930, (commandContext, suggestionsBuilder) -> commandContext.getSource().getCompletions(commandContext, suggestionsBuilder)
	);
	public static final SuggestionProvider<ServerCommandSource> ALL_RECIPES = method_10022(
		new Identifier("all_recipes"),
		(commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(commandContext.getSource().getRecipeIds(), suggestionsBuilder)
	);
	public static final SuggestionProvider<ServerCommandSource> AVAILABLE_SOUNDS = method_10022(
		new Identifier("available_sounds"),
		(commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(commandContext.getSource().getSoundIds(), suggestionsBuilder)
	);
	public static final SuggestionProvider<ServerCommandSource> SUMMONABLE_ENTITIES = method_10022(
		new Identifier("summonable_entities"),
		(commandContext, suggestionsBuilder) -> CommandSource.suggestFromIdentifier(
				Registry.ENTITY_TYPE.stream().filter(EntityType::isSummonable),
				suggestionsBuilder,
				EntityType::method_5890,
				entityType -> new TranslatableTextComponent(SystemUtil.method_646("entity", EntityType.method_5890(entityType)))
			)
	);

	public static <S extends CommandSource> SuggestionProvider<S> method_10022(Identifier identifier, SuggestionProvider<CommandSource> suggestionProvider) {
		if (REGISTRY.containsKey(identifier)) {
			throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + identifier);
		} else {
			REGISTRY.put(identifier, suggestionProvider);
			return new SuggestionProviders.LocalProvider(identifier, suggestionProvider);
		}
	}

	public static SuggestionProvider<CommandSource> method_10024(Identifier identifier) {
		return (SuggestionProvider<CommandSource>)REGISTRY.getOrDefault(identifier, ASK_SERVER);
	}

	public static Identifier method_10027(SuggestionProvider<CommandSource> suggestionProvider) {
		return suggestionProvider instanceof SuggestionProviders.LocalProvider ? ((SuggestionProviders.LocalProvider)suggestionProvider).field_10936 : field_10930;
	}

	public static SuggestionProvider<CommandSource> getLocalProvider(SuggestionProvider<CommandSource> suggestionProvider) {
		return suggestionProvider instanceof SuggestionProviders.LocalProvider ? suggestionProvider : ASK_SERVER;
	}

	public static class LocalProvider implements SuggestionProvider<CommandSource> {
		private final SuggestionProvider<CommandSource> provider;
		private final Identifier field_10936;

		public LocalProvider(Identifier identifier, SuggestionProvider<CommandSource> suggestionProvider) {
			this.provider = suggestionProvider;
			this.field_10936 = identifier;
		}

		@Override
		public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
			return this.provider.getSuggestions(commandContext, suggestionsBuilder);
		}
	}
}
