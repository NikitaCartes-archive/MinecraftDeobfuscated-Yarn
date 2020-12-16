/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.function;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;

public class CommandFunctionManager {
    private static final Identifier TICK_FUNCTION = new Identifier("tick");
    private static final Identifier LOAD_FUNCTION = new Identifier("load");
    private final MinecraftServer server;
    private boolean executing;
    private final ArrayDeque<Entry> chain = new ArrayDeque();
    private final List<Entry> pending = Lists.newArrayList();
    private final List<CommandFunction> tickFunctions = Lists.newArrayList();
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
            List<CommandFunction> collection = this.loader.getTags().getTagOrEmpty(LOAD_FUNCTION).values();
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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int execute(CommandFunction function, ServerCommandSource source) {
        int i = this.getMaxCommandChainLength();
        if (this.executing) {
            if (this.chain.size() + this.pending.size() < i) {
                this.pending.add(new Entry(this, source, new CommandFunction.FunctionElement(function)));
            }
            return 0;
        }
        try {
            this.executing = true;
            int j = 0;
            CommandFunction.Element[] elements = function.getElements();
            for (int k = elements.length - 1; k >= 0; --k) {
                this.chain.push(new Entry(this, source, elements[k]));
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
        private final CommandFunctionManager manager;
        private final ServerCommandSource source;
        private final CommandFunction.Element element;

        public Entry(CommandFunctionManager manager, ServerCommandSource source, CommandFunction.Element element) {
            this.manager = manager;
            this.source = source;
            this.element = element;
        }

        public void execute(ArrayDeque<Entry> stack, int maxChainLength) {
            try {
                this.element.execute(this.manager, this.source, stack, maxChainLength);
            } catch (Throwable throwable) {
                // empty catch block
            }
        }

        public String toString() {
            return this.element.toString();
        }
    }
}

