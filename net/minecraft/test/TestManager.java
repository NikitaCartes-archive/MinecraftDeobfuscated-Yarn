/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import com.google.common.collect.Lists;
import java.util.Collection;
import net.minecraft.test.GameTest;

public class TestManager {
    public static final TestManager INSTANCE = new TestManager();
    private final Collection<GameTest> tests = Lists.newCopyOnWriteArrayList();

    public void start(GameTest gameTest) {
        this.tests.add(gameTest);
    }

    public void clear() {
        this.tests.clear();
    }

    public void tick() {
        this.tests.forEach(GameTest::tick);
        this.tests.removeIf(GameTest::isCompleted);
    }
}

