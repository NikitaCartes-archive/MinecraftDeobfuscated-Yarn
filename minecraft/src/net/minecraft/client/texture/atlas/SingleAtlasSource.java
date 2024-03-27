package net.minecraft.client.texture.atlas;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

@Environment(EnvType.CLIENT)
public class SingleAtlasSource implements AtlasSource {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final MapCodec<SingleAtlasSource> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					Identifier.CODEC.fieldOf("resource").forGetter(singleAtlasSource -> singleAtlasSource.resource),
					Identifier.CODEC.optionalFieldOf("sprite").forGetter(singleAtlasSource -> singleAtlasSource.sprite)
				)
				.apply(instance, SingleAtlasSource::new)
	);
	private final Identifier resource;
	private final Optional<Identifier> sprite;

	public SingleAtlasSource(Identifier resource, Optional<Identifier> sprite) {
		this.resource = resource;
		this.sprite = sprite;
	}

	@Override
	public void load(ResourceManager resourceManager, AtlasSource.SpriteRegions regions) {
		Identifier identifier = RESOURCE_FINDER.toResourcePath(this.resource);
		Optional<Resource> optional = resourceManager.getResource(identifier);
		if (optional.isPresent()) {
			regions.add((Identifier)this.sprite.orElse(this.resource), (Resource)optional.get());
		} else {
			LOGGER.warn("Missing sprite: {}", identifier);
		}
	}

	@Override
	public AtlasSourceType getType() {
		return AtlasSourceManager.SINGLE;
	}
}
