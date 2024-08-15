package net.minecraft.client.render.entity.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.state.ZombifiedPiglinEntityRenderState;

@Environment(EnvType.CLIENT)
public class ZombifiedPiglinEntityModel extends PiglinBaseEntityModel<ZombifiedPiglinEntityRenderState> {
	public ZombifiedPiglinEntityModel(ModelPart modelPart) {
		super(modelPart);
	}

	public void setAngles(ZombifiedPiglinEntityRenderState zombifiedPiglinEntityRenderState) {
		super.setAngles(zombifiedPiglinEntityRenderState);
		CrossbowPosing.meleeAttack(
			this.leftArm,
			this.rightArm,
			zombifiedPiglinEntityRenderState.attacking,
			zombifiedPiglinEntityRenderState.handSwingProgress,
			zombifiedPiglinEntityRenderState.age
		);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		this.leftSleeve.visible = visible;
		this.rightSleeve.visible = visible;
		this.leftPants.visible = visible;
		this.rightPants.visible = visible;
		this.jacket.visible = visible;
	}
}
