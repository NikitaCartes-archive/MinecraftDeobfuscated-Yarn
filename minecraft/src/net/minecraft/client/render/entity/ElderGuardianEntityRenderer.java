package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.mob.ElderGuardianEntity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ElderGuardianEntityRenderer extends GuardianEntityRenderer {
	private static final Identifier SKIN = new Identifier("textures/entity/guardian_elder.png");

	public ElderGuardianEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, 1.2F);
	}

	protected void method_3910(GuardianEntity guardianEntity, float f) {
		RenderSystem.scalef(ElderGuardianEntity.field_17492, ElderGuardianEntity.field_17492, ElderGuardianEntity.field_17492);
	}

	@Override
	protected Identifier method_3976(GuardianEntity guardianEntity) {
		return SKIN;
	}
}
