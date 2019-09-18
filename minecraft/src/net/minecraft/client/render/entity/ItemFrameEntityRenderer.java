package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class ItemFrameEntityRenderer extends EntityRenderer<ItemFrameEntity> {
	private static final Identifier MAP_BACKGROUND_TEX = new Identifier("textures/map/map_background.png");
	private static final ModelIdentifier NORMAL_FRAME = new ModelIdentifier("item_frame", "map=false");
	private static final ModelIdentifier MAP_FRAME = new ModelIdentifier("item_frame", "map=true");
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final ItemRenderer itemRenderer;

	public ItemFrameEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.itemRenderer = itemRenderer;
	}

	public void method_3994(ItemFrameEntity itemFrameEntity, double d, double e, double f, float g, float h) {
		RenderSystem.pushMatrix();
		BlockPos blockPos = itemFrameEntity.getDecorationBlockPos();
		double i = (double)blockPos.getX() - itemFrameEntity.x + d;
		double j = (double)blockPos.getY() - itemFrameEntity.y + e;
		double k = (double)blockPos.getZ() - itemFrameEntity.z + f;
		RenderSystem.translated(i + 0.5, j + 0.5, k + 0.5);
		RenderSystem.rotatef(itemFrameEntity.pitch, 1.0F, 0.0F, 0.0F);
		RenderSystem.rotatef(180.0F - itemFrameEntity.yaw, 0.0F, 1.0F, 0.0F);
		this.renderManager.textureManager.method_22813(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
		BakedModelManager bakedModelManager = blockRenderManager.getModels().getModelManager();
		ModelIdentifier modelIdentifier = itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP ? MAP_FRAME : NORMAL_FRAME;
		RenderSystem.pushMatrix();
		RenderSystem.translatef(-0.5F, -0.5F, -0.5F);
		if (this.renderOutlines) {
			RenderSystem.enableColorMaterial();
			RenderSystem.setupSolidRenderingTextureCombine(this.getOutlineColor(itemFrameEntity));
		}

		blockRenderManager.getModelRenderer().render(bakedModelManager.getModel(modelIdentifier), 1.0F, 1.0F, 1.0F, 1.0F);
		if (this.renderOutlines) {
			RenderSystem.tearDownSolidRenderingTextureCombine();
			RenderSystem.disableColorMaterial();
		}

		RenderSystem.popMatrix();
		RenderSystem.enableLighting();
		if (itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP) {
			RenderSystem.pushLightingAttributes();
			GuiLighting.enable();
		}

		RenderSystem.translatef(0.0F, 0.0F, 0.4375F);
		this.renderItem(itemFrameEntity);
		if (itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP) {
			GuiLighting.disable();
			RenderSystem.popAttributes();
		}

		RenderSystem.enableLighting();
		RenderSystem.popMatrix();
		this.method_3995(
			itemFrameEntity,
			d + (double)((float)itemFrameEntity.getHorizontalFacing().getOffsetX() * 0.3F),
			e - 0.25,
			f + (double)((float)itemFrameEntity.getHorizontalFacing().getOffsetZ() * 0.3F)
		);
	}

	@Nullable
	protected Identifier method_3993(ItemFrameEntity itemFrameEntity) {
		return null;
	}

	private void renderItem(ItemFrameEntity itemFrameEntity) {
		ItemStack itemStack = itemFrameEntity.getHeldItemStack();
		if (!itemStack.isEmpty()) {
			RenderSystem.pushMatrix();
			boolean bl = itemStack.getItem() == Items.FILLED_MAP;
			int i = bl ? itemFrameEntity.getRotation() % 4 * 2 : itemFrameEntity.getRotation();
			RenderSystem.rotatef((float)i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
			if (bl) {
				RenderSystem.disableLighting();
				this.renderManager.textureManager.method_22813(MAP_BACKGROUND_TEX);
				RenderSystem.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
				float f = 0.0078125F;
				RenderSystem.scalef(0.0078125F, 0.0078125F, 0.0078125F);
				RenderSystem.translatef(-64.0F, -64.0F, 0.0F);
				MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, itemFrameEntity.world);
				RenderSystem.translatef(0.0F, 0.0F, -1.0F);
				if (mapState != null) {
					this.client.gameRenderer.getMapRenderer().draw(mapState, true);
				}
			} else {
				RenderSystem.scalef(0.5F, 0.5F, 0.5F);
				this.itemRenderer.renderItem(itemStack, ModelTransformation.Type.FIXED);
			}

			RenderSystem.popMatrix();
		}
	}

	protected void method_3995(ItemFrameEntity itemFrameEntity, double d, double e, double f) {
		if (MinecraftClient.isHudEnabled()
			&& !itemFrameEntity.getHeldItemStack().isEmpty()
			&& itemFrameEntity.getHeldItemStack().hasCustomName()
			&& this.renderManager.targetedEntity == itemFrameEntity) {
			double g = itemFrameEntity.squaredDistanceTo(this.renderManager.camera.getPos());
			float h = itemFrameEntity.method_21751() ? 32.0F : 64.0F;
			if (!(g >= (double)(h * h))) {
				String string = itemFrameEntity.getHeldItemStack().getName().asFormattedString();
				this.renderLabel(itemFrameEntity, string, d, e, f, 64);
			}
		}
	}
}
