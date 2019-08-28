/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import java.util.Collection;
import java.util.function.Consumer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.test.TestFunction;
import org.jetbrains.annotations.Nullable;

public class GameTestBatch {
    private final String id;
    private final Collection<TestFunction> testFunctions;
    @Nullable
    private final Consumer<ServerWorld> worldSetter;

    public GameTestBatch(String string, Collection<TestFunction> collection, @Nullable Consumer<ServerWorld> consumer) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("A GameTestBatch must include at least one TestFunction!");
        }
        this.id = string;
        this.testFunctions = collection;
        this.worldSetter = consumer;
    }

    public String getId() {
        return this.id;
    }

    public Collection<TestFunction> getTestFunctions() {
        return this.testFunctions;
    }

    public void setWorld(ServerWorld serverWorld) {
        if (this.worldSetter != null) {
            this.worldSetter.accept(serverWorld);
        }
    }
}

