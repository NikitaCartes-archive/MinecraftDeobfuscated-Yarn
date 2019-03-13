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
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

public class DimensionArgumentType implements ArgumentType<DimensionType> {
	private static final Collection<String> EXAMPLES = (Collection<String>)Stream.of(DimensionType.field_13072, DimensionType.field_13076)
		.map(dimensionType -> DimensionType.method_12485(dimensionType).toString())
		.collect(Collectors.toList());
	public static final DynamicCommandExceptionType INVALID_DIMENSION_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.dimension.invalid", object)
	);

	public DimensionType method_9287(StringReader stringReader) throws CommandSyntaxException {
		Identifier identifier = Identifier.parse(stringReader);
		return (DimensionType)Registry.DIMENSION.method_17966(identifier).orElseThrow(() -> INVALID_DIMENSION_EXCEPTION.create(identifier));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestIdentifiers(Streams.stream(DimensionType.getAll()).map(DimensionType::method_12485), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static DimensionArgumentType create() {
		return new DimensionArgumentType();
	}

	public static DimensionType getDimensionArgument(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, DimensionType.class);
	}
}
