package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
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
import net.minecraft.util.math.Vec3d;

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

	public void method_3994(ItemFrameEntity itemFrameEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		super.render(itemFrameEntity, d, e, f, g, h, arg, arg2);
		arg.method_22903();
		Direction direction = itemFrameEntity.getHorizontalFacing();
		Vec3d vec3d = this.method_23174(itemFrameEntity, d, e, f, h);
		arg.method_22904(-vec3d.getX(), -vec3d.getY(), -vec3d.getZ());
		double i = 0.46875;
		arg.method_22904((double)direction.getOffsetX() * 0.46875, (double)direction.getOffsetY() * 0.46875, (double)direction.getOffsetZ() * 0.46875);
		arg.method_22907(Vector3f.field_20703.method_23214(itemFrameEntity.pitch, true));
		arg.method_22907(Vector3f.field_20705.method_23214(180.0F - itemFrameEntity.yaw, true));
		BlockRenderManager blockRenderManager = this.client.getBlockRenderManager();
		BakedModelManager bakedModelManager = blockRenderManager.getModels().getModelManager();
		ModelIdentifier modelIdentifier = itemFrameEntity.getHeldItemStack().getItem() == Items.FILLED_MAP ? MAP_FRAME : NORMAL_FRAME;
		arg.method_22903();
		arg.method_22904(-0.5, -0.5, -0.5);
		int j = itemFrameEntity.getLightmapCoordinates();
		blockRenderManager.getModelRenderer()
			.render(arg.method_22910(), arg2.getBuffer(BlockRenderLayer.SOLID), null, bakedModelManager.getModel(modelIdentifier), 1.0F, 1.0F, 1.0F, j);
		arg.method_22909();
		ItemStack itemStack = itemFrameEntity.getHeldItemStack();
		if (!itemStack.isEmpty()) {
			boolean bl = itemStack.getItem() == Items.FILLED_MAP;
			arg.method_22904(0.0, 0.0, 0.4375);
			int k = bl ? itemFrameEntity.getRotation() % 4 * 2 : itemFrameEntity.getRotation();
			arg.method_22907(Vector3f.field_20707.method_23214((float)k * 360.0F / 8.0F, true));
			if (bl) {
				this.renderManager.textureManager.bindTexture(MAP_BACKGROUND_TEX);
				arg.method_22907(Vector3f.field_20707.method_23214(180.0F, true));
				float l = 0.0078125F;
				arg.method_22905(0.0078125F, 0.0078125F, 0.0078125F);
				arg.method_22904(-64.0, -64.0, 0.0);
				MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, itemFrameEntity.world);
				arg.method_22904(0.0, 0.0, -1.0);
				if (mapState != null) {
					this.client.gameRenderer.getMapRenderer().draw(arg, arg2, mapState, true);
				}
			} else {
				arg.method_22905(0.5F, 0.5F, 0.5F);
				this.itemRenderer.method_23178(itemStack, ModelTransformation.Type.FIXED, j, arg, arg2);
			}
		}

		arg.method_22909();
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

	protected void method_23175(ItemFrameEntity itemFrameEntity, String string, class_4587 arg, class_4597 arg2) {
		super.renderLabelIfPresent(itemFrameEntity, itemFrameEntity.getHeldItemStack().getName().asFormattedString(), arg, arg2);
	}
}
