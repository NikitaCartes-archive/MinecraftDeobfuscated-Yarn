package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ElderGuardianEntityRenderer extends GuardianEntityRenderer {
	public static final Identifier TEXTURE = new Identifier("textures/entity/guardian_elder.png");

	public ElderGuardianEntityRenderer(class_5617.class_5618 arg) {
		super(arg, 1.2F, EntityModelLayers.ELDER_GUARDIAN);
	}

	protected void scale(GuardianEntity guardianEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(ElderGuardianEntity.SCALE, ElderGuardianEntity.SCALE, ElderGuardianEntity.SCALE);
	}

	@Override
	public Identifier getTexture(GuardianEntity guardianEntity) {
		return TEXTURE;
	}
}
