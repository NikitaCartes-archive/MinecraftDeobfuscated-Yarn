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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.TagContainer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Tickable;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandFunctionManager implements Tickable, ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Identifier TICK_FUNCTION = new Identifier("tick");
	private static final Identifier LOAD_FUNCTION = new Identifier("load");
	public static final int PATH_PREFIX_LENGTH = "functions/".length();
	public static final int EXTENSION_LENGTH = ".mcfunction".length();
	private final MinecraftServer server;
	private final Map<Identifier, CommandFunction> idMap = Maps.<Identifier, CommandFunction>newHashMap();
	private final ArrayDeque<CommandFunctionManager.class_2992> field_13413 = new ArrayDeque();
	private boolean field_13411;
	private final TagContainer<CommandFunction> field_13416 = new TagContainer<>(
		identifier -> this.getFunction(identifier) != null, this::getFunction, "tags/functions", true, "function"
	);
	private final List<CommandFunction> tickFunctions = Lists.<CommandFunction>newArrayList();
	private boolean field_13422;

	public CommandFunctionManager(MinecraftServer minecraftServer) {
		this.server = minecraftServer;
	}

	@Nullable
	public CommandFunction getFunction(Identifier identifier) {
		return (CommandFunction)this.idMap.get(identifier);
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

	@Override
	public void tick() {
		this.server.getProfiler().begin(TICK_FUNCTION::toString);

		for (CommandFunction commandFunction : this.tickFunctions) {
			this.method_12904(commandFunction, this.method_12899());
		}

		this.server.getProfiler().end();
		if (this.field_13422) {
			this.field_13422 = false;
			Collection<CommandFunction> collection = this.method_12901().getOrCreate(LOAD_FUNCTION).values();
			this.server.getProfiler().begin(LOAD_FUNCTION::toString);

			for (CommandFunction commandFunction2 : collection) {
				this.method_12904(commandFunction2, this.method_12899());
			}

			this.server.getProfiler().end();
		}
	}

	public int method_12904(CommandFunction commandFunction, ServerCommandSource serverCommandSource) {
		int i = this.getMaxCommandChainLength();
		if (this.field_13411) {
			if (this.field_13413.size() < i) {
				this.field_13413.addFirst(new CommandFunctionManager.class_2992(this, serverCommandSource, new CommandFunction.class_2162(commandFunction)));
			}

			return 0;
		} else {
			int var16;
			try {
				this.field_13411 = true;
				int j = 0;
				CommandFunction.class_2161[] lvs = commandFunction.method_9193();

				for (int k = lvs.length - 1; k >= 0; k--) {
					this.field_13413.push(new CommandFunctionManager.class_2992(this, serverCommandSource, lvs[k]));
				}

				do {
					if (this.field_13413.isEmpty()) {
						return j;
					}

					try {
						CommandFunctionManager.class_2992 lv = (CommandFunctionManager.class_2992)this.field_13413.removeFirst();
						this.server.getProfiler().begin(lv::toString);
						lv.method_12914(this.field_13413, i);
					} finally {
						this.server.getProfiler().end();
					}
				} while (++j < i);

				var16 = j;
			} finally {
				this.field_13413.clear();
				this.field_13411 = false;
			}

			return var16;
		}
	}

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		this.idMap.clear();
		this.tickFunctions.clear();
		this.field_13416.clear();
		Collection<Identifier> collection = resourceManager.findResources("functions", stringx -> stringx.endsWith(".mcfunction"));
		List<CompletableFuture<CommandFunction>> list = Lists.<CompletableFuture<CommandFunction>>newArrayList();

		for (Identifier identifier : collection) {
			String string = identifier.getPath();
			Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(PATH_PREFIX_LENGTH, string.length() - EXTENSION_LENGTH));
			list.add(
				CompletableFuture.supplyAsync(() -> method_12906(resourceManager, identifier), ResourceImpl.RESOURCE_IO_EXECUTOR)
					.thenApplyAsync(listx -> CommandFunction.method_9195(identifier2, this, listx))
					.handle((commandFunction, throwable) -> this.load(commandFunction, throwable, identifier))
			);
		}

		CompletableFuture.allOf((CompletableFuture[])list.toArray(new CompletableFuture[0])).join();
		if (!this.idMap.isEmpty()) {
			LOGGER.info("Loaded {} custom command functions", this.idMap.size());
		}

		this.field_13416.load(resourceManager);
		this.tickFunctions.addAll(this.field_13416.getOrCreate(TICK_FUNCTION).values());
		this.field_13422 = true;
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

	private static List<String> method_12906(ResourceManager resourceManager, Identifier identifier) {
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

	public ServerCommandSource method_12899() {
		return this.server.method_3739().withLevel(2).withSilent();
	}

	public TagContainer<CommandFunction> method_12901() {
		return this.field_13416;
	}

	public static class class_2992 {
		private final CommandFunctionManager manager;
		private final ServerCommandSource field_13424;
		private final CommandFunction.class_2161 field_13425;

		public class_2992(CommandFunctionManager commandFunctionManager, ServerCommandSource serverCommandSource, CommandFunction.class_2161 arg) {
			this.manager = commandFunctionManager;
			this.field_13424 = serverCommandSource;
			this.field_13425 = arg;
		}

		public void method_12914(ArrayDeque<CommandFunctionManager.class_2992> arrayDeque, int i) {
			try {
				this.field_13425.method_9198(this.manager, this.field_13424, arrayDeque, i);
			} catch (Throwable var4) {
			}
		}

		public String toString() {
			return this.field_13425.toString();
		}
	}
}
