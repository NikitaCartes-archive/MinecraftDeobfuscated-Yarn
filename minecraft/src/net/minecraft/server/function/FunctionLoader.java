package net.minecraft.server.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

/**
 * The function loader holds the functions and function tags for a {@link
 * CommandFunctionManager} to use. In the reloads, it loads the tags in one
 * completable future and each function in a completable future for all functions.
 * 
 * <p>The functions are stored in {@code .mcfunction} files; each line is one
 * Minecraft command, with blank lines and contents starting with a trailing hash
 * {@code #} sign ignored.
 * 
 * <p>The function tags are ordered, unlike other tags. Each function's order in
 * the collection of functions from the tag is determined by the order it is listed
 * in the JSON files; if it appears multiple times through tag nesting, only its
 * first appearance will be considered.
 * 
 * @see CommandFunctionManager
 */
public class FunctionLoader implements ResourceReloader {
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final String EXTENSION = ".mcfunction";
	private static final int PATH_PREFIX_LENGTH = "functions/".length();
	private static final int EXTENSION_LENGTH = ".mcfunction".length();
	private volatile Map<Identifier, CommandFunction> functions = ImmutableMap.of();
	private final TagGroupLoader<CommandFunction> tagLoader = new TagGroupLoader<>(this::get, "tags/functions");
	private volatile Map<Identifier, Tag<CommandFunction>> tags = Map.of();
	private final int level;
	private final CommandDispatcher<ServerCommandSource> commandDispatcher;

	public Optional<CommandFunction> get(Identifier id) {
		return Optional.ofNullable((CommandFunction)this.functions.get(id));
	}

	public Map<Identifier, CommandFunction> getFunctions() {
		return this.functions;
	}

	public Tag<CommandFunction> getTagOrEmpty(Identifier id) {
		return (Tag<CommandFunction>)this.tags.getOrDefault(id, Tag.empty());
	}

	public Iterable<Identifier> getTags() {
		return this.tags.keySet();
	}

	public FunctionLoader(int level, CommandDispatcher<ServerCommandSource> commandDispatcher) {
		this.level = level;
		this.commandDispatcher = commandDispatcher;
	}

	@Override
	public CompletableFuture<Void> reload(
		ResourceReloader.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture = CompletableFuture.supplyAsync(() -> this.tagLoader.loadTags(manager), prepareExecutor);
		CompletableFuture<Map<Identifier, CompletableFuture<CommandFunction>>> completableFuture2 = CompletableFuture.supplyAsync(
				() -> manager.findResources("functions", path -> path.endsWith(".mcfunction")), prepareExecutor
			)
			.thenCompose(
				ids -> {
					Map<Identifier, CompletableFuture<CommandFunction>> map = Maps.<Identifier, CompletableFuture<CommandFunction>>newHashMap();
					ServerCommandSource serverCommandSource = new ServerCommandSource(
						CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, null, this.level, "", LiteralText.EMPTY, null, null
					);

					for (Identifier identifier : ids) {
						String string = identifier.getPath();
						Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(PATH_PREFIX_LENGTH, string.length() - EXTENSION_LENGTH));
						map.put(identifier2, CompletableFuture.supplyAsync(() -> {
							List<String> list = readLines(manager, identifier);
							return CommandFunction.create(identifier2, this.commandDispatcher, serverCommandSource, list);
						}, prepareExecutor));
					}

					CompletableFuture<?>[] completableFutures = (CompletableFuture<?>[])map.values().toArray(new CompletableFuture[0]);
					return CompletableFuture.allOf(completableFutures).handle((unused, ex) -> map);
				}
			);
		return completableFuture.thenCombine(completableFuture2, Pair::of).thenCompose(synchronizer::whenPrepared).thenAcceptAsync(intermediate -> {
			Map<Identifier, CompletableFuture<CommandFunction>> map = (Map<Identifier, CompletableFuture<CommandFunction>>)intermediate.getSecond();
			Builder<Identifier, CommandFunction> builder = ImmutableMap.builder();
			map.forEach((id, functionFuture) -> functionFuture.handle((function, ex) -> {
					if (ex != null) {
						LOGGER.error("Failed to load function {}", id, ex);
					} else {
						builder.put(id, function);
					}

					return null;
				}).join());
			this.functions = builder.build();
			this.tags = this.tagLoader.buildGroup((Map<Identifier, Tag.Builder>)intermediate.getFirst());
		}, applyExecutor);
	}

	private static List<String> readLines(ResourceManager resourceManager, Identifier id) {
		try {
			Resource resource = resourceManager.getResource(id);

			List var3;
			try {
				var3 = IOUtils.readLines(resource.getInputStream(), StandardCharsets.UTF_8);
			} catch (Throwable var6) {
				if (resource != null) {
					try {
						resource.close();
					} catch (Throwable var5) {
						var6.addSuppressed(var5);
					}
				}

				throw var6;
			}

			if (resource != null) {
				resource.close();
			}

			return var3;
		} catch (IOException var7) {
			throw new CompletionException(var7);
		}
	}
}
