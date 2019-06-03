/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

public interface ProgressListener {
    public void method_15412(Text var1);

    @Environment(value=EnvType.CLIENT)
    public void method_15413(Text var1);

    public void method_15414(Text var1);

    public void progressStagePercentage(int var1);

    @Environment(value=EnvType.CLIENT)
    public void setDone();
}

