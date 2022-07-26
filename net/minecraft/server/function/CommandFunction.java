/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.server.function;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

public class CommandFunction {
    private final Element[] elements;
    final Identifier id;

    public CommandFunction(Identifier id, Element[] elements) {
        this.id = id;
        this.elements = elements;
    }

    public Identifier getId() {
        return this.id;
    }

    public Element[] getElements() {
        return this.elements;
    }

    /**
     * Parses a function in the context of {@code source}.
     * 
     * <p>Any syntax errors, such as improper comment lines or unknown commands, will be thrown at this point.
     * 
     * @param lines the raw lines (including comments) read from a function file
     */
    public static CommandFunction create(Identifier id, CommandDispatcher<ServerCommandSource> dispatcher, ServerCommandSource source, List<String> lines) {
        ArrayList<CommandElement> list = Lists.newArrayListWithCapacity(lines.size());
        for (int i = 0; i < lines.size(); ++i) {
            int j = i + 1;
            String string = lines.get(i).trim();
            StringReader stringReader = new StringReader(string);
            if (!stringReader.canRead() || stringReader.peek() == '#') continue;
            if (stringReader.peek() == '/') {
                stringReader.skip();
                if (stringReader.peek() == '/') {
                    throw new IllegalArgumentException("Unknown or invalid command '" + string + "' on line " + j + " (if you intended to make a comment, use '#' not '//')");
                }
                String string2 = stringReader.readUnquotedString();
                throw new IllegalArgumentException("Unknown or invalid command '" + string + "' on line " + j + " (did you mean '" + string2 + "'? Do not use a preceding forwards slash.)");
            }
            try {
                ParseResults<ServerCommandSource> parseResults = dispatcher.parse(stringReader, source);
                if (parseResults.getReader().canRead()) {
                    throw CommandManager.getException(parseResults);
                }
                list.add(new CommandElement(parseResults));
                continue;
            } catch (CommandSyntaxException commandSyntaxException) {
                throw new IllegalArgumentException("Whilst parsing command on line " + j + ": " + commandSyntaxException.getMessage());
            }
        }
        return new CommandFunction(id, list.toArray(new Element[0]));
    }

    @FunctionalInterface
    public static interface Element {
        public void execute(CommandFunctionManager var1, ServerCommandSource var2, Deque<CommandFunctionManager.Entry> var3, int var4, int var5, @Nullable CommandFunctionManager.Tracer var6) throws CommandSyntaxException;
    }

    public static class CommandElement
    implements Element {
        private final ParseResults<ServerCommandSource> parsed;

        public CommandElement(ParseResults<ServerCommandSource> parsed) {
            this.parsed = parsed;
        }

        @Override
        public void execute(CommandFunctionManager commandFunctionManager, ServerCommandSource serverCommandSource, Deque<CommandFunctionManager.Entry> deque, int i, int j, @Nullable CommandFunctionManager.Tracer tracer) throws CommandSyntaxException {
            if (tracer != null) {
                String string = this.parsed.getReader().getString();
                tracer.traceCommandStart(j, string);
                int k = this.execute(commandFunctionManager, serverCommandSource);
                tracer.traceCommandEnd(j, string, k);
            } else {
                this.execute(commandFunctionManager, serverCommandSource);
            }
        }

        private int execute(CommandFunctionManager manager, ServerCommandSource source) throws CommandSyntaxException {
            return manager.getDispatcher().execute(CommandManager.withCommandSource(this.parsed, serverCommandSource2 -> source));
        }

        public String toString() {
            return this.parsed.getReader().getString();
        }
    }

    public static class LazyContainer {
        public static final LazyContainer EMPTY = new LazyContainer((Identifier)null);
        @Nullable
        private final Identifier id;
        private boolean initialized;
        private Optional<CommandFunction> function = Optional.empty();

        public LazyContainer(@Nullable Identifier id) {
            this.id = id;
        }

        public LazyContainer(CommandFunction function) {
            this.initialized = true;
            this.id = null;
            this.function = Optional.of(function);
        }

        public Optional<CommandFunction> get(CommandFunctionManager manager) {
            if (!this.initialized) {
                if (this.id != null) {
                    this.function = manager.getFunction(this.id);
                }
                this.initialized = true;
            }
            return this.function;
        }

        @Nullable
        public Identifier getId() {
            return this.function.map(f -> f.id).orElse(this.id);
        }
    }

    public static class FunctionElement
    implements Element {
        private final LazyContainer function;

        public FunctionElement(CommandFunction function) {
            this.function = new LazyContainer(function);
        }

        @Override
        public void execute(CommandFunctionManager commandFunctionManager, ServerCommandSource serverCommandSource, Deque<CommandFunctionManager.Entry> deque, int i, int j, @Nullable CommandFunctionManager.Tracer tracer) {
            Util.ifPresentOrElse(this.function.get(commandFunctionManager), f -> {
                Element[] elements = f.getElements();
                if (tracer != null) {
                    tracer.traceFunctionCall(j, f.getId(), elements.length);
                }
                int k = i - deque.size();
                int l = Math.min(elements.length, k);
                for (int m = l - 1; m >= 0; --m) {
                    deque.addFirst(new CommandFunctionManager.Entry(serverCommandSource, j + 1, elements[m]));
                }
            }, () -> {
                if (tracer != null) {
                    tracer.traceFunctionCall(j, this.function.getId(), -1);
                }
            });
        }

        public String toString() {
            return "function " + this.function.getId();
        }
    }
}

