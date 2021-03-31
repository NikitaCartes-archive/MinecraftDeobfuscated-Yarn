/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import net.minecraft.test.GameTestState;

public interface TestListener {
    public void onStarted(GameTestState var1);

    public void onPassed(GameTestState var1);

    public void onFailed(GameTestState var1);
}

