package net.minecraft.command.arguments;

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
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

public class DimensionArgumentType implements ArgumentType<Identifier> {
	private static final Collection<String> EXAMPLES = (Collection<String>)Stream.of(DimensionType.OVERWORLD_REGISTRY_KEY, DimensionType.THE_NETHER_REGISTRY_KEY)
		.map(registryKey -> registryKey.getValue().toString())
		.collect(Collectors.toList());
	private static final DynamicCommandExceptionType INVALID_DIMENSION_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("argument.dimension.invalid", object)
	);

	public Identifier parse(StringReader stringReader) throws CommandSyntaxException {
		return Identifier.fromCommandInput(stringReader);
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return context.getSource() instanceof CommandSource
			? CommandSource.suggestIdentifiers(((CommandSource)context.getSource()).method_29038().getRegistry().getIds().stream(), builder)
			: Suggestions.empty();
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}

	public static DimensionArgumentType dimension() {
		return new DimensionArgumentType();
	}

	public static RegistryKey<DimensionType> getDimensionArgument(CommandContext<ServerCommandSource> context, String name) throws CommandSyntaxException {
		Identifier identifier = context.getArgument(name, Identifier.class);
		RegistryKey<DimensionType> registryKey = RegistryKey.of(Registry.DIMENSION_TYPE_KEY, identifier);
		if (!context.getSource().getMinecraftServer().method_29174().getRegistry().containsKey(registryKey)) {
			throw INVALID_DIMENSION_EXCEPTION.create(identifier);
		} else {
			return registryKey;
		}
	}
}
