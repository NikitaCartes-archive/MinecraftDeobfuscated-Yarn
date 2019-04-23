/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.registry.Registry;

public class SuggestionProviders {
    private static final Map<Identifier, SuggestionProvider<CommandSource>> REGISTRY = Maps.newHashMap();
    private static final Identifier ASK_SERVER_NAME = new Identifier("ask_server");
    public static final SuggestionProvider<CommandSource> ASK_SERVER = SuggestionProviders.register(ASK_SERVER_NAME, (commandContext, suggestionsBuilder) -> ((CommandSource)commandContext.getSource()).getCompletions(commandContext, suggestionsBuilder));
    public static final SuggestionProvider<ServerCommandSource> ALL_RECIPES = SuggestionProviders.register(new Identifier("all_recipes"), (commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(((CommandSource)commandContext.getSource()).getRecipeIds(), suggestionsBuilder));
    public static final SuggestionProvider<ServerCommandSource> AVAILABLE_SOUNDS = SuggestionProviders.register(new Identifier("available_sounds"), (commandContext, suggestionsBuilder) -> CommandSource.suggestIdentifiers(((CommandSource)commandContext.getSource()).getSoundIds(), suggestionsBuilder));
    public static final SuggestionProvider<ServerCommandSource> SUMMONABLE_ENTITIES = SuggestionProviders.register(new Identifier("summonable_entities"), (commandContext, suggestionsBuilder) -> CommandSource.suggestFromIdentifier(Registry.ENTITY_TYPE.stream().filter(EntityType::isSummonable), suggestionsBuilder, EntityType::getId, entityType -> new TranslatableComponent(SystemUtil.createTranslationKey("entity", EntityType.getId(entityType)), new Object[0])));

    public static <S extends CommandSource> SuggestionProvider<S> register(Identifier identifier, SuggestionProvider<CommandSource> suggestionProvider) {
        if (REGISTRY.containsKey(identifier)) {
            throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + identifier);
        }
        REGISTRY.put(identifier, suggestionProvider);
        return new LocalProvider(identifier, suggestionProvider);
    }

    public static SuggestionProvider<CommandSource> byId(Identifier identifier) {
        return REGISTRY.getOrDefault(identifier, ASK_SERVER);
    }

    public static Identifier computeName(SuggestionProvider<CommandSource> suggestionProvider) {
        if (suggestionProvider instanceof LocalProvider) {
            return ((LocalProvider)suggestionProvider).name;
        }
        return ASK_SERVER_NAME;
    }

    public static SuggestionProvider<CommandSource> getLocalProvider(SuggestionProvider<CommandSource> suggestionProvider) {
        if (suggestionProvider instanceof LocalProvider) {
            return suggestionProvider;
        }
        return ASK_SERVER;
    }

    public static class LocalProvider
    implements SuggestionProvider<CommandSource> {
        private final SuggestionProvider<CommandSource> provider;
        private final Identifier name;

        public LocalProvider(Identifier identifier, SuggestionProvider<CommandSource> suggestionProvider) {
            this.provider = suggestionProvider;
            this.name = identifier;
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> commandContext, SuggestionsBuilder suggestionsBuilder) throws CommandSyntaxException {
            return this.provider.getSuggestions(commandContext, suggestionsBuilder);
        }
    }
}

