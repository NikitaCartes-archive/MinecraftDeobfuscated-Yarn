package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.entity.model.LlamaSpitEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.LlamaSpitEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class LlamaSpitEntityRenderer extends EntityRenderer<LlamaSpitEntity> {
	private static final Identifier SKIN = new Identifier("textures/entity/llama/spit.png");
	private final LlamaSpitEntityModel<LlamaSpitEntity> model = new LlamaSpitEntityModel<>();

	public LlamaSpitEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4061(LlamaSpitEntity llamaSpitEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		arg.method_22903();
		arg.method_22904(0.0, 0.15F, 0.0);
		arg.method_22907(Vector3f.field_20705.method_23214(MathHelper.lerp(h, llamaSpitEntity.prevYaw, llamaSpitEntity.yaw) - 90.0F, true));
		arg.method_22907(Vector3f.field_20707.method_23214(MathHelper.lerp(h, llamaSpitEntity.prevPitch, llamaSpitEntity.pitch), true));
		int i = llamaSpitEntity.getLightmapCoordinates();
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(SKIN));
		class_4608.method_23211(lv);
		this.model.setAngles(llamaSpitEntity, h, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		this.model.method_22957(arg, lv, i);
		lv.method_22923();
		arg.method_22909();
		super.render(llamaSpitEntity, d, e, f, g, h, arg, arg2);
	}

	public Identifier method_4062(LlamaSpitEntity llamaSpitEntity) {
		return SKIN;
	}
}
