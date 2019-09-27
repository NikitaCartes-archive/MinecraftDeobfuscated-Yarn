package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class DragonFireballEntityRenderer extends EntityRenderer<DragonFireballEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon_fireball.png");

	public DragonFireballEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_3906(DragonFireballEntity dragonFireballEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		arg.method_22903();
		arg.method_22905(2.0F, 2.0F, 2.0F);
		float i = 1.0F;
		float j = 0.5F;
		float k = 0.25F;
		arg.method_22907(Vector3f.field_20705.method_23214(180.0F - this.renderManager.cameraYaw, true));
		arg.method_22907(Vector3f.field_20703.method_23214((float)(this.renderManager.gameOptions.perspective == 2 ? -1 : 1) * -this.renderManager.cameraPitch, true));
		Matrix4f matrix4f = arg.method_22910();
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(SKIN));
		class_4608.method_23211(lv);
		int l = dragonFireballEntity.getLightmapCoordinates();
		lv.method_22918(matrix4f, -0.5F, -0.25F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 1.0F).method_22916(l).method_22914(0.0F, 1.0F, 0.0F).next();
		lv.method_22918(matrix4f, 0.5F, -0.25F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 1.0F).method_22916(l).method_22914(0.0F, 1.0F, 0.0F).next();
		lv.method_22918(matrix4f, 0.5F, 0.75F, 0.0F).color(255, 255, 255, 255).texture(1.0F, 0.0F).method_22916(l).method_22914(0.0F, 1.0F, 0.0F).next();
		lv.method_22918(matrix4f, -0.5F, 0.75F, 0.0F).color(255, 255, 255, 255).texture(0.0F, 0.0F).method_22916(l).method_22914(0.0F, 1.0F, 0.0F).next();
		arg.method_22909();
		lv.method_22923();
		super.render(dragonFireballEntity, d, e, f, g, h, arg, arg2);
	}

	public Identifier method_3905(DragonFireballEntity dragonFireballEntity) {
		return SKIN;
	}
}
