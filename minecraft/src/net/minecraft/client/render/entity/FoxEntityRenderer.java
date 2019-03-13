package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4043;
import net.minecraft.client.render.entity.model.FoxModel;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class FoxEntityRenderer extends MobEntityRenderer<FoxEntity, FoxModel<FoxEntity>> {
	private static final Identifier field_18026 = new Identifier("textures/entity/fox/fox.png");
	private static final Identifier field_18027 = new Identifier("textures/entity/fox/fox_sleep.png");
	private static final Identifier field_18028 = new Identifier("textures/entity/fox/snow_fox.png");
	private static final Identifier field_18029 = new Identifier("textures/entity/fox/snow_fox_sleep.png");

	public FoxEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new FoxModel<>(), 0.4F);
		this.method_4046(new class_4043(this));
	}

	protected void method_18334(FoxEntity foxEntity, float f, float g, float h) {
		super.setupTransforms(foxEntity, f, g, h);
		if (foxEntity.isChasing() || foxEntity.isWalking()) {
			GlStateManager.rotatef(-MathHelper.lerp(h, foxEntity.prevPitch, foxEntity.pitch), 1.0F, 0.0F, 0.0F);
		}
	}

	@Nullable
	protected Identifier method_18333(FoxEntity foxEntity) {
		if (foxEntity.getType() == FoxEntity.Type.field_17996) {
			return foxEntity.isSleeping() ? field_18027 : field_18026;
		} else {
			return foxEntity.isSleeping() ? field_18029 : field_18028;
		}
	}
}
