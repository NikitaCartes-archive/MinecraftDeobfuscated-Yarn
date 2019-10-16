package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ElderGuardianEntityRenderer extends GuardianEntityRenderer {
	private static final Identifier SKIN = new Identifier("textures/entity/guardian_elder.png");

	public ElderGuardianEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, 1.2F);
	}

	protected void method_3910(GuardianEntity guardianEntity, MatrixStack matrixStack, float f) {
		matrixStack.scale(ElderGuardianEntity.field_17492, ElderGuardianEntity.field_17492, ElderGuardianEntity.field_17492);
	}

	@Override
	public Identifier method_3976(GuardianEntity guardianEntity) {
		return SKIN;
	}
}
