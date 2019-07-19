/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.tag.TagContainer;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public class CommandFunctionManager
implements SynchronousResourceReloadListener {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Identifier TICK_FUNCTION = new Identifier("tick");
    private static final Identifier LOAD_FUNCTION = new Identifier("load");
    public static final int PATH_PREFIX_LENGTH = "functions/".length();
    public static final int EXTENSION_LENGTH = ".mcfunction".length();
    private final MinecraftServer server;
    private final Map<Identifier, CommandFunction> idMap = Maps.newHashMap();
    private boolean executing;
    private final ArrayDeque<Entry> chain = new ArrayDeque();
    private final List<Entry> pending = Lists.newArrayList();
    private final TagContainer<CommandFunction> tags = new TagContainer(this::getFunction, "tags/functions", true, "function");
    private final List<CommandFunction> tickFunctions = Lists.newArrayList();
    private boolean needToRunLoadFunctions;

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
        return this.server.getGameRules().getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);
    }

    public Map<Identifier, CommandFunction> getFunctions() {
        return this.idMap;
    }

    public CommandDispatcher<ServerCommandSource> getDispatcher() {
        return this.server.getCommandManager().getDispatcher();
    }

    public void tick() {
        this.server.getProfiler().push(TICK_FUNCTION::toString);
        for (CommandFunction commandFunction : this.tickFunctions) {
            this.execute(commandFunction, this.getTaggedFunctionSource());
        }
        this.server.getProfiler().pop();
        if (this.needToRunLoadFunctions) {
            this.needToRunLoadFunctions = false;
            Collection<CommandFunction> collection = this.getTags().getOrCreate(LOAD_FUNCTION).values();
            this.server.getProfiler().push(LOAD_FUNCTION::toString);
            for (CommandFunction commandFunction2 : collection) {
                this.execute(commandFunction2, this.getTaggedFunctionSource());
            }
            this.server.getProfiler().pop();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int execute(CommandFunction commandFunction, ServerCommandSource serverCommandSource) {
        int i = this.getMaxCommandChainLength();
        if (this.executing) {
            if (this.chain.size() + this.pending.size() < i) {
                this.pending.add(new Entry(this, serverCommandSource, new CommandFunction.FunctionElement(commandFunction)));
            }
            return 0;
        }
        try {
            this.executing = true;
            int j = 0;
            CommandFunction.Element[] elements = commandFunction.getElements();
            for (int k = elements.length - 1; k >= 0; --k) {
                this.chain.push(new Entry(this, serverCommandSource, elements[k]));
            }
            while (!this.chain.isEmpty()) {
                try {
                    Entry entry = this.chain.removeFirst();
                    this.server.getProfiler().push(entry::toString);
                    entry.execute(this.chain, i);
                    if (!this.pending.isEmpty()) {
                        Lists.reverse(this.pending).forEach(this.chain::addFirst);
                        this.pending.clear();
                    }
                } finally {
                    this.server.getProfiler().pop();
                }
                if (++j < i) continue;
                int n = j;
                return n;
            }
            int n = j;
            return n;
        } finally {
            this.chain.clear();
            this.pending.clear();
            this.executing = false;
        }
    }

    @Override
    public void apply(ResourceManager resourceManager) {
        this.idMap.clear();
        this.tickFunctions.clear();
        Collection<Identifier> collection = resourceManager.findResources("functions", string -> string.endsWith(".mcfunction"));
        ArrayList<CompletionStage> list2 = Lists.newArrayList();
        for (Identifier identifier : collection) {
            String string2 = identifier.getPath();
            Identifier identifier2 = new Identifier(identifier.getNamespace(), string2.substring(PATH_PREFIX_LENGTH, string2.length() - EXTENSION_LENGTH));
            list2.add(((CompletableFuture)CompletableFuture.supplyAsync(() -> CommandFunctionManager.readLines(resourceManager, identifier), ResourceImpl.RESOURCE_IO_EXECUTOR).thenApplyAsync(list -> CommandFunction.create(identifier2, this, list), this.server.getWorkerExecutor())).handle((commandFunction, throwable) -> this.load((CommandFunction)commandFunction, (Throwable)throwable, identifier)));
        }
        CompletableFuture.allOf(list2.toArray(new CompletableFuture[0])).join();
        if (!this.idMap.isEmpty()) {
            LOGGER.info("Loaded {} custom command functions", (Object)this.idMap.size());
        }
        this.tags.applyReload(this.tags.prepareReload(resourceManager, this.server.getWorkerExecutor()).join());
        this.tickFunctions.addAll(this.tags.getOrCreate(TICK_FUNCTION).values());
        this.needToRunLoadFunctions = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private CommandFunction load(CommandFunction commandFunction, @Nullable Throwable throwable, Identifier identifier) {
        if (throwable != null) {
            LOGGER.error("Couldn't load function at {}", (Object)identifier, (Object)throwable);
            return null;
        }
        Map<Identifier, CommandFunction> map = this.idMap;
        synchronized (map) {
            this.idMap.put(commandFunction.getId(), commandFunction);
        }
        return commandFunction;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static List<String> readLines(ResourceManager resourceManager, Identifier identifier) {
        try (Resource resource = resourceManager.getResource(identifier);){
            List<String> list = IOUtils.readLines(resource.getInputStream(), StandardCharsets.UTF_8);
            return list;
        } catch (IOException iOException) {
            throw new CompletionException(iOException);
        }
    }

    public ServerCommandSource getTaggedFunctionSource() {
        return this.server.getCommandSource().withLevel(2).withSilent();
    }

    public ServerCommandSource method_20796() {
        return new ServerCommandSource(CommandOutput.DUMMY, Vec3d.ZERO, Vec2f.ZERO, null, this.server.method_21714(), "", new LiteralText(""), this.server, null);
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

        public void execute(ArrayDeque<Entry> arrayDeque, int i) {
            try {
                this.element.execute(this.manager, this.source, arrayDeque, i);
            } catch (Throwable throwable) {
                // empty catch block
            }
        }

        public String toString() {
            return this.element.toString();
        }
    }
}

