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
import net.minecraft.command.CommandSource;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ParticleArgumentType implements ArgumentType<ParticleEffect> {
	private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
	public static final DynamicCommandExceptionType UNKNOWN_PARTICLE_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("particle.notFound", object)
	);

	public static ParticleArgumentType particle() {
		return new ParticleArgumentType();
	}

	public static ParticleEffect getParticle(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, ParticleEffect.class);
	}

	public ParticleEffect parse(StringReader stringReader) throws CommandSyntaxException {
		return readParameters(stringReader);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static ParticleEffect readParameters(StringReader reader) throws CommandSyntaxException {
		Identifier identifier = Identifier.fromCommandInput(reader);
		ParticleType<?> particleType = (ParticleType<?>)Registry.PARTICLE_TYPE
			.getOrEmpty(identifier)
			.orElseThrow(() -> UNKNOWN_PARTICLE_EXCEPTION.create(identifier));
		return readParameters(reader, (ParticleType<ParticleEffect>)particleType);
	}

	private static <T extends ParticleEffect> T readParameters(StringReader reader, ParticleType<T> type) throws CommandSyntaxException {
		return type.getParametersFactory().read(type, reader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(Registry.PARTICLE_TYPE.getIds(), builder);
	}
}
