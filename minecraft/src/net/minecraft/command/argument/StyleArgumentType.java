package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.JsonReaderUtils;

public class StyleArgumentType implements ArgumentType<Style> {
	private static final Collection<String> EXAMPLES = List.of("{\"bold\": true}\n");
	public static final DynamicCommandExceptionType INVALID_STYLE_EXCEPTION = new DynamicCommandExceptionType(
		style -> Text.stringifiedTranslatable("argument.style.invalid", style)
	);
	private final RegistryWrapper.WrapperLookup registryLookup;

	private StyleArgumentType(RegistryWrapper.WrapperLookup registryLookup) {
		this.registryLookup = registryLookup;
	}

	public static Style getStyle(CommandContext<ServerCommandSource> context, String style) {
		return context.getArgument(style, Style.class);
	}

	public static StyleArgumentType style(CommandRegistryAccess registryAccess) {
		return new StyleArgumentType(registryAccess);
	}

	public Style parse(StringReader stringReader) throws CommandSyntaxException {
		try {
			return JsonReaderUtils.parse(this.registryLookup, stringReader, Style.Codecs.CODEC);
		} catch (Exception var4) {
			String string = var4.getCause() != null ? var4.getCause().getMessage() : var4.getMessage();
			throw INVALID_STYLE_EXCEPTION.createWithContext(stringReader, string);
		}
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
