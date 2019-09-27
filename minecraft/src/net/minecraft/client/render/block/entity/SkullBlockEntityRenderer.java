package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BlockRenderLayer;
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

	public SkullBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_3577(SkullBlockEntity skullBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
		float h = skullBlockEntity.getTicksPowered(g);
		BlockState blockState = skullBlockEntity.getCachedState();
		boolean bl = blockState.getBlock() instanceof WallSkullBlock;
		Direction direction = bl ? blockState.get(WallSkullBlock.FACING) : null;
		float j = 22.5F * (float)(bl ? (2 + direction.getHorizontal()) * 4 : (Integer)blockState.get(SkullBlock.ROTATION));
		render(direction, j, ((AbstractSkullBlock)blockState.getBlock()).getSkullType(), skullBlockEntity.getOwner(), h, arg, arg2, i);
	}

	public static void render(
		@Nullable Direction direction, float f, SkullBlock.SkullType skullType, @Nullable GameProfile gameProfile, float g, class_4587 arg, class_4597 arg2, int i
	) {
		SkullEntityModel skullEntityModel = (SkullEntityModel)MODELS.get(skullType);
		arg.method_22903();
		if (direction == null) {
			arg.method_22904(0.5, 0.0, 0.5);
		} else {
			switch (direction) {
				case NORTH:
					arg.method_22904(0.5, 0.25, 0.74F);
					break;
				case SOUTH:
					arg.method_22904(0.5, 0.25, 0.26F);
					break;
				case WEST:
					arg.method_22904(0.74F, 0.25, 0.5);
					break;
				case EAST:
				default:
					arg.method_22904(0.26F, 0.25, 0.5);
			}
		}

		arg.method_22905(-1.0F, -1.0F, 1.0F);
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(method_3578(skullType, gameProfile)));
		class_4608.method_23211(lv);
		skullEntityModel.render(arg, lv, g, f, 0.0F, 0.0625F, i);
		lv.method_22923();
		arg.method_22909();
	}

	private static Identifier method_3578(SkullBlock.SkullType skullType, @Nullable GameProfile gameProfile) {
		Identifier identifier = (Identifier)TEXTURES.get(skullType);
		if (skullType == SkullBlock.Type.PLAYER && gameProfile != null) {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			Map<Type, MinecraftProfileTexture> map = minecraftClient.getSkinProvider().getTextures(gameProfile);
			if (map.containsKey(Type.SKIN)) {
				identifier = minecraftClient.getSkinProvider().loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN);
			} else {
				identifier = DefaultSkinHelper.getTexture(PlayerEntity.getUuidFromProfile(gameProfile));
			}
		}

		return identifier;
	}
}
