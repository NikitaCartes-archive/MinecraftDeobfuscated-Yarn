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
import net.minecraft.client.render.entity.model.ChestDoubleEntityModel;
import net.minecraft.client.render.entity.model.ChestEntityModel;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class ChestBlockEntityRenderer<T extends BlockEntity & ChestAnimationProgress> extends BlockEntityRenderer<T> {
	private static final Identifier field_4360 = new Identifier("textures/entity/chest/trapped_double.png");
	private static final Identifier field_4362 = new Identifier("textures/entity/chest/christmas_double.png");
	private static final Identifier field_4359 = new Identifier("textures/entity/chest/normal_double.png");
	private static final Identifier field_4357 = new Identifier("textures/entity/chest/trapped.png");
	private static final Identifier field_4363 = new Identifier("textures/entity/chest/christmas.png");
	private static final Identifier field_4364 = new Identifier("textures/entity/chest/normal.png");
	private static final Identifier field_4366 = new Identifier("textures/entity/chest/ender.png");
	private final ChestEntityModel modelSingleChest = new ChestEntityModel();
	private final ChestEntityModel modelDoubleChest = new ChestDoubleEntityModel();
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
		BlockState blockState = blockEntity.hasWorld()
			? blockEntity.method_11010()
			: Blocks.field_10034.method_9564().method_11657(ChestBlock.field_10768, Direction.SOUTH);
		ChestType chestType = blockState.method_11570((Property<T>)ChestBlock.field_10770) ? blockState.method_11654(ChestBlock.field_10770) : ChestType.field_12569;
		if (chestType != ChestType.field_12574) {
			boolean bl = chestType != ChestType.field_12569;
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
			float h = ((Direction)blockState.method_11654(ChestBlock.field_10768)).asRotation();
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
			identifier = field_4368[i];
		} else if (this.isChristmas) {
			identifier = bl ? field_4362 : field_4363;
		} else if (blockEntity instanceof TrappedChestBlockEntity) {
			identifier = bl ? field_4360 : field_4357;
		} else if (blockEntity instanceof EnderChestBlockEntity) {
			identifier = field_4366;
		} else {
			identifier = bl ? field_4359 : field_4364;
		}

		this.method_3566(identifier);
		return bl ? this.modelDoubleChest : this.modelSingleChest;
	}

	private void method_3561(T blockEntity, float f, ChestEntityModel chestEntityModel) {
		float g = blockEntity.getAnimationProgress(f);
		g = 1.0F - g;
		g = 1.0F - g * g * g;
		chestEntityModel.method_2798().pitch = -(g * (float) (Math.PI / 2));
	}
}
