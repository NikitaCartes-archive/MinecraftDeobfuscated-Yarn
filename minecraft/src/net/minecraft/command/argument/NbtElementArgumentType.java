package net.minecraft.command.argument;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.StringNbtReader;

public class NbtElementArgumentType implements ArgumentType<NbtElement> {
	private static final Collection<String> EXAMPLES = Arrays.asList("0", "0b", "0l", "0.0", "\"foo\"", "{foo=bar}", "[0]");

	private NbtElementArgumentType() {
	}

	public static NbtElementArgumentType nbtElement() {
		return new NbtElementArgumentType();
	}

	public static <S> NbtElement getNbtElement(CommandContext<S> context, String name) {
		return context.getArgument(name, NbtElement.class);
	}

	public NbtElement parse(StringReader stringReader) throws CommandSyntaxException {
		return new StringNbtReader(stringReader).parseElement();
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
