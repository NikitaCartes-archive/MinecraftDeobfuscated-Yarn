/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.function.FunctionLoader;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

/**
 * The command function manager implements execution of functions, like that from
 * the {@code function} command.
 */
public class CommandFunctionManager {
    /**
     * A localized piece of text indicating that calling the debug command to debug
     * functions from within a function is not supported.
     */
    private static final Text NO_TRACE_IN_FUNCTION_TEXT = Text.translatable("commands.debug.function.noRecursion");
    private static final Identifier TICK_TAG_ID = new Identifier("tick");
    private static final Identifier LOAD_TAG_ID = new Identifier("load");
    final MinecraftServer server;
    /**
     * The active execution within this manager.
     */
    @Nullable
    private Execution execution;
    /**
     * A list of {@code minecraft:tick} tag functions to run on every tick. Set up on
     * load, this is more efficient than polling the tag from the {@link #loader}
     * every tick.
     */
    private List<CommandFunction> tickFunctions = ImmutableList.of();
    /**
     * Whether this command function manager has just {@linkplain #load(FunctionLoader)
     * loaded} and should run all functions in the {@code minecraft:load} function tag.
     */
    private boolean justLoaded;
    /**
     * The source of functions for this command function manager.
     */
    private FunctionLoader loader;

    public CommandFunctionManager(MinecraftServer server, FunctionLoader loader) {
        this.server = server;
        this.loader = loader;
        this.load(loader);
    }

    public int getMaxCommandChainLength() {
        return this.server.getGameRules().getInt(GameRules.MAX_COMMAND_CHAIN_LENGTH);
    }

    public CommandDispatcher<ServerCommandSource> getDispatcher() {
        return this.server.getCommandManager().getDispatcher();
    }

    public void tick() {
        this.executeAll(this.tickFunctions, TICK_TAG_ID);
        if (this.justLoaded) {
            this.justLoaded = false;
            List<CommandFunction> collection = this.loader.getTagOrEmpty(LOAD_TAG_ID).values();
            this.executeAll(collection, LOAD_TAG_ID);
        }
    }

    private void executeAll(Collection<CommandFunction> functions, Identifier label) {
        this.server.getProfiler().push(label::toString);
        for (CommandFunction commandFunction : functions) {
            this.execute(commandFunction, this.getScheduledCommandSource());
        }
        this.server.getProfiler().pop();
    }

    /**
     * Executes a function.
     * 
     * <p>This is same as calling {@link #execute(CommandFunction, ServerCommandSource,
     * Tracer) execute(function, source, null)}.
     * 
     * @return the command output value
     * @see #execute(CommandFunction, ServerCommandSource, Tracer)
     * 
     * @param function the function
     * @param source the command source to execute with
     */
    public int execute(CommandFunction function, ServerCommandSource source) {
        return this.execute(function, source, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    /**
     * Executes a function. This may have two cases: new or recursive.
     * 
     * <p>In a new execution, the {@link #execution execution == null}, and a custom
     * {@code tracer} can be specified. The return value indicates the number of
     * commands and nested functions ran.
     * 
     * <p>In a recursive execution, {@link #execution execution != null}. It is
     * required that {@code tracer == null}, or the execution reports an error and is
     * skipped. The return value is {@code 0}.
     * 
     * @return a non-zero value for a new execution, or {@code 0} for a recursive
     * execution
     * @see #execute(CommandFunction, ServerCommandSource)
     * 
     * @param function the function
     * @param tracer a tracer for a non-recursive function execution
     * @param source the command source to execute with
     */
    public int execute(CommandFunction function, ServerCommandSource source, @Nullable Tracer tracer) {
        if (this.execution != null) {
            if (tracer != null) {
                this.execution.reportError(NO_TRACE_IN_FUNCTION_TEXT.getString());
                return 0;
            }
            this.execution.recursiveRun(function, source);
            return 0;
        }
        try {
            this.execution = new Execution(tracer);
            int n = this.execution.run(function, source);
            return n;
        } finally {
            this.execution = null;
        }
    }

    /**
     * Sets the functions that this command function manager will use in executions.
     * 
     * @param loader the new loader functions will be taken from
     */
    public void setFunctions(FunctionLoader loader) {
        this.loader = loader;
        this.load(loader);
    }

    private void load(FunctionLoader loader) {
        this.tickFunctions = ImmutableList.copyOf(loader.getTagOrEmpty(TICK_TAG_ID).values());
        this.justLoaded = true;
    }

    /**
     * {@return the command source to execute scheduled functions} Scheduled functions
     * are those from the {@code /schedule} command and those from the {@code
     * minecraft:tick} tag.
     * 
     * <p>This command source {@linkplain ServerCommandSource#hasPermissionLevel(int)
     * has permission level 2} and is {@linkplain ServerCommandSource#withSilent()
     * silent} compared to the server's {@linkplain MinecraftServer#getCommandSource()
     * command source}.
     */
    public ServerCommandSource getScheduledCommandSource() {
        return this.server.getCommandSource().withLevel(2).withSilent();
    }

    public Optional<CommandFunction> getFunction(Identifier id) {
        return this.loader.get(id);
    }

    public Tag<CommandFunction> getTag(Identifier id) {
        return this.loader.getTagOrEmpty(id);
    }

    public Iterable<Identifier> getAllFunctions() {
        return this.loader.getFunctions().keySet();
    }

    public Iterable<Identifier> getFunctionTags() {
        return this.loader.getTags();
    }

    public static interface Tracer {
        public void traceCommandStart(int var1, String var2);

        public void traceCommandEnd(int var1, String var2, int var3);

        public void traceError(int var1, String var2);

        public void traceFunctionCall(int var1, Identifier var2, int var3);
    }

    class Execution {
        private int depth;
        @Nullable
        private final Tracer tracer;
        private final Deque<Entry> queue = Queues.newArrayDeque();
        private final List<Entry> waitlist = Lists.newArrayList();

        Execution(Tracer tracer) {
            this.tracer = tracer;
        }

        void recursiveRun(CommandFunction function, ServerCommandSource source) {
            int i = CommandFunctionManager.this.getMaxCommandChainLength();
            if (this.queue.size() + this.waitlist.size() < i) {
                this.waitlist.add(new Entry(source, this.depth, new CommandFunction.FunctionElement(function)));
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        int run(CommandFunction function, ServerCommandSource source) {
            int i = CommandFunctionManager.this.getMaxCommandChainLength();
            int j = 0;
            CommandFunction.Element[] elements = function.getElements();
            for (int k = elements.length - 1; k >= 0; --k) {
                this.queue.push(new Entry(source, 0, elements[k]));
            }
            while (!this.queue.isEmpty()) {
                try {
                    Entry entry = this.queue.removeFirst();
                    CommandFunctionManager.this.server.getProfiler().push(entry::toString);
                    this.depth = entry.depth;
                    entry.execute(CommandFunctionManager.this, this.queue, i, this.tracer);
                    if (!this.waitlist.isEmpty()) {
                        Lists.reverse(this.waitlist).forEach(this.queue::addFirst);
                        this.waitlist.clear();
                    }
                } finally {
                    CommandFunctionManager.this.server.getProfiler().pop();
                }
                if (++j < i) continue;
                return j;
            }
            return j;
        }

        public void reportError(String message) {
            if (this.tracer != null) {
                this.tracer.traceError(this.depth, message);
            }
        }
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

        public void execute(CommandFunctionManager manager, Deque<Entry> entries, int maxChainLength, @Nullable Tracer tracer) {
            block4: {
                try {
                    this.element.execute(manager, this.source, entries, maxChainLength, this.depth, tracer);
                } catch (CommandSyntaxException commandSyntaxException) {
                    if (tracer != null) {
                        tracer.traceError(this.depth, commandSyntaxException.getRawMessage().getString());
                    }
                } catch (Exception exception) {
                    if (tracer == null) break block4;
                    tracer.traceError(this.depth, exception.getMessage());
                }
            }
        }

        public String toString() {
            return this.element.toString();
        }
    }
}

