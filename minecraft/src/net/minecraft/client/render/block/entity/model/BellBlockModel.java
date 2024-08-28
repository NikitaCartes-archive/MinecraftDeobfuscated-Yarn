package net.minecraft.client.render.block.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.entity.BellBlockEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BellBlockModel extends Model {
	private static final String BELL_BODY = "bell_body";
	private final ModelPart bellBody;

	public BellBlockModel(ModelPart root) {
		super(root, RenderLayer::getEntitySolid);
		this.bellBody = root.getChild("bell_body");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		ModelPartData modelPartData2 = modelPartData.addChild(
			"bell_body", ModelPartBuilder.create().uv(0, 0).cuboid(-3.0F, -6.0F, -3.0F, 6.0F, 7.0F, 6.0F), ModelTransform.pivot(8.0F, 12.0F, 8.0F)
		);
		modelPartData2.addChild(
			"bell_base", ModelPartBuilder.create().uv(0, 13).cuboid(4.0F, 4.0F, 4.0F, 8.0F, 2.0F, 8.0F), ModelTransform.pivot(-8.0F, -12.0F, -8.0F)
		);
		return TexturedModelData.of(modelData, 32, 32);
	}

	public void update(BellBlockEntity blockEntity, float tickDelta) {
		float f = (float)blockEntity.ringTicks + tickDelta;
		float g = 0.0F;
		float h = 0.0F;
		if (blockEntity.ringing) {
			float i = MathHelper.sin(f / (float) Math.PI) / (4.0F + f / 3.0F);
			if (blockEntity.lastSideHit == Direction.NORTH) {
				g = -i;
			} else if (blockEntity.lastSideHit == Direction.SOUTH) {
				g = i;
			} else if (blockEntity.lastSideHit == Direction.EAST) {
				h = -i;
			} else if (blockEntity.lastSideHit == Direction.WEST) {
				h = i;
			}
		}

		this.bellBody.pitch = g;
		this.bellBody.roll = h;
	}
}
