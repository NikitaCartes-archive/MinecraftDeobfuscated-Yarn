package net.minecraft.client.render.entity;

import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class EnderDragonEntityRenderer extends EntityRenderer<EnderDragonEntity> {
	public static final Identifier CRYSTAL_BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/end_crystal/end_crystal_beam.png");
	private static final Identifier EXPLOSION_TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon_exploding.png");
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon.png");
	private static final Identifier EYE_TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon_eyes.png");
	private static final RenderLayer DRAGON_CUTOUT = RenderLayer.getEntityCutoutNoCull(TEXTURE);
	private static final RenderLayer DRAGON_DECAL = RenderLayer.getEntityDecal(TEXTURE);
	private static final RenderLayer DRAGON_EYES = RenderLayer.getEyes(EYE_TEXTURE);
	private static final RenderLayer CRYSTAL_BEAM_LAYER = RenderLayer.getEntitySmoothCutout(CRYSTAL_BEAM_TEXTURE);
	private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
	private final EnderDragonEntityRenderer.DragonEntityModel model;

	public EnderDragonEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0.5F;
		this.model = new EnderDragonEntityRenderer.DragonEntityModel(context.getPart(EntityModelLayers.ENDER_DRAGON));
	}

	public void render(EnderDragonEntity enderDragonEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		float h = (float)enderDragonEntity.getSegmentProperties(7, g)[0];
		float j = (float)(enderDragonEntity.getSegmentProperties(5, g)[1] - enderDragonEntity.getSegmentProperties(10, g)[1]);
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-h));
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j * 10.0F));
		matrixStack.translate(0.0F, 0.0F, 1.0F);
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		matrixStack.translate(0.0F, -1.501F, 0.0F);
		boolean bl = enderDragonEntity.hurtTime > 0;
		this.model.animateModel(enderDragonEntity, 0.0F, 0.0F, g);
		if (enderDragonEntity.ticksSinceDeath > 0) {
			float k = (float)enderDragonEntity.ticksSinceDeath / 200.0F;
			int l = ColorHelper.Argb.withAlpha(MathHelper.floor(k * 255.0F), -1);
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityAlpha(EXPLOSION_TEXTURE));
			this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, l);
			VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(DRAGON_DECAL);
			this.model.render(matrixStack, vertexConsumer2, i, OverlayTexture.getUv(0.0F, bl));
		} else {
			VertexConsumer vertexConsumer3 = vertexConsumerProvider.getBuffer(DRAGON_CUTOUT);
			this.model.render(matrixStack, vertexConsumer3, i, OverlayTexture.getUv(0.0F, bl));
		}

		VertexConsumer vertexConsumer3 = vertexConsumerProvider.getBuffer(DRAGON_EYES);
		this.model.render(matrixStack, vertexConsumer3, i, OverlayTexture.DEFAULT_UV);
		if (enderDragonEntity.ticksSinceDeath > 0) {
			float m = ((float)enderDragonEntity.ticksSinceDeath + g) / 200.0F;
			matrixStack.push();
			matrixStack.translate(0.0F, -1.0F, -2.0F);
			renderDeathAnimation(matrixStack, m, vertexConsumerProvider.getBuffer(RenderLayer.getDragonRays()));
			renderDeathAnimation(matrixStack, m, vertexConsumerProvider.getBuffer(RenderLayer.getDragonRaysDepth()));
			matrixStack.pop();
		}

		matrixStack.pop();
		if (enderDragonEntity.connectedCrystal != null) {
			matrixStack.push();
			float m = (float)(enderDragonEntity.connectedCrystal.getX() - MathHelper.lerp((double)g, enderDragonEntity.prevX, enderDragonEntity.getX()));
			float n = (float)(enderDragonEntity.connectedCrystal.getY() - MathHelper.lerp((double)g, enderDragonEntity.prevY, enderDragonEntity.getY()));
			float o = (float)(enderDragonEntity.connectedCrystal.getZ() - MathHelper.lerp((double)g, enderDragonEntity.prevZ, enderDragonEntity.getZ()));
			renderCrystalBeam(
				m, n + EndCrystalEntityRenderer.getYOffset(enderDragonEntity.connectedCrystal, g), o, g, enderDragonEntity.age, matrixStack, vertexConsumerProvider, i
			);
			matrixStack.pop();
		}

		super.render(enderDragonEntity, f, g, matrixStack, vertexConsumerProvider, i);
	}

	private static void renderDeathAnimation(MatrixStack matrices, float animationProgress, VertexConsumer vertexCOnsumer) {
		matrices.push();
		float f = Math.min(animationProgress > 0.8F ? (animationProgress - 0.8F) / 0.2F : 0.0F, 1.0F);
		int i = ColorHelper.Argb.fromFloats(1.0F - f, 1.0F, 1.0F, 1.0F);
		int j = 16711935;
		Random random = Random.create(432L);
		Vector3f vector3f = new Vector3f();
		Vector3f vector3f2 = new Vector3f();
		Vector3f vector3f3 = new Vector3f();
		Vector3f vector3f4 = new Vector3f();
		Quaternionf quaternionf = new Quaternionf();
		int k = MathHelper.floor((animationProgress + animationProgress * animationProgress) / 2.0F * 60.0F);

		for (int l = 0; l < k; l++) {
			quaternionf.rotationXYZ(random.nextFloat() * (float) (Math.PI * 2), random.nextFloat() * (float) (Math.PI * 2), random.nextFloat() * (float) (Math.PI * 2))
				.rotateXYZ(
					random.nextFloat() * (float) (Math.PI * 2),
					random.nextFloat() * (float) (Math.PI * 2),
					random.nextFloat() * (float) (Math.PI * 2) + animationProgress * (float) (Math.PI / 2)
				);
			matrices.multiply(quaternionf);
			float g = random.nextFloat() * 20.0F + 5.0F + f * 10.0F;
			float h = random.nextFloat() * 2.0F + 1.0F + f * 2.0F;
			vector3f2.set(-HALF_SQRT_3 * h, g, -0.5F * h);
			vector3f3.set(HALF_SQRT_3 * h, g, -0.5F * h);
			vector3f4.set(0.0F, g, h);
			MatrixStack.Entry entry = matrices.peek();
			vertexCOnsumer.vertex(entry, vector3f).color(i);
			vertexCOnsumer.vertex(entry, vector3f2).color(16711935);
			vertexCOnsumer.vertex(entry, vector3f3).color(16711935);
			vertexCOnsumer.vertex(entry, vector3f).color(i);
			vertexCOnsumer.vertex(entry, vector3f3).color(16711935);
			vertexCOnsumer.vertex(entry, vector3f4).color(16711935);
			vertexCOnsumer.vertex(entry, vector3f).color(i);
			vertexCOnsumer.vertex(entry, vector3f4).color(16711935);
			vertexCOnsumer.vertex(entry, vector3f2).color(16711935);
		}

		matrices.pop();
	}

	public static void renderCrystalBeam(
		float dx, float dy, float dz, float tickDelta, int age, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light
	) {
		float f = MathHelper.sqrt(dx * dx + dz * dz);
		float g = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
		matrices.push();
		matrices.translate(0.0F, 2.0F, 0.0F);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)(-Math.atan2((double)dz, (double)dx)) - (float) (Math.PI / 2)));
		matrices.multiply(RotationAxis.POSITIVE_X.rotation((float)(-Math.atan2((double)f, (double)dy)) - (float) (Math.PI / 2)));
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(CRYSTAL_BEAM_LAYER);
		float h = 0.0F - ((float)age + tickDelta) * 0.01F;
		float i = MathHelper.sqrt(dx * dx + dy * dy + dz * dz) / 32.0F - ((float)age + tickDelta) * 0.01F;
		int j = 8;
		float k = 0.0F;
		float l = 0.75F;
		float m = 0.0F;
		MatrixStack.Entry entry = matrices.peek();

		for (int n = 1; n <= 8; n++) {
			float o = MathHelper.sin((float)n * (float) (Math.PI * 2) / 8.0F) * 0.75F;
			float p = MathHelper.cos((float)n * (float) (Math.PI * 2) / 8.0F) * 0.75F;
			float q = (float)n / 8.0F;
			vertexConsumer.vertex(entry, k * 0.2F, l * 0.2F, 0.0F)
				.color(Colors.BLACK)
				.texture(m, h)
				.overlay(OverlayTexture.DEFAULT_UV)
				.light(light)
				.normal(entry, 0.0F, -1.0F, 0.0F);
			vertexConsumer.vertex(entry, k, l, g).color(Colors.WHITE).texture(m, i).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0F, -1.0F, 0.0F);
			vertexConsumer.vertex(entry, o, p, g).color(Colors.WHITE).texture(q, i).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(entry, 0.0F, -1.0F, 0.0F);
			vertexConsumer.vertex(entry, o * 0.2F, p * 0.2F, 0.0F)
				.color(Colors.BLACK)
				.texture(q, h)
				.overlay(OverlayTexture.DEFAULT_UV)
				.light(light)
				.normal(entry, 0.0F, -1.0F, 0.0F);
			k = o;
			l = p;
			m = q;
		}

		matrices.pop();
	}

	public Identifier getTexture(EnderDragonEntity enderDragonEntity) {
		return TEXTURE;
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();
		float f = -16.0F;
		ModelPartData modelPartData2 = modelPartData.addChild(
			EntityModelPartNames.HEAD,
			ModelPartBuilder.create()
				.cuboid("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 176, 44)
				.cuboid("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, 112, 30)
				.mirrored()
				.cuboid("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0)
				.cuboid("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0)
				.mirrored()
				.cuboid("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0)
				.cuboid("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0),
			ModelTransform.NONE
		);
		modelPartData2.addChild(
			EntityModelPartNames.JAW,
			ModelPartBuilder.create().cuboid(EntityModelPartNames.JAW, -6.0F, 0.0F, -16.0F, 12, 4, 16, 176, 65),
			ModelTransform.pivot(0.0F, 4.0F, -8.0F)
		);
		modelPartData.addChild(
			EntityModelPartNames.NECK,
			ModelPartBuilder.create().cuboid("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, 192, 104).cuboid("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, 48, 0),
			ModelTransform.NONE
		);
		modelPartData.addChild(
			EntityModelPartNames.BODY,
			ModelPartBuilder.create()
				.cuboid(EntityModelPartNames.BODY, -12.0F, 0.0F, -16.0F, 24, 24, 64, 0, 0)
				.cuboid("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, 220, 53)
				.cuboid("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, 220, 53)
				.cuboid("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, 220, 53),
			ModelTransform.pivot(0.0F, 4.0F, 8.0F)
		);
		ModelPartData modelPartData3 = modelPartData.addChild(
			EntityModelPartNames.LEFT_WING,
			ModelPartBuilder.create()
				.mirrored()
				.cuboid(EntityModelPartNames.BONE, 0.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88)
				.cuboid("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88),
			ModelTransform.pivot(12.0F, 5.0F, 2.0F)
		);
		modelPartData3.addChild(
			EntityModelPartNames.LEFT_WING_TIP,
			ModelPartBuilder.create()
				.mirrored()
				.cuboid(EntityModelPartNames.BONE, 0.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136)
				.cuboid("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144),
			ModelTransform.pivot(56.0F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData4 = modelPartData.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG,
			ModelPartBuilder.create().cuboid("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 112, 104),
			ModelTransform.pivot(12.0F, 20.0F, 2.0F)
		);
		ModelPartData modelPartData5 = modelPartData4.addChild(
			EntityModelPartNames.LEFT_FRONT_LEG_TIP,
			ModelPartBuilder.create().cuboid("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 226, 138),
			ModelTransform.pivot(0.0F, 20.0F, -1.0F)
		);
		modelPartData5.addChild(
			EntityModelPartNames.LEFT_FRONT_FOOT,
			ModelPartBuilder.create().cuboid("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 144, 104),
			ModelTransform.pivot(0.0F, 23.0F, 0.0F)
		);
		ModelPartData modelPartData6 = modelPartData.addChild(
			EntityModelPartNames.LEFT_HIND_LEG,
			ModelPartBuilder.create().cuboid("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0, 0),
			ModelTransform.pivot(16.0F, 16.0F, 42.0F)
		);
		ModelPartData modelPartData7 = modelPartData6.addChild(
			EntityModelPartNames.LEFT_HIND_LEG_TIP,
			ModelPartBuilder.create().cuboid("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 196, 0),
			ModelTransform.pivot(0.0F, 32.0F, -4.0F)
		);
		modelPartData7.addChild(
			EntityModelPartNames.LEFT_HIND_FOOT,
			ModelPartBuilder.create().cuboid("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 112, 0),
			ModelTransform.pivot(0.0F, 31.0F, 4.0F)
		);
		ModelPartData modelPartData8 = modelPartData.addChild(
			EntityModelPartNames.RIGHT_WING,
			ModelPartBuilder.create().cuboid(EntityModelPartNames.BONE, -56.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88).cuboid("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88),
			ModelTransform.pivot(-12.0F, 5.0F, 2.0F)
		);
		modelPartData8.addChild(
			EntityModelPartNames.RIGHT_WING_TIP,
			ModelPartBuilder.create()
				.cuboid(EntityModelPartNames.BONE, -56.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136)
				.cuboid("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144),
			ModelTransform.pivot(-56.0F, 0.0F, 0.0F)
		);
		ModelPartData modelPartData9 = modelPartData.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG,
			ModelPartBuilder.create().cuboid("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 112, 104),
			ModelTransform.pivot(-12.0F, 20.0F, 2.0F)
		);
		ModelPartData modelPartData10 = modelPartData9.addChild(
			EntityModelPartNames.RIGHT_FRONT_LEG_TIP,
			ModelPartBuilder.create().cuboid("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 226, 138),
			ModelTransform.pivot(0.0F, 20.0F, -1.0F)
		);
		modelPartData10.addChild(
			EntityModelPartNames.RIGHT_FRONT_FOOT,
			ModelPartBuilder.create().cuboid("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 144, 104),
			ModelTransform.pivot(0.0F, 23.0F, 0.0F)
		);
		ModelPartData modelPartData11 = modelPartData.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG,
			ModelPartBuilder.create().cuboid("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0, 0),
			ModelTransform.pivot(-16.0F, 16.0F, 42.0F)
		);
		ModelPartData modelPartData12 = modelPartData11.addChild(
			EntityModelPartNames.RIGHT_HIND_LEG_TIP,
			ModelPartBuilder.create().cuboid("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 196, 0),
			ModelTransform.pivot(0.0F, 32.0F, -4.0F)
		);
		modelPartData12.addChild(
			EntityModelPartNames.RIGHT_HIND_FOOT,
			ModelPartBuilder.create().cuboid("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 112, 0),
			ModelTransform.pivot(0.0F, 31.0F, 4.0F)
		);
		return TexturedModelData.of(modelData, 256, 256);
	}

	@Environment(EnvType.CLIENT)
	public static class DragonEntityModel extends EntityModel<EnderDragonEntity> {
		private final ModelPart head;
		private final ModelPart neck;
		private final ModelPart jaw;
		private final ModelPart body;
		private final ModelPart leftWing;
		private final ModelPart leftWingTip;
		private final ModelPart leftFrontLeg;
		private final ModelPart leftFrontLegTip;
		private final ModelPart leftFrontFoot;
		private final ModelPart leftHindLeg;
		private final ModelPart leftHindLegTip;
		private final ModelPart leftHindFoot;
		private final ModelPart rightWing;
		private final ModelPart rightWingTip;
		private final ModelPart rightFrontLeg;
		private final ModelPart rightFrontLegTip;
		private final ModelPart rightFrontFoot;
		private final ModelPart rightHindLeg;
		private final ModelPart rightHindLegTip;
		private final ModelPart rightHindFoot;
		@Nullable
		private EnderDragonEntity dragon;
		private float tickDelta;

		public DragonEntityModel(ModelPart part) {
			this.head = part.getChild(EntityModelPartNames.HEAD);
			this.jaw = this.head.getChild(EntityModelPartNames.JAW);
			this.neck = part.getChild(EntityModelPartNames.NECK);
			this.body = part.getChild(EntityModelPartNames.BODY);
			this.leftWing = part.getChild(EntityModelPartNames.LEFT_WING);
			this.leftWingTip = this.leftWing.getChild(EntityModelPartNames.LEFT_WING_TIP);
			this.leftFrontLeg = part.getChild(EntityModelPartNames.LEFT_FRONT_LEG);
			this.leftFrontLegTip = this.leftFrontLeg.getChild(EntityModelPartNames.LEFT_FRONT_LEG_TIP);
			this.leftFrontFoot = this.leftFrontLegTip.getChild(EntityModelPartNames.LEFT_FRONT_FOOT);
			this.leftHindLeg = part.getChild(EntityModelPartNames.LEFT_HIND_LEG);
			this.leftHindLegTip = this.leftHindLeg.getChild(EntityModelPartNames.LEFT_HIND_LEG_TIP);
			this.leftHindFoot = this.leftHindLegTip.getChild(EntityModelPartNames.LEFT_HIND_FOOT);
			this.rightWing = part.getChild(EntityModelPartNames.RIGHT_WING);
			this.rightWingTip = this.rightWing.getChild(EntityModelPartNames.RIGHT_WING_TIP);
			this.rightFrontLeg = part.getChild(EntityModelPartNames.RIGHT_FRONT_LEG);
			this.rightFrontLegTip = this.rightFrontLeg.getChild(EntityModelPartNames.RIGHT_FRONT_LEG_TIP);
			this.rightFrontFoot = this.rightFrontLegTip.getChild(EntityModelPartNames.RIGHT_FRONT_FOOT);
			this.rightHindLeg = part.getChild(EntityModelPartNames.RIGHT_HIND_LEG);
			this.rightHindLegTip = this.rightHindLeg.getChild(EntityModelPartNames.RIGHT_HIND_LEG_TIP);
			this.rightHindFoot = this.rightHindLegTip.getChild(EntityModelPartNames.RIGHT_HIND_FOOT);
		}

		public void animateModel(EnderDragonEntity enderDragonEntity, float f, float g, float h) {
			this.dragon = enderDragonEntity;
			this.tickDelta = h;
		}

		public void setAngles(EnderDragonEntity enderDragonEntity, float f, float g, float h, float i, float j) {
		}

		@Override
		public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
			matrices.push();
			float f = MathHelper.lerp(this.tickDelta, this.dragon.prevWingPosition, this.dragon.wingPosition);
			this.jaw.pitch = (float)(Math.sin((double)(f * (float) (Math.PI * 2))) + 1.0) * 0.2F;
			float g = (float)(Math.sin((double)(f * (float) (Math.PI * 2) - 1.0F)) + 1.0);
			g = (g * g + g * 2.0F) * 0.05F;
			matrices.translate(0.0F, g - 2.0F, -3.0F);
			matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g * 2.0F));
			float h = 0.0F;
			float i = 20.0F;
			float j = -12.0F;
			float k = 1.5F;
			double[] ds = this.dragon.getSegmentProperties(6, this.tickDelta);
			float l = MathHelper.wrapDegrees((float)(this.dragon.getSegmentProperties(5, this.tickDelta)[0] - this.dragon.getSegmentProperties(10, this.tickDelta)[0]));
			float m = MathHelper.wrapDegrees((float)(this.dragon.getSegmentProperties(5, this.tickDelta)[0] + (double)(l / 2.0F)));
			float n = f * (float) (Math.PI * 2);

			for (int o = 0; o < 5; o++) {
				double[] es = this.dragon.getSegmentProperties(5 - o, this.tickDelta);
				float p = (float)Math.cos((double)((float)o * 0.45F + n)) * 0.15F;
				this.neck.yaw = MathHelper.wrapDegrees((float)(es[0] - ds[0])) * (float) (Math.PI / 180.0) * 1.5F;
				this.neck.pitch = p + this.dragon.getChangeInNeckPitch(o, ds, es) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
				this.neck.roll = -MathHelper.wrapDegrees((float)(es[0] - (double)m)) * (float) (Math.PI / 180.0) * 1.5F;
				this.neck.pivotY = i;
				this.neck.pivotZ = j;
				this.neck.pivotX = h;
				i += MathHelper.sin(this.neck.pitch) * 10.0F;
				j -= MathHelper.cos(this.neck.yaw) * MathHelper.cos(this.neck.pitch) * 10.0F;
				h -= MathHelper.sin(this.neck.yaw) * MathHelper.cos(this.neck.pitch) * 10.0F;
				this.neck.render(matrices, vertices, light, overlay, color);
			}

			this.head.pivotY = i;
			this.head.pivotZ = j;
			this.head.pivotX = h;
			double[] fs = this.dragon.getSegmentProperties(0, this.tickDelta);
			this.head.yaw = MathHelper.wrapDegrees((float)(fs[0] - ds[0])) * (float) (Math.PI / 180.0);
			this.head.pitch = MathHelper.wrapDegrees(this.dragon.getChangeInNeckPitch(6, ds, fs)) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
			this.head.roll = -MathHelper.wrapDegrees((float)(fs[0] - (double)m)) * (float) (Math.PI / 180.0);
			this.head.render(matrices, vertices, light, overlay, color);
			matrices.push();
			matrices.translate(0.0F, 1.0F, 0.0F);
			matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-l * 1.5F));
			matrices.translate(0.0F, -1.0F, 0.0F);
			this.body.roll = 0.0F;
			this.body.render(matrices, vertices, light, overlay, color);
			float q = f * (float) (Math.PI * 2);
			this.leftWing.pitch = 0.125F - (float)Math.cos((double)q) * 0.2F;
			this.leftWing.yaw = -0.25F;
			this.leftWing.roll = -((float)(Math.sin((double)q) + 0.125)) * 0.8F;
			this.leftWingTip.roll = (float)(Math.sin((double)(q + 2.0F)) + 0.5) * 0.75F;
			this.rightWing.pitch = this.leftWing.pitch;
			this.rightWing.yaw = -this.leftWing.yaw;
			this.rightWing.roll = -this.leftWing.roll;
			this.rightWingTip.roll = -this.leftWingTip.roll;
			this.renderLimbs(
				matrices,
				vertices,
				light,
				overlay,
				g,
				this.leftWing,
				this.leftFrontLeg,
				this.leftFrontLegTip,
				this.leftFrontFoot,
				this.leftHindLeg,
				this.leftHindLegTip,
				this.leftHindFoot,
				color
			);
			this.renderLimbs(
				matrices,
				vertices,
				light,
				overlay,
				g,
				this.rightWing,
				this.rightFrontLeg,
				this.rightFrontLegTip,
				this.rightFrontFoot,
				this.rightHindLeg,
				this.rightHindLegTip,
				this.rightHindFoot,
				color
			);
			matrices.pop();
			float p = -MathHelper.sin(f * (float) (Math.PI * 2)) * 0.0F;
			n = f * (float) (Math.PI * 2);
			i = 10.0F;
			j = 60.0F;
			h = 0.0F;
			ds = this.dragon.getSegmentProperties(11, this.tickDelta);

			for (int r = 0; r < 12; r++) {
				fs = this.dragon.getSegmentProperties(12 + r, this.tickDelta);
				p += MathHelper.sin((float)r * 0.45F + n) * 0.05F;
				this.neck.yaw = (MathHelper.wrapDegrees((float)(fs[0] - ds[0])) * 1.5F + 180.0F) * (float) (Math.PI / 180.0);
				this.neck.pitch = p + (float)(fs[1] - ds[1]) * (float) (Math.PI / 180.0) * 1.5F * 5.0F;
				this.neck.roll = MathHelper.wrapDegrees((float)(fs[0] - (double)m)) * (float) (Math.PI / 180.0) * 1.5F;
				this.neck.pivotY = i;
				this.neck.pivotZ = j;
				this.neck.pivotX = h;
				i += MathHelper.sin(this.neck.pitch) * 10.0F;
				j -= MathHelper.cos(this.neck.yaw) * MathHelper.cos(this.neck.pitch) * 10.0F;
				h -= MathHelper.sin(this.neck.yaw) * MathHelper.cos(this.neck.pitch) * 10.0F;
				this.neck.render(matrices, vertices, light, overlay, color);
			}

			matrices.pop();
		}

		private void renderLimbs(
			MatrixStack matrices,
			VertexConsumer vertices,
			int light,
			int overlay,
			float offset,
			ModelPart wing,
			ModelPart frontLeg,
			ModelPart frontLegTip,
			ModelPart frontFoot,
			ModelPart hindLeg,
			ModelPart hindLegTip,
			ModelPart hindFoot,
			int color
		) {
			hindLeg.pitch = 1.0F + offset * 0.1F;
			hindLegTip.pitch = 0.5F + offset * 0.1F;
			hindFoot.pitch = 0.75F + offset * 0.1F;
			frontLeg.pitch = 1.3F + offset * 0.1F;
			frontLegTip.pitch = -0.5F - offset * 0.1F;
			frontFoot.pitch = 0.75F + offset * 0.1F;
			wing.render(matrices, vertices, light, overlay, color);
			frontLeg.render(matrices, vertices, light, overlay, color);
			hindLeg.render(matrices, vertices, light, overlay, color);
		}
	}
}
