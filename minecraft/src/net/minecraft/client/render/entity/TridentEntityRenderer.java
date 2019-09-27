package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class TridentEntityRenderer extends EntityRenderer<TridentEntity> {
	public static final Identifier SKIN = new Identifier("textures/entity/trident.png");
	private final TridentEntityModel model = new TridentEntityModel();

	public TridentEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
	}

	public void method_4133(TridentEntity tridentEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		arg.method_22903();
		arg.method_22907(Vector3f.field_20705.method_23214(MathHelper.lerp(h, tridentEntity.prevYaw, tridentEntity.yaw) - 90.0F, true));
		arg.method_22907(Vector3f.field_20707.method_23214(MathHelper.lerp(h, tridentEntity.prevPitch, tridentEntity.pitch) + 90.0F, true));
		int i = tridentEntity.getLightmapCoordinates();
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(this.method_4134(tridentEntity)));
		class_4608.method_23211(lv);
		this.model.renderItem(arg, lv, i);
		lv.method_22923();
		arg.method_22909();
		super.render(tridentEntity, d, e, f, g, h, arg, arg2);
	}

	public Identifier method_4134(TridentEntity tridentEntity) {
		return SKIN;
	}
}
