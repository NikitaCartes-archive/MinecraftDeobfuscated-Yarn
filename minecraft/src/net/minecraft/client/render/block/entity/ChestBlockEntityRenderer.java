package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Calendar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.client.render.entity.model.ChestEntityModel;
import net.minecraft.client.render.entity.model.LargeChestEntityModel;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity & ChestAnimationProgress> extends BlockEntityRenderer<T> {
	private static final Identifier TRAPPED_DOUBLE_TEX = new Identifier("textures/entity/chest/trapped_double.png");
	private static final Identifier CHRISTMAS_DOUBLE_TEX = new Identifier("textures/entity/chest/christmas_double.png");
	private static final Identifier NORMAL_DOUBLE_TEX = new Identifier("textures/entity/chest/normal_double.png");
	private static final Identifier TRAPPED_TEX = new Identifier("textures/entity/chest/trapped.png");
	private static final Identifier CHRISTMAS_TEX = new Identifier("textures/entity/chest/christmas.png");
	private static final Identifier NORMAL_TEX = new Identifier("textures/entity/chest/normal.png");
	private static final Identifier ENDER_TEX = new Identifier("textures/entity/chest/ender.png");
	private final ChestEntityModel modelSingleChest = new ChestEntityModel();
	private final ChestEntityModel modelDoubleChest = new LargeChestEntityModel();
	private boolean isChristmas;

	public ChestBlockEntityRenderer() {
		Calendar calendar = Calendar.getInstance();
		if (calendar.get(2) + 1 == 12 && calendar.get(5) >= 24 && calendar.get(5) <= 26) {
			this.isChristmas = true;
		}
	}

	@Override
	public void render(T blockEntity, double d, double e, double f, float g, int i) {
		GlStateManager.enableDepthTest();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		BlockState blockState = blockEntity.hasWorld() ? blockEntity.getCachedState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);
		ChestType chestType = blockState.contains((Property<T>)ChestBlock.CHEST_TYPE) ? blockState.get(ChestBlock.CHEST_TYPE) : ChestType.SINGLE;
		if (chestType != ChestType.LEFT) {
			boolean bl = chestType != ChestType.SINGLE;
			ChestEntityModel chestEntityModel = this.method_3562(blockEntity, i, bl);
			if (i >= 0) {
				GlStateManager.matrixMode(5890);
				GlStateManager.pushMatrix();
				GlStateManager.scalef(bl ? 8.0F : 4.0F, 4.0F, 1.0F);
				GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
				GlStateManager.matrixMode(5888);
			} else {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			}

			GlStateManager.pushMatrix();
			GlStateManager.enableRescaleNormal();
			GlStateManager.translatef((float)d, (float)e + 1.0F, (float)f + 1.0F);
			GlStateManager.scalef(1.0F, -1.0F, -1.0F);
			float h = ((Direction)blockState.get(ChestBlock.FACING)).asRotation();
			if ((double)Math.abs(h) > 1.0E-5) {
				GlStateManager.translatef(0.5F, 0.5F, 0.5F);
				GlStateManager.rotatef(h, 0.0F, 1.0F, 0.0F);
				GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
			}

			this.method_3561(blockEntity, g, chestEntityModel);
			chestEntityModel.method_2799();
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (i >= 0) {
				GlStateManager.matrixMode(5890);
				GlStateManager.popMatrix();
				GlStateManager.matrixMode(5888);
			}
		}
	}

	private ChestEntityModel method_3562(T blockEntity, int i, boolean bl) {
		Identifier identifier;
		if (i >= 0) {
			identifier = DESTROY_STAGE_TEXTURES[i];
		} else if (this.isChristmas) {
			identifier = bl ? CHRISTMAS_DOUBLE_TEX : CHRISTMAS_TEX;
		} else if (blockEntity instanceof TrappedChestBlockEntity) {
			identifier = bl ? TRAPPED_DOUBLE_TEX : TRAPPED_TEX;
		} else if (blockEntity instanceof EnderChestBlockEntity) {
			identifier = ENDER_TEX;
		} else {
			identifier = bl ? NORMAL_DOUBLE_TEX : NORMAL_TEX;
		}

		this.bindTexture(identifier);
		return bl ? this.modelDoubleChest : this.modelSingleChest;
	}

	private void method_3561(T blockEntity, float f, ChestEntityModel chestEntityModel) {
		float g = blockEntity.getAnimationProgress(f);
		g = 1.0F - g;
		g = 1.0F - g * g * g;
		chestEntityModel.method_2798().pitch = -(g * (float) (Math.PI / 2));
	}
}
