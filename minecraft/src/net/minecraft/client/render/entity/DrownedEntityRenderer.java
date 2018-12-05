package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_980;
import net.minecraft.client.render.entity.model.DrownedEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class DrownedEntityRenderer extends ZombieEntityRenderer {
	private static final Identifier field_4659 = new Identifier("textures/entity/zombie/drowned.png");

	public DrownedEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher, new DrownedEntityModel(0.0F, 0.0F, 64, 64));
		this.addLayer(new class_980(this));
	}

	@Override
	protected ArmorBipedEntityRenderer method_4162() {
		return new ArmorBipedEntityRenderer(this) {
			@Override
			protected void init() {
				this.modelLeggings = new DrownedEntityModel(0.5F, true);
				this.modelBody = new DrownedEntityModel(1.0F, true);
			}
		};
	}

	@Nullable
	@Override
	protected Identifier getTexture(ZombieEntity zombieEntity) {
		return field_4659;
	}

	@Override
	protected void method_4164(ZombieEntity zombieEntity, float f, float g, float h) {
		float i = zombieEntity.method_6024(h);
		super.method_4164(zombieEntity, f, g, h);
		if (i > 0.0F) {
			GlStateManager.rotatef(MathHelper.lerp(i, zombieEntity.pitch, -10.0F - zombieEntity.pitch), 1.0F, 0.0F, 0.0F);
		}
	}
}
