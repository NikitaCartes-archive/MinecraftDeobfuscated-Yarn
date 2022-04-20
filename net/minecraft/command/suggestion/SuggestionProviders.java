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
import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.Registry;

public class SuggestionProviders {
    private static final Map<Identifier, SuggestionProvider<CommandSource>> REGISTRY = Maps.newHashMap();
    private static final Identifier ASK_SERVER_NAME = new Identifier("ask_server");
    public static final SuggestionProvider<CommandSource> ASK_SERVER = SuggestionProviders.register(ASK_SERVER_NAME, (context, builder) -> ((CommandSource)context.getSource()).getCompletions(context));
    public static final SuggestionProvider<ServerCommandSource> ALL_RECIPES = SuggestionProviders.register(new Identifier("all_recipes"), (context, builder) -> CommandSource.suggestIdentifiers(((CommandSource)context.getSource()).getRecipeIds(), builder));
    public static final SuggestionProvider<ServerCommandSource> AVAILABLE_SOUNDS = SuggestionProviders.register(new Identifier("available_sounds"), (context, builder) -> CommandSource.suggestIdentifiers(((CommandSource)context.getSource()).getSoundIds(), builder));
    public static final SuggestionProvider<ServerCommandSource> SUMMONABLE_ENTITIES = SuggestionProviders.register(new Identifier("summonable_entities"), (context, builder) -> CommandSource.suggestFromIdentifier(Registry.ENTITY_TYPE.stream().filter(EntityType::isSummonable), builder, EntityType::getId, entityType -> Text.method_43471(Util.createTranslationKey("entity", EntityType.getId(entityType)))));

    public static <S extends CommandSource> SuggestionProvider<S> register(Identifier id, SuggestionProvider<CommandSource> provider) {
        if (REGISTRY.containsKey(id)) {
            throw new IllegalArgumentException("A command suggestion provider is already registered with the name " + id);
        }
        REGISTRY.put(id, provider);
        return new LocalProvider(id, provider);
    }

    public static SuggestionProvider<CommandSource> byId(Identifier id) {
        return REGISTRY.getOrDefault(id, ASK_SERVER);
    }

    public static Identifier computeId(SuggestionProvider<CommandSource> provider) {
        if (provider instanceof LocalProvider) {
            return ((LocalProvider)provider).id;
        }
        return ASK_SERVER_NAME;
    }

    public static SuggestionProvider<CommandSource> getLocalProvider(SuggestionProvider<CommandSource> provider) {
        if (provider instanceof LocalProvider) {
            return provider;
        }
        return ASK_SERVER;
    }

    protected static class LocalProvider
    implements SuggestionProvider<CommandSource> {
        private final SuggestionProvider<CommandSource> provider;
        final Identifier id;

        public LocalProvider(Identifier id, SuggestionProvider<CommandSource> provider) {
            this.provider = provider;
            this.id = id;
        }

        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSource> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            return this.provider.getSuggestions(context, builder);
        }
    }
}

