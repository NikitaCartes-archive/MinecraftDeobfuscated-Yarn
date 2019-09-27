package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.entity.model.SkullEntityModel;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class WitherSkullEntityRenderer extends EntityRenderer<WitherSkullEntity> {
	private static final Identifier INVINCIBLE_SKIN = new Identifier("textures/entity/wither/wither_invulnerable.png");
	private static final Identifier SKIN = new Identifier("textures/entity/wither/wither.png");
	private final SkullEntityModel model = new SkullEntityModel();

	public WitherSkullEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4159(WitherSkullEntity witherSkullEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		arg.method_22903();
		float i = 0.0625F;
		arg.method_22905(-1.0F, -1.0F, 1.0F);
		float j = MathHelper.method_22859(witherSkullEntity.prevYaw, witherSkullEntity.yaw, h);
		float k = MathHelper.lerp(h, witherSkullEntity.prevPitch, witherSkullEntity.pitch);
		int l = witherSkullEntity.getLightmapCoordinates();
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(this.method_4160(witherSkullEntity)));
		class_4608.method_23211(lv);
		this.model.render(arg, lv, 0.0F, j, k, 0.0625F, l);
		lv.method_22923();
		arg.method_22909();
		super.render(witherSkullEntity, d, e, f, g, h, arg, arg2);
	}

	public Identifier method_4160(WitherSkullEntity witherSkullEntity) {
		return witherSkullEntity.isCharged() ? INVINCIBLE_SKIN : SKIN;
	}
}
