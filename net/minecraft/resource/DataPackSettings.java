/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public class DataPackSettings {
    public static final DataPackSettings SAFE_MODE = new DataPackSettings(ImmutableList.of("vanilla"), ImmutableList.of());
    public static final Codec<DataPackSettings> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.STRING.listOf().fieldOf("Enabled")).forGetter(settings -> settings.enabled), ((MapCodec)Codec.STRING.listOf().fieldOf("Disabled")).forGetter(settings -> settings.disabled)).apply((Applicative<DataPackSettings, ?>)instance, DataPackSettings::new));
    private final List<String> enabled;
    private final List<String> disabled;

    public DataPackSettings(List<String> enabled, List<String> disabled) {
        this.enabled = ImmutableList.copyOf(enabled);
        this.disabled = ImmutableList.copyOf(disabled);
    }

    public List<String> getEnabled() {
        return this.enabled;
    }

    public List<String> getDisabled() {
        return this.disabled;
    }
}

