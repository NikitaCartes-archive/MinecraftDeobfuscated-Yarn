package net.minecraft.client.util;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class SpriteIdentifier {
	private final Identifier atlas;
	private final Identifier texture;
	@Nullable
	private RenderLayer layer;

	public SpriteIdentifier(Identifier atlas, Identifier texture) {
		this.atlas = atlas;
		this.texture = texture;
	}

	public Identifier getAtlasId() {
		return this.atlas;
	}

	public Identifier getTextureId() {
		return this.texture;
	}

	public Sprite getSprite() {
		return (Sprite)MinecraftClient.getInstance().getSpriteAtlas(this.getAtlasId()).apply(this.getTextureId());
	}

	public RenderLayer getRenderLayer(Function<Identifier, RenderLayer> layerFactory) {
		if (this.layer == null) {
			this.layer = (RenderLayer)layerFactory.apply(this.atlas);
		}

		return this.layer;
	}

	public VertexConsumer getVertexConsumer(VertexConsumerProvider vertexConsumers, Function<Identifier, RenderLayer> layerFactory) {
		return this.getSprite().getTextureSpecificVertexConsumer(vertexConsumers.getBuffer(this.getRenderLayer(layerFactory)));
	}

	public VertexConsumer method_30001(VertexConsumerProvider vertexConsumerProvider, Function<Identifier, RenderLayer> function, boolean bl) {
		return this.getSprite().getTextureSpecificVertexConsumer(ItemRenderer.method_29711(vertexConsumerProvider, this.getRenderLayer(function), true, bl));
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			SpriteIdentifier spriteIdentifier = (SpriteIdentifier)object;
			return this.atlas.equals(spriteIdentifier.atlas) && this.texture.equals(spriteIdentifier.texture);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Objects.hash(new Object[]{this.atlas, this.texture});
	}

	public String toString() {
		return "Material{atlasLocation=" + this.atlas + ", texture=" + this.texture + '}';
	}
}
