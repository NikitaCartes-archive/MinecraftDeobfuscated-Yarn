package net.minecraft.server.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
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
	public static final RegistryKey<Registry<CommandFunction<ServerCommandSource>>> FUNCTION_REGISTRY_KEY = RegistryKey.ofRegistry(
		Identifier.ofVanilla("function")
	);
	private static final ResourceFinder FINDER = new ResourceFinder(RegistryKeys.getPath(FUNCTION_REGISTRY_KEY), ".mcfunction");
	private volatile Map<Identifier, CommandFunction<ServerCommandSource>> functions = ImmutableMap.of();
	private final TagGroupLoader<CommandFunction<ServerCommandSource>> tagLoader = new TagGroupLoader<>(this::get, RegistryKeys.getTagPath(FUNCTION_REGISTRY_KEY));
	private volatile Map<Identifier, List<CommandFunction<ServerCommandSource>>> tags = Map.of();
	private final int level;
	private final CommandDispatcher<ServerCommandSource> commandDispatcher;

	public Optional<CommandFunction<ServerCommandSource>> get(Identifier id) {
		return Optional.ofNullable((CommandFunction)this.functions.get(id));
	}

	public Map<Identifier, CommandFunction<ServerCommandSource>> getFunctions() {
		return this.functions;
	}

	public List<CommandFunction<ServerCommandSource>> getTagOrEmpty(Identifier id) {
		return (List<CommandFunction<ServerCommandSource>>)this.tags.getOrDefault(id, List.of());
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
		CompletableFuture<Map<Identifier, List<TagGroupLoader.TrackedEntry>>> completableFuture = CompletableFuture.supplyAsync(
			() -> this.tagLoader.loadTags(manager), prepareExecutor
		);
		CompletableFuture<Map<Identifier, CompletableFuture<CommandFunction<ServerCommandSource>>>> completableFuture2 = CompletableFuture.supplyAsync(
				() -> FINDER.findResources(manager), prepareExecutor
			)
			.thenCompose(
				functions -> {
					Map<Identifier, CompletableFuture<CommandFunction<ServerCommandSource>>> map = Maps.<Identifier, CompletableFuture<CommandFunction<ServerCommandSource>>>newHashMap();
					ServerCommandSource serverCommandSource = new ServerCommandSource(
						CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, null, this.level, "", ScreenTexts.EMPTY, null, null
					);

					for (Entry<Identifier, Resource> entry : functions.entrySet()) {
						Identifier identifier = (Identifier)entry.getKey();
						Identifier identifier2 = FINDER.toResourceId(identifier);
						map.put(identifier2, CompletableFuture.supplyAsync(() -> {
							List<String> list = readLines((Resource)entry.getValue());
							return CommandFunction.create(identifier2, this.commandDispatcher, serverCommandSource, list);
						}, prepareExecutor));
					}

					CompletableFuture<?>[] completableFutures = (CompletableFuture<?>[])map.values().toArray(new CompletableFuture[0]);
					return CompletableFuture.allOf(completableFutures).handle((unused, ex) -> map);
				}
			);
		return completableFuture.thenCombine(completableFuture2, Pair::of)
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(
				intermediate -> {
					Map<Identifier, CompletableFuture<CommandFunction<ServerCommandSource>>> map = (Map<Identifier, CompletableFuture<CommandFunction<ServerCommandSource>>>)intermediate.getSecond();
					Builder<Identifier, CommandFunction<ServerCommandSource>> builder = ImmutableMap.builder();
					map.forEach((id, functionFuture) -> functionFuture.handle((function, ex) -> {
							if (ex != null) {
								LOGGER.error("Failed to load function {}", id, ex);
							} else {
								builder.put(id, function);
							}

							return null;
						}).join());
					this.functions = builder.build();
					this.tags = this.tagLoader.buildGroup((Map<Identifier, List<TagGroupLoader.TrackedEntry>>)intermediate.getFirst());
				},
				applyExecutor
			);
	}

	private static List<String> readLines(Resource resource) {
		try {
			BufferedReader bufferedReader = resource.getReader();

			List var2;
			try {
				var2 = bufferedReader.lines().toList();
			} catch (Throwable var5) {
				if (bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (Throwable var4) {
						var5.addSuppressed(var4);
					}
				}

				throw var5;
			}

			if (bufferedReader != null) {
				bufferedReader.close();
			}

			return var2;
		} catch (IOException var6) {
			throw new CompletionException(var6);
		}
	}
}
