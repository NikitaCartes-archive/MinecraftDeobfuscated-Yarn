package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixStack;

@Environment(EnvType.CLIENT)
public class EnderDragonEntityRenderer extends EntityRenderer<EnderDragonEntity> {
	public static final Identifier CRYSTAL_BEAM = new Identifier("textures/entity/end_crystal/end_crystal_beam.png");
	private static final Identifier EXPLOSION_TEX = new Identifier("textures/entity/enderdragon/dragon_exploding.png");
	private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon.png");
	private static final Identifier field_21006 = new Identifier("textures/entity/enderdragon/dragon_eyes.png");
	private static final float field_21007 = (float)(Math.sqrt(3.0) / 2.0);
	private final EnderDragonEntityRenderer.DragonEntityModel field_21008 = new EnderDragonEntityRenderer.DragonEntityModel(0.0F);

	public EnderDragonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.field_4673 = 0.5F;
	}

	public void method_3918(
		EnderDragonEntity enderDragonEntity,
		double d,
		double e,
		double f,
		float g,
		float h,
		MatrixStack matrixStack,
		LayeredVertexConsumerStorage layeredVertexConsumerStorage
	) {
		matrixStack.push();
		float i = (float)enderDragonEntity.method_6817(7, h)[0];
		float j = (float)(enderDragonEntity.method_6817(5, h)[1] - enderDragonEntity.method_6817(10, h)[1]);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(-i, true));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(j * 10.0F, true));
		matrixStack.translate(0.0, 0.0, 1.0);
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		float k = 0.0625F;
		matrixStack.translate(0.0, -1.501F, 0.0);
		boolean bl = enderDragonEntity.hurtTime > 0;
		int l = enderDragonEntity.getLightmapCoordinates();
		if (enderDragonEntity.field_7031 > 0) {
			float m = (float)enderDragonEntity.field_7031 / 200.0F;
			VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23020(EXPLOSION_TEX, false, true, true, m, false, true));
			OverlayTexture.clearDefaultOverlay(vertexConsumer);
			this.field_21008.method_17137(matrixStack, vertexConsumer, enderDragonEntity, 0.0625F, h, l);
			vertexConsumer.clearDefaultOverlay();
			VertexConsumer vertexConsumer2 = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23020(SKIN, false, true, true, 0.1F, true, true));
			vertexConsumer2.defaultOverlay(OverlayTexture.getU(0.0F), OverlayTexture.getV(bl));
			this.field_21008.method_17137(matrixStack, vertexConsumer2, enderDragonEntity, 0.0625F, h, l);
			vertexConsumer2.clearDefaultOverlay();
		} else {
			VertexConsumer vertexConsumer3 = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23019(SKIN, false, true, true));
			vertexConsumer3.defaultOverlay(OverlayTexture.getU(0.0F), OverlayTexture.getV(bl));
			this.field_21008.method_17137(matrixStack, vertexConsumer3, enderDragonEntity, 0.0625F, h, l);
			vertexConsumer3.clearDefaultOverlay();
		}

		VertexConsumer vertexConsumer3 = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23026(field_21006));
		OverlayTexture.clearDefaultOverlay(vertexConsumer3);
		this.field_21008.method_17137(matrixStack, vertexConsumer3, enderDragonEntity, 0.0625F, h, l);
		vertexConsumer3.clearDefaultOverlay();
		if (enderDragonEntity.field_7031 > 0) {
			float n = ((float)enderDragonEntity.field_7031 + h) / 200.0F;
			float o = 0.0F;
			if (n > 0.8F) {
				o = (n - 0.8F) / 0.2F;
			}

			Random random = new Random(432L);
			VertexConsumer vertexConsumer4 = layeredVertexConsumerStorage.getBuffer(RenderLayer.LIGHTNING);
			matrixStack.push();
			matrixStack.translate(0.0, -1.0, -2.0);

			for (int p = 0; (float)p < (n + n * n) / 2.0F * 60.0F; p++) {
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(random.nextFloat() * 360.0F, true));
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(random.nextFloat() * 360.0F, true));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(random.nextFloat() * 360.0F, true));
				matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(random.nextFloat() * 360.0F, true));
				matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(random.nextFloat() * 360.0F, true));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(random.nextFloat() * 360.0F + n * 90.0F, true));
				float q = random.nextFloat() * 20.0F + 5.0F + o * 10.0F;
				float r = random.nextFloat() * 2.0F + 1.0F + o * 2.0F;
				Matrix4f matrix4f = matrixStack.peek();
				int s = (int)(255.0F * (1.0F - o));
				method_23157(vertexConsumer4, matrix4f, s);
				method_23156(vertexConsumer4, matrix4f, q, r);
				method_23158(vertexConsumer4, matrix4f, q, r);
				method_23157(vertexConsumer4, matrix4f, s);
				method_23158(vertexConsumer4, matrix4f, q, r);
				method_23159(vertexConsumer4, matrix4f, q, r);
				method_23157(vertexConsumer4, matrix4f, s);
				method_23159(vertexConsumer4, matrix4f, q, r);
				method_23156(vertexConsumer4, matrix4f, q, r);
			}

			matrixStack.pop();
		}

		matrixStack.pop();
		if (enderDragonEntity.connectedCrystal != null) {
			matrixStack.push();
			float n = (float)(enderDragonEntity.connectedCrystal.x - MathHelper.lerp((double)h, enderDragonEntity.prevX, enderDragonEntity.x));
			float o = (float)(enderDragonEntity.connectedCrystal.y - MathHelper.lerp((double)h, enderDragonEntity.prevY, enderDragonEntity.y));
			float t = (float)(enderDragonEntity.connectedCrystal.z - MathHelper.lerp((double)h, enderDragonEntity.prevZ, enderDragonEntity.z));
			renderCrystalBeam(
				n,
				o + EnderCrystalEntityRenderer.method_23155(enderDragonEntity.connectedCrystal, h),
				t,
				h,
				enderDragonEntity.age,
				matrixStack,
				layeredVertexConsumerStorage,
				l
			);
			matrixStack.pop();
		}

		super.render(enderDragonEntity, d, e, f, g, h, matrixStack, layeredVertexConsumerStorage);
	}

	private static void method_23157(VertexConsumer vertexConsumer, Matrix4f matrix4f, int i) {
		vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(255, 255, 255, i).next();
		vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, 0.0F).color(255, 255, 255, i).next();
	}

	private static void method_23156(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g) {
		vertexConsumer.vertex(matrix4f, -field_21007 * g, f, -0.5F * g).color(255, 0, 255, 0).next();
	}

	private static void method_23158(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g) {
		vertexConsumer.vertex(matrix4f, field_21007 * g, f, -0.5F * g).color(255, 0, 255, 0).next();
	}

	private static void method_23159(VertexConsumer vertexConsumer, Matrix4f matrix4f, float f, float g) {
		vertexConsumer.vertex(matrix4f, 0.0F, f, 1.0F * g).color(255, 0, 255, 0).next();
	}

	public static void renderCrystalBeam(
		float f, float g, float h, float i, int j, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int k
	) {
		float l = MathHelper.sqrt(f * f + h * h);
		float m = MathHelper.sqrt(f * f + g * g + h * h);
		matrixStack.push();
		matrixStack.translate(0.0, 2.0, 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)(-Math.atan2((double)h, (double)f)) - (float) (Math.PI / 2), false));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion((float)(-Math.atan2((double)l, (double)g)) - (float) (Math.PI / 2), false));
		VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23019(CRYSTAL_BEAM, false, true, true));
		OverlayTexture.clearDefaultOverlay(vertexConsumer);
		float n = 0.0F - ((float)j + i) * 0.01F;
		float o = MathHelper.sqrt(f * f + g * g + h * h) / 32.0F - ((float)j + i) * 0.01F;
		int p = 8;
		float q = 0.0F;
		float r = 0.75F;
		float s = 0.0F;
		Matrix4f matrix4f = matrixStack.peek();

		for (int t = 1; t <= 8; t++) {
			float u = MathHelper.sin((float)(t % 8) * (float) (Math.PI * 2) / 8.0F) * 0.75F;
			float v = MathHelper.cos((float)(t % 8) * (float) (Math.PI * 2) / 8.0F) * 0.75F;
			float w = (float)(t % 8) / 8.0F;
			vertexConsumer.vertex(matrix4f, q * 0.2F, r * 0.2F, 0.0F).color(0, 0, 0, 255).texture(s, n).light(k).normal(0.0F, 1.0F, 0.0F).next();
			vertexConsumer.vertex(matrix4f, q, r, m).color(255, 255, 255, 255).texture(s, o).light(k).normal(0.0F, 1.0F, 0.0F).next();
			vertexConsumer.vertex(matrix4f, u, v, m).color(255, 255, 255, 255).texture(w, o).light(k).normal(0.0F, 1.0F, 0.0F).next();
			vertexConsumer.vertex(matrix4f, u * 0.2F, v * 0.2F, 0.0F).color(0, 0, 0, 255).texture(w, n).light(k).normal(0.0F, 1.0F, 0.0F).next();
			q = u;
			r = v;
			s = w;
		}

		matrixStack.pop();
		vertexConsumer.clearDefaultOverlay();
	}

	public Identifier method_3914(EnderDragonEntity enderDragonEntity) {
		return SKIN;
	}

	@Environment(EnvType.CLIENT)
	public static class DragonEntityModel extends Model {
		private final ModelPart head;
		private final ModelPart neck;
		private final ModelPart jaw;
		private final ModelPart body;
		private final ModelPart rearLeg;
		private final ModelPart frontLeg;
		private final ModelPart rearLegTip;
		private final ModelPart frontLegTip;
		private final ModelPart rearFoot;
		private final ModelPart frontFoot;
		private final ModelPart wing;
		private final ModelPart wingTip;

		public DragonEntityModel(float f) {
			this.textureWidth = 256;
			this.textureHeight = 256;
			float g = -16.0F;
			this.head = new ModelPart(this);
			this.head.addCuboid("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, f, 176, 44);
			this.head.addCuboid("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, f, 112, 30);
			this.head.mirror = true;
			this.head.addCuboid("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
			this.head.addCuboid("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
			this.head.mirror = false;
			this.head.addCuboid("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, f, 0, 0);
			this.head.addCuboid("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, f, 112, 0);
			this.jaw = new ModelPart(this);
			this.jaw.setPivot(0.0F, 4.0F, -8.0F);
			this.jaw.addCuboid("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, f, 176, 65);
			this.head.addChild(this.jaw);
			this.neck = new ModelPart(this);
			this.neck.addCuboid("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, f, 192, 104);
			this.neck.addCuboid("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, f, 48, 0);
			this.body = new ModelPart(this);
			this.body.setPivot(0.0F, 4.0F, 8.0F);
			this.body.addCuboid("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, f, 0, 0);
			this.body.addCuboid("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, f, 220, 53);
			this.body.addCuboid("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, f, 220, 53);
			this.body.addCuboid("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, f, 220, 53);
			this.wing = new ModelPart(this);
			this.wing.setPivot(-12.0F, 5.0F, 2.0F);
			this.wing.addCuboid("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, f, 112, 88);
			this.wing.addCuboid("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, f, -56, 88);
			this.wingTip = new ModelPart(this);
			this.wingTip.setPivot(-56.0F, 0.0F, 0.0F);
			this.wingTip.addCuboid("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, f, 112, 136);
			this.wingTip.addCuboid("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, f, -56, 144);
			this.wing.addChild(this.wingTip);
			this.frontLeg = new ModelPart(this);
			this.frontLeg.setPivot(-12.0F, 20.0F, 2.0F);
			this.frontLeg.addCuboid("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, f, 112, 104);
			this.frontLegTip = new ModelPart(this);
			this.frontLegTip.setPivot(0.0F, 20.0F, -1.0F);
			this.frontLegTip.addCuboid("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, f, 226, 138);
			this.frontLeg.addChild(this.frontLegTip);
			this.frontFoot = new ModelPart(this);
			this.frontFoot.setPivot(0.0F, 23.0F, 0.0F);
			this.frontFoot.addCuboid("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, f, 144, 104);
			this.frontLegTip.addChild(this.frontFoot);
			this.rearLeg = new ModelPart(this);
			this.rearLeg.setPivot(-16.0F, 16.0F, 42.0F);
			this.rearLeg.addCuboid("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, f, 0, 0);
			this.rearLegTip = new ModelPart(this);
			this.rearLegTip.setPivot(0.0F, 32.0F, -4.0F);
			this.rearLegTip.addCuboid("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, f, 196, 0);
			this.rearLeg.addChild(this.rearLegTip);
			this.rearFoot = new ModelPart(this);
			this.rearFoot.setPivot(0.0F, 31.0F, 4.0F);
			this.rearFoot.addCuboid("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, f, 112, 0);
			this.rearLegTip.addChild(this.rearFoot);
		}

		public void method_17137(MatrixStack matrixStack, VertexConsumer vertexConsumer, EnderDragonEntity enderDragonEntity, float f, float g, int i) {
			matrixStack.push();
			float h = MathHelper.lerp(g, enderDragonEntity.field_7019, enderDragonEntity.field_7030);
			this.jaw.pitch = (float)(Math.sin((double)(h * (float) (Math.PI * 2))) + 1.0) * 0.2F;
			float j = (float)(Math.sin((double)(h * (float) (Math.PI * 2) - 1.0F)) + 1.0);
			j = (j * j + j * 2.0F) * 0.05F;
			matrixStack.translate(0.0, (double)(j - 2.0F), -3.0);
			matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(j * 2.0F, true));
			float k = 0.0F;
			float l = 20.0F;
			float m = -12.0F;
			float n = 1.5F;
			double[] ds = enderDragonEntity.method_6817(6, g);
			float o = MathHelper.method_22860(enderDragonEntity.method_6817(5, g)[0] - enderDragonEntity.method_6817(10, g)[0]);
			float p = MathHelper.method_22860(enderDragonEntity.method_6817(5, g)[0] + (double)(o / 2.0F));
			float q = h * (float) (Math.PI * 2);

			for (int r = 0; r < 5; r++) {
				double[] es = enderDragonEntity.method_6817(5 - r, g);
				float s = (float)Math.cos((double)((float)r * 0.45F + q)) * 0.15F;
				this.neck.yaw = MathHelper.method_22860(es[0] - ds[0]) * (float) (Math.PI / 180.0) * 1.5F;
				this.neck.pitch = s + enderDragonEntity.method_6823(r, ds, es) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
				this.neck.roll = -MathHelper.method_22860(es[0] - (double)p) * (float) (Math.PI / 180.0) * 1.5F;
				this.neck.pivotY = l;
				this.neck.pivotZ = m;
				this.neck.pivotX = k;
				l = (float)((double)l + Math.sin((double)this.neck.pitch) * 10.0);
				m = (float)((double)m - Math.cos((double)this.neck.yaw) * Math.cos((double)this.neck.pitch) * 10.0);
				k = (float)((double)k - Math.sin((double)this.neck.yaw) * Math.cos((double)this.neck.pitch) * 10.0);
				this.neck.render(matrixStack, vertexConsumer, f, i, null);
			}

			this.head.pivotY = l;
			this.head.pivotZ = m;
			this.head.pivotX = k;
			double[] fs = enderDragonEntity.method_6817(0, g);
			this.head.yaw = MathHelper.method_22860(fs[0] - ds[0]) * (float) (Math.PI / 180.0);
			this.head.pitch = MathHelper.method_22860((double)enderDragonEntity.method_6823(6, ds, fs)) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
			this.head.roll = -MathHelper.method_22860(fs[0] - (double)p) * (float) (Math.PI / 180.0);
			this.head.render(matrixStack, vertexConsumer, f, i, null);
			matrixStack.push();
			matrixStack.translate(0.0, 1.0, 0.0);
			matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(-o * 1.5F, true));
			matrixStack.translate(0.0, -1.0, 0.0);
			this.body.roll = 0.0F;
			this.body.render(matrixStack, vertexConsumer, f, i, null);

			for (int t = 0; t < 2; t++) {
				float s = h * (float) (Math.PI * 2);
				this.wing.pitch = 0.125F - (float)Math.cos((double)s) * 0.2F;
				this.wing.yaw = 0.25F;
				this.wing.roll = (float)(Math.sin((double)s) + 0.125) * 0.8F;
				this.wingTip.roll = -((float)(Math.sin((double)(s + 2.0F)) + 0.5)) * 0.75F;
				this.rearLeg.pitch = 1.0F + j * 0.1F;
				this.rearLegTip.pitch = 0.5F + j * 0.1F;
				this.rearFoot.pitch = 0.75F + j * 0.1F;
				this.frontLeg.pitch = 1.3F + j * 0.1F;
				this.frontLegTip.pitch = -0.5F - j * 0.1F;
				this.frontFoot.pitch = 0.75F + j * 0.1F;
				this.wing.render(matrixStack, vertexConsumer, f, i, null);
				this.frontLeg.render(matrixStack, vertexConsumer, f, i, null);
				this.rearLeg.render(matrixStack, vertexConsumer, f, i, null);
				matrixStack.scale(-1.0F, 1.0F, 1.0F);
			}

			matrixStack.pop();
			float u = -((float)Math.sin((double)(h * (float) (Math.PI * 2)))) * 0.0F;
			q = h * (float) (Math.PI * 2);
			l = 10.0F;
			m = 60.0F;
			k = 0.0F;
			ds = enderDragonEntity.method_6817(11, g);

			for (int v = 0; v < 12; v++) {
				fs = enderDragonEntity.method_6817(12 + v, g);
				u = (float)((double)u + Math.sin((double)((float)v * 0.45F + q)) * 0.05F);
				this.neck.yaw = (MathHelper.method_22860(fs[0] - ds[0]) * 1.5F + 180.0F) * (float) (Math.PI / 180.0);
				this.neck.pitch = u + (float)(fs[1] - ds[1]) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
				this.neck.roll = MathHelper.method_22860(fs[0] - (double)p) * (float) (Math.PI / 180.0) * 1.5F;
				this.neck.pivotY = l;
				this.neck.pivotZ = m;
				this.neck.pivotX = k;
				l = (float)((double)l + Math.sin((double)this.neck.pitch) * 10.0);
				m = (float)((double)m - Math.cos((double)this.neck.yaw) * Math.cos((double)this.neck.pitch) * 10.0);
				k = (float)((double)k - Math.sin((double)this.neck.yaw) * Math.cos((double)this.neck.pitch) * 10.0);
				this.neck.render(matrixStack, vertexConsumer, f, i, null);
			}

			matrixStack.pop();
		}
	}
}
