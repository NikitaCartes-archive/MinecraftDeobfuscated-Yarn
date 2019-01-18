package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SkullBlock;
import net.minecraft.block.WallSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.model.DragonHeadEntityModel;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.entity.model.SkullOverlayEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class SkullBlockEntityRenderer extends BlockEntityRenderer<SkullBlockEntity> {
	public static SkullBlockEntityRenderer INSTANCE;
	private static final Map<SkullBlock.SkullType, SkullEntityModel> MODELS = SystemUtil.consume(
		Maps.<SkullBlock.SkullType, SkullEntityModel>newHashMap(), hashMap -> {
			SkullEntityModel skullEntityModel = new SkullEntityModel(0, 0, 64, 32);
			SkullEntityModel skullEntityModel2 = new SkullOverlayEntityModel();
			DragonHeadEntityModel dragonHeadEntityModel = new DragonHeadEntityModel(0.0F);
			hashMap.put(SkullBlock.Type.SKELETON, skullEntityModel);
			hashMap.put(SkullBlock.Type.WITHER_SKELETON, skullEntityModel);
			hashMap.put(SkullBlock.Type.PLAYER, skullEntityModel2);
			hashMap.put(SkullBlock.Type.ZOMBIE, skullEntityModel2);
			hashMap.put(SkullBlock.Type.CREEPER, skullEntityModel);
			hashMap.put(SkullBlock.Type.DRAGON, dragonHeadEntityModel);
		}
	);
	private static final Map<SkullBlock.SkullType, Identifier> TEXTURES = SystemUtil.consume(Maps.<SkullBlock.SkullType, Identifier>newHashMap(), hashMap -> {
		hashMap.put(SkullBlock.Type.SKELETON, new Identifier("textures/entity/skeleton/skeleton.png"));
		hashMap.put(SkullBlock.Type.WITHER_SKELETON, new Identifier("textures/entity/skeleton/wither_skeleton.png"));
		hashMap.put(SkullBlock.Type.ZOMBIE, new Identifier("textures/entity/zombie/zombie.png"));
		hashMap.put(SkullBlock.Type.CREEPER, new Identifier("textures/entity/creeper/creeper.png"));
		hashMap.put(SkullBlock.Type.DRAGON, new Identifier("textures/entity/enderdragon/dragon.png"));
		hashMap.put(SkullBlock.Type.PLAYER, DefaultSkinHelper.getTexture());
	});

	public void render(SkullBlockEntity skullBlockEntity, double d, double e, double f, float g, int i) {
		float h = skullBlockEntity.getTicksPowered(g);
		BlockState blockState = skullBlockEntity.getCachedState();
		boolean bl = blockState.getBlock() instanceof WallSkullBlock;
		Direction direction = bl ? blockState.get(WallSkullBlock.FACING) : null;
		float j = 22.5F * (float)(bl ? (2 + direction.getHorizontal()) * 4 : (Integer)blockState.get(SkullBlock.ROTATION));
		this.render((float)d, (float)e, (float)f, direction, j, ((AbstractSkullBlock)blockState.getBlock()).getSkullType(), skullBlockEntity.getOwner(), i, h);
	}

	@Override
	public void setRenderManager(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super.setRenderManager(blockEntityRenderDispatcher);
		INSTANCE = this;
	}

	public void render(
		float f, float g, float h, @Nullable Direction direction, float i, SkullBlock.SkullType skullType, @Nullable GameProfile gameProfile, int j, float k
	) {
		SkullEntityModel skullEntityModel = (SkullEntityModel)MODELS.get(skullType);
		if (j >= 0) {
			this.bindTexture(DESTROY_STAGE_TEXTURES[j]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(4.0F, 2.0F, 1.0F);
			GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			this.bindTexture(this.method_3578(skullType, gameProfile));
		}

		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		if (direction == null) {
			GlStateManager.translatef(f + 0.5F, g, h + 0.5F);
		} else {
			switch (direction) {
				case NORTH:
					GlStateManager.translatef(f + 0.5F, g + 0.25F, h + 0.74F);
					break;
				case SOUTH:
					GlStateManager.translatef(f + 0.5F, g + 0.25F, h + 0.26F);
					break;
				case WEST:
					GlStateManager.translatef(f + 0.74F, g + 0.25F, h + 0.5F);
					break;
				case EAST:
				default:
					GlStateManager.translatef(f + 0.26F, g + 0.25F, h + 0.5F);
			}
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlphaTest();
		if (skullType == SkullBlock.Type.PLAYER) {
			GlStateManager.setProfile(GlStateManager.RenderMode.PLAYER_SKIN);
		}

		skullEntityModel.setRotationAngles(k, 0.0F, 0.0F, i, 0.0F, 0.0625F);
		GlStateManager.popMatrix();
		if (j >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}

	private Identifier method_3578(SkullBlock.SkullType skullType, @Nullable GameProfile gameProfile) {
		Identifier identifier = (Identifier)TEXTURES.get(skullType);
		if (skullType == SkullBlock.Type.PLAYER && gameProfile != null) {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			Map<Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().method_4654(gameProfile);
			if (map.containsKey(Type.SKIN)) {
				identifier = minecraftClient.getSkinProvider().method_4656((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
			} else {
				identifier = DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(gameProfile));
			}
		}

		return identifier;
	}
}
