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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class StatusEffectArgumentType implements ArgumentType<StatusEffect> {
	private static final Collection<String> EXAMPLES = Arrays.asList("spooky", "effect");
	public static final DynamicCommandExceptionType INVALID_EFFECT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableText("effect.effectNotFound", object)
	);

	public static StatusEffectArgumentType statusEffect() {
		return new StatusEffectArgumentType();
	}

	public static StatusEffect getStatusEffect(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, StatusEffect.class);
	}

	public StatusEffect parse(StringReader stringReader) throws CommandSyntaxException {
		Identifier identifier = Identifier.fromCommandInput(stringReader);
		return (StatusEffect)Registry.STATUS_EFFECT.getOrEmpty(identifier).orElseThrow(() -> INVALID_EFFECT_EXCEPTION.create(identifier));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
		return CommandSource.suggestIdentifiers(Registry.STATUS_EFFECT.getIds(), builder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
