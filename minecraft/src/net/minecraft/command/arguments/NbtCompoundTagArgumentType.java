package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;

public class NbtCompoundTagArgumentType implements ArgumentType<CompoundTag> {
	private static final Collection<String> EXAMPLES = Arrays.asList("{}", "{foo=bar}");

	private NbtCompoundTagArgumentType() {
	}

	public static NbtCompoundTagArgumentType create() {
		return new NbtCompoundTagArgumentType();
	}

	public static <S> CompoundTag getCompoundTag(CommandContext<S> commandContext, String string) {
		return commandContext.getArgument(string, CompoundTag.class);
	}

	public CompoundTag method_9286(StringReader stringReader) throws CommandSyntaxException {
		return new StringNbtReader(stringReader).parseCompoundTag();
	}

	@Override
	public Collection<String> getExamples() {
		return EXAMPLES;
	}
}
