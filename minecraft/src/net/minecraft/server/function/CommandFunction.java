package net.minecraft.server.function;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.NbtByte;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtLong;
import net.minecraft.nbt.NbtShort;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public class CommandFunction {
	private final CommandFunction.Element[] elements;
	final Identifier id;

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

	public CommandFunction withMacroReplaced(@Nullable NbtCompound arguments, CommandDispatcher<ServerCommandSource> dispatcher, ServerCommandSource source) throws MacroException {
		return this;
	}

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
	public static CommandFunction create(Identifier id, CommandDispatcher<ServerCommandSource> dispatcher, ServerCommandSource source, List<String> lines) {
		List<CommandFunction.Element> list = new ArrayList(lines.size());
		Set<String> set = new ObjectArraySet<>();

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
				} while (continuesToNextLine(stringBuilder));

				string3 = stringBuilder.toString();
			} else {
				string3 = string;
			}

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
					CommandFunction.MacroElement macroElement = parseMacro(string3.substring(1), j);
					list.add(macroElement);
					set.addAll(macroElement.getVariables());
				} else {
					try {
						ParseResults<ServerCommandSource> parseResults = dispatcher.parse(stringReader, source);
						if (parseResults.getReader().canRead()) {
							throw CommandManager.getException(parseResults);
						}

						list.add(new CommandFunction.CommandElement(parseResults));
					} catch (CommandSyntaxException var12) {
						throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + var12.getMessage());
					}
				}
			}
		}

		return (CommandFunction)(set.isEmpty()
			? new CommandFunction(id, (CommandFunction.Element[])list.toArray(CommandFunction.Element[]::new))
			: new CommandFunction.Macro(id, (CommandFunction.Element[])list.toArray(CommandFunction.Element[]::new), List.copyOf(set)));
	}

	@VisibleForTesting
	public static CommandFunction.MacroElement parseMacro(String macro, int line) {
		Builder<String> builder = ImmutableList.builder();
		Builder<String> builder2 = ImmutableList.builder();
		int i = macro.length();
		int j = 0;
		int k = macro.indexOf(36);

		while (k != -1) {
			if (k != i - 1 && macro.charAt(k + 1) == '(') {
				builder.add(macro.substring(j, k));
				int l = macro.indexOf(41, k + 1);
				if (l == -1) {
					throw new IllegalArgumentException("Unterminated macro variable in macro '" + macro + "' on line " + line);
				}

				String string = macro.substring(k + 2, l);
				if (!isValidMacroVariableName(string)) {
					throw new IllegalArgumentException("Invalid macro variable name '" + string + "' on line " + line);
				}

				builder2.add(string);
				j = l + 1;
				k = macro.indexOf(36, j);
			} else {
				k = macro.indexOf(36, k + 1);
			}
		}

		if (j == 0) {
			throw new IllegalArgumentException("Macro without variables on line " + line);
		} else {
			if (j != i) {
				builder.add(macro.substring(j));
			}

			return new CommandFunction.MacroElement(builder.build(), builder2.build());
		}
	}

	private static boolean isValidMacroVariableName(String name) {
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (!Character.isLetterOrDigit(c) && c != '_') {
				return false;
			}
		}

		return true;
	}

	/**
	 * A standard element of a command function. Functions created by {@link
	 * CommandFunction#create} will only contain these elements.
	 */
	public static class CommandElement implements CommandFunction.Element {
		private final ParseResults<ServerCommandSource> parsed;

		public CommandElement(ParseResults<ServerCommandSource> parsed) {
			this.parsed = parsed;
		}

		@Override
		public void execute(
			CommandFunctionManager commandFunctionManager,
			ServerCommandSource serverCommandSource,
			Deque<CommandFunctionManager.Entry> deque,
			int i,
			int j,
			@Nullable CommandFunctionManager.Tracer tracer
		) throws CommandSyntaxException {
			if (tracer != null) {
				String string = this.parsed.getReader().getString();
				tracer.traceCommandStart(j, string);
				int k = this.execute(commandFunctionManager, serverCommandSource);
				tracer.traceCommandEnd(j, string, k);
			} else {
				this.execute(commandFunctionManager, serverCommandSource);
			}
		}

		private int execute(CommandFunctionManager manager, ServerCommandSource source) throws CommandSyntaxException {
			return manager.getDispatcher().execute(CommandManager.withCommandSource(this.parsed, currentSource -> source));
		}

		public String toString() {
			return this.parsed.getReader().getString();
		}
	}

	@FunctionalInterface
	public interface Element {
		void execute(
			CommandFunctionManager manager,
			ServerCommandSource source,
			Deque<CommandFunctionManager.Entry> entries,
			int maxChainLength,
			int depth,
			@Nullable CommandFunctionManager.Tracer tracer
		) throws CommandSyntaxException;
	}

	/**
	 * A synthetic element to be stored in a {@link CommandFunctionManager.Entry}.
	 * This is not present as parts of command functions, but created by {@link
	 * net.minecraft.server.function.CommandFunctionManager.Execution#recursiveRun}.
	 */
	public static class FunctionElement implements CommandFunction.Element {
		private final CommandFunction.LazyContainer function;

		public FunctionElement(CommandFunction function) {
			this.function = new CommandFunction.LazyContainer(function);
		}

		@Override
		public void execute(
			CommandFunctionManager commandFunctionManager,
			ServerCommandSource serverCommandSource,
			Deque<CommandFunctionManager.Entry> deque,
			int i,
			int j,
			@Nullable CommandFunctionManager.Tracer tracer
		) {
			Util.ifPresentOrElse(this.function.get(commandFunctionManager), f -> {
				CommandFunction.Element[] elements = f.getElements();
				if (tracer != null) {
					tracer.traceFunctionCall(j, f.getId(), elements.length);
				}

				int k = i - deque.size();
				int l = Math.min(elements.length, k);

				for (int m = l - 1; m >= 0; m--) {
					deque.addFirst(new CommandFunctionManager.Entry(serverCommandSource, j + 1, elements[m]));
				}
			}, () -> {
				if (tracer != null) {
					tracer.traceFunctionCall(j, this.function.getId(), -1);
				}
			});
		}

		public String toString() {
			return "function " + this.function.getId();
		}
	}

	/**
	 * A lazy reference to another command function that may or may not exist.
	 * 
	 * <p>Notice that such an instance does not refresh upon reloads and may become
	 * invalid.
	 */
	public static class LazyContainer {
		public static final CommandFunction.LazyContainer EMPTY = new CommandFunction.LazyContainer((Identifier)null);
		@Nullable
		private final Identifier id;
		private boolean initialized;
		private Optional<CommandFunction> function = Optional.empty();

		public LazyContainer(@Nullable Identifier id) {
			this.id = id;
		}

		public LazyContainer(CommandFunction function) {
			this.initialized = true;
			this.id = null;
			this.function = Optional.of(function);
		}

		public Optional<CommandFunction> get(CommandFunctionManager manager) {
			if (!this.initialized) {
				if (this.id != null) {
					this.function = manager.getFunction(this.id);
				}

				this.initialized = true;
			}

			return this.function;
		}

		@Nullable
		public Identifier getId() {
			return (Identifier)this.function.map(f -> f.id).orElse(this.id);
		}
	}

	static class Macro extends CommandFunction {
		private final List<String> variables;
		private static final int CACHE_SIZE = 8;
		private final Object2ObjectLinkedOpenHashMap<List<String>, CommandFunction> cache = new Object2ObjectLinkedOpenHashMap<>(8, 0.25F);

		public Macro(Identifier id, CommandFunction.Element[] elements, List<String> variables) {
			super(id, elements);
			this.variables = variables;
		}

		@Override
		public CommandFunction withMacroReplaced(@Nullable NbtCompound arguments, CommandDispatcher<ServerCommandSource> dispatcher, ServerCommandSource source) throws MacroException {
			if (arguments == null) {
				throw new MacroException(Text.translatable("commands.function.error.missing_arguments", this.getId()));
			} else {
				List<String> list = new ArrayList(this.variables.size());

				for (String string : this.variables) {
					if (!arguments.contains(string)) {
						throw new MacroException(Text.translatable("commands.function.error.missing_argument", this.getId(), string));
					}

					list.add(toString(arguments.get(string)));
				}

				CommandFunction commandFunction = this.cache.getAndMoveToLast(list);
				if (commandFunction != null) {
					return commandFunction;
				} else {
					if (this.cache.size() >= 8) {
						this.cache.removeFirst();
					}

					CommandFunction commandFunction2 = this.withMacroReplaced(list, dispatcher, source);
					if (commandFunction2 != null) {
						this.cache.put(list, commandFunction2);
					}

					return commandFunction2;
				}
			}
		}

		private static String toString(NbtElement nbt) {
			if (nbt instanceof NbtFloat nbtFloat) {
				return String.valueOf(nbtFloat.floatValue());
			} else if (nbt instanceof NbtDouble nbtDouble) {
				return String.valueOf(nbtDouble.doubleValue());
			} else if (nbt instanceof NbtByte nbtByte) {
				return String.valueOf(nbtByte.byteValue());
			} else if (nbt instanceof NbtShort nbtShort) {
				return String.valueOf(nbtShort.shortValue());
			} else {
				return nbt instanceof NbtLong nbtLong ? String.valueOf(nbtLong.longValue()) : nbt.asString();
			}
		}

		private CommandFunction withMacroReplaced(List<String> arguments, CommandDispatcher<ServerCommandSource> dispatcher, ServerCommandSource source) throws MacroException {
			CommandFunction.Element[] elements = this.getElements();
			CommandFunction.Element[] elements2 = new CommandFunction.Element[elements.length];

			for (int i = 0; i < elements.length; i++) {
				CommandFunction.Element element = elements[i];
				if (!(element instanceof CommandFunction.MacroElement macroElement)) {
					elements2[i] = element;
				} else {
					List<String> list = macroElement.getVariables();
					List<String> list2 = new ArrayList(list.size());

					for (String string : list) {
						list2.add((String)arguments.get(this.variables.indexOf(string)));
					}

					String string2 = macroElement.getCommand(list2);

					try {
						ParseResults<ServerCommandSource> parseResults = dispatcher.parse(string2, source);
						if (parseResults.getReader().canRead()) {
							throw CommandManager.getException(parseResults);
						}

						elements2[i] = new CommandFunction.CommandElement(parseResults);
					} catch (CommandSyntaxException var13) {
						throw new MacroException(Text.translatable("commands.function.error.parse", this.getId(), string2, var13.getMessage()));
					}
				}
			}

			Identifier identifier = this.getId();
			return new CommandFunction(new Identifier(identifier.getNamespace(), identifier.getPath() + "/" + arguments.hashCode()), elements2);
		}
	}

	public static class MacroElement implements CommandFunction.Element {
		private final List<String> parts;
		private final List<String> variables;

		public MacroElement(List<String> parts, List<String> variables) {
			this.parts = parts;
			this.variables = variables;
		}

		public List<String> getVariables() {
			return this.variables;
		}

		public String getCommand(List<String> arguments) {
			StringBuilder stringBuilder = new StringBuilder();

			for (int i = 0; i < this.variables.size(); i++) {
				stringBuilder.append((String)this.parts.get(i)).append((String)arguments.get(i));
			}

			if (this.parts.size() > this.variables.size()) {
				stringBuilder.append((String)this.parts.get(this.parts.size() - 1));
			}

			return stringBuilder.toString();
		}

		@Override
		public void execute(
			CommandFunctionManager commandFunctionManager,
			ServerCommandSource serverCommandSource,
			Deque<CommandFunctionManager.Entry> deque,
			int i,
			int j,
			@Nullable CommandFunctionManager.Tracer tracer
		) throws CommandSyntaxException {
			throw new IllegalStateException("Tried to execute an uninstantiated macro");
		}
	}
}
