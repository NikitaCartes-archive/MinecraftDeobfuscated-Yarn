package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.model.LargePufferfishEntityModel;
import net.minecraft.client.render.entity.model.MediumPufferfishEntityModel;
import net.minecraft.client.render.entity.model.SmallPufferfishEntityModel;
import net.minecraft.entity.passive.PufferfishEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class PufferfishEntityRenderer extends EntityMobRenderer<PufferfishEntity> {
	private static final Identifier field_4762 = new Identifier("textures/entity/fish/pufferfish.png");
	private int field_4765;
	private final SmallPufferfishEntityModel smallModel = new SmallPufferfishEntityModel();
	private final MediumPufferfishEntityModel mediumModel = new MediumPufferfishEntityModel();
	private final LargePufferfishEntityModel largeModel = new LargePufferfishEntityModel();

	public PufferfishEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new LargePufferfishEntityModel(), 0.1F);
		this.field_4765 = 3;
	}

	@Nullable
	protected Identifier getTexture(PufferfishEntity pufferfishEntity) {
		return field_4762;
	}

	public void method_4094(PufferfishEntity pufferfishEntity, double d, double e, double f, float g, float h) {
		int i = pufferfishEntity.method_6594();
		if (i != this.field_4765) {
			if (i == 0) {
				this.model = this.smallModel;
			} else if (i == 1) {
				this.model = this.mediumModel;
			} else {
				this.model = this.largeModel;
			}
		}

		this.field_4765 = i;
		this.field_4673 = 0.1F + 0.1F * (float)i;
		super.method_4072(pufferfishEntity, d, e, f, g, h);
	}

	protected void method_4095(PufferfishEntity pufferfishEntity, float f, float g, float h) {
		GlStateManager.translatef(0.0F, MathHelper.cos(f * 0.05F) * 0.08F, 0.0F);
		super.method_4058(pufferfishEntity, f, g, h);
	}
}
