/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundInstance;

@Environment(value=EnvType.CLIENT)
public interface TickableSoundInstance
extends SoundInstance {
    public boolean isDone();

    public void tick();
}

