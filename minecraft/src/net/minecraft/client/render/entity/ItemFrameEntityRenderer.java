package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.MapIdComponent;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class ItemFrameEntityRenderer<T extends ItemFrameEntity> extends EntityRenderer<T> {
	private static final ModelIdentifier NORMAL_FRAME = ModelIdentifier.ofVanilla("item_frame", "map=false");
	private static final ModelIdentifier MAP_FRAME = ModelIdentifier.ofVanilla("item_frame", "map=true");
	private static final ModelIdentifier GLOW_FRAME = ModelIdentifier.ofVanilla("glow_item_frame", "map=false");
	private static final ModelIdentifier MAP_GLOW_FRAME = ModelIdentifier.ofVanilla("glow_item_frame", "map=true");
	public static final int GLOW_FRAME_BLOCK_LIGHT = 5;
	public static final int field_32933 = 30;
	private final ItemRenderer itemRenderer;
	private final BlockRenderManager blockRenderManager;

	public ItemFrameEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.itemRenderer = context.getItemRenderer();
		this.blockRenderManager = context.getBlockRenderManager();
	}

	protected int getBlockLight(T itemFrameEntity, BlockPos blockPos) {
		return itemFrameEntity.getType() == EntityType.GLOW_ITEM_FRAME
			? Math.max(5, super.getBlockLight(itemFrameEntity, blockPos))
			: super.getBlockLight(itemFrameEntity, blockPos);
	}

	public void render(T itemFrameEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		super.render(itemFrameEntity, f, g, matrixStack, vertexConsumerProvider, i);
		matrixStack.push();
		Direction direction = itemFrameEntity.getHorizontalFacing();
		Vec3d vec3d = this.getPositionOffset(itemFrameEntity, g);
		matrixStack.translate(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
		double d = 0.46875;
		matrixStack.translate((double)direction.getOffsetX() * 0.46875, (double)direction.getOffsetY() * 0.46875, (double)direction.getOffsetZ() * 0.46875);
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(itemFrameEntity.getPitch()));
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - itemFrameEntity.getYaw()));
		boolean bl = itemFrameEntity.isInvisible();
		ItemStack itemStack = itemFrameEntity.getHeldItemStack();
		if (!bl) {
			BakedModelManager bakedModelManager = this.blockRenderManager.getModels().getModelManager();
			ModelIdentifier modelIdentifier = this.getModelId(itemFrameEntity, itemStack);
			matrixStack.push();
			matrixStack.translate(-0.5F, -0.5F, -0.5F);
			this.blockRenderManager
				.getModelRenderer()
				.render(
					matrixStack.peek(),
					vertexConsumerProvider.getBuffer(TexturedRenderLayers.getEntitySolid()),
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
			MapIdComponent mapIdComponent = itemFrameEntity.getMapId(itemStack);
			if (bl) {
				matrixStack.translate(0.0F, 0.0F, 0.5F);
			} else {
				matrixStack.translate(0.0F, 0.0F, 0.4375F);
			}

			int j = mapIdComponent != null ? itemFrameEntity.getRotation() % 4 * 2 : itemFrameEntity.getRotation();
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)j * 360.0F / 8.0F));
			if (mapIdComponent != null) {
				matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
				float h = 0.0078125F;
				matrixStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
				matrixStack.translate(-64.0F, -64.0F, 0.0F);
				MapState mapState = FilledMapItem.getMapState(mapIdComponent, itemFrameEntity.getWorld());
				matrixStack.translate(0.0F, 0.0F, -1.0F);
				if (mapState != null) {
					int k = this.getLight(itemFrameEntity, LightmapTextureManager.MAX_SKY_LIGHT_COORDINATE | 210, i);
					MinecraftClient.getInstance().gameRenderer.getMapRenderer().draw(matrixStack, vertexConsumerProvider, mapIdComponent, mapState, true, k);
				}
			} else {
				int l = this.getLight(itemFrameEntity, LightmapTextureManager.MAX_LIGHT_COORDINATE, i);
				matrixStack.scale(0.5F, 0.5F, 0.5F);
				this.itemRenderer
					.renderItem(
						itemStack,
						ModelTransformationMode.FIXED,
						l,
						OverlayTexture.DEFAULT_UV,
						matrixStack,
						vertexConsumerProvider,
						itemFrameEntity.getWorld(),
						itemFrameEntity.getId()
					);
			}
		}

		matrixStack.pop();
	}

	private int getLight(T itemFrame, int glowLight, int regularLight) {
		return itemFrame.getType() == EntityType.GLOW_ITEM_FRAME ? glowLight : regularLight;
	}

	private ModelIdentifier getModelId(T entity, ItemStack stack) {
		boolean bl = entity.getType() == EntityType.GLOW_ITEM_FRAME;
		if (stack.isOf(Items.FILLED_MAP)) {
			return bl ? MAP_GLOW_FRAME : MAP_FRAME;
		} else {
			return bl ? GLOW_FRAME : NORMAL_FRAME;
		}
	}

	public Vec3d getPositionOffset(T itemFrameEntity, float f) {
		return new Vec3d(
			(double)((float)itemFrameEntity.getHorizontalFacing().getOffsetX() * 0.3F),
			-0.25,
			(double)((float)itemFrameEntity.getHorizontalFacing().getOffsetZ() * 0.3F)
		);
	}

	public Identifier getTexture(T itemFrameEntity) {
		return SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE;
	}

	protected boolean hasLabel(T itemFrameEntity) {
		if (MinecraftClient.isHudEnabled()
			&& !itemFrameEntity.getHeldItemStack().isEmpty()
			&& itemFrameEntity.getHeldItemStack().contains(DataComponentTypes.CUSTOM_NAME)
			&& this.dispatcher.targetedEntity == itemFrameEntity) {
			double d = this.dispatcher.getSquaredDistanceToCamera(itemFrameEntity);
			float f = itemFrameEntity.isSneaky() ? 32.0F : 64.0F;
			return d < (double)(f * f);
		} else {
			return false;
		}
	}

	protected void renderLabelIfPresent(T itemFrameEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f) {
		super.renderLabelIfPresent(itemFrameEntity, itemFrameEntity.getHeldItemStack().getName(), matrixStack, vertexConsumerProvider, i, f);
	}
}
