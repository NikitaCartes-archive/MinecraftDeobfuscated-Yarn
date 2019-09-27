package net.minecraft.client.render.block.entity;

import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.entity.BeaconBlockEntity;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BeaconBlockEntityRenderer extends BlockEntityRenderer<BeaconBlockEntity> {
	public static final Identifier BEAM_TEX = new Identifier("textures/entity/beacon_beam.png");

	public BeaconBlockEntityRenderer(BlockEntityRenderDispatcher blockEntityRenderDispatcher) {
		super(blockEntityRenderDispatcher);
	}

	public void method_3541(BeaconBlockEntity beaconBlockEntity, double d, double e, double f, float g, class_4587 arg, class_4597 arg2, int i) {
		long l = beaconBlockEntity.getWorld().getTime();
		List<BeaconBlockEntity.BeamSegment> list = beaconBlockEntity.getBeamSegments();
		int j = 0;

		for (int k = 0; k < list.size(); k++) {
			BeaconBlockEntity.BeamSegment beamSegment = (BeaconBlockEntity.BeamSegment)list.get(k);
			render(arg, arg2, g, l, j, k == list.size() - 1 ? 1024 : beamSegment.getHeight(), beamSegment.getColor());
			j += beamSegment.getHeight();
		}
	}

	private static void render(class_4587 arg, class_4597 arg2, float f, long l, int i, int j, float[] fs) {
		renderLightBeam(arg, arg2, BEAM_TEX, f, 1.0F, l, i, j, fs, 0.2F, 0.25F);
	}

	public static void renderLightBeam(
		class_4587 arg, class_4597 arg2, Identifier identifier, float f, float g, long l, int i, int j, float[] fs, float h, float k
	) {
		int m = i + j;
		arg.method_22903();
		arg.method_22904(0.5, 0.0, 0.5);
		float n = (float)Math.floorMod(l, 40L) + f;
		float o = j < 0 ? n : -n;
		float p = MathHelper.method_22450(o * 0.2F - (float)MathHelper.floor(o * 0.1F));
		float q = fs[0];
		float r = fs[1];
		float s = fs[2];
		arg.method_22903();
		arg.method_22907(Vector3f.field_20705.method_23214(n * 2.25F - 45.0F, true));
		float t = 0.0F;
		float w = 0.0F;
		float x = -h;
		float y = 0.0F;
		float z = 0.0F;
		float aa = -h;
		float ab = 0.0F;
		float ac = 1.0F;
		float ad = -1.0F + p;
		float ae = (float)j * g * (0.5F / h) + ad;
		class_4588 lv = arg2.getBuffer(BlockRenderLayer.method_23017(identifier));
		class_4608.method_23211(lv);
		method_22741(arg, lv, q, r, s, 1.0F, i, m, 0.0F, h, h, 0.0F, x, 0.0F, 0.0F, aa, 0.0F, 1.0F, ae, ad);
		lv.method_22923();
		arg.method_22909();
		t = -k;
		float u = -k;
		w = -k;
		x = -k;
		ab = 0.0F;
		ac = 1.0F;
		ad = -1.0F + p;
		ae = (float)j * g + ad;
		method_22741(arg, arg2.getBuffer(BlockRenderLayer.BEACON_BEAM), q, r, s, 0.125F, i, m, t, u, k, w, x, k, k, k, 0.0F, 1.0F, ae, ad);
		arg.method_22909();
	}

	private static void method_22741(
		class_4587 arg,
		class_4588 arg2,
		float f,
		float g,
		float h,
		float i,
		int j,
		int k,
		float l,
		float m,
		float n,
		float o,
		float p,
		float q,
		float r,
		float s,
		float t,
		float u,
		float v,
		float w
	) {
		Matrix4f matrix4f = arg.method_22910();
		method_22740(matrix4f, arg2, f, g, h, i, j, k, l, m, n, o, t, u, v, w);
		method_22740(matrix4f, arg2, f, g, h, i, j, k, r, s, p, q, t, u, v, w);
		method_22740(matrix4f, arg2, f, g, h, i, j, k, n, o, r, s, t, u, v, w);
		method_22740(matrix4f, arg2, f, g, h, i, j, k, p, q, l, m, t, u, v, w);
	}

	private static void method_22740(
		Matrix4f matrix4f, class_4588 arg, float f, float g, float h, float i, int j, int k, float l, float m, float n, float o, float p, float q, float r, float s
	) {
		method_23076(matrix4f, arg, f, g, h, i, k, l, m, q, r);
		method_23076(matrix4f, arg, f, g, h, i, j, l, m, q, s);
		method_23076(matrix4f, arg, f, g, h, i, j, n, o, p, s);
		method_23076(matrix4f, arg, f, g, h, i, k, n, o, p, r);
	}

	private static void method_23076(Matrix4f matrix4f, class_4588 arg, float f, float g, float h, float i, int j, float k, float l, float m, float n) {
		arg.method_22918(matrix4f, k, (float)j, l).method_22915(f, g, h, i).texture(m, n).method_22916(15728880).method_22914(0.0F, 1.0F, 0.0F).next();
	}

	public boolean method_3542(BeaconBlockEntity beaconBlockEntity) {
		return true;
	}
}
