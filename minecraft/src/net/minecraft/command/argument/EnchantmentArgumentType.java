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
import net.minecraft.enchantment.Enchantment;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EnchantmentArgumentType implements ArgumentType<Enchantment> {
	private static final Collection<String> EXAMPLES = Arrays.asList("unbreaking", "silk_touch");
	public static final DynamicCommandExceptionType UNKNOWN_ENCHANTMENT_EXCEPTION = new DynamicCommandExceptionType(
		id -> Text.method_43469("enchantment.unknown", id)
	);

	public static EnchantmentArgumentType enchantment() {
		return new EnchantmentArgumentType();
	}

	public static Enchantment getEnchantment(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, Enchantment.class);
	}

	public Enchantment parse(StringReader stringReader) throws CommandSyntaxException {
		Identifier identifier = Identifier.fromCommandInput(stringReader);
		return (Enchantment)Registry.ENCHANTMENT.getOrEmpty(identifier).orElseThrow(() -> UNKNOWN_ENCHANTMENT_EXCEPTION.create(identifier));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(Registry.ENCHANTMENT.getIds(), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
