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
import net.minecraft.command.CommandRegistryAccess;
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

public class RegistryKeyArgumentType<T> implements ArgumentType<RegistryKey<T>> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
	private static final DynamicCommandExceptionType UNKNOWN_ATTRIBUTE_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("attribute.unknown", id)
	);
	private static final DynamicCommandExceptionType INVALID_CONFIGURED_FEATURE_EXCEPTION = new DynamicCommandExceptionType(
		id -> new TranslatableText("commands.placefeature.invalid", id)
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
		return (RegistryKey<T>)optional.orElseThrow(() -> invalidException.create(registryKey));
	}

	private static <T> Registry<T> getRegistry(CommandContext<ServerCommandSource> context, RegistryKey<? extends Registry<T>> registryRef) {
		return context.getSource().getServer().getRegistryManager().get(registryRef);
	}

	public static EntityAttribute getAttribute(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		RegistryKey<EntityAttribute> registryKey = getKey(context, name, Registry.ATTRIBUTE_KEY, UNKNOWN_ATTRIBUTE_EXCEPTION);
		return (EntityAttribute)getRegistry(context, Registry.ATTRIBUTE_KEY)
			.getOrEmpty(registryKey)
			.orElseThrow(() -> UNKNOWN_ATTRIBUTE_EXCEPTION.create(registryKey.getValue()));
	}

	public static RegistryEntry<ConfiguredFeature<?, ?>> getConfiguredFeatureEntry(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		RegistryKey<ConfiguredFeature<?, ?>> registryKey = getKey(context, name, Registry.CONFIGURED_FEATURE_KEY, INVALID_CONFIGURED_FEATURE_EXCEPTION);
		return (RegistryEntry<ConfiguredFeature<?, ?>>)getRegistry(context, Registry.CONFIGURED_FEATURE_KEY)
			.getEntry(registryKey)
			.orElseThrow(() -> INVALID_CONFIGURED_FEATURE_EXCEPTION.create(registryKey.getValue()));
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
		public void writePacket(RegistryKeyArgumentType.Serializer.Properties properties, PacketByteBuf packetByteBuf) {
			packetByteBuf.writeIdentifier(properties.registryRef.getValue());
		}

		public RegistryKeyArgumentType.Serializer<T>.Properties fromPacket(PacketByteBuf packetByteBuf) {
			Identifier identifier = packetByteBuf.readIdentifier();
			return new RegistryKeyArgumentType.Serializer.Properties(RegistryKey.ofRegistry(identifier));
		}

		public void writeJson(RegistryKeyArgumentType.Serializer.Properties properties, JsonObject jsonObject) {
			jsonObject.addProperty("registry", properties.registryRef.getValue().toString());
		}

		public RegistryKeyArgumentType.Serializer<T>.Properties getArgumentTypeProperties(RegistryKeyArgumentType<T> registryKeyArgumentType) {
			return new RegistryKeyArgumentType.Serializer.Properties(registryKeyArgumentType.registryRef);
		}

		public final class Properties implements ArgumentSerializer.ArgumentTypeProperties<RegistryKeyArgumentType<T>> {
			final RegistryKey<? extends Registry<T>> registryRef;

			Properties(RegistryKey<? extends Registry<T>> registryRef) {
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
