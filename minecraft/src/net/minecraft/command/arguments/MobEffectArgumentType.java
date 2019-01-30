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
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.server.command.CommandSource;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class MobEffectArgumentType implements ArgumentType<StatusEffect> {
	private static final Collection<String> EXAMPLES = Arrays.asList("spooky", "effect");
	public static final DynamicCommandExceptionType INVALID_EFFECT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("effect.effectNotFound", object)
	);

	public static MobEffectArgumentType create() {
		return new MobEffectArgumentType();
	}

	public static StatusEffect getEffectArgument(CommandContext<ServerCommandSource> commandContext, String string) throws CommandSyntaxException {
		return commandContext.getArgument(string, StatusEffect.class);
	}

	public StatusEffect method_9348(StringReader stringReader) throws CommandSyntaxException {
		Identifier identifier = Identifier.parse(stringReader);
		return (StatusEffect)Registry.STATUS_EFFECT.getOptional(identifier).orElseThrow(() -> INVALID_EFFECT_EXCEPTION.create(identifier));
	}

	@Override
	public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> commandContext, SuggestionsBuilder suggestionsBuilder) {
		return CommandSource.suggestIdentifiers(Registry.STATUS_EFFECT.keys(), suggestionsBuilder);
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
