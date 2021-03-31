/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util;

import net.minecraft.text.Text;

public interface ProgressListener {
    public void setTitle(Text var1);

    public void setTitleAndTask(Text var1);

    public void setTask(Text var1);

    public void progressStagePercentage(int var1);

    public void setDone();
}

