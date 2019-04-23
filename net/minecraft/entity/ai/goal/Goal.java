/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.goal;

import java.util.EnumSet;

public abstract class Goal {
    private final EnumSet<Control> controls = EnumSet.noneOf(Control.class);

    public abstract boolean canStart();

    public boolean shouldContinue() {
        return this.canStart();
    }

    public boolean canStop() {
        return true;
    }

    public void start() {
    }

    public void stop() {
    }

    public void tick() {
    }

    public void setControls(EnumSet<Control> enumSet) {
        this.controls.clear();
        this.controls.addAll(enumSet);
    }

    public EnumSet<Control> getControls() {
        return this.controls;
    }

    public static enum Control {
        MOVE,
        LOOK,
        JUMP,
        TARGET;

    }
}

