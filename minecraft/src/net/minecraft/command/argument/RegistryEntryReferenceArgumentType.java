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

public class RegistryEntryReferenceArgumentType<T> implements ArgumentType<RegistryEntry.Reference<T>> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
	private static final DynamicCommandExceptionType NOT_SUMMONABLE_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("entity.not_summonable", id)
	);
	public static final Dynamic2CommandExceptionType NOT_FOUND_EXCEPTION = new Dynamic2CommandExceptionType(
		(element, type) -> Text.stringifiedTranslatable("argument.resource.not_found", element, type)
	);
	public static final Dynamic3CommandExceptionType INVALID_TYPE_EXCEPTION = new Dynamic3CommandExceptionType(
		(element, type, expectedType) -> Text.stringifiedTranslatable("argument.resource.invalid_type", element, type, expectedType)
	);
	final RegistryKey<? extends Registry<T>> registryRef;
	private final RegistryWrapper<T> registryWrapper;

	public RegistryEntryReferenceArgumentType(CommandRegistryAccess registryAccess, RegistryKey<? extends Registry<T>> registryRef) {
		this.registryRef = registryRef;
		this.registryWrapper = registryAccess.getOrThrow(registryRef);
	}

	public static <T> RegistryEntryReferenceArgumentType<T> registryEntry(CommandRegistryAccess registryAccess, RegistryKey<? extends Registry<T>> registryRef) {
		return new RegistryEntryReferenceArgumentType<>(registryAccess, registryRef);
	}

	public static <T> RegistryEntry.Reference<T> getRegistryEntry(CommandContext<ServerCommandSource> context, String name, RegistryKey<Registry<T>> registryRef) throws CommandSyntaxException {
		RegistryEntry.Reference<T> reference = context.getArgument(name, RegistryEntry.Reference.class);
		RegistryKey<?> registryKey = reference.registryKey();
		if (registryKey.isOf(registryRef)) {
			return reference;
		} else {
			throw INVALID_TYPE_EXCEPTION.create(registryKey.getValue(), registryKey.getRegistry(), registryRef.getValue());
		}
	}

	public static RegistryEntry.Reference<EntityAttribute> getEntityAttribute(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getRegistryEntry(context, name, RegistryKeys.ATTRIBUTE);
	}

	public static RegistryEntry.Reference<ConfiguredFeature<?, ?>> getConfiguredFeature(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getRegistryEntry(context, name, RegistryKeys.CONFIGURED_FEATURE);
	}

	public static RegistryEntry.Reference<Structure> getStructure(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getRegistryEntry(context, name, RegistryKeys.STRUCTURE);
	}

	public static RegistryEntry.Reference<EntityType<?>> getEntityType(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getRegistryEntry(context, name, RegistryKeys.ENTITY_TYPE);
	}

	public static RegistryEntry.Reference<EntityType<?>> getSummonableEntityType(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		RegistryEntry.Reference<EntityType<?>> reference = getRegistryEntry(context, name, RegistryKeys.ENTITY_TYPE);
		if (!reference.value().isSummonable()) {
			throw NOT_SUMMONABLE_EXCEPTION.create(reference.registryKey().getValue().toString());
		} else {
			return reference;
		}
	}

	public static RegistryEntry.Reference<StatusEffect> getStatusEffect(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getRegistryEntry(context, name, RegistryKeys.STATUS_EFFECT);
	}

	public static RegistryEntry.Reference<Enchantment> getEnchantment(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		return getRegistryEntry(context, name, RegistryKeys.ENCHANTMENT);
	}

	public RegistryEntry.Reference<T> parse(StringReader stringReader) throws CommandSyntaxException {
		Identifier identifier = Identifier.fromCommandInput(stringReader);
		RegistryKey<T> registryKey = RegistryKey.of(this.registryRef, identifier);
		return (RegistryEntry.Reference<T>)this.registryWrapper
			.getOptional(registryKey)
			.orElseThrow(() -> NOT_FOUND_EXCEPTION.createWithContext(stringReader, identifier, this.registryRef.getValue()));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(this.registryWrapper.streamKeys().map(RegistryKey::getValue), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static class Serializer<T>
		implements ArgumentSerializer<RegistryEntryReferenceArgumentType<T>, RegistryEntryReferenceArgumentType.Serializer<T>.Properties> {
		public void writePacket(RegistryEntryReferenceArgumentType.Serializer<T>.Properties properties, PacketByteBuf packetByteBuf) {
			packetByteBuf.writeRegistryKey(properties.registryRef);
		}

		public RegistryEntryReferenceArgumentType.Serializer<T>.Properties fromPacket(PacketByteBuf packetByteBuf) {
			return new RegistryEntryReferenceArgumentType.Serializer.Properties(packetByteBuf.readRegistryRefKey());
		}

		public void writeJson(RegistryEntryReferenceArgumentType.Serializer<T>.Properties properties, JsonObject jsonObject) {
			jsonObject.addProperty("registry", properties.registryRef.getValue().toString());
		}

		public RegistryEntryReferenceArgumentType.Serializer<T>.Properties getArgumentTypeProperties(
			RegistryEntryReferenceArgumentType<T> registryEntryReferenceArgumentType
		) {
			return new RegistryEntryReferenceArgumentType.Serializer.Properties(registryEntryReferenceArgumentType.registryRef);
		}

		public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<RegistryEntryReferenceArgumentType<T>> {
			final RegistryKey<? extends Registry<T>> registryRef;

			Properties(final RegistryKey<? extends Registry<T>> registryRef) {
				this.registryRef = registryRef;
			}

			public RegistryEntryReferenceArgumentType<T> createType(CommandRegistryAccess commandRegistryAccess) {
				return new RegistryEntryReferenceArgumentType<>(commandRegistryAccess, this.registryRef);
			}

			@Override
			public ArgumentSerializer<RegistryEntryReferenceArgumentType<T>, ?> getSerializer() {
				return Serializer.this;
			}
		}
	}
}
