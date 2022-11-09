package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ParticleEffectArgumentType implements ArgumentType<ParticleEffect> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
	public static final DynamicCommandExceptionType UNKNOWN_PARTICLE_EXCEPTION = new DynamicCommandExceptionType(id -> Text.translatable("particle.notFound", id));
	private final RegistryWrapper<ParticleType<?>> registryWrapper;

	public ParticleEffectArgumentType(CommandRegistryAccess registryAccess) {
		this.registryWrapper = registryAccess.createWrapper(RegistryKeys.PARTICLE_TYPE);
	}

	public static ParticleEffectArgumentType particleEffect(CommandRegistryAccess registryAccess) {
		return new ParticleEffectArgumentType(registryAccess);
	}

	public static ParticleEffect getParticle(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, ParticleEffect.class);
	}

	public ParticleEffect parse(StringReader stringReader) throws CommandSyntaxException {
		return readParameters(stringReader, this.registryWrapper);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static ParticleEffect readParameters(StringReader reader, RegistryWrapper<ParticleType<?>> registryWrapper) throws CommandSyntaxException {
		ParticleType<?> particleType = getType(reader, registryWrapper);
		return readParameters(reader, (ParticleType<ParticleEffect>)particleType);
	}

	private static ParticleType<?> getType(StringReader reader, RegistryWrapper<ParticleType<?>> registryWrapper) throws CommandSyntaxException {
		Identifier identifier = Identifier.fromCommandInput(reader);
		RegistryKey<ParticleType<?>> registryKey = RegistryKey.of(RegistryKeys.PARTICLE_TYPE, identifier);
		return (ParticleType<?>)((RegistryEntry.Reference)registryWrapper.getOptional(registryKey).orElseThrow(() -> UNKNOWN_PARTICLE_EXCEPTION.create(identifier)))
			.value();
	}

	private static <T extends ParticleEffect> T readParameters(StringReader reader, ParticleType<T> type) throws CommandSyntaxException {
		return type.getParametersFactory().read(type, reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(this.registryWrapper.streamKeys().map(RegistryKey::getValue), builder);
	}
}
