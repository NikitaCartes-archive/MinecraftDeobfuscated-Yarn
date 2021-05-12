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
	private static final Text NO_RECURSION_TEXT = new TranslatableText("commands.debug.function.noRecursion");
	private static final Identifier TICK_FUNCTION = new Identifier("tick");
	private static final Identifier LOAD_FUNCTION = new Identifier("load");
	final MinecraftServer server;
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
		return this.execute(function, source, null);
	}

	public int execute(CommandFunction function, ServerCommandSource source, @Nullable CommandFunctionManager.Tracer tracer) {
		if (this.field_33543 != null) {
			if (tracer != null) {
				this.field_33543.reportError(NO_RECURSION_TEXT.getString());
				return 0;
			} else {
				this.field_33543.method_36343(function, source);
				return 0;
			}
		} else {
			int var4;
			try {
				this.field_33543 = new CommandFunctionManager.class_6345(tracer);
				var4 = this.field_33543.method_36346(function, source);
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
		final int depth;
		private final CommandFunction.Element element;

		public Entry(ServerCommandSource source, int depth, CommandFunction.Element element) {
			this.source = source;
			this.depth = depth;
			this.element = element;
		}

		public void execute(
			CommandFunctionManager manager, Deque<CommandFunctionManager.Entry> deque, int maxChainLength, @Nullable CommandFunctionManager.Tracer tracer
		) {
			try {
				this.element.execute(manager, this.source, deque, maxChainLength, this.depth, tracer);
			} catch (CommandSyntaxException var6) {
				if (tracer != null) {
					tracer.traceError(this.depth, var6.getRawMessage().getString());
				}
			} catch (Exception var7) {
				if (tracer != null) {
					tracer.traceError(this.depth, var7.getMessage());
				}
			}
		}

		public String toString() {
			return this.element.toString();
		}
	}

	public interface Tracer {
		void traceCommandStart(int depth, String command);

		void traceCommandEnd(int depth, String command, int result);

		void traceError(int depth, String message);

		void traceFunctionCall(int depth, Identifier function, int size);
	}

	class class_6345 {
		private int depth;
		@Nullable
		private final CommandFunctionManager.Tracer tracer;
		private final Deque<CommandFunctionManager.Entry> field_33547 = Queues.<CommandFunctionManager.Entry>newArrayDeque();
		private final List<CommandFunctionManager.Entry> field_33548 = Lists.<CommandFunctionManager.Entry>newArrayList();

		class_6345(@Nullable CommandFunctionManager.Tracer tracer) {
			this.tracer = tracer;
		}

		void method_36343(CommandFunction function, ServerCommandSource source) {
			int i = CommandFunctionManager.this.getMaxCommandChainLength();
			if (this.field_33547.size() + this.field_33548.size() < i) {
				this.field_33548.add(new CommandFunctionManager.Entry(source, this.depth, new CommandFunction.FunctionElement(function)));
			}
		}

		int method_36346(CommandFunction function, ServerCommandSource source) {
			int i = CommandFunctionManager.this.getMaxCommandChainLength();
			int j = 0;
			CommandFunction.Element[] elements = function.getElements();

			for (int k = elements.length - 1; k >= 0; k--) {
				this.field_33547.push(new CommandFunctionManager.Entry(source, 0, elements[k]));
			}

			while (!this.field_33547.isEmpty()) {
				try {
					CommandFunctionManager.Entry entry = (CommandFunctionManager.Entry)this.field_33547.removeFirst();
					CommandFunctionManager.this.server.getProfiler().push(entry::toString);
					this.depth = entry.depth;
					entry.execute(CommandFunctionManager.this, this.field_33547, i, this.tracer);
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

		public void reportError(String message) {
			if (this.tracer != null) {
				this.tracer.traceError(this.depth, message);
			}
		}
	}
}
