package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.MapRenderer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.state.ItemFrameEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ItemFrameEntityRenderer<T extends ItemFrameEntity> extends EntityRenderer<T, ItemFrameEntityRenderState> {
	public static final int GLOW_FRAME_BLOCK_LIGHT = 5;
	public static final int field_32933 = 30;
	private final ItemRenderer itemRenderer;
	private final MapRenderer mapRenderer;
	private final BlockRenderManager blockRenderManager;

	public ItemFrameEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
		this.mapRenderer = context.getMapRenderer();
		this.blockRenderManager = context.getBlockRenderManager();
	}

	protected int getBlockLight(T itemFrameEntity, BlockPos blockPos) {
		return itemFrameEntity.getType() == EntityType.GLOW_ITEM_FRAME
			? Math.max(5, super.getBlockLight(itemFrameEntity, blockPos))
			: super.getBlockLight(itemFrameEntity, blockPos);
	}

	public void render(ItemFrameEntityRenderState itemFrameEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		super.render(itemFrameEntityRenderState, matrixStack, vertexConsumerProvider, i);
		matrixStack.push();
		Direction direction = itemFrameEntityRenderState.facing;
		Vec3d vec3d = this.getPositionOffset(itemFrameEntityRenderState);
		matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
		double d = 0.46875;
		matrixStack.translate((double)direction.getOffsetX() * 0.46875, (double)direction.getOffsetY() * 0.46875, (double)direction.getOffsetZ() * 0.46875);
		float f;
		float g;
		if (direction.getAxis().isHorizontal()) {
			f = 0.0F;
			g = 180.0F - direction.asRotation();
		} else {
			f = (float)(-90 * direction.getDirection().offset());
			g = 180.0F;
		}

		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(f));
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
		ItemStack itemStack = itemFrameEntityRenderState.contents;
		if (!itemFrameEntityRenderState.invisible) {
			BakedModelManager bakedModelManager = this.blockRenderManager.getModels().getModelManager();
			ModelIdentifier modelIdentifier = this.getModelId(itemFrameEntityRenderState.glow, itemStack);
			matrixStack.push();
			matrixStack.translate(-0.5F, -0.5F, -0.5F);
			this.blockRenderManager
				.getModelRenderer()
				.render(
					matrixStack.peek(),
					vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolidZOffsetForward(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE)),
					null,
					bakedModelManager.getModel(modelIdentifier),
					1.0F,
					1.0F,
					1.0F,
					i,
					OverlayTexture.DEFAULT_UV
				);
			matrixStack.pop();
		}

		if (!itemStack.isEmpty()) {
			MapIdComponent mapIdComponent = itemFrameEntityRenderState.mapId;
			if (itemFrameEntityRenderState.invisible) {
				matrixStack.translate(0.0F, 0.0F, 0.5F);
			} else {
				matrixStack.translate(0.0F, 0.0F, 0.4375F);
			}

			int j = mapIdComponent != null ? itemFrameEntityRenderState.rotation % 4 * 2 : itemFrameEntityRenderState.rotation;
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)j * 360.0F / 8.0F));
			if (mapIdComponent != null) {
				matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
				float h = 0.0078125F;
				matrixStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
				matrixStack.translate(-64.0F, -64.0F, 0.0F);
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				int k = this.getLight(itemFrameEntityRenderState.glow, 15728850, i);
				this.mapRenderer.draw(itemFrameEntityRenderState.mapRenderState, matrixStack, vertexConsumerProvider, true, k);
			} else if (itemFrameEntityRenderState.itemModel != null) {
				int l = this.getLight(itemFrameEntityRenderState.glow, 15728880, i);
				matrixStack.scale(0.5F, 0.5F, 0.5F);
				this.itemRenderer
					.renderItem(
						itemStack, ModelTransformationMode.FIXED, false, matrixStack, vertexConsumerProvider, l, OverlayTexture.DEFAULT_UV, itemFrameEntityRenderState.itemModel
					);
			}
		}

		matrixStack.pop();
	}

	private int getLight(boolean glow, int glowLight, int regularLight) {
		return glow ? glowLight : regularLight;
	}

	private ModelIdentifier getModelId(boolean glow, ItemStack stack) {
		if (stack.contains(DataComponentTypes.MAP_ID)) {
			return glow ? BlockStatesLoader.MAP_GLOW_ITEM_FRAME_MODEL_ID : BlockStatesLoader.MAP_ITEM_FRAME_MODEL_ID;
		} else {
			return glow ? BlockStatesLoader.GLOW_ITEM_FRAME_MODEL_ID : BlockStatesLoader.ITEM_FRAME_MODEL_ID;
		}
	}

	public Vec3d getPositionOffset(ItemFrameEntityRenderState itemFrameEntityRenderState) {
		return new Vec3d(
			(double)((float)itemFrameEntityRenderState.facing.getOffsetX() * 0.3F), -0.25, (double)((float)itemFrameEntityRenderState.facing.getOffsetZ() * 0.3F)
		);
	}

	protected boolean hasLabel(T itemFrameEntity, double d) {
		return MinecraftClient.isHudEnabled()
			&& !itemFrameEntity.getHeldItemStack().isEmpty()
			&& itemFrameEntity.getHeldItemStack().contains(DataComponentTypes.CUSTOM_NAME)
			&& this.dispatcher.targetedEntity == itemFrameEntity;
	}

	protected Text getDisplayName(T itemFrameEntity) {
		return itemFrameEntity.getHeldItemStack().getName();
	}

	public ItemFrameEntityRenderState createRenderState() {
		return new ItemFrameEntityRenderState();
	}

	public void updateRenderState(T itemFrameEntity, ItemFrameEntityRenderState itemFrameEntityRenderState, float f) {
		super.updateRenderState(itemFrameEntity, itemFrameEntityRenderState, f);
		itemFrameEntityRenderState.facing = itemFrameEntity.getHorizontalFacing();
		ItemStack itemStack = itemFrameEntity.getHeldItemStack();
		itemFrameEntityRenderState.contents = itemStack.copy();
		itemFrameEntityRenderState.rotation = itemFrameEntity.getRotation();
		itemFrameEntityRenderState.glow = itemFrameEntity.getType() == EntityType.GLOW_ITEM_FRAME;
		itemFrameEntityRenderState.itemModel = null;
		itemFrameEntityRenderState.mapId = null;
		if (!itemFrameEntityRenderState.contents.isEmpty()) {
			MapIdComponent mapIdComponent = itemFrameEntity.getMapId(itemStack);
			if (mapIdComponent != null) {
				MapState mapState = itemFrameEntity.getWorld().getMapState(mapIdComponent);
				if (mapState != null) {
					this.mapRenderer.update(mapIdComponent, mapState, itemFrameEntityRenderState.mapRenderState);
					itemFrameEntityRenderState.mapId = mapIdComponent;
				}
			} else {
				itemFrameEntityRenderState.itemModel = this.itemRenderer.getModel(itemStack, itemFrameEntity.getWorld(), null, itemFrameEntity.getId());
			}
		}
	}
}
