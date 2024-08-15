package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.EnderDragonEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.Phase;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.feature.EndPortalFeature;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Environment(EnvType.CLIENT)
public class EnderDragonEntityRenderer extends EntityRenderer<EnderDragonEntity, EnderDragonEntityRenderState> {
	public static final Identifier CRYSTAL_BEAM_TEXTURE = Identifier.ofVanilla("textures/entity/end_crystal/end_crystal_beam.png");
	private static final Identifier EXPLOSION_TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon_exploding.png");
	private static final Identifier TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon.png");
	private static final Identifier EYE_TEXTURE = Identifier.ofVanilla("textures/entity/enderdragon/dragon_eyes.png");
	private static final RenderLayer DRAGON_CUTOUT = RenderLayer.getEntityCutoutNoCull(TEXTURE);
	private static final RenderLayer DRAGON_DECAL = RenderLayer.getEntityDecal(TEXTURE);
	private static final RenderLayer DRAGON_EYES = RenderLayer.getEyes(EYE_TEXTURE);
	private static final RenderLayer CRYSTAL_BEAM_LAYER = RenderLayer.getEntitySmoothCutout(CRYSTAL_BEAM_TEXTURE);
	private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
	private final DragonEntityModel model;

	public EnderDragonEntityRenderer(EntityRendererFactory.Context context) {
		super(context);
		this.shadowRadius = 0.5F;
		this.model = new DragonEntityModel(context.getPart(EntityModelLayers.ENDER_DRAGON));
	}

	public void render(EnderDragonEntityRenderState enderDragonEntityRenderState, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
		matrixStack.push();
		float f = enderDragonEntityRenderState.getLerpedFrame(7).yRot();
		float g = (float)(enderDragonEntityRenderState.getLerpedFrame(5).y() - enderDragonEntityRenderState.getLerpedFrame(10).y());
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-f));
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g * 10.0F));
		matrixStack.translate(0.0F, 0.0F, 1.0F);
		matrixStack.scale(-1.0F, -1.0F, 1.0F);
		matrixStack.translate(0.0F, -1.501F, 0.0F);
		this.model.setAngles(enderDragonEntityRenderState);
		if (enderDragonEntityRenderState.ticksSinceDeath > 0.0F) {
			float h = enderDragonEntityRenderState.ticksSinceDeath / 200.0F;
			int j = ColorHelper.withAlpha(MathHelper.floor(h * 255.0F), Colors.WHITE);
			VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityAlpha(EXPLOSION_TEXTURE));
			this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV, j);
			VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(DRAGON_DECAL);
			this.model.render(matrixStack, vertexConsumer2, i, OverlayTexture.getUv(0.0F, enderDragonEntityRenderState.hurt));
		} else {
			VertexConsumer vertexConsumer3 = vertexConsumerProvider.getBuffer(DRAGON_CUTOUT);
			this.model.render(matrixStack, vertexConsumer3, i, OverlayTexture.getUv(0.0F, enderDragonEntityRenderState.hurt));
		}

		VertexConsumer vertexConsumer3 = vertexConsumerProvider.getBuffer(DRAGON_EYES);
		this.model.render(matrixStack, vertexConsumer3, i, OverlayTexture.DEFAULT_UV);
		if (enderDragonEntityRenderState.ticksSinceDeath > 0.0F) {
			float k = enderDragonEntityRenderState.ticksSinceDeath / 200.0F;
			matrixStack.push();
			matrixStack.translate(0.0F, -1.0F, -2.0F);
			renderDeathAnimation(matrixStack, k, vertexConsumerProvider.getBuffer(RenderLayer.getDragonRays()));
			renderDeathAnimation(matrixStack, k, vertexConsumerProvider.getBuffer(RenderLayer.getDragonRaysDepth()));
			matrixStack.pop();
		}

		matrixStack.pop();
		if (enderDragonEntityRenderState.crystalBeamPos != null) {
			renderCrystalBeam(
				(float)enderDragonEntityRenderState.crystalBeamPos.x,
				(float)enderDragonEntityRenderState.crystalBeamPos.y,
				(float)enderDragonEntityRenderState.crystalBeamPos.z,
				enderDragonEntityRenderState.age,
				matrixStack,
				vertexConsumerProvider,
				i
			);
		}

		super.render(enderDragonEntityRenderState, matrixStack, vertexConsumerProvider, i);
	}

	private static void renderDeathAnimation(MatrixStack matrices, float animationProgress, VertexConsumer vertexCOnsumer) {
		matrices.push();
		float f = Math.min(animationProgress > 0.8F ? (animationProgress - 0.8F) / 0.2F : 0.0F, 1.0F);
		int i = ColorHelper.fromFloats(1.0F - f, 1.0F, 1.0F, 1.0F);
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

	public static void renderCrystalBeam(float dx, float dy, float dz, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		float f = MathHelper.sqrt(dx * dx + dz * dz);
		float g = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
		matrices.push();
		matrices.translate(0.0F, 2.0F, 0.0F);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotation((float)(-Math.atan2((double)dz, (double)dx)) - (float) (Math.PI / 2)));
		matrices.multiply(RotationAxis.POSITIVE_X.rotation((float)(-Math.atan2((double)f, (double)dy)) - (float) (Math.PI / 2)));
		VertexConsumer vertexConsumer = vertexConsumers.getBuffer(CRYSTAL_BEAM_LAYER);
		float h = 0.0F - tickDelta * 0.01F;
		float i = g / 32.0F - tickDelta * 0.01F;
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

	public Identifier getTexture(EnderDragonEntityRenderState enderDragonEntityRenderState) {
		return TEXTURE;
	}

	public EnderDragonEntityRenderState getRenderState() {
		return new EnderDragonEntityRenderState();
	}

	public void updateRenderState(EnderDragonEntity enderDragonEntity, EnderDragonEntityRenderState enderDragonEntityRenderState, float f) {
		super.updateRenderState(enderDragonEntity, enderDragonEntityRenderState, f);
		enderDragonEntityRenderState.wingPosition = MathHelper.lerp(f, enderDragonEntity.prevWingPosition, enderDragonEntity.wingPosition);
		enderDragonEntityRenderState.ticksSinceDeath = enderDragonEntity.ticksSinceDeath > 0 ? (float)enderDragonEntity.ticksSinceDeath + f : 0.0F;
		enderDragonEntityRenderState.hurt = enderDragonEntity.hurtTime > 0;
		EndCrystalEntity endCrystalEntity = enderDragonEntity.connectedCrystal;
		if (endCrystalEntity != null) {
			Vec3d vec3d = endCrystalEntity.getLerpedPos(f).add(0.0, (double)EndCrystalEntityRenderer.getYOffset((float)endCrystalEntity.endCrystalAge + f), 0.0);
			enderDragonEntityRenderState.crystalBeamPos = vec3d.subtract(enderDragonEntity.getLerpedPos(f));
		} else {
			enderDragonEntityRenderState.crystalBeamPos = null;
		}

		Phase phase = enderDragonEntity.getPhaseManager().getCurrent();
		enderDragonEntityRenderState.inLandingOrTakeoffPhase = phase == PhaseType.LANDING || phase == PhaseType.TAKEOFF;
		enderDragonEntityRenderState.sittingOrHovering = phase.isSittingOrHovering();
		BlockPos blockPos = enderDragonEntity.getWorld()
			.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPortalFeature.offsetOrigin(enderDragonEntity.getFightOrigin()));
		enderDragonEntityRenderState.squaredDistanceFromOrigin = blockPos.getSquaredDistance(enderDragonEntity.getPos());
		enderDragonEntityRenderState.tickDelta = enderDragonEntity.isDead() ? 0.0F : f;
		enderDragonEntityRenderState.frameTracker.copyFrom(enderDragonEntity.frameTracker);
	}

	protected boolean canBeCulled(EnderDragonEntity enderDragonEntity) {
		return false;
	}
}
