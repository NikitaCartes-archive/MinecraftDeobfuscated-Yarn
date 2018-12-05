package net.minecraft.server.function;

import com.google.common.collect.Lists;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayDeque;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;

public class CommandFunction {
	private final CommandFunction.class_2161[] field_9805;
	private final Identifier id;

	public CommandFunction(Identifier identifier, CommandFunction.class_2161[] args) {
		this.id = identifier;
		this.field_9805 = args;
	}

	public Identifier getId() {
		return this.id;
	}

	public CommandFunction.class_2161[] method_9193() {
		return this.field_9805;
	}

	public static CommandFunction method_9195(Identifier identifier, CommandFunctionManager commandFunctionManager, List<String> list) {
		List<CommandFunction.class_2161> list2 = Lists.<CommandFunction.class_2161>newArrayListWithCapacity(list.size());

		for (int i = 0; i < list.size(); i++) {
			int j = i + 1;
			String string = ((String)list.get(i)).trim();
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
						.parse(stringReader, commandFunctionManager.method_12899());
					if (parseResults.getReader().canRead()) {
						if (parseResults.getExceptions().size() == 1) {
							throw (CommandSyntaxException)parseResults.getExceptions().values().iterator().next();
						}

						if (parseResults.getContext().getRange().isEmpty()) {
							throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().createWithContext(parseResults.getReader());
						}

						throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().createWithContext(parseResults.getReader());
					}

					list2.add(new CommandFunction.class_2160(parseResults));
				} catch (CommandSyntaxException var9) {
					throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + var9.getMessage());
				}
			}
		}

		return new CommandFunction(identifier, (CommandFunction.class_2161[])list2.toArray(new CommandFunction.class_2161[0]));
	}

	public static class class_2159 {
		public static final CommandFunction.class_2159 field_9809 = new CommandFunction.class_2159((Identifier)null);
		@Nullable
		private final Identifier field_9807;
		private boolean field_9810;
		private CommandFunction field_9808;

		public class_2159(@Nullable Identifier identifier) {
			this.field_9807 = identifier;
		}

		public class_2159(CommandFunction commandFunction) {
			this.field_9807 = null;
			this.field_9808 = commandFunction;
		}

		@Nullable
		public CommandFunction method_9196(CommandFunctionManager commandFunctionManager) {
			if (!this.field_9810) {
				if (this.field_9807 != null) {
					this.field_9808 = commandFunctionManager.getFunction(this.field_9807);
				}

				this.field_9810 = true;
			}

			return this.field_9808;
		}

		@Nullable
		public Identifier method_9197() {
			return this.field_9808 != null ? this.field_9808.id : this.field_9807;
		}
	}

	public static class class_2160 implements CommandFunction.class_2161 {
		private final ParseResults<ServerCommandSource> field_9811;

		public class_2160(ParseResults<ServerCommandSource> parseResults) {
			this.field_9811 = parseResults;
		}

		@Override
		public void method_9198(
			CommandFunctionManager commandFunctionManager, ServerCommandSource serverCommandSource, ArrayDeque<CommandFunctionManager.class_2992> arrayDeque, int i
		) throws CommandSyntaxException {
			commandFunctionManager.getDispatcher()
				.execute(
					new ParseResults<>(
						this.field_9811.getContext().withSource(serverCommandSource),
						this.field_9811.getStartIndex(),
						this.field_9811.getReader(),
						this.field_9811.getExceptions()
					)
				);
		}

		public String toString() {
			return this.field_9811.getReader().getString();
		}
	}

	public interface class_2161 {
		void method_9198(
			CommandFunctionManager commandFunctionManager, ServerCommandSource serverCommandSource, ArrayDeque<CommandFunctionManager.class_2992> arrayDeque, int i
		) throws CommandSyntaxException;
	}

	public static class class_2162 implements CommandFunction.class_2161 {
		private final CommandFunction.class_2159 field_9812;

		public class_2162(CommandFunction commandFunction) {
			this.field_9812 = new CommandFunction.class_2159(commandFunction);
		}

		@Override
		public void method_9198(
			CommandFunctionManager commandFunctionManager, ServerCommandSource serverCommandSource, ArrayDeque<CommandFunctionManager.class_2992> arrayDeque, int i
		) {
			CommandFunction commandFunction = this.field_9812.method_9196(commandFunctionManager);
			if (commandFunction != null) {
				CommandFunction.class_2161[] lvs = commandFunction.method_9193();
				int j = i - arrayDeque.size();
				int k = Math.min(lvs.length, j);

				for (int l = k - 1; l >= 0; l--) {
					arrayDeque.addFirst(new CommandFunctionManager.class_2992(commandFunctionManager, serverCommandSource, lvs[l]));
				}
			}
		}

		public String toString() {
			return "function " + this.field_9812.method_9197();
		}
	}
}
