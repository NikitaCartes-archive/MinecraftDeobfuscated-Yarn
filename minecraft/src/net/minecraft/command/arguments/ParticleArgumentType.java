package net.minecraft.command.arguments;

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
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleArgumentType implements ArgumentType<ParticleEffect> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
	public static final DynamicCommandExceptionType UNKNOWN_PARTICLE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("particle.notFound", object)
	);

	public static ParticleArgumentType create() {
		return new ParticleArgumentType();
	}

	public static ParticleEffect getParticle(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, ParticleEffect.class);
	}

	public ParticleEffect method_9416(StringReader stringReader) throws CommandSyntaxException {
		return readParameters(stringReader);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static ParticleEffect readParameters(StringReader stringReader) throws CommandSyntaxException {
		Identifier identifier = Identifier.parse(stringReader);
		ParticleType<?> particleType = (ParticleType<?>)Registry.PARTICLE_TYPE
			.getOrEmpty(identifier)
			.orElseThrow(() -> UNKNOWN_PARTICLE_EXCEPTION.create(identifier));
		return readParameters(stringReader, (ParticleType<ParticleEffect>)particleType);
	}

	private static <T extends ParticleEffect> T readParameters(StringReader stringReader, ParticleType<T> particleType) throws CommandSyntaxException {
		return particleType.getParametersFactory().read(particleType, stringReader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestIdentifiers(Registry.PARTICLE_TYPE.getIds(), suggestionsBuilder);
	}
}
