package net.minecraft.client.render.block.entity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
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
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.DragonHeadEntityModel;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.client.render.entity.model.SkullOverlayEntityModel;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixStack;

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

	public void method_3577(
		SkullBlockEntity skullBlockEntity,
		double d,
		double e,
		double f,
		float g,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i
	) {
		float h = skullBlockEntity.getTicksPowered(g);
		BlockState blockState = skullBlockEntity.getCachedState();
		boolean bl = blockState.getBlock() instanceof WallSkullBlock;
		Direction direction = bl ? blockState.get(WallSkullBlock.FACING) : null;
		float j = 22.5F * (float)(bl ? (2 + direction.getHorizontal()) * 4 : (Integer)blockState.get(SkullBlock.ROTATION));
		render(direction, j, ((AbstractSkullBlock)blockState.getBlock()).getSkullType(), skullBlockEntity.getOwner(), h, matrixStack, layeredVertexConsumerStorage, i);
	}

	public static void render(
		@Nullable Direction direction,
		float f,
		SkullBlock.SkullType skullType,
		@Nullable GameProfile gameProfile,
		float g,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage,
		int i
	) {
		SkullEntityModel skullEntityModel = (SkullEntityModel)MODELS.get(skullType);
		matrixStack.push();
		if (direction == null) {
			matrixStack.translate(0.5, 0.0, 0.5);
		} else {
			switch (direction) {
				case NORTH:
					matrixStack.translate(0.5, 0.25, 0.74F);
					break;
				case SOUTH:
					matrixStack.translate(0.5, 0.25, 0.26F);
					break;
				case WEST:
					matrixStack.translate(0.74F, 0.25, 0.5);
					break;
				case EAST:
				default:
					matrixStack.translate(0.26F, 0.25, 0.5);
			}
		}

		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23017(method_3578(skullType, gameProfile)));
		OverlayTexture.clearDefaultOverlay(vertexConsumer);
		skullEntityModel.render(matrixStack, vertexConsumer, g, f, 0.0F, 0.0625F, i);
		vertexConsumer.clearDefaultOverlay();
		matrixStack.pop();
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
