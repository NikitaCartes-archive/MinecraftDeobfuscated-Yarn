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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class ItemEnchantmentArgumentType implements ArgumentType<Enchantment> {
	private static final Collection<String> EXAMPLES = Arrays.asList("unbreaking", "silk_touch");
	public static final DynamicCommandExceptionType field_9872 = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("enchantment.unknown", object)
	);

	public static ItemEnchantmentArgumentType create() {
		return new ItemEnchantmentArgumentType();
	}

	public static Enchantment method_9334(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, Enchantment.class);
	}

	public Enchantment method_9335(StringReader stringReader) throws CommandSyntaxException {
		Identifier identifier = Identifier.parse(stringReader);
		return (Enchantment)Registry.ENCHANTMENT.method_17966(identifier).orElseThrow(() -> field_9872.create(identifier));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestIdentifiers(Registry.ENCHANTMENT.keys(), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
