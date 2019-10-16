package net.minecraft.client.render;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.util.SortedMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.chunk.BlockLayeredBufferBuilderStorage;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.SystemUtil;

@Environment(EnvType.CLIENT)
public class LayeredBufferBuilderStorage {
	private final BlockLayeredBufferBuilderStorage blockBufferBuilders = new BlockLayeredBufferBuilderStorage();
	private final SortedMap<RenderLayer, BufferBuilder> bufferBuilders = SystemUtil.consume(
		new Object2ObjectLinkedOpenHashMap<>(),
		object2ObjectLinkedOpenHashMap -> {
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getEntitySolid(SpriteAtlasTexture.BLOCK_ATLAS_TEX), this.blockBufferBuilders.get(RenderLayer.getSolid()));
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getEntityCutout(SpriteAtlasTexture.BLOCK_ATLAS_TEX), this.blockBufferBuilders.get(RenderLayer.getCutout()));
			object2ObjectLinkedOpenHashMap.put(
				RenderLayer.getEntityNoOutline(SpriteAtlasTexture.BLOCK_ATLAS_TEX), this.blockBufferBuilders.get(RenderLayer.getCutoutMipped())
			);
			object2ObjectLinkedOpenHashMap.put(
				RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEX), this.blockBufferBuilders.get(RenderLayer.getTranslucent())
			);
			object2ObjectLinkedOpenHashMap.put(
				RenderLayer.getTranslucentNoCrumbling(), new BufferBuilder(RenderLayer.getTranslucentNoCrumbling().getExpectedBufferSize())
			);
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getGlint(), new BufferBuilder(RenderLayer.getGlint().getExpectedBufferSize()));
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getEntityGlint(), new BufferBuilder(RenderLayer.getEntityGlint().getExpectedBufferSize()));
			object2ObjectLinkedOpenHashMap.put(RenderLayer.getWaterMask(), new BufferBuilder(RenderLayer.getWaterMask().getExpectedBufferSize()));
		}
	);
	private final LayeredVertexConsumerStorage.Drawer generalDrawer = LayeredVertexConsumerStorage.makeDrawer(this.bufferBuilders, new BufferBuilder(256));
	private final LayeredVertexConsumerStorage.Drawer blockBreakingProgressDrawer = LayeredVertexConsumerStorage.makeDrawer(new BufferBuilder(256));
	private final FixedColorLayeredDrawer teamColorAwareOutlineDrawer = new FixedColorLayeredDrawer(this.generalDrawer);

	public BlockLayeredBufferBuilderStorage getBlockBufferBuilders() {
		return this.blockBufferBuilders;
	}

	public LayeredVertexConsumerStorage.Drawer getGeneralDrawer() {
		return this.generalDrawer;
	}

	public LayeredVertexConsumerStorage.Drawer getBlockBreakingProgressDrawer() {
		return this.blockBreakingProgressDrawer;
	}

	public FixedColorLayeredDrawer getTeamColorAwareOutlineDrawer() {
		return this.teamColorAwareOutlineDrawer;
	}
}
