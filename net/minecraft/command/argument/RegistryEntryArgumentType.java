/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.command.argument;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.structure.Structure;

public class RegistryEntryArgumentType<T>
implements ArgumentType<RegistryEntry.Reference<T>> {
    private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
    private static final DynamicCommandExceptionType NOT_SUMMONABLE_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("entity.not_summonable", id));
    public static final Dynamic2CommandExceptionType NOT_FOUND_EXCEPTION = new Dynamic2CommandExceptionType((element, type) -> Text.translatable("argument.resource.not_found", element, type));
    public static final Dynamic3CommandExceptionType INVALID_TYPE_EXCEPTION = new Dynamic3CommandExceptionType((element, type, expectedType) -> Text.translatable("argument.resource.invalid_type", element, type, expectedType));
    final RegistryKey<? extends Registry<T>> registryRef;
    private final RegistryWrapper<T> registryWrapper;

    public RegistryEntryArgumentType(CommandRegistryAccess registryAccess, RegistryKey<? extends Registry<T>> registryRef) {
        this.registryRef = registryRef;
        this.registryWrapper = registryAccess.createWrapper(registryRef);
    }

    public static <T> RegistryEntryArgumentType<T> registryEntry(CommandRegistryAccess registryAccess, RegistryKey<? extends Registry<T>> registryRef) {
        return new RegistryEntryArgumentType<T>(registryAccess, registryRef);
    }

    public static <T> RegistryEntry.Reference<T> getRegistryEntry(CommandContext<ServerCommandSource> context, String name, RegistryKey<Registry<T>> registryRef) throws CommandSyntaxException {
        RegistryEntry.Reference reference = context.getArgument(name, RegistryEntry.Reference.class);
        RegistryKey registryKey = reference.registryKey();
        if (registryKey.isOf(registryRef)) {
            return reference;
        }
        throw INVALID_TYPE_EXCEPTION.create(registryKey.getValue(), registryKey.getRegistry(), registryRef.getValue());
    }

    public static RegistryEntry.Reference<EntityAttribute> getEntityAttribute(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return RegistryEntryArgumentType.getRegistryEntry(context, name, RegistryKeys.ATTRIBUTE);
    }

    public static RegistryEntry.Reference<ConfiguredFeature<?, ?>> getConfiguredFeature(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return RegistryEntryArgumentType.getRegistryEntry(context, name, RegistryKeys.CONFIGURED_FEATURE);
    }

    public static RegistryEntry.Reference<Structure> getStructure(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return RegistryEntryArgumentType.getRegistryEntry(context, name, RegistryKeys.STRUCTURE);
    }

    public static RegistryEntry.Reference<EntityType<?>> getEntityType(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return RegistryEntryArgumentType.getRegistryEntry(context, name, RegistryKeys.ENTITY_TYPE);
    }

    public static RegistryEntry.Reference<EntityType<?>> getSummonableEntityType(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        RegistryEntry.Reference<EntityType<?>> reference = RegistryEntryArgumentType.getRegistryEntry(context, name, RegistryKeys.ENTITY_TYPE);
        if (!((EntityType)reference.value()).isSummonable()) {
            throw NOT_SUMMONABLE_EXCEPTION.create(reference.registryKey().getValue().toString());
        }
        return reference;
    }

    public static RegistryEntry.Reference<StatusEffect> getStatusEffect(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return RegistryEntryArgumentType.getRegistryEntry(context, name, RegistryKeys.STATUS_EFFECT);
    }

    public static RegistryEntry.Reference<Enchantment> getEnchantment(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
        return RegistryEntryArgumentType.getRegistryEntry(context, name, RegistryKeys.ENCHANTMENT);
    }

    @Override
    public RegistryEntry.Reference<T> parse(StringReader stringReader) throws CommandSyntaxException {
        Identifier identifier = Identifier.fromCommandInput(stringReader);
        RegistryKey registryKey = RegistryKey.of(this.registryRef, identifier);
        return this.registryWrapper.getOptional(registryKey).orElseThrow(() -> NOT_FOUND_EXCEPTION.create(identifier, this.registryRef.getValue()));
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return CommandSource.suggestIdentifiers(this.registryWrapper.streamKeys().map(RegistryKey::getValue), builder);
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    @Override
    public /* synthetic */ Object parse(StringReader reader) throws CommandSyntaxException {
        return this.parse(reader);
    }

    public static class Serializer<T>
    implements ArgumentSerializer<RegistryEntryArgumentType<T>, Properties> {
        @Override
        public void writePacket(Properties properties, PacketByteBuf packetByteBuf) {
            packetByteBuf.writeIdentifier(properties.registryRef.getValue());
        }

        @Override
        public Properties fromPacket(PacketByteBuf packetByteBuf) {
            Identifier identifier = packetByteBuf.readIdentifier();
            return new Properties(RegistryKey.ofRegistry(identifier));
        }

        @Override
        public void writeJson(Properties properties, JsonObject jsonObject) {
            jsonObject.addProperty("registry", properties.registryRef.getValue().toString());
        }

        @Override
        public Properties getArgumentTypeProperties(RegistryEntryArgumentType<T> registryEntryArgumentType) {
            return new Properties(registryEntryArgumentType.registryRef);
        }

        @Override
        public /* synthetic */ ArgumentSerializer.ArgumentTypeProperties fromPacket(PacketByteBuf buf) {
            return this.fromPacket(buf);
        }

        public final class Properties
        implements ArgumentSerializer.ArgumentTypeProperties<RegistryEntryArgumentType<T>> {
            final RegistryKey<? extends Registry<T>> registryRef;

            Properties(RegistryKey<? extends Registry<T>> registryRef) {
                this.registryRef = registryRef;
            }

            @Override
            public RegistryEntryArgumentType<T> createType(CommandRegistryAccess commandRegistryAccess) {
                return new RegistryEntryArgumentType(commandRegistryAccess, this.registryRef);
            }

            @Override
            public ArgumentSerializer<RegistryEntryArgumentType<T>, ?> getSerializer() {
                return Serializer.this;
            }

            @Override
            public /* synthetic */ ArgumentType createType(CommandRegistryAccess commandRegistryAccess) {
                return this.createType(commandRegistryAccess);
            }
        }
    }
}

