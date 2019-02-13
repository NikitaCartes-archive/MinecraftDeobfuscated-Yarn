package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
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
	private static final ModelIdentifier field_4721 = new ModelIdentifier("item_frame", "map=false");
	private static final ModelIdentifier field_4723 = new ModelIdentifier("item_frame", "map=true");
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final ItemRenderer itemRenderer;

	public ItemFrameEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.itemRenderer = itemRenderer;
	}

	public void method_3994(ItemFrameEntity itemFrameEntity, double d, double e, double f, float g, float h) {
		GlStateManager.pushMatrix();
		BlockPos blockPos = itemFrameEntity.getDecorationBlockPos();
		double i = (double)blockPos.getX() - itemFrameEntity.x + d;
		double j = (double)blockPos.getY() - itemFrameEntity.y + e;
		double k = (double)blockPos.getZ() - itemFrameEntity.z + f;
		GlStateManager.translated(i + 0.5, j + 0.5, k + 0.5);
		GlStateManager.rotatef(itemFrameEntity.pitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotatef(180.0F - itemFrameEntity.yaw, 0.0F, 1.0F, 0.0F);
		this.renderManager.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
		BakedModelManager bakedModelManager = blockRenderManager.getModels().getModelManager();
		ModelIdentifier modelIdentifier = itemFrameEntity.getHeldItemStack().getItem() == Items.field_8204 ? field_4723 : field_4721;
		GlStateManager.pushMatrix();
		GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(itemFrameEntity));
		}

		blockRenderManager.getModelRenderer().render(bakedModelManager.getModel(modelIdentifier), 1.0F, 1.0F, 1.0F, 1.0F);
		if (this.renderOutlines) {
			GlStateManager.tearDownSolidRenderingTextureCombine();
			GlStateManager.disableColorMaterial();
		}

		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		if (itemFrameEntity.getHeldItemStack().getItem() == Items.field_8204) {
			GlStateManager.pushLightingAttributes();
			GuiLighting.enable();
		}

		GlStateManager.translatef(0.0F, 0.0F, 0.4375F);
		this.method_3992(itemFrameEntity);
		if (itemFrameEntity.getHeldItemStack().getItem() == Items.field_8204) {
			GuiLighting.disable();
			GlStateManager.popAttributes();
		}

		GlStateManager.enableLighting();
		GlStateManager.popMatrix();
		this.method_3995(
			itemFrameEntity, d + (double)((float)itemFrameEntity.facing.getOffsetX() * 0.3F), e - 0.25, f + (double)((float)itemFrameEntity.facing.getOffsetZ() * 0.3F)
		);
	}

	@Nullable
	protected Identifier method_3993(ItemFrameEntity itemFrameEntity) {
		return null;
	}

	private void method_3992(ItemFrameEntity itemFrameEntity) {
		ItemStack itemStack = itemFrameEntity.getHeldItemStack();
		if (!itemStack.isEmpty()) {
			GlStateManager.pushMatrix();
			boolean bl = itemStack.getItem() == Items.field_8204;
			int i = bl ? itemFrameEntity.getRotation() % 4 * 2 : itemFrameEntity.getRotation();
			GlStateManager.rotatef((float)i * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
			if (bl) {
				GlStateManager.disableLighting();
				this.renderManager.textureManager.bindTexture(MAP_BACKGROUND_TEX);
				GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
				float f = 0.0078125F;
				GlStateManager.scalef(0.0078125F, 0.0078125F, 0.0078125F);
				GlStateManager.translatef(-64.0F, -64.0F, 0.0F);
				MapState mapState = FilledMapItem.method_8001(itemStack, itemFrameEntity.world);
				GlStateManager.translatef(0.0F, 0.0F, -1.0F);
				if (mapState != null) {
					this.client.gameRenderer.getMapRenderer().draw(mapState, true);
				}
			} else {
				GlStateManager.scalef(0.5F, 0.5F, 0.5F);
				this.itemRenderer.renderItem(itemStack, ModelTransformation.Type.FIXED);
			}

			GlStateManager.popMatrix();
		}
	}

	protected void method_3995(ItemFrameEntity itemFrameEntity, double d, double e, double f) {
		if (MinecraftClient.isHudEnabled()
			&& !itemFrameEntity.getHeldItemStack().isEmpty()
			&& itemFrameEntity.getHeldItemStack().hasDisplayName()
			&& this.renderManager.field_4678 == itemFrameEntity) {
			double g = itemFrameEntity.squaredDistanceTo(this.renderManager.field_4686);
			float h = itemFrameEntity.isSneaking() ? 32.0F : 64.0F;
			if (!(g >= (double)(h * h))) {
				String string = itemFrameEntity.getHeldItemStack().getDisplayName().getFormattedText();
				this.renderEntityLabel(itemFrameEntity, string, d, e, f, 64);
			}
		}
	}
}
