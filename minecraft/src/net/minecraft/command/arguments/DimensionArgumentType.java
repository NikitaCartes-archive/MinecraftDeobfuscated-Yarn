package net.minecraft.command.arguments;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class DimensionArgumentType implements ArgumentType<DimensionType> {
	private static final Collection<String> EXAMPLES = (Collection<String>)Stream.of(DimensionType.OVERWORLD, DimensionType.THE_NETHER)
		.map(dimensionType -> DimensionType.getId(dimensionType).toString())
		.collect(Collectors.toList());
	public static final DynamicCommandExceptionType INVALID_DIMENSION_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.dimension.invalid", object)
	);

	public DimensionType parse(StringReader stringReader) throws CommandSyntaxException {
		Identifier identifier = Identifier.fromCommandInput(stringReader);
		return (DimensionType)Registry.DIMENSION.getOrEmpty(identifier).orElseThrow(() -> INVALID_DIMENSION_EXCEPTION.create(identifier));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(Streams.stream(DimensionType.getAll()).map(DimensionType::getId), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static DimensionArgumentType dimension() {
		return new DimensionArgumentType();
	}

	public static DimensionType getDimensionArgument(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, DimensionType.class);
	}
}
