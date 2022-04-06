/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.util.math.random.AbstractRandom;

@Environment(value=EnvType.CLIENT)
public interface SoundContainer<T> {
    public int getWeight();

    public T getSound(AbstractRandom var1);

    public void preload(SoundSystem var1);
}

