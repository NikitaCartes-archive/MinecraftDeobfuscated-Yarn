/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture.atlas;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.metadata.BlockEntry;

@Environment(value=EnvType.CLIENT)
public class FilterAtlasSource
implements AtlasSource {
    public static final Codec<FilterAtlasSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)BlockEntry.CODEC.fieldOf("pattern")).forGetter(filterAtlasSource -> filterAtlasSource.pattern)).apply((Applicative<FilterAtlasSource, ?>)instance, FilterAtlasSource::new));
    private final BlockEntry pattern;

    public FilterAtlasSource(BlockEntry pattern) {
        this.pattern = pattern;
    }

    @Override
    public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
        regions.removeIf(this.pattern.getIdentifierPredicate());
    }

    @Override
    public AtlasSourceType getType() {
        return AtlasSourceManager.FILTER;
    }
}

