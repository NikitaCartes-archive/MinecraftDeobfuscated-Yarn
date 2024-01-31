package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.JsonReaderUtils;

public class TextArgumentType implements ArgumentType<Text> {
	private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "\"\"", "\"{\"text\":\"hello world\"}", "[\"\"]");
	public static final DynamicCommandExceptionType INVALID_COMPONENT_EXCEPTION = new DynamicCommandExceptionType(
		text -> Text.stringifiedTranslatable("argument.component.invalid", text)
	);
	private final RegistryWrapper.WrapperLookup field_48917;

	private TextArgumentType(RegistryWrapper.WrapperLookup wrapperLookup) {
		this.field_48917 = wrapperLookup;
	}

	public static Text getTextArgument(CommandContext<ServerCommandSource> context, String name) {
		return context.getArgument(name, Text.class);
	}

	public static TextArgumentType text(CommandRegistryAccess commandRegistryAccess) {
		return new TextArgumentType(commandRegistryAccess);
	}

	public Text parse(StringReader stringReader) throws CommandSyntaxException {
		try {
			return JsonReaderUtils.parse(this.field_48917, stringReader, TextCodecs.CODEC);
		} catch (Exception var4) {
			String string = var4.getCause() != null ? var4.getCause().getMessage() : var4.getMessage();
			throw INVALID_COMPONENT_EXCEPTION.createWithContext(stringReader, string);
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
