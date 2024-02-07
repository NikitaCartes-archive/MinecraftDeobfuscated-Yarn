package net.minecraft.server.function;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.ContextChain;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.command.SingleCommandAction;
import net.minecraft.command.SourcedCommandAction;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.AbstractServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.Identifier;

public interface CommandFunction<T> {
	Identifier id();

	Procedure<T> withMacroReplaced(@Nullable NbtCompound arguments, CommandDispatcher<T> dispatcher) throws MacroException;

	private static boolean continuesToNextLine(CharSequence string) {
		int i = string.length();
		return i > 0 && string.charAt(i - 1) == '\\';
	}

	/**
	 * Parses a function in the context of {@code source}.
	 * 
	 * <p>Any syntax errors, such as improper comment lines or unknown commands, will be thrown at this point.
	 * 
	 * @param lines the raw lines (including comments) read from a function file
	 */
	static <T extends AbstractServerCommandSource<T>> CommandFunction<T> create(Identifier id, CommandDispatcher<T> dispatcher, T source, List<String> lines) {
		FunctionBuilder<T> functionBuilder = new FunctionBuilder<>();

		for (int i = 0; i < lines.size(); i++) {
			int j = i + 1;
			String string = ((String)lines.get(i)).trim();
			String string3;
			if (continuesToNextLine(string)) {
				StringBuilder stringBuilder = new StringBuilder(string);

				do {
					if (++i == lines.size()) {
						throw new IllegalArgumentException("Line continuation at end of file");
					}

					stringBuilder.deleteCharAt(stringBuilder.length() - 1);
					String string2 = ((String)lines.get(i)).trim();
					stringBuilder.append(string2);
					validateCommandLength(stringBuilder);
				} while (continuesToNextLine(stringBuilder));

				string3 = stringBuilder.toString();
			} else {
				string3 = string;
			}

			validateCommandLength(string3);
			StringReader stringReader = new StringReader(string3);
			if (stringReader.canRead() && stringReader.peek() != '#') {
				if (stringReader.peek() == '/') {
					stringReader.skip();
					if (stringReader.peek() == '/') {
						throw new IllegalArgumentException("Unknown or invalid command '" + string3 + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
					}

					String string2 = stringReader.readUnquotedString();
					throw new IllegalArgumentException(
						"Unknown or invalid command '" + string3 + "' on line " + j + " (did you mean '" + string2 + "'? Do not use a preceding forwards slash.)"
					);
				}

				if (stringReader.peek() == '$') {
					functionBuilder.addMacroCommand(string3.substring(1), j, source);
				} else {
					try {
						functionBuilder.addAction(parse(dispatcher, source, stringReader));
					} catch (CommandSyntaxException var11) {
						throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + var11.getMessage());
					}
				}
			}
		}

		return functionBuilder.toCommandFunction(id);
	}

	static void validateCommandLength(CharSequence command) {
		if (command.length() > 2000000) {
			CharSequence charSequence = command.subSequence(0, Math.min(512, 2000000));
			throw new IllegalStateException("Command too long: " + command.length() + " characters, contents: " + charSequence + "...");
		}
	}

	static <T extends AbstractServerCommandSource<T>> SourcedCommandAction<T> parse(CommandDispatcher<T> dispatcher, T source, StringReader reader) throws CommandSyntaxException {
		ParseResults<T> parseResults = dispatcher.parse(reader, source);
		CommandManager.throwException(parseResults);
		Optional<ContextChain<T>> optional = ContextChain.tryFlatten(parseResults.getContext().build(reader.getString()));
		if (optional.isEmpty()) {
			throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults.getReader());
		} else {
			return new SingleCommandAction.Sourced<>(reader.getString(), (ContextChain<T>)optional.get());
		}
	}
}
