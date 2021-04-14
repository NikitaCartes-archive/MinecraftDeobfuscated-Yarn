package net.minecraft.server.function;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class CommandFunctionManager {
	private static final Text field_33542 = new TranslatableText("commands.debug.function.noRecursion");
	private static final Identifier TICK_FUNCTION = new Identifier("tick");
	private static final Identifier LOAD_FUNCTION = new Identifier("load");
	private final MinecraftServer server;
	@Nullable
	private CommandFunctionManager.class_6345 field_33543;
	private final List<CommandFunction> tickFunctions = Lists.<CommandFunction>newArrayList();
	private boolean needToRunLoadFunctions;
	private FunctionLoader loader;

	public CommandFunctionManager(MinecraftServer server, FunctionLoader loader) {
		this.server = server;
		this.loader = loader;
		this.initialize(loader);
	}

	public int getMaxCommandChainLength() {
		return this.server.getGameRules().getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);
	}

	public CommandDispatcher<ServerCommandSource> getDispatcher() {
		return this.server.getCommandManager().getDispatcher();
	}

	public void tick() {
		this.executeAll(this.tickFunctions, TICK_FUNCTION);
		if (this.needToRunLoadFunctions) {
			this.needToRunLoadFunctions = false;
			Collection<CommandFunction> collection = this.loader.getTags().getTagOrEmpty(LOAD_FUNCTION).values();
			this.executeAll(collection, LOAD_FUNCTION);
		}
	}

	private void executeAll(Collection<CommandFunction> functions, Identifier label) {
		this.server.getProfiler().push(label::toString);

		for (CommandFunction commandFunction : functions) {
			this.execute(commandFunction, this.getTaggedFunctionSource());
		}

		this.server.getProfiler().pop();
	}

	public int execute(CommandFunction function, ServerCommandSource source) {
		return this.method_36341(function, source, null);
	}

	public int method_36341(CommandFunction commandFunction, ServerCommandSource serverCommandSource, @Nullable CommandFunctionManager.class_6346 arg) {
		if (this.field_33543 != null) {
			if (arg != null) {
				this.field_33543.method_36344(field_33542.getString());
				return 0;
			} else {
				this.field_33543.method_36343(commandFunction, serverCommandSource);
				return 0;
			}
		} else {
			int var4;
			try {
				this.field_33543 = new CommandFunctionManager.class_6345(arg);
				var4 = this.field_33543.method_36346(commandFunction, serverCommandSource);
			} finally {
				this.field_33543 = null;
			}

			return var4;
		}
	}

	/**
	 * Called to update the loaded functions on datapack reload.
	 * 
	 * @param loader the new loader functions will be taken from
	 */
	public void update(FunctionLoader loader) {
		this.loader = loader;
		this.initialize(loader);
	}

	private void initialize(FunctionLoader loader) {
		this.tickFunctions.clear();
		this.tickFunctions.addAll(loader.getTags().getTagOrEmpty(TICK_FUNCTION).values());
		this.needToRunLoadFunctions = true;
	}

	public ServerCommandSource getTaggedFunctionSource() {
		return this.server.getCommandSource().withLevel(2).withSilent();
	}

	public Optional<CommandFunction> getFunction(Identifier id) {
		return this.loader.get(id);
	}

	public Tag<CommandFunction> getTaggedFunctions(Identifier tag) {
		return this.loader.getOrCreateTag(tag);
	}

	public Iterable<Identifier> getAllFunctions() {
		return this.loader.getFunctions().keySet();
	}

	public Iterable<Identifier> getFunctionTags() {
		return this.loader.getTags().getTagIds();
	}

	public static class Entry {
		private final ServerCommandSource source;
		private final int field_33549;
		private final CommandFunction.Element element;

		public Entry(ServerCommandSource serverCommandSource, int i, CommandFunction.Element element) {
			this.source = serverCommandSource;
			this.field_33549 = i;
			this.element = element;
		}

		public void execute(
			CommandFunctionManager commandFunctionManager, Deque<CommandFunctionManager.Entry> deque, int i, @Nullable CommandFunctionManager.class_6346 arg
		) {
			try {
				this.element.execute(commandFunctionManager, this.source, deque, i, this.field_33549, arg);
			} catch (CommandSyntaxException var6) {
				if (arg != null) {
					arg.method_36352(this.field_33549, var6.getRawMessage().getString());
				}
			} catch (Exception var7) {
				if (arg != null) {
					arg.method_36352(this.field_33549, var7.getMessage());
				}
			}
		}

		public String toString() {
			return this.element.toString();
		}
	}

	class class_6345 {
		private int field_33545;
		@Nullable
		private final CommandFunctionManager.class_6346 field_33546;
		private final Deque<CommandFunctionManager.Entry> field_33547 = Queues.<CommandFunctionManager.Entry>newArrayDeque();
		private final List<CommandFunctionManager.Entry> field_33548 = Lists.<CommandFunctionManager.Entry>newArrayList();

		private class_6345(@Nullable CommandFunctionManager.class_6346 arg) {
			this.field_33546 = arg;
		}

		private void method_36343(CommandFunction commandFunction, ServerCommandSource serverCommandSource) {
			int i = CommandFunctionManager.this.getMaxCommandChainLength();
			if (this.field_33547.size() + this.field_33548.size() < i) {
				this.field_33548.add(new CommandFunctionManager.Entry(serverCommandSource, this.field_33545, new CommandFunction.FunctionElement(commandFunction)));
			}
		}

		private int method_36346(CommandFunction commandFunction, ServerCommandSource serverCommandSource) {
			int i = CommandFunctionManager.this.getMaxCommandChainLength();
			int j = 0;
			CommandFunction.Element[] elements = commandFunction.getElements();

			for (int k = elements.length - 1; k >= 0; k--) {
				this.field_33547.push(new CommandFunctionManager.Entry(serverCommandSource, 0, elements[k]));
			}

			while (!this.field_33547.isEmpty()) {
				try {
					CommandFunctionManager.Entry entry = (CommandFunctionManager.Entry)this.field_33547.removeFirst();
					CommandFunctionManager.this.server.getProfiler().push(entry::toString);
					this.field_33545 = entry.field_33549;
					entry.execute(CommandFunctionManager.this, this.field_33547, i, this.field_33546);
					if (!this.field_33548.isEmpty()) {
						Lists.reverse(this.field_33548).forEach(this.field_33547::addFirst);
						this.field_33548.clear();
					}
				} finally {
					CommandFunctionManager.this.server.getProfiler().pop();
				}

				if (++j >= i) {
					return j;
				}
			}

			return j;
		}

		public void method_36344(String string) {
			if (this.field_33546 != null) {
				this.field_33546.method_36352(this.field_33545, string);
			}
		}
	}

	public interface class_6346 {
		void method_36349(int i, String string);

		void method_36350(int i, String string, int j);

		void method_36352(int i, String string);

		void method_36351(int i, Identifier identifier, int j);
	}
}
