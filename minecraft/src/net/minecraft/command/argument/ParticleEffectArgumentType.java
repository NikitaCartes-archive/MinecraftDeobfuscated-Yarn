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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringNbtReader;
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
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle{foo:bar}");
	public static final DynamicCommandExceptionType UNKNOWN_PARTICLE_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.stringifiedTranslatable("particle.notFound", id)
	);
	public static final DynamicCommandExceptionType INVALID_OPTIONS_EXCEPTION = new DynamicCommandExceptionType(
		error -> Text.stringifiedTranslatable("particle.invalidOptions", error)
	);
	private final RegistryWrapper.WrapperLookup registryLookup;

	public ParticleEffectArgumentType(CommandRegistryAccess registryAccess) {
		this.registryLookup = registryAccess;
	}

	public static ParticleEffectArgumentType particleEffect(CommandRegistryAccess registryAccess) {
		return new ParticleEffectArgumentType(registryAccess);
	}

	public static ParticleEffect getParticle(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, ParticleEffect.class);
	}

	public ParticleEffect parse(StringReader stringReader) throws CommandSyntaxException {
		return readParameters(stringReader, this.registryLookup);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static ParticleEffect readParameters(StringReader reader, RegistryWrapper.WrapperLookup registryLookup) throws CommandSyntaxException {
		ParticleType<?> particleType = getType(reader, registryLookup.getWrapperOrThrow(RegistryKeys.PARTICLE_TYPE));
		return readParameters(reader, (ParticleType<ParticleEffect>)particleType, registryLookup);
	}

	private static ParticleType<?> getType(StringReader reader, RegistryWrapper<ParticleType<?>> registryWrapper) throws CommandSyntaxException {
		Identifier identifier = Identifier.fromCommandInput(reader);
		RegistryKey<ParticleType<?>> registryKey = RegistryKey.of(RegistryKeys.PARTICLE_TYPE, identifier);
		return (ParticleType<?>)((RegistryEntry.Reference)registryWrapper.getOptional(registryKey)
				.orElseThrow(() -> UNKNOWN_PARTICLE_EXCEPTION.createWithContext(reader, identifier)))
			.value();
	}

	private static <T extends ParticleEffect> T readParameters(StringReader reader, ParticleType<T> type, RegistryWrapper.WrapperLookup registryLookup) throws CommandSyntaxException {
		NbtCompound nbtCompound;
		if (reader.canRead() && reader.peek() == '{') {
			nbtCompound = new StringNbtReader(reader).parseCompound();
		} else {
			nbtCompound = new NbtCompound();
		}

		return type.getCodec().codec().parse(registryLookup.getOps(NbtOps.INSTANCE), nbtCompound).getOrThrow(INVALID_OPTIONS_EXCEPTION::create);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		RegistryWrapper.Impl<ParticleType<?>> impl = this.registryLookup.getWrapperOrThrow(RegistryKeys.PARTICLE_TYPE);
		return CommandSource.suggestIdentifiers(impl.streamKeys().map(RegistryKey::getValue), builder);
	}
}
