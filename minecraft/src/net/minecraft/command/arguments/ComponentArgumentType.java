package net.minecraft.command.arguments;

import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.command.ServerCommandSource;

public class ComponentArgumentType implements ArgumentType<Component> {
	private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
	public static final DynamicCommandExceptionType INVALID_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(
		object -> new TranslatableComponent("argument.component.invalid", object)
	);

	private ComponentArgumentType() {
	}

	public static Component getComponent(CommandContext<ServerCommandSource> commandContext, String string) {
		return commandContext.getArgument(string, Component.class);
	}

	public static ComponentArgumentType create() {
		return new ComponentArgumentType();
	}

	public Component method_9283(StringReader stringReader) throws CommandSyntaxException {
		try {
			Component component = Component.Serializer.fromJsonString(stringReader);
			if (component == null) {
				throw INVALID_COMPONENT_EXCEPTION.createWithContext(stringReader, "empty");
			} else {
				return component;
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
