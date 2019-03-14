package net.minecraft.client.render.block.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.enums.BedPart;
import net.minecraft.client.render.entity.model.BedEntityModel;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

@Environment(EnvType.CLIENT)
public class BedBlockEntityRenderer extends BlockEntityRenderer<BedBlockEntity> {
	private static final Identifier[] TEXTURES = (Identifier[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(dyeColor -> new Identifier("textures/entity/bed/" + dyeColor.getName() + ".png"))
		.toArray(Identifier[]::new);
	private final BedEntityModel model = new BedEntityModel();

	public void method_3557(BedBlockEntity bedBlockEntity, double d, double e, double f, float g, int i) {
		if (i >= 0) {
			this.bindTexture(DESTROY_STAGE_TEXTURES[i]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(4.0F, 4.0F, 1.0F);
			GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		} else {
			Identifier identifier = TEXTURES[bedBlockEntity.getColor().getId()];
			if (identifier != null) {
				this.bindTexture(identifier);
			}
		}

		if (bedBlockEntity.hasWorld()) {
			BlockState blockState = bedBlockEntity.getCachedState();
			this.method_3558(blockState.get(BedBlock.PART) == BedPart.field_12560, d, e, f, blockState.get(BedBlock.field_11177));
		} else {
			this.method_3558(true, d, e, f, Direction.SOUTH);
			this.method_3558(false, d, e, f - 1.0, Direction.SOUTH);
		}

		if (i >= 0) {
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}

	private void method_3558(boolean bl, double d, double e, double f, Direction direction) {
		this.model.setVisible(bl);
		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)d, (float)e + 0.5625F, (float)f);
		GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.translatef(0.5F, 0.5F, 0.5F);
		GlStateManager.rotatef(180.0F + direction.asRotation(), 0.0F, 0.0F, 1.0F);
		GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
		GlStateManager.enableRescaleNormal();
		this.model.render();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}
}
