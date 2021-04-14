/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.function;

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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;

public class CommandFunctionManager {
    private static final Text field_33542 = new TranslatableText("commands.debug.function.noRecursion");
    private static final Identifier TICK_FUNCTION = new Identifier("tick");
    private static final Identifier LOAD_FUNCTION = new Identifier("load");
    private final MinecraftServer server;
    @Nullable
    private class_6345 field_33543;
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

    public int execute(CommandFunction function, ServerCommandSource source) {
        return this.method_36341(function, source, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int method_36341(CommandFunction commandFunction, ServerCommandSource serverCommandSource, @Nullable class_6346 arg) {
        if (this.field_33543 != null) {
            if (arg != null) {
                this.field_33543.method_36344(field_33542.getString());
                return 0;
            }
            this.field_33543.method_36343(commandFunction, serverCommandSource);
            return 0;
        }
        try {
            this.field_33543 = new class_6345(arg);
            int n = this.field_33543.method_36346(commandFunction, serverCommandSource);
            return n;
        } finally {
            this.field_33543 = null;
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

    public static interface class_6346 {
        public void method_36349(int var1, String var2);

        public void method_36350(int var1, String var2, int var3);

        public void method_36352(int var1, String var2);

        public void method_36351(int var1, Identifier var2, int var3);
    }

    class class_6345 {
        private int field_33545;
        @Nullable
        private final class_6346 field_33546;
        private final Deque<Entry> field_33547 = Queues.newArrayDeque();
        private final List<Entry> field_33548 = Lists.newArrayList();

        private class_6345(class_6346 arg) {
            this.field_33546 = arg;
        }

        private void method_36343(CommandFunction commandFunction, ServerCommandSource serverCommandSource) {
            int i = CommandFunctionManager.this.getMaxCommandChainLength();
            if (this.field_33547.size() + this.field_33548.size() < i) {
                this.field_33548.add(new Entry(serverCommandSource, this.field_33545, new CommandFunction.FunctionElement(commandFunction)));
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private int method_36346(CommandFunction commandFunction, ServerCommandSource serverCommandSource) {
            int i = CommandFunctionManager.this.getMaxCommandChainLength();
            int j = 0;
            CommandFunction.Element[] elements = commandFunction.getElements();
            for (int k = elements.length - 1; k >= 0; --k) {
                this.field_33547.push(new Entry(serverCommandSource, 0, elements[k]));
            }
            while (!this.field_33547.isEmpty()) {
                try {
                    Entry entry = this.field_33547.removeFirst();
                    CommandFunctionManager.this.server.getProfiler().push(entry::toString);
                    this.field_33545 = entry.field_33549;
                    entry.execute(CommandFunctionManager.this, this.field_33547, i, this.field_33546);
                    if (!this.field_33548.isEmpty()) {
                        Lists.reverse(this.field_33548).forEach(this.field_33547::addFirst);
                        this.field_33548.clear();
                    }
                } finally {
                    CommandFunctionManager.this.server.getProfiler().pop();
                }
                if (++j < i) continue;
                return j;
            }
            return j;
        }

        public void method_36344(String string) {
            if (this.field_33546 != null) {
                this.field_33546.method_36352(this.field_33545, string);
            }
        }
    }

    public static class Entry {
        private final ServerCommandSource source;
        private final int field_33549;
        private final CommandFunction.Element element;

        public Entry(ServerCommandSource serverCommandSource, int i, CommandFunction.Element element) {
            this.source = serverCommandSource;
            this.field_33549 = i;
            this.element = element;
        }

        public void execute(CommandFunctionManager commandFunctionManager, Deque<Entry> deque, int i, @Nullable class_6346 arg) {
            block4: {
                try {
                    this.element.execute(commandFunctionManager, this.source, deque, i, this.field_33549, arg);
                } catch (CommandSyntaxException commandSyntaxException) {
                    if (arg != null) {
                        arg.method_36352(this.field_33549, commandSyntaxException.getRawMessage().getString());
                    }
                } catch (Exception exception) {
                    if (arg == null) break block4;
                    arg.method_36352(this.field_33549, exception.getMessage());
                }
            }
        }

        public String toString() {
            return this.element.toString();
        }
    }
}

