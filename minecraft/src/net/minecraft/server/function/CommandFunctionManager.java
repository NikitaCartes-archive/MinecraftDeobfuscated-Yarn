package net.minecraft.server.function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandFunctionManager implements SynchronousResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier TICK_FUNCTION = new Identifier("tick");
	private static final Identifier LOAD_FUNCTION = new Identifier("load");
	public static final int PATH_PREFIX_LENGTH = "functions/".length();
	public static final int EXTENSION_LENGTH = ".mcfunction".length();
	private final MinecraftServer server;
	private final Map<Identifier, CommandFunction> idMap = Maps.<Identifier, CommandFunction>newHashMap();
	private final ArrayDeque<CommandFunctionManager.Entry> chain = new ArrayDeque();
	private boolean field_13411;
	private final TagContainer<CommandFunction> tags = new TagContainer<>(this::getFunction, "tags/functions", true, "function");
	private final List<CommandFunction> tickFunctions = Lists.<CommandFunction>newArrayList();
	private boolean justLoaded;

	public CommandFunctionManager(MinecraftServer minecraftServer) {
		this.server = minecraftServer;
	}

	public Optional<CommandFunction> getFunction(Identifier identifier) {
		return Optional.ofNullable(this.idMap.get(identifier));
	}

	public MinecraftServer getServer() {
		return this.server;
	}

	public int getMaxCommandChainLength() {
		return this.server.getGameRules().getInteger("maxCommandChainLength");
	}

	public Map<Identifier, CommandFunction> getFunctions() {
		return this.idMap;
	}

	public CommandDispatcher<ServerCommandSource> getDispatcher() {
		return this.server.getCommandManager().getDispatcher();
	}

	public void method_18699() {
		this.server.getProfiler().push(TICK_FUNCTION::toString);

		for (CommandFunction commandFunction : this.tickFunctions) {
			this.execute(commandFunction, this.getFunctionCommandSource());
		}

		this.server.getProfiler().pop();
		if (this.justLoaded) {
			this.justLoaded = false;
			Collection<CommandFunction> collection = this.getTags().getOrCreate(LOAD_FUNCTION).values();
			this.server.getProfiler().push(LOAD_FUNCTION::toString);

			for (CommandFunction commandFunction2 : collection) {
				this.execute(commandFunction2, this.getFunctionCommandSource());
			}

			this.server.getProfiler().pop();
		}
	}

	public int execute(CommandFunction commandFunction, ServerCommandSource serverCommandSource) {
		int i = this.getMaxCommandChainLength();
		if (this.field_13411) {
			if (this.chain.size() < i) {
				this.chain.addFirst(new CommandFunctionManager.Entry(this, serverCommandSource, new CommandFunction.FunctionElement(commandFunction)));
			}

			return 0;
		} else {
			int var16;
			try {
				this.field_13411 = true;
				int j = 0;
				CommandFunction.Element[] elements = commandFunction.getElements();

				for (int k = elements.length - 1; k >= 0; k--) {
					this.chain.push(new CommandFunctionManager.Entry(this, serverCommandSource, elements[k]));
				}

				do {
					if (this.chain.isEmpty()) {
						return j;
					}

					try {
						CommandFunctionManager.Entry entry = (CommandFunctionManager.Entry)this.chain.removeFirst();
						this.server.getProfiler().push(entry::toString);
						entry.execute(this.chain, i);
					} finally {
						this.server.getProfiler().pop();
					}
				} while (++j < i);

				var16 = j;
			} finally {
				this.chain.clear();
				this.field_13411 = false;
			}

			return var16;
		}
	}

	@Override
	public void apply(ResourceManager resourceManager) {
		this.idMap.clear();
		this.tickFunctions.clear();
		this.tags.clear();
		Collection<Identifier> collection = resourceManager.findResources("functions", stringx -> stringx.endsWith(".mcfunction"));
		List<CompletableFuture<CommandFunction>> list = Lists.<CompletableFuture<CommandFunction>>newArrayList();

		for (Identifier identifier : collection) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(PATH_PREFIX_LENGTH, string.length() - EXTENSION_LENGTH));
			list.add(
				CompletableFuture.supplyAsync(() -> readLines(resourceManager, identifier), ResourceImpl.RESOURCE_IO_EXECUTOR)
					.thenApplyAsync(listx -> CommandFunction.create(identifier2, this, listx), this.server.getWorkerExecutor())
					.handle((commandFunction, throwable) -> this.load(commandFunction, throwable, identifier))
			);
		}

		CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
		if (!this.idMap.isEmpty()) {
			LOGGER.info("Loaded {} custom command functions", this.idMap.size());
		}

		this.tags.applyReload((Map<Identifier, Tag.Builder<CommandFunction>>)this.tags.prepareReload(resourceManager, this.server.getWorkerExecutor()).join());
		this.tickFunctions.addAll(this.tags.getOrCreate(TICK_FUNCTION).values());
		this.justLoaded = true;
	}

	@Nullable
	private CommandFunction load(CommandFunction commandFunction, @Nullable Throwable throwable, Identifier identifier) {
		if (throwable != null) {
			LOGGER.error("Couldn't load function at {}", identifier, throwable);
			return null;
		} else {
			synchronized (this.idMap) {
				this.idMap.put(commandFunction.getId(), commandFunction);
				return commandFunction;
			}
		}
	}

	private static List<String> readLines(ResourceManager resourceManager, Identifier identifier) {
		try {
			Resource resource = resourceManager.getResource(identifier);
			Throwable var3 = null;

			List var4;
			try {
				var4 = IOUtils.readLines(resource.getInputStream(), StandardCharsets.UTF_8);
			} catch (Throwable var14) {
				var3 = var14;
				throw var14;
			} finally {
				if (resource != null) {
					if (var3 != null) {
						try {
							resource.close();
						} catch (Throwable var13) {
							var3.addSuppressed(var13);
						}
					} else {
						resource.close();
					}
				}
			}

			return var4;
		} catch (IOException var16) {
			throw new CompletionException(var16);
		}
	}

	public ServerCommandSource getFunctionCommandSource() {
		return this.server.getCommandSource().withLevel(2).withSilent();
	}

	public TagContainer<CommandFunction> getTags() {
		return this.tags;
	}

	public static class Entry {
		private final CommandFunctionManager manager;
		private final ServerCommandSource source;
		private final CommandFunction.Element element;

		public Entry(CommandFunctionManager commandFunctionManager, ServerCommandSource serverCommandSource, CommandFunction.Element element) {
			this.manager = commandFunctionManager;
			this.source = serverCommandSource;
			this.element = element;
		}

		public void execute(ArrayDeque<CommandFunctionManager.Entry> arrayDeque, int i) {
			try {
				this.element.execute(this.manager, this.source, arrayDeque, i);
			} catch (Throwable var4) {
			}
		}

		public String toString() {
			return this.element.toString();
		}
	}
}
