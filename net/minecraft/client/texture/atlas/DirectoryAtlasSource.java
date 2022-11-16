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
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public class DirectoryAtlasSource
implements AtlasSource {
    public static final Codec<DirectoryAtlasSource> CODEC = RecordCodecBuilder.create(instance -> instance.group(((MapCodec)Codec.STRING.fieldOf("source")).forGetter(directoryAtlasSource -> directoryAtlasSource.source), ((MapCodec)Codec.STRING.fieldOf("prefix")).forGetter(directoryAtlasSource -> directoryAtlasSource.prefix)).apply((Applicative<DirectoryAtlasSource, ?>)instance, DirectoryAtlasSource::new));
    private final String source;
    private final String prefix;

    public DirectoryAtlasSource(String source, String prefix) {
        this.source = source;
        this.prefix = prefix;
    }

    @Override
    public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
        ResourceFinder resourceFinder = new ResourceFinder("textures/" + this.source, ".png");
        resourceFinder.findResources(resourceManager).forEach((identifier, resource) -> {
            Identifier identifier2 = resourceFinder.toResourceId((Identifier)identifier).withPrefixedPath(this.prefix);
            regions.add(identifier2, (Resource)resource);
        });
    }

    @Override
    public AtlasSourceType getType() {
        return AtlasSourceManager.DIRECTORY;
    }
}

