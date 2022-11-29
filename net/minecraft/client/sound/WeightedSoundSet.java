/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.sound;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundContainer;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WeightedSoundSet
implements SoundContainer<Sound> {
    private final List<SoundContainer<Sound>> sounds = Lists.newArrayList();
    @Nullable
    private final Text subtitle;

    public WeightedSoundSet(Identifier id, @Nullable String subtitle) {
        this.subtitle = subtitle == null ? null : Text.translatable(subtitle);
    }

    @Override
    public int getWeight() {
        int i = 0;
        for (SoundContainer<Sound> soundContainer : this.sounds) {
            i += soundContainer.getWeight();
        }
        return i;
    }

    @Override
    public Sound getSound(Random random) {
        int i = this.getWeight();
        if (this.sounds.isEmpty() || i == 0) {
            return SoundManager.MISSING_SOUND;
        }
        int j = random.nextInt(i);
        for (SoundContainer<Sound> soundContainer : this.sounds) {
            if ((j -= soundContainer.getWeight()) >= 0) continue;
            return soundContainer.getSound(random);
        }
        return SoundManager.MISSING_SOUND;
    }

    public void add(SoundContainer<Sound> container) {
        this.sounds.add(container);
    }

    @Nullable
    public Text getSubtitle() {
        return this.subtitle;
    }

    @Override
    public void preload(SoundSystem soundSystem) {
        for (SoundContainer<Sound> soundContainer : this.sounds) {
            soundContainer.preload(soundSystem);
        }
    }

    @Override
    public /* synthetic */ Object getSound(Random random) {
        return this.getSound(random);
    }
}

