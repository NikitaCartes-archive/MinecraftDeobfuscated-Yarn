package net.minecraft.command.arguments;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;

public class ComponentArgumentType implements ArgumentType<TextComponent> {
	private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
	public static final DynamicCommandExceptionType INVALID_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableTextComponent("argument.component.invalid", object)
	);

	private ComponentArgumentType() {
	}

	public static TextComponent method_9280(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, TextComponent.class);
	}

	public static ComponentArgumentType create() {
		return new ComponentArgumentType();
	}

	public TextComponent method_9283(StringReader stringReader) throws CommandSyntaxException {
		try {
			TextComponent textComponent = TextComponent.Serializer.fromJsonString(stringReader);
			if (textComponent == null) {
				throw INVALID_COMPONENT_EXCEPTION.createWithContext(stringReader, "empty");
			} else {
				return textComponent;
			}
		} catch (JsonParseException var4) {
			String string = var4.getCause() != null ? var4.getCause().getMessage() : var4.getMessage();
			throw INVALID_COMPONENT_EXCEPTION.createWithContext(stringReader, string);
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
