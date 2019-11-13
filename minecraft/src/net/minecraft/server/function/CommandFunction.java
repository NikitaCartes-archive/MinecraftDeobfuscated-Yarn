package net.minecraft.server.function;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public class CommandFunction {
	private final CommandFunction.Element[] elements;
	private final Identifier id;

	public CommandFunction(Identifier id, CommandFunction.Element[] elements) {
		this.id = id;
		this.elements = elements;
	}

	public Identifier getId() {
		return this.id;
	}

	public CommandFunction.Element[] getElements() {
		return this.elements;
	}

	public static CommandFunction create(Identifier id, CommandFunctionManager commandFunctionManager, List<String> fileLines) {
		List<CommandFunction.Element> list = Lists.<CommandFunction.Element>newArrayListWithCapacity(fileLines.size());

		for (int i = 0; i < fileLines.size(); i++) {
			int j = i + 1;
			String string = ((String)fileLines.get(i)).trim();
			StringReader stringReader = new StringReader(string);
			if (stringReader.canRead() && stringReader.peek() != '#') {
				if (stringReader.peek() == '/') {
					stringReader.skip();
					if (stringReader.peek() == '/') {
						throw new IllegalArgumentException("Unknown or invalid command '" + string + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
					}

					String string2 = stringReader.readUnquotedString();
					throw new IllegalArgumentException(
						"Unknown or invalid command '" + string + "' on line " + j + " (did you mean '" + string2 + "'? Do not use a preceding forwards slash.)"
					);
				}

				try {
					ParseResults<ServerCommandSource> parseResults = commandFunctionManager.getServer()
						.getCommandManager()
						.getDispatcher()
						.parse(stringReader, commandFunctionManager.getCommandFunctionSource());
					if (parseResults.getReader().canRead()) {
						throw CommandManager.getException(parseResults);
					}

					list.add(new CommandFunction.CommandElement(parseResults));
				} catch (CommandSyntaxException var9) {
					throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + var9.getMessage());
				}
			}
		}

		return new CommandFunction(id, (CommandFunction.Element[])list.toArray(new CommandFunction.Element[0]));
	}

	public static class CommandElement implements CommandFunction.Element {
		private final ParseResults<ServerCommandSource> parsed;

		public CommandElement(ParseResults<ServerCommandSource> parseResults) {
			this.parsed = parseResults;
		}

		@Override
		public void execute(
			CommandFunctionManager commandFunctionManager, ServerCommandSource serverCommandSource, ArrayDeque<CommandFunctionManager.Entry> arrayDeque, int i
		) throws CommandSyntaxException {
			commandFunctionManager.getDispatcher()
				.execute(new ParseResults<>(this.parsed.getContext().withSource(serverCommandSource), this.parsed.getReader(), this.parsed.getExceptions()));
		}

		public String toString() {
			return this.parsed.getReader().getString();
		}
	}

	public interface Element {
		void execute(
			CommandFunctionManager commandFunctionManager, ServerCommandSource serverCommandSource, ArrayDeque<CommandFunctionManager.Entry> arrayDeque, int i
		) throws CommandSyntaxException;
	}

	public static class FunctionElement implements CommandFunction.Element {
		private final CommandFunction.LazyContainer function;

		public FunctionElement(CommandFunction commandFunction) {
			this.function = new CommandFunction.LazyContainer(commandFunction);
		}

		@Override
		public void execute(
			CommandFunctionManager commandFunctionManager, ServerCommandSource serverCommandSource, ArrayDeque<CommandFunctionManager.Entry> arrayDeque, int i
		) {
			this.function.get(commandFunctionManager).ifPresent(commandFunction -> {
				CommandFunction.Element[] elements = commandFunction.getElements();
				int j = i - arrayDeque.size();
				int k = Math.min(elements.length, j);

				for (int l = k - 1; l >= 0; l--) {
					arrayDeque.addFirst(new CommandFunctionManager.Entry(commandFunctionManager, serverCommandSource, elements[l]));
				}
			});
		}

		public String toString() {
			return "function " + this.function.getId();
		}
	}

	public static class LazyContainer {
		public static final CommandFunction.LazyContainer EMPTY = new CommandFunction.LazyContainer((Identifier)null);
		@Nullable
		private final Identifier id;
		private boolean initialized;
		private Optional<CommandFunction> function = Optional.empty();

		public LazyContainer(@Nullable Identifier id) {
			this.id = id;
		}

		public LazyContainer(CommandFunction commandFunction) {
			this.initialized = true;
			this.id = null;
			this.function = Optional.of(commandFunction);
		}

		public Optional<CommandFunction> get(CommandFunctionManager commandFunctionManager) {
			if (!this.initialized) {
				if (this.id != null) {
					this.function = commandFunctionManager.getFunction(this.id);
				}

				this.initialized = true;
			}

			return this.function;
		}

		@Nullable
		public Identifier getId() {
			return (Identifier)this.function.map(commandFunction -> commandFunction.id).orElse(this.id);
		}
	}
}
