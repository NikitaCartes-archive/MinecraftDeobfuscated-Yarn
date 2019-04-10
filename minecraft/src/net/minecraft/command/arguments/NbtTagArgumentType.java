package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.Tag;
import net.minecraft.sortme.JsonLikeTagParser;

public class NbtTagArgumentType implements ArgumentType<Tag> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]");

	private NbtTagArgumentType() {
	}

	public static NbtTagArgumentType create() {
		return new NbtTagArgumentType();
	}

	public static <S> Tag getTag(CommandContext<S> commandContext, String string) {
		return commandContext.getArgument(string, Tag.class);
	}

	public Tag method_9388(StringReader stringReader) throws CommandSyntaxException {
		return new JsonLikeTagParser(stringReader).parseTag();
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
