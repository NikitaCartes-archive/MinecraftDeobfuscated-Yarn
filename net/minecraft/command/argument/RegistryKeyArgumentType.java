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
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class RegistryKeyArgumentType<T>
implements ArgumentType<RegistryKey<T>> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType UNKNOWN_ATTRIBUTE_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("attribute.unknown", id));
    private static final DynamicCommandExceptionType INVALID_CONFIGURED_FEATURE_EXCEPTION = new DynamicCommandExceptionType(id -> new TranslatableText("commands.placefeature.invalid", id));
    final RegistryKey<? extends Registry<T>> registryRef;

    public RegistryKeyArgumentType(RegistryKey<? extends Registry<T>> registryRef) {
        this.registryRef = registryRef;
    }

    public static <T> RegistryKeyArgumentType<T> registryKey(RegistryKey<? extends Registry<T>> registryRef) {
        return new RegistryKeyArgumentType<T>(registryRef);
    }

    private static <T> RegistryKey<T> getKey(CommandContext<ServerCommandSource> context, String name, RegistryKey<Registry<T>> registryRef, DynamicCommandExceptionType invalidException) throws CommandSyntaxException {
        RegistryKey registryKey = context.getArgument(name, RegistryKey.class);
        Optional<RegistryKey<T>> optional = registryKey.tryCast(registryRef);
        return optional.orElseThrow(() -> invalidException.create(registryKey));
    }

    private static <T> Registry<T> getRegistry(CommandContext<ServerCommandSource> context, RegistryKey<? extends Registry<T>> registryRef) {
        return context.getSource().getServer().getRegistryManager().get(registryRef);
    }

    public static EntityAttribute getAttribute(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        RegistryKey registryKey = RegistryKeyArgumentType.getKey(context, name, Registry.ATTRIBUTE_KEY, UNKNOWN_ATTRIBUTE_EXCEPTION);
        return RegistryKeyArgumentType.getRegistry(context, Registry.ATTRIBUTE_KEY).getOrEmpty(registryKey).orElseThrow(() -> UNKNOWN_ATTRIBUTE_EXCEPTION.create(registryKey.getValue()));
    }

    public static RegistryEntry<ConfiguredFeature<?, ?>> getConfiguredFeatureEntry(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        RegistryKey registryKey = RegistryKeyArgumentType.getKey(context, name, Registry.CONFIGURED_FEATURE_KEY, INVALID_CONFIGURED_FEATURE_EXCEPTION);
        return RegistryKeyArgumentType.getRegistry(context, Registry.CONFIGURED_FEATURE_KEY).getEntry(registryKey).orElseThrow(() -> INVALID_CONFIGURED_FEATURE_EXCEPTION.create(registryKey.getValue()));
    }

    @Override
    public RegistryKey<T> parse(StringReader stringReader) throws CommandSyntaxException {
        Identifier identifier = Identifier.fromCommandInput(stringReader);
        return RegistryKey.of(this.registryRef, identifier);
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        S s = context.getSource();
        if (s instanceof CommandSource) {
            CommandSource commandSource = (CommandSource)s;
            return commandSource.listIdSuggestions(this.registryRef, CommandSource.SuggestedIdType.ELEMENTS, builder, context);
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

    public static class Serializer
    implements ArgumentSerializer<RegistryKeyArgumentType<?>> {
        @Override
        public void toPacket(RegistryKeyArgumentType<?> registryKeyArgumentType, PacketByteBuf packetByteBuf) {
            packetByteBuf.writeIdentifier(registryKeyArgumentType.registryRef.getValue());
        }

        @Override
        public RegistryKeyArgumentType<?> fromPacket(PacketByteBuf packetByteBuf) {
            Identifier identifier = packetByteBuf.readIdentifier();
            return new RegistryKeyArgumentType(RegistryKey.ofRegistry(identifier));
        }

        @Override
        public void toJson(RegistryKeyArgumentType<?> registryKeyArgumentType, JsonObject jsonObject) {
            jsonObject.addProperty("registry", registryKeyArgumentType.registryRef.getValue().toString());
        }

        @Override
        public /* synthetic */ ArgumentType fromPacket(PacketByteBuf buf) {
            return this.fromPacket(buf);
        }
    }
}

