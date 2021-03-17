/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.function;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.datafixers.util.Pair;
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
import net.minecraft.resource.ResourceReloader;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionLoader
implements ResourceReloader {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int PATH_PREFIX_LENGTH = "functions/".length();
    private static final int PATH_SUFFIX_LENGTH = ".mcfunction".length();
    private volatile Map<Identifier, CommandFunction> functions = ImmutableMap.of();
    private final TagGroupLoader<CommandFunction> tagLoader = new TagGroupLoader(this::get, "tags/functions");
    private volatile TagGroup<CommandFunction> tags = TagGroup.createEmpty();
    private final int level;
    private final CommandDispatcher<ServerCommandSource> commandDispatcher;

    public Optional<CommandFunction> get(Identifier id) {
        return Optional.ofNullable(this.functions.get(id));
    }

    public Map<Identifier, CommandFunction> getFunctions() {
        return this.functions;
    }

    public TagGroup<CommandFunction> getTags() {
        return this.tags;
    }

    public Tag<CommandFunction> getOrCreateTag(Identifier id) {
        return this.tags.getTagOrEmpty(id);
    }

    public FunctionLoader(int level, CommandDispatcher<ServerCommandSource> commandDispatcher) {
        this.level = level;
        this.commandDispatcher = commandDispatcher;
    }

    @Override
    public CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        CompletableFuture<Map> completableFuture = CompletableFuture.supplyAsync(() -> this.tagLoader.loadTags(manager), prepareExecutor);
        CompletionStage completableFuture2 = CompletableFuture.supplyAsync(() -> manager.findResources("functions", string -> string.endsWith(".mcfunction")), prepareExecutor).thenCompose(collection -> {
            HashMap<Identifier, CompletableFuture<CommandFunction>> map = Maps.newHashMap();
            ServerCommandSource serverCommandSource = new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, null, this.level, "", LiteralText.EMPTY, null, null);
            for (Identifier identifier : collection) {
                String string = identifier.getPath();
                Identifier identifier2 = new Identifier(identifier.getNamespace(), string.substring(PATH_PREFIX_LENGTH, string.length() - PATH_SUFFIX_LENGTH));
                map.put(identifier2, CompletableFuture.supplyAsync(() -> {
                    List<String> list = FunctionLoader.readLines(manager, identifier);
                    return CommandFunction.create(identifier2, this.commandDispatcher, serverCommandSource, list);
                }, prepareExecutor));
            }
            CompletableFuture[] completableFutures = map.values().toArray(new CompletableFuture[0]);
            return CompletableFuture.allOf(completableFutures).handle((void_, throwable) -> map);
        });
        return ((CompletableFuture)((CompletableFuture)completableFuture.thenCombine(completableFuture2, Pair::of)).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(pair -> {
            Map map = (Map)pair.getSecond();
            ImmutableMap.Builder builder = ImmutableMap.builder();
            map.forEach((identifier, completableFuture) -> ((CompletableFuture)completableFuture.handle((commandFunction, throwable) -> {
                if (throwable != null) {
                    LOGGER.error("Failed to load function {}", identifier, throwable);
                } else {
                    builder.put(identifier, commandFunction);
                }
                return null;
            })).join());
            this.functions = builder.build();
            this.tags = this.tagLoader.buildGroup((Map)pair.getFirst());
        }, applyExecutor);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static List<String> readLines(ResourceManager resourceManager, Identifier id) {
        try (Resource resource = resourceManager.getResource(id);){
            List<String> list = IOUtils.readLines(resource.getInputStream(), StandardCharsets.UTF_8);
            return list;
        } catch (IOException iOException) {
            throw new CompletionException(iOException);
        }
    }
}

