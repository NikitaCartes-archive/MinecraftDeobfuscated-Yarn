/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class SoundEntry {
    private final List<Sound> sounds;
    private final boolean replace;
    private final String subtitle;

    public SoundEntry(List<Sound> list, boolean bl, String string) {
        this.sounds = list;
        this.replace = bl;
        this.subtitle = string;
    }

    public List<Sound> getSounds() {
        return this.sounds;
    }

    public boolean canReplace() {
        return this.replace;
    }

    @Nullable
    public String getSubtitle() {
        return this.subtitle;
    }
}

