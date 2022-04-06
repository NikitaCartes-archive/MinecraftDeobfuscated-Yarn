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
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.AbstractRandom;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class WeightedSoundSet
implements SoundContainer<Sound> {
    private final List<SoundContainer<Sound>> sounds = Lists.newArrayList();
    private final AbstractRandom random = AbstractRandom.createAtomic();
    private final Identifier id;
    @Nullable
    private final Text subtitle;

    public WeightedSoundSet(Identifier id, @Nullable String subtitle) {
        this.id = id;
        this.subtitle = subtitle == null ? null : new TranslatableText(subtitle);
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
    public Sound getSound(AbstractRandom abstractRandom) {
        int i = this.getWeight();
        if (this.sounds.isEmpty() || i == 0) {
            return SoundManager.MISSING_SOUND;
        }
        int j = abstractRandom.nextInt(i);
        for (SoundContainer<Sound> soundContainer : this.sounds) {
            if ((j -= soundContainer.getWeight()) >= 0) continue;
            return soundContainer.getSound(abstractRandom);
        }
        return SoundManager.MISSING_SOUND;
    }

    public void add(SoundContainer<Sound> container) {
        this.sounds.add(container);
    }

    public Identifier getId() {
        return this.id;
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
    public /* synthetic */ Object getSound(AbstractRandom random) {
        return this.getSound(random);
    }
}

