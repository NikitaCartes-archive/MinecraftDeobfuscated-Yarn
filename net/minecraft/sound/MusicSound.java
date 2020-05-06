/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.sound.SoundEvent;

public class MusicSound {
    private final SoundEvent event;
    private final int field_24058;
    private final int field_24059;
    private final boolean field_24060;

    public MusicSound(SoundEvent event, int i, int j, boolean bl) {
        this.event = event;
        this.field_24058 = i;
        this.field_24059 = j;
        this.field_24060 = bl;
    }

    @Environment(value=EnvType.CLIENT)
    public SoundEvent getEvent() {
        return this.event;
    }

    @Environment(value=EnvType.CLIENT)
    public int method_27280() {
        return this.field_24058;
    }

    @Environment(value=EnvType.CLIENT)
    public int method_27281() {
        return this.field_24059;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean method_27282() {
        return this.field_24060;
    }
}

