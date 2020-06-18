package net.minecraft.server.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagContainer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionLoader implements ResourceReloadListener {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final int PATH_PREFIX_LENGTH = "functions/".length();
	private static final int PATH_SUFFIX_LENGTH = ".mcfunction".length();
	private volatile Map<Identifier, CommandFunction> functions = ImmutableMap.of();
	private final TagContainer<CommandFunction> tags = new TagContainer<>(this::get, "tags/functions", "function");
	private final int level;
	private final CommandDispatcher<ServerCommandSource> commandDispatcher;

	public Optional<CommandFunction> get(Identifier id) {
		return Optional.ofNullable(this.functions.get(id));
	}

	public Map<Identifier, CommandFunction> getFunctions() {
		return this.functions;
	}

	public TagContainer<CommandFunction> getTags() {
		return this.tags;
	}

	public Tag<CommandFunction> getOrCreateTag(Identifier id) {
		return this.tags.getOrCreate(id);
	}

	public FunctionLoader(int level, CommandDispatcher<ServerCommandSource> commandDispatcher) {
		this.level = level;
		this.commandDispatcher = commandDispatcher;
	}

	@Override
	public CompletableFuture<Void> reload(
		ResourceReloadListener.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		CompletableFuture<Map<Identifier, Tag.Builder>> completableFuture = this.tags.prepareReload(manager, prepareExecutor);
		CompletableFuture<Map<Identifier, CompletableFuture<CommandFunction>>> completableFuture2 = CompletableFuture.supplyAsync(
				() -> manager.findResources("functions", string -> string.endsWith(".mcfunction")), prepareExecutor
			)
			.thenCompose(
				collection -> {
					Map<Identifier, CompletableFuture<CommandFunction>> map = Maps.<Identifier, CompletableFuture<CommandFunction>>newHashMap();
					ServerCommandSource serverCommandSource = new ServerCommandSource(
						CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, null, this.level, "", LiteralText.EMPTY, null, null
					);

					for (Identifier identifier : collection) {
						String string = identifier.getPath();
						Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(PATH_PREFIX_LENGTH, string.length() - PATH_SUFFIX_LENGTH));
						map.put(identifier2, CompletableFuture.supplyAsync(() -> {
							List<String> list = readLines(manager, identifier);
							return CommandFunction.create(identifier2, this.commandDispatcher, serverCommandSource, list);
						}, prepareExecutor));
					}

					CompletableFuture<?>[] completableFutures = (CompletableFuture<?>[])map.values().toArray(new CompletableFuture[0]);
					return CompletableFuture.allOf(completableFutures).handle((void_, throwable) -> map);
				}
			);
		return completableFuture.thenCombine(completableFuture2, Pair::of).thenCompose(synchronizer::whenPrepared).thenAcceptAsync(pair -> {
			Map<Identifier, CompletableFuture<CommandFunction>> map = (Map<Identifier, CompletableFuture<CommandFunction>>)pair.getSecond();
			Builder<Identifier, CommandFunction> builder = ImmutableMap.builder();
			map.forEach((identifier, completableFuturex) -> completableFuturex.handle((commandFunction, throwable) -> {
					if (throwable != null) {
						LOGGER.error("Failed to load function {}", identifier, throwable);
					} else {
						builder.put(identifier, commandFunction);
					}

					return null;
				}).join());
			this.functions = builder.build();
			this.tags.applyReload((Map<Identifier, Tag.Builder>)pair.getFirst());
		}, applyExecutor);
	}

	private static List<String> readLines(ResourceManager resourceManager, Identifier id) {
		try {
			Resource resource = resourceManager.getResource(id);
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
}
