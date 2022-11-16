/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.texture.atlas;

import com.mojang.datafixers.kinds.Applicative;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.atlas.AtlasSource;
import net.minecraft.client.texture.atlas.AtlasSourceManager;
import net.minecraft.client.texture.atlas.AtlasSourceType;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public class SingleAtlasSource
implements AtlasSource {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Codec<SingleAtlasSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Identifier.CODEC.fieldOf("resource")).forGetter(singleAtlasSource -> singleAtlasSource.resource), Identifier.CODEC.optionalFieldOf("sprite").forGetter(singleAtlasSource -> singleAtlasSource.sprite)).apply((Applicative<SingleAtlasSource, ?>)instance, SingleAtlasSource::new));
    private final ResourceFinder FINDER = new ResourceFinder("textures", ".png");
    private final Identifier resource;
    private final Optional<Identifier> sprite;

    public SingleAtlasSource(Identifier resource, Optional<Identifier> sprite) {
        this.resource = resource;
        this.sprite = sprite;
    }

    @Override
    public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
        Identifier identifier = this.FINDER.toResourcePath(this.resource);
        Optional<Resource> optional = resourceManager.getResource(identifier);
        if (optional.isPresent()) {
            regions.add(this.sprite.orElse(this.resource), optional.get());
        } else {
            LOGGER.warn("Missing sprite: {}", (Object)identifier);
        }
    }

    @Override
    public AtlasSourceType getType() {
        return AtlasSourceManager.SINGLE;
    }
}

