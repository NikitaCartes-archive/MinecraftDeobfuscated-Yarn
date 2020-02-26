package net.minecraft.client.render.entity;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class EnderDragonEntityRenderer extends EntityRenderer<EnderDragonEntity> {
	public static final Identifier CRYSTAL_BEAM_TEX = new Identifier("textures/entity/end_crystal/end_crystal_beam.png");
	private static final Identifier EXPLOSION_TEX = new Identifier("textures/entity/enderdragon/dragon_exploding.png");
	private static final Identifier SKIN = new Identifier("textures/entity/enderdragon/dragon.png");
	private static final Identifier EYES_TEX = new Identifier("textures/entity/enderdragon/dragon_eyes.png");
	private static final RenderLayer field_21737 = RenderLayer.getEntityCutoutNoCull(SKIN);
	private static final RenderLayer field_21738 = RenderLayer.getEntityDecal(SKIN);
	private static final RenderLayer field_21739 = RenderLayer.getEyes(EYES_TEX);
	private static final RenderLayer field_21740 = RenderLayer.getEntitySmoothCutout(CRYSTAL_BEAM_TEX);
	private static final float field_21007 = (float)(Math.sqrt(3.0) / 2.0);
	private final EnderDragonEntityRenderer.DragonEntityModel field_21008 = new EnderDragonEntityRenderer.DragonEntityModel();

	public EnderDragonEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
		super(entityRenderDispatcher);
		this.shadowSize = 0.5F;
	}

	public void render(EnderDragonEntity enderDragonEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		float h = (float)enderDragonEntity.method_6817(7, g)[0];
		float j = (float)(enderDragonEntity.method_6817(5, g)[1] - enderDragonEntity.method_6817(10, g)[1]);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-h));
		matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(j * 10.0F));
		matrixStack.translate(0.0, 0.0, 1.0);
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		matrixStack.translate(0.0, -1.501F, 0.0);
		boolean bl = enderDragonEntity.hurtTime > 0;
		this.field_21008.animateModel(enderDragonEntity, 0.0F, 0.0F, g);
		if (enderDragonEntity.field_7031 > 0) {
			float k = (float)enderDragonEntity.field_7031 / 200.0F;
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityAlpha(EXPLOSION_TEX, k));
			this.field_21008.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
			VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(field_21738);
			this.field_21008.render(matrixStack, vertexConsumer2, i, OverlayTexture.getUv(0.0F, bl), 1.0F, 1.0F, 1.0F, 1.0F);
		} else {
			VertexConsumer vertexConsumer3 = vertexConsumerProvider.getBuffer(field_21737);
			this.field_21008.render(matrixStack, vertexConsumer3, i, OverlayTexture.getUv(0.0F, bl), 1.0F, 1.0F, 1.0F, 1.0F);
		}

		VertexConsumer vertexConsumer3 = vertexConsumerProvider.getBuffer(field_21739);
		this.field_21008.render(matrixStack, vertexConsumer3, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		if (enderDragonEntity.field_7031 > 0) {
			float l = ((float)enderDragonEntity.field_7031 + g) / 200.0F;
			float m = 0.0F;
			if (l > 0.8F) {
				m = (l - 0.8F) / 0.2F;
			}

			Random random = new Random(432L);
			VertexConsumer vertexConsumer4 = vertexConsumerProvider.getBuffer(RenderLayer.getLightning());
			matrixStack.push();
			matrixStack.translate(0.0, -1.0, -2.0);

			for (int n = 0; (float)n < (l + l * l) / 2.0F * 60.0F; n++) {
				matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(random.nextFloat() * 360.0F));
				matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(random.nextFloat() * 360.0F));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(random.nextFloat() * 360.0F));
				matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(random.nextFloat() * 360.0F));
				matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(random.nextFloat() * 360.0F));
				matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(random.nextFloat() * 360.0F + l * 90.0F));
				float o = random.nextFloat() * 20.0F + 5.0F + m * 10.0F;
				float p = random.nextFloat() * 2.0F + 1.0F + m * 2.0F;
				Matrix4f matrix4f = matrixStack.peek().getModel();
				int q = (int)(255.0F * (1.0F - m));
				method_23157(vertexConsumer4, matrix4f, q);
				method_23156(vertexConsumer4, matrix4f, o, p);
				method_23158(vertexConsumer4, matrix4f, o, p);
				method_23157(vertexConsumer4, matrix4f, q);
				method_23158(vertexConsumer4, matrix4f, o, p);
				method_23159(vertexConsumer4, matrix4f, o, p);
				method_23157(vertexConsumer4, matrix4f, q);
				method_23159(vertexConsumer4, matrix4f, o, p);
				method_23156(vertexConsumer4, matrix4f, o, p);
			}

			matrixStack.pop();
		}

		matrixStack.pop();
		if (enderDragonEntity.connectedCrystal != null) {
			matrixStack.push();
			float l = (float)(enderDragonEntity.connectedCrystal.getX() - MathHelper.lerp((double)g, enderDragonEntity.prevX, enderDragonEntity.getX()));
			float m = (float)(enderDragonEntity.connectedCrystal.getY() - MathHelper.lerp((double)g, enderDragonEntity.prevY, enderDragonEntity.getY()));
			float r = (float)(enderDragonEntity.connectedCrystal.getZ() - MathHelper.lerp((double)g, enderDragonEntity.prevZ, enderDragonEntity.getZ()));
			renderCrystalBeam(
				l, m + EnderCrystalEntityRenderer.method_23155(enderDragonEntity.connectedCrystal, g), r, g, enderDragonEntity.age, matrixStack, vertexConsumerProvider, i
			);
			matrixStack.pop();
		}

		super.render(enderDragonEntity, f, g, matrixStack, vertexConsumerProvider, i);
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

	public static void renderCrystalBeam(float f, float g, float h, float i, int j, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int k) {
		float l = MathHelper.sqrt(f * f + h * h);
		float m = MathHelper.sqrt(f * f + g * g + h * h);
		matrixStack.push();
		matrixStack.translate(0.0, 2.0, 0.0);
		matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float)(-Math.atan2((double)h, (double)f)) - (float) (Math.PI / 2)));
		matrixStack.multiply(Vector3f.POSITIVE_X.getRadialQuaternion((float)(-Math.atan2((double)l, (double)g)) - (float) (Math.PI / 2)));
		VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(field_21740);
		float n = 0.0F - ((float)j + i) * 0.01F;
		float o = MathHelper.sqrt(f * f + g * g + h * h) / 32.0F - ((float)j + i) * 0.01F;
		int p = 8;
		float q = 0.0F;
		float r = 0.75F;
		float s = 0.0F;
		MatrixStack.Entry entry = matrixStack.peek();
		Matrix4f matrix4f = entry.getModel();
		Matrix3f matrix3f = entry.getNormal();

		for (int t = 1; t <= 8; t++) {
			float u = MathHelper.sin((float)t * (float) (Math.PI * 2) / 8.0F) * 0.75F;
			float v = MathHelper.cos((float)t * (float) (Math.PI * 2) / 8.0F) * 0.75F;
			float w = (float)t / 8.0F;
			vertexConsumer.vertex(matrix4f, q * 0.2F, r * 0.2F, 0.0F)
				.color(0, 0, 0, 255)
				.texture(s, n)
				.overlay(OverlayTexture.DEFAULT_UV)
				.light(k)
				.normal(matrix3f, 0.0F, -1.0F, 0.0F)
				.next();
			vertexConsumer.vertex(matrix4f, q, r, m)
				.color(255, 255, 255, 255)
				.texture(s, o)
				.overlay(OverlayTexture.DEFAULT_UV)
				.light(k)
				.normal(matrix3f, 0.0F, -1.0F, 0.0F)
				.next();
			vertexConsumer.vertex(matrix4f, u, v, m)
				.color(255, 255, 255, 255)
				.texture(w, o)
				.overlay(OverlayTexture.DEFAULT_UV)
				.light(k)
				.normal(matrix3f, 0.0F, -1.0F, 0.0F)
				.next();
			vertexConsumer.vertex(matrix4f, u * 0.2F, v * 0.2F, 0.0F)
				.color(0, 0, 0, 255)
				.texture(w, n)
				.overlay(OverlayTexture.DEFAULT_UV)
				.light(k)
				.normal(matrix3f, 0.0F, -1.0F, 0.0F)
				.next();
			q = u;
			r = v;
			s = w;
		}

		matrixStack.pop();
	}

	public Identifier getTexture(EnderDragonEntity enderDragonEntity) {
		return SKIN;
	}

	@Environment(EnvType.CLIENT)
	public static class DragonEntityModel extends EntityModel<EnderDragonEntity> {
		private final ModelPart head;
		private final ModelPart neck;
		private final ModelPart jaw;
		private final ModelPart body;
		private ModelPart wing;
		private ModelPart field_21548;
		private ModelPart field_21549;
		private ModelPart field_21550;
		private ModelPart field_21551;
		private ModelPart field_21552;
		private ModelPart field_21553;
		private ModelPart field_21554;
		private ModelPart field_21555;
		private ModelPart wingTip;
		private ModelPart frontLeg;
		private ModelPart frontLegTip;
		private ModelPart frontFoot;
		private ModelPart rearLeg;
		private ModelPart rearLegTip;
		private ModelPart rearFoot;
		@Nullable
		private EnderDragonEntity dragon;
		private float field_21442;

		public DragonEntityModel() {
			this.textureWidth = 256;
			this.textureHeight = 256;
			float f = -16.0F;
			this.head = new ModelPart(this);
			this.head.addCuboid("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 0.0F, 176, 44);
			this.head.addCuboid("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, 0.0F, 112, 30);
			this.head.mirror = true;
			this.head.addCuboid("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
			this.head.addCuboid("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
			this.head.mirror = false;
			this.head.addCuboid("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
			this.head.addCuboid("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
			this.jaw = new ModelPart(this);
			this.jaw.setPivot(0.0F, 4.0F, -8.0F);
			this.jaw.addCuboid("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, 0.0F, 176, 65);
			this.head.addChild(this.jaw);
			this.neck = new ModelPart(this);
			this.neck.addCuboid("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, 0.0F, 192, 104);
			this.neck.addCuboid("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, 0.0F, 48, 0);
			this.body = new ModelPart(this);
			this.body.setPivot(0.0F, 4.0F, 8.0F);
			this.body.addCuboid("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, 0.0F, 0, 0);
			this.body.addCuboid("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, 0.0F, 220, 53);
			this.body.addCuboid("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, 0.0F, 220, 53);
			this.body.addCuboid("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, 0.0F, 220, 53);
			this.wing = new ModelPart(this);
			this.wing.mirror = true;
			this.wing.setPivot(12.0F, 5.0F, 2.0F);
			this.wing.addCuboid("bone", 0.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
			this.wing.addCuboid("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 88);
			this.field_21548 = new ModelPart(this);
			this.field_21548.mirror = true;
			this.field_21548.setPivot(56.0F, 0.0F, 0.0F);
			this.field_21548.addCuboid("bone", 0.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
			this.field_21548.addCuboid("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 144);
			this.wing.addChild(this.field_21548);
			this.field_21549 = new ModelPart(this);
			this.field_21549.setPivot(12.0F, 20.0F, 2.0F);
			this.field_21549.addCuboid("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
			this.field_21550 = new ModelPart(this);
			this.field_21550.setPivot(0.0F, 20.0F, -1.0F);
			this.field_21550.addCuboid("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
			this.field_21549.addChild(this.field_21550);
			this.field_21551 = new ModelPart(this);
			this.field_21551.setPivot(0.0F, 23.0F, 0.0F);
			this.field_21551.addCuboid("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
			this.field_21550.addChild(this.field_21551);
			this.field_21552 = new ModelPart(this);
			this.field_21552.setPivot(16.0F, 16.0F, 42.0F);
			this.field_21552.addCuboid("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
			this.field_21553 = new ModelPart(this);
			this.field_21553.setPivot(0.0F, 32.0F, -4.0F);
			this.field_21553.addCuboid("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
			this.field_21552.addChild(this.field_21553);
			this.field_21554 = new ModelPart(this);
			this.field_21554.setPivot(0.0F, 31.0F, 4.0F);
			this.field_21554.addCuboid("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
			this.field_21553.addChild(this.field_21554);
			this.field_21555 = new ModelPart(this);
			this.field_21555.setPivot(-12.0F, 5.0F, 2.0F);
			this.field_21555.addCuboid("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
			this.field_21555.addCuboid("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 88);
			this.wingTip = new ModelPart(this);
			this.wingTip.setPivot(-56.0F, 0.0F, 0.0F);
			this.wingTip.addCuboid("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
			this.wingTip.addCuboid("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 144);
			this.field_21555.addChild(this.wingTip);
			this.frontLeg = new ModelPart(this);
			this.frontLeg.setPivot(-12.0F, 20.0F, 2.0F);
			this.frontLeg.addCuboid("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
			this.frontLegTip = new ModelPart(this);
			this.frontLegTip.setPivot(0.0F, 20.0F, -1.0F);
			this.frontLegTip.addCuboid("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
			this.frontLeg.addChild(this.frontLegTip);
			this.frontFoot = new ModelPart(this);
			this.frontFoot.setPivot(0.0F, 23.0F, 0.0F);
			this.frontFoot.addCuboid("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
			this.frontLegTip.addChild(this.frontFoot);
			this.rearLeg = new ModelPart(this);
			this.rearLeg.setPivot(-16.0F, 16.0F, 42.0F);
			this.rearLeg.addCuboid("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
			this.rearLegTip = new ModelPart(this);
			this.rearLegTip.setPivot(0.0F, 32.0F, -4.0F);
			this.rearLegTip.addCuboid("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
			this.rearLeg.addChild(this.rearLegTip);
			this.rearFoot = new ModelPart(this);
			this.rearFoot.setPivot(0.0F, 31.0F, 4.0F);
			this.rearFoot.addCuboid("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
			this.rearLegTip.addChild(this.rearFoot);
		}

		public void animateModel(EnderDragonEntity enderDragonEntity, float f, float g, float h) {
			this.dragon = enderDragonEntity;
			this.field_21442 = h;
		}

		public void setAngles(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j) {
		}

		@Override
		public void render(MatrixStack matrices, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
			matrices.push();
			float f = MathHelper.lerp(this.field_21442, this.dragon.field_7019, this.dragon.field_7030);
			this.jaw.pitch = (float)(Math.sin((double)(f * (float) (Math.PI * 2))) + 1.0) * 0.2F;
			float g = (float)(Math.sin((double)(f * (float) (Math.PI * 2) - 1.0F)) + 1.0);
			g = (g * g + g * 2.0F) * 0.05F;
			matrices.translate(0.0, (double)(g - 2.0F), -3.0);
			matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(g * 2.0F));
			float h = 0.0F;
			float i = 20.0F;
			float j = -12.0F;
			float k = 1.5F;
			double[] ds = this.dragon.method_6817(6, this.field_21442);
			float l = MathHelper.fwrapDegrees(this.dragon.method_6817(5, this.field_21442)[0] - this.dragon.method_6817(10, this.field_21442)[0]);
			float m = MathHelper.fwrapDegrees(this.dragon.method_6817(5, this.field_21442)[0] + (double)(l / 2.0F));
			float n = f * (float) (Math.PI * 2);

			for (int o = 0; o < 5; o++) {
				double[] es = this.dragon.method_6817(5 - o, this.field_21442);
				float p = (float)Math.cos((double)((float)o * 0.45F + n)) * 0.15F;
				this.neck.yaw = MathHelper.fwrapDegrees(es[0] - ds[0]) * (float) (Math.PI / 180.0) * 1.5F;
				this.neck.pitch = p + this.dragon.method_6823(o, ds, es) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
				this.neck.roll = -MathHelper.fwrapDegrees(es[0] - (double)m) * (float) (Math.PI / 180.0) * 1.5F;
				this.neck.pivotY = i;
				this.neck.pivotZ = j;
				this.neck.pivotX = h;
				i = (float)((double)i + Math.sin((double)this.neck.pitch) * 10.0);
				j = (float)((double)j - Math.cos((double)this.neck.yaw) * Math.cos((double)this.neck.pitch) * 10.0);
				h = (float)((double)h - Math.sin((double)this.neck.yaw) * Math.cos((double)this.neck.pitch) * 10.0);
				this.neck.render(matrices, vertexConsumer, light, overlay);
			}

			this.head.pivotY = i;
			this.head.pivotZ = j;
			this.head.pivotX = h;
			double[] fs = this.dragon.method_6817(0, this.field_21442);
			this.head.yaw = MathHelper.fwrapDegrees(fs[0] - ds[0]) * (float) (Math.PI / 180.0);
			this.head.pitch = MathHelper.fwrapDegrees((double)this.dragon.method_6823(6, ds, fs)) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
			this.head.roll = -MathHelper.fwrapDegrees(fs[0] - (double)m) * (float) (Math.PI / 180.0);
			this.head.render(matrices, vertexConsumer, light, overlay);
			matrices.push();
			matrices.translate(0.0, 1.0, 0.0);
			matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(-l * 1.5F));
			matrices.translate(0.0, -1.0, 0.0);
			this.body.roll = 0.0F;
			this.body.render(matrices, vertexConsumer, light, overlay);
			float q = f * (float) (Math.PI * 2);
			this.wing.pitch = 0.125F - (float)Math.cos((double)q) * 0.2F;
			this.wing.yaw = -0.25F;
			this.wing.roll = -((float)(Math.sin((double)q) + 0.125)) * 0.8F;
			this.field_21548.roll = (float)(Math.sin((double)(q + 2.0F)) + 0.5) * 0.75F;
			this.field_21555.pitch = this.wing.pitch;
			this.field_21555.yaw = -this.wing.yaw;
			this.field_21555.roll = -this.wing.roll;
			this.wingTip.roll = -this.field_21548.roll;
			this.method_23838(
				matrices,
				vertexConsumer,
				light,
				overlay,
				g,
				this.wing,
				this.field_21549,
				this.field_21550,
				this.field_21551,
				this.field_21552,
				this.field_21553,
				this.field_21554
			);
			this.method_23838(
				matrices,
				vertexConsumer,
				light,
				overlay,
				g,
				this.field_21555,
				this.frontLeg,
				this.frontLegTip,
				this.frontFoot,
				this.rearLeg,
				this.rearLegTip,
				this.rearFoot
			);
			matrices.pop();
			float p = -((float)Math.sin((double)(f * (float) (Math.PI * 2)))) * 0.0F;
			n = f * (float) (Math.PI * 2);
			i = 10.0F;
			j = 60.0F;
			h = 0.0F;
			ds = this.dragon.method_6817(11, this.field_21442);

			for (int r = 0; r < 12; r++) {
				fs = this.dragon.method_6817(12 + r, this.field_21442);
				p = (float)((double)p + Math.sin((double)((float)r * 0.45F + n)) * 0.05F);
				this.neck.yaw = (MathHelper.fwrapDegrees(fs[0] - ds[0]) * 1.5F + 180.0F) * (float) (Math.PI / 180.0);
				this.neck.pitch = p + (float)(fs[1] - ds[1]) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
				this.neck.roll = MathHelper.fwrapDegrees(fs[0] - (double)m) * (float) (Math.PI / 180.0) * 1.5F;
				this.neck.pivotY = i;
				this.neck.pivotZ = j;
				this.neck.pivotX = h;
				i = (float)((double)i + Math.sin((double)this.neck.pitch) * 10.0);
				j = (float)((double)j - Math.cos((double)this.neck.yaw) * Math.cos((double)this.neck.pitch) * 10.0);
				h = (float)((double)h - Math.sin((double)this.neck.yaw) * Math.cos((double)this.neck.pitch) * 10.0);
				this.neck.render(matrices, vertexConsumer, light, overlay);
			}

			matrices.pop();
		}

		private void method_23838(
			MatrixStack matrixStack,
			VertexConsumer vertexConsumer,
			int i,
			int j,
			float f,
			ModelPart modelPart,
			ModelPart modelPart2,
			ModelPart modelPart3,
			ModelPart modelPart4,
			ModelPart modelPart5,
			ModelPart modelPart6,
			ModelPart modelPart7
		) {
			modelPart5.pitch = 1.0F + f * 0.1F;
			modelPart6.pitch = 0.5F + f * 0.1F;
			modelPart7.pitch = 0.75F + f * 0.1F;
			modelPart2.pitch = 1.3F + f * 0.1F;
			modelPart3.pitch = -0.5F - f * 0.1F;
			modelPart4.pitch = 0.75F + f * 0.1F;
			modelPart.render(matrixStack, vertexConsumer, i, j);
			modelPart2.render(matrixStack, vertexConsumer, i, j);
			modelPart5.render(matrixStack, vertexConsumer, i, j);
		}
	}
}
