package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ItemFrameEntityRenderer extends EntityRenderer<ItemFrameEntity> {
	private static final ModelIdentifier NORMAL_FRAME = new ModelIdentifier("item_frame", "map=false");
	private static final ModelIdentifier MAP_FRAME = new ModelIdentifier("item_frame", "map=true");
	private final MinecraftClient client = MinecraftClient.getInstance();
	private final ItemRenderer itemRenderer;

	public ItemFrameEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
		super(entityRenderDispatcher);
		this.itemRenderer = itemRenderer;
	}

	public void method_3994(
		ItemFrameEntity itemFrameEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		super.render(itemFrameEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
		matrixStack.push();
		Direction direction = itemFrameEntity.getHorizontalFacing();
		Vec3d vec3d = this.method_23174(itemFrameEntity, d, e, f, h);
		matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
		double i = 0.46875;
		matrixStack.translate((double)direction.getOffsetX() * 0.46875, (double)direction.getOffsetY() * 0.46875, (double)direction.getOffsetZ() * 0.46875);
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(itemFrameEntity.pitch));
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0F - itemFrameEntity.yaw));
		BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
		BakedModelManager bakedModelManager = blockRenderManager.getModels().getModelManager();
		ModelIdentifier modelIdentifier = itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP ? MAP_FRAME : NORMAL_FRAME;
		matrixStack.push();
		matrixStack.translate(-0.5, -0.5, -0.5);
		int j = itemFrameEntity.getLightmapCoordinates();
		blockRenderManager.getModelRenderer()
			.render(
				matrixStack.peek(),
				matrixStack.method_23478(),
				layeredVertexConsumerStorage.getBuffer(RenderLayer.getSolid()),
				null,
				bakedModelManager.getModel(modelIdentifier),
				1.0F,
				1.0F,
				1.0F,
				j,
				OverlayTexture.field_21444
			);
		matrixStack.pop();
		ItemStack itemStack = itemFrameEntity.getHeldItemStack();
		if (!itemStack.isEmpty()) {
			boolean bl = itemStack.getItem() == Items.FILLED_MAP;
			matrixStack.translate(0.0, 0.0, 0.4375);
			int k = bl ? itemFrameEntity.getRotation() % 4 * 2 : itemFrameEntity.getRotation();
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)k * 360.0F / 8.0F));
			if (bl) {
				this.renderManager.textureManager.bindTexture(MapRenderer.field_21056);
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(180.0F));
				float l = 0.0078125F;
				matrixStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
				matrixStack.translate(-64.0, -64.0, 0.0);
				MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, itemFrameEntity.world);
				matrixStack.translate(0.0, 0.0, -1.0);
				if (mapState != null) {
					this.client.gameRenderer.getMapRenderer().draw(matrixStack, layeredVertexConsumerStorage, mapState, true, j);
				}
			} else {
				matrixStack.scale(0.5F, 0.5F, 0.5F);
				this.itemRenderer.method_23178(itemStack, ModelTransformation.Type.FIXED, j, OverlayTexture.field_21444, matrixStack, layeredVertexConsumerStorage);
			}
		}

		matrixStack.pop();
	}

	public Vec3d method_23174(ItemFrameEntity itemFrameEntity, double d, double e, double f, float g) {
		return new Vec3d(
			(double)((float)itemFrameEntity.getHorizontalFacing().getOffsetX() * 0.3F),
			-0.25,
			(double)((float)itemFrameEntity.getHorizontalFacing().getOffsetZ() * 0.3F)
		);
	}

	public Identifier method_3993(ItemFrameEntity itemFrameEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
	}

	protected boolean method_23176(ItemFrameEntity itemFrameEntity) {
		if (MinecraftClient.isHudEnabled()
			&& !itemFrameEntity.getHeldItemStack().isEmpty()
			&& itemFrameEntity.getHeldItemStack().hasCustomName()
			&& this.renderManager.targetedEntity == itemFrameEntity) {
			double d = this.renderManager.method_23168(itemFrameEntity);
			float f = itemFrameEntity.method_21751() ? 32.0F : 64.0F;
			return d < (double)(f * f);
		} else {
			return false;
		}
	}

	protected void method_23175(ItemFrameEntity itemFrameEntity, String string, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
		super.renderLabelIfPresent(itemFrameEntity, itemFrameEntity.getHeldItemStack().getName().asFormattedString(), matrixStack, layeredVertexConsumerStorage);
	}
}
