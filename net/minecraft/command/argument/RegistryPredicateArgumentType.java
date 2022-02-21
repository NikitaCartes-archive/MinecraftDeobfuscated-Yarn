/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
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

public class RegistryPredicateArgumentType<T>
implements ArgumentType<RegistryPredicate<T>> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012", "#skeletons", "#minecraft:skeletons");
    private static final DynamicCommandExceptionType INVALID_BIOME_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("commands.locatebiome.invalid", id));
    private static final DynamicCommandExceptionType INVALID_CONFIGURED_STRUCTURE_FEATURE_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("commands.locate.invalid", id));
    final RegistryKey<? extends Registry<T>> registryRef;

    public RegistryPredicateArgumentType(RegistryKey<? extends Registry<T>> registryRef) {
        this.registryRef = registryRef;
    }

    public static <T> RegistryPredicateArgumentType<T> registryPredicate(RegistryKey<? extends Registry<T>> registryRef) {
        return new RegistryPredicateArgumentType<T>(registryRef);
    }

    private static <T> RegistryPredicate<T> getPredicate(CommandContext<ServerCommandSource> context, String name, RegistryKey<Registry<T>> registryRef, DynamicCommandExceptionType invalidException) throws CommandSyntaxException {
        RegistryPredicate registryPredicate = context.getArgument(name, RegistryPredicate.class);
        Optional<RegistryPredicate<T>> optional = registryPredicate.tryCast(registryRef);
        return optional.orElseThrow(() -> invalidException.create(registryPredicate));
    }

    public static RegistryPredicate<Biome> getBiomePredicate(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return RegistryPredicateArgumentType.getPredicate(context, name, Registry.BIOME_KEY, INVALID_BIOME_EXCEPTION);
    }

    public static RegistryPredicate<ConfiguredStructureFeature<?, ?>> getConfiguredStructureFeaturePredicate(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return RegistryPredicateArgumentType.getPredicate(context, name, Registry.CONFIGURED_STRUCTURE_FEATURE_KEY, INVALID_CONFIGURED_STRUCTURE_FEATURE_EXCEPTION);
    }

    @Override
    public RegistryPredicate<T> parse(StringReader stringReader) throws CommandSyntaxException {
        if (stringReader.canRead() && stringReader.peek() == '#') {
            int i = stringReader.getCursor();
            try {
                stringReader.skip();
                Identifier identifier = Identifier.fromCommandInput(stringReader);
                return new TagBased(TagKey.of(this.registryRef, identifier));
            } catch (CommandSyntaxException commandSyntaxException) {
                stringReader.setCursor(i);
                throw commandSyntaxException;
            }
        }
        Identifier identifier2 = Identifier.fromCommandInput(stringReader);
        return new RegistryKeyBased(RegistryKey.of(this.registryRef, identifier2));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        S s = context.getSource();
        if (s instanceof CommandSource) {
            CommandSource commandSource = (CommandSource)s;
            commandSource.getRegistryManager().getOptional(this.registryRef).ifPresent(registry -> {
                CommandSource.suggestIdentifiers(registry.streamTags().map(TagKey::id), builder, "#");
                CommandSource.suggestIdentifiers(registry.getIds(), builder);
            });
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }

    public static interface RegistryPredicate<T>
    extends Predicate<RegistryEntry<T>> {
        public Either<RegistryKey<T>, TagKey<T>> getKey();

        public <E> Optional<RegistryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> var1);

        public String asString();
    }

    record TagBased<T>(TagKey<T> key) implements RegistryPredicate<T>
    {
        @Override
        public Either<RegistryKey<T>, TagKey<T>> getKey() {
            return Either.right(this.key);
        }

        @Override
        public <E> Optional<RegistryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
            return this.key.tryCast(registryRef).map(TagBased::new);
        }

        @Override
        public boolean test(RegistryEntry<T> registryEntry) {
            return registryEntry.isIn(this.key);
        }

        @Override
        public String asString() {
            return "#" + this.key.id();
        }

        @Override
        public /* synthetic */ boolean test(Object entry) {
            return this.test((RegistryEntry)entry);
        }
    }

    record RegistryKeyBased<T>(RegistryKey<T> key) implements RegistryPredicate<T>
    {
        @Override
        public Either<RegistryKey<T>, TagKey<T>> getKey() {
            return Either.left(this.key);
        }

        @Override
        public <E> Optional<RegistryPredicate<E>> tryCast(RegistryKey<? extends Registry<E>> registryRef) {
            return this.key.tryCast(registryRef).map(RegistryKeyBased::new);
        }

        @Override
        public boolean test(RegistryEntry<T> registryEntry) {
            return registryEntry.matchesKey(this.key);
        }

        @Override
        public String asString() {
            return this.key.getValue().toString();
        }

        @Override
        public /* synthetic */ boolean test(Object entry) {
            return this.test((RegistryEntry)entry);
        }
    }

    public static class Serializer
    implements ArgumentSerializer<RegistryPredicateArgumentType<?>> {
        @Override
        public void toPacket(RegistryPredicateArgumentType<?> registryPredicateArgumentType, PacketByteBuf packetByteBuf) {
            packetByteBuf.writeIdentifier(registryPredicateArgumentType.registryRef.getValue());
        }

        @Override
        public RegistryPredicateArgumentType<?> fromPacket(PacketByteBuf packetByteBuf) {
            Identifier identifier = packetByteBuf.readIdentifier();
            return new RegistryPredicateArgumentType(RegistryKey.ofRegistry(identifier));
        }

        @Override
        public void toJson(RegistryPredicateArgumentType<?> registryPredicateArgumentType, JsonObject jsonObject) {
            jsonObject.addProperty("registry", registryPredicateArgumentType.registryRef.getValue().toString());
        }

        @Override
        public /* synthetic */ ArgumentType fromPacket(PacketByteBuf buf) {
            return this.fromPacket(buf);
        }
    }
}

