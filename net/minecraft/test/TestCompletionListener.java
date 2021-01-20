/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.test;

import net.minecraft.test.GameTest;

public interface TestCompletionListener {
    public void onTestFailed(GameTest var1);

    public void onTestPassed(GameTest var1);
}

