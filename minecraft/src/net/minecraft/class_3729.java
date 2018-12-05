package net.minecraft;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityMobRenderer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.HeadEntityRenderer;
import net.minecraft.client.render.entity.model.EvilVillagerEntityModel;
import net.minecraft.entity.mob.HostileEntity;

@Environment(EnvType.CLIENT)
public abstract class class_3729 extends EntityMobRenderer<HostileEntity> {
	public class_3729(EntityRenderDispatcher entityRenderDispatcher, EvilVillagerEntityModel evilVillagerEntityModel, float f) {
		super(entityRenderDispatcher, evilVillagerEntityModel, f);
		this.addLayer(new HeadEntityRenderer(((EvilVillagerEntityModel)this.model).method_16207()));
	}

	public class_3729(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new EvilVillagerEntityModel(0.0F, 0.0F, 64, 64), 0.5F);
		this.addLayer(new HeadEntityRenderer(((EvilVillagerEntityModel)this.model).method_16207()));
	}

	protected void method_16460(HostileEntity hostileEntity, float f) {
		float g = 0.9375F;
		GlStateManager.scalef(0.9375F, 0.9375F, 0.9375F);
	}
}
