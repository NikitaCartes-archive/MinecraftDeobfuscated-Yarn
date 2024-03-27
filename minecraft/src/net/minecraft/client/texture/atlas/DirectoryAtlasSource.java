package net.minecraft.client.texture.atlas;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceFinder;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DirectoryAtlasSource implements AtlasSource {
	public static final MapCodec<DirectoryAtlasSource> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Codec.STRING.fieldOf("source").forGetter(directoryAtlasSource -> directoryAtlasSource.source),
					Codec.STRING.fieldOf("prefix").forGetter(directoryAtlasSource -> directoryAtlasSource.prefix)
				)
				.apply(instance, DirectoryAtlasSource::new)
	);
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
			Identifier identifier2 = resourceFinder.toResourceId(identifier).withPrefixedPath(this.prefix);
			regions.add(identifier2, resource);
		});
	}

	@Override
	public AtlasSourceType getType() {
		return AtlasSourceManager.DIRECTORY;
	}
}
