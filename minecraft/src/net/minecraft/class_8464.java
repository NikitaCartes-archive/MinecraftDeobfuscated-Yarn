package net.minecraft;

import java.util.Collection;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.compress.utils.Lists;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class class_8464 {
	private static final RenderLayer[] field_44416 = new RenderLayer[]{RenderLayer.getDynamicLightStencil(), RenderLayer.getDynamicLightColor()};

	public static void method_51054(class_8464.class_8465[] args, Matrix4f matrix4f, VertexConsumerProvider vertexConsumerProvider, int i) {
		int j = ColorHelper.Argb.getRed(i);
		int k = ColorHelper.Argb.getGreen(i);
		int l = ColorHelper.Argb.getBlue(i);
		int m = ColorHelper.Argb.getAlpha(i);

		for (RenderLayer renderLayer : field_44416) {
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);

			for (class_8464.class_8465 lv : args) {
				vertexConsumer.vertex(matrix4f, lv.p0.x, lv.p0.y, lv.p0.z).color(j, k, l, m).next();
				vertexConsumer.vertex(matrix4f, lv.p2.x, lv.p2.y, lv.p2.z).color(j, k, l, m).next();
				vertexConsumer.vertex(matrix4f, lv.p1.x, lv.p1.y, lv.p1.z).color(j, k, l, m).next();
			}
		}
	}

	private static List<class_8464.class_8465> method_51052() {
		float f = (float)(1.0 / Math.sqrt(2.0));
		Vector3f[] vector3fs = new Vector3f[]{
			new Vector3f(0.0F, 1.0F, 0.0F),
			new Vector3f(0.0F, -1.0F, 0.0F),
			new Vector3f(-1.0F, 0.0F, -1.0F).mul(f),
			new Vector3f(1.0F, 0.0F, -1.0F).mul(f),
			new Vector3f(1.0F, 0.0F, 1.0F).mul(f),
			new Vector3f(-1.0F, 0.0F, 1.0F).mul(f)
		};
		List<class_8464.class_8465> list = Lists.<class_8464.class_8465>newArrayList();
		list.add(new class_8464.class_8465(vector3fs[0], vector3fs[3], vector3fs[4]));
		list.add(new class_8464.class_8465(vector3fs[0], vector3fs[4], vector3fs[5]));
		list.add(new class_8464.class_8465(vector3fs[0], vector3fs[5], vector3fs[2]));
		list.add(new class_8464.class_8465(vector3fs[0], vector3fs[2], vector3fs[3]));
		list.add(new class_8464.class_8465(vector3fs[1], vector3fs[4], vector3fs[3]));
		list.add(new class_8464.class_8465(vector3fs[1], vector3fs[5], vector3fs[4]));
		list.add(new class_8464.class_8465(vector3fs[1], vector3fs[2], vector3fs[5]));
		list.add(new class_8464.class_8465(vector3fs[1], vector3fs[3], vector3fs[2]));
		return list;
	}

	public static class_8464.class_8465[] method_51053(int i) {
		List<class_8464.class_8465> list = method_51052();

		for (int j = 0; j < i; j++) {
			int k = list.size();

			for (int l = 0; l < k; l++) {
				list.addAll(((class_8464.class_8465)list.remove(0)).method_51056());
			}
		}

		return (class_8464.class_8465[])list.toArray(new class_8464.class_8465[0]);
	}

	public static class_8464.class_8465[] method_51055(int i) {
		float f = (float) (Math.PI * 2) / (float)i;
		List<Vector3f> list = Lists.<Vector3f>newArrayList();

		for (int j = 0; j < i; j++) {
			list.add(new Vector3f(MathHelper.cos((float)j * f), 0.0F, MathHelper.sin((float)j * f)));
		}

		Vector3f vector3f = new Vector3f(0.0F, 0.0F, 0.0F);
		Vector3f vector3f2 = new Vector3f(0.0F, -1.0F, 0.0F);
		List<class_8464.class_8465> list2 = Lists.<class_8464.class_8465>newArrayList();

		for (int k = 0; k < i; k++) {
			list2.add(new class_8464.class_8465((Vector3f)list.get(k), (Vector3f)list.get((k + 1) % i), vector3f));
			list2.add(new class_8464.class_8465((Vector3f)list.get((k + 1) % i), (Vector3f)list.get(k), vector3f2));
		}

		return (class_8464.class_8465[])list2.toArray(new class_8464.class_8465[0]);
	}

	@Environment(EnvType.CLIENT)
	public static record class_8465(Vector3f p0, Vector3f p1, Vector3f p2) {

		Collection<class_8464.class_8465> method_51056() {
			Vector3f vector3f = this.p0.add(this.p1, new Vector3f()).div(2.0F).normalize();
			Vector3f vector3f2 = this.p1.add(this.p2, new Vector3f()).div(2.0F).normalize();
			Vector3f vector3f3 = this.p2.add(this.p0, new Vector3f()).div(2.0F).normalize();
			return List.of(
				new class_8464.class_8465(this.p0, vector3f, vector3f3),
				new class_8464.class_8465(vector3f, this.p1, vector3f2),
				new class_8464.class_8465(vector3f2, this.p2, vector3f3),
				new class_8464.class_8465(vector3f, vector3f2, vector3f3)
			);
		}
	}
}
