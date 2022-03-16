/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceRef;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
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
public class FunctionLoader
implements ResourceReloader {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String EXTENSION = ".mcfunction";
    private static final int PATH_PREFIX_LENGTH = "functions/".length();
    private static final int EXTENSION_LENGTH = ".mcfunction".length();
    private volatile Map<Identifier, CommandFunction> functions = ImmutableMap.of();
    private final TagGroupLoader<CommandFunction> tagLoader = new TagGroupLoader(this::get, "tags/functions");
    private volatile Map<Identifier, Tag<CommandFunction>> tags = Map.of();
    private final int level;
    private final CommandDispatcher<ServerCommandSource> commandDispatcher;

    public Optional<CommandFunction> get(Identifier id) {
        return Optional.ofNullable(this.functions.get(id));
    }

    public Map<Identifier, CommandFunction> getFunctions() {
        return this.functions;
    }

    public Tag<CommandFunction> getTagOrEmpty(Identifier id) {
        return this.tags.getOrDefault(id, Tag.empty());
    }

    public Iterable<Identifier> getTags() {
        return this.tags.keySet();
    }

    public FunctionLoader(int level, CommandDispatcher<ServerCommandSource> commandDispatcher) {
        this.level = level;
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        CompletableFuture<Map> completableFuture = CompletableFuture.supplyAsync(() -> this.tagLoader.loadTags(manager), prepareExecutor);
        CompletionStage completableFuture2 = CompletableFuture.supplyAsync(() -> manager.findResources("functions", identifier -> identifier.getPath().endsWith(EXTENSION)), prepareExecutor).thenCompose(map -> {
            HashMap<Identifier, CompletableFuture<CommandFunction>> map2 = Maps.newHashMap();
            ServerCommandSource serverCommandSource = new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, null, this.level, "", LiteralText.EMPTY, null, null);
            for (Map.Entry entry : map.entrySet()) {
                Identifier identifier = (Identifier)entry.getKey();
                String string = identifier.getPath();
                Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(PATH_PREFIX_LENGTH, string.length() - EXTENSION_LENGTH));
                map2.put(identifier2, CompletableFuture.supplyAsync(() -> {
                    List<String> list = FunctionLoader.readLines((ResourceRef)entry.getValue());
                    return CommandFunction.create(identifier2, this.commandDispatcher, serverCommandSource, list);
                }, prepareExecutor));
            }
            CompletableFuture[] completableFutures = map2.values().toArray(new CompletableFuture[0]);
            return CompletableFuture.allOf(completableFutures).handle((unused, ex) -> map2);
        });
        return ((CompletableFuture)((CompletableFuture)completableFuture.thenCombine(completableFuture2, Pair::of)).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(intermediate -> {
            Map map = (Map)intermediate.getSecond();
            ImmutableMap.Builder builder = ImmutableMap.builder();
            map.forEach((id, functionFuture) -> ((CompletableFuture)functionFuture.handle((function, ex) -> {
                if (ex != null) {
                    LOGGER.error("Failed to load function {}", id, ex);
                } else {
                    builder.put(id, function);
                }
                return null;
            })).join());
            this.functions = builder.build();
            this.tags = this.tagLoader.buildGroup((Map)intermediate.getFirst());
        }, applyExecutor);
    }

    private static List<String> readLines(ResourceRef resourceRef) {
        List<String> list;
        block8: {
            Resource resource = resourceRef.open();
            try {
                list = IOUtils.readLines(resource.getInputStream(), StandardCharsets.UTF_8);
                if (resource == null) break block8;
            } catch (Throwable throwable) {
                try {
                    if (resource != null) {
                        try {
                            resource.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                } catch (IOException iOException) {
                    throw new CompletionException(iOException);
                }
            }
            resource.close();
        }
        return list;
    }
}

