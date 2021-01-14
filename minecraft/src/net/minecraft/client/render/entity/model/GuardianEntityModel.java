package net.minecraft.client.render.entity.model;

import com.google.common.collect.ImmutableList;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.GuardianEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(EnvType.CLIENT)
public class GuardianEntityModel extends CompositeEntityModel<GuardianEntity> {
	private static final float[] SPIKE_PITCHES = new float[]{1.75F, 0.25F, 0.0F, 0.0F, 0.5F, 0.5F, 0.5F, 0.5F, 1.25F, 0.75F, 0.0F, 0.0F};
	private static final float[] SPIKE_YAWS = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.25F, 1.75F, 1.25F, 0.75F, 0.0F, 0.0F, 0.0F, 0.0F};
	private static final float[] SPIKE_ROLLS = new float[]{0.0F, 0.0F, 0.25F, 1.75F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.75F, 1.25F};
	private static final float[] SPIKE_PIVOTS_X = new float[]{0.0F, 0.0F, 8.0F, -8.0F, -8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F, 8.0F, -8.0F};
	private static final float[] SPIKE_PIVOTS_Y = new float[]{-8.0F, -8.0F, -8.0F, -8.0F, 0.0F, 0.0F, 0.0F, 0.0F, 8.0F, 8.0F, 8.0F, 8.0F};
	private static final float[] SPIKE_PIVOTS_Z = new float[]{8.0F, -8.0F, 0.0F, 0.0F, -8.0F, -8.0F, 8.0F, 8.0F, 8.0F, -8.0F, 0.0F, 0.0F};
	private final ModelPart head;
	private final ModelPart eye;
	private final ModelPart[] spikes;
	private final ModelPart[] tail;

	public GuardianEntityModel() {
		this.textureWidth = 64;
		this.textureHeight = 64;
		this.spikes = new ModelPart[12];
		this.head = new ModelPart(this);
		this.head.setTextureOffset(0, 0).addCuboid(-6.0F, 10.0F, -8.0F, 12.0F, 12.0F, 16.0F);
		this.head.setTextureOffset(0, 28).addCuboid(-8.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F);
		this.head.setTextureOffset(0, 28).addCuboid(6.0F, 10.0F, -6.0F, 2.0F, 12.0F, 12.0F, true);
		this.head.setTextureOffset(16, 40).addCuboid(-6.0F, 8.0F, -6.0F, 12.0F, 2.0F, 12.0F);
		this.head.setTextureOffset(16, 40).addCuboid(-6.0F, 22.0F, -6.0F, 12.0F, 2.0F, 12.0F);

		for (int i = 0; i < this.spikes.length; i++) {
			this.spikes[i] = new ModelPart(this, 0, 0);
			this.spikes[i].addCuboid(-1.0F, -4.5F, -1.0F, 2.0F, 9.0F, 2.0F);
			this.head.addChild(this.spikes[i]);
		}

		this.eye = new ModelPart(this, 8, 0);
		this.eye.addCuboid(-1.0F, 15.0F, 0.0F, 2.0F, 2.0F, 1.0F);
		this.head.addChild(this.eye);
		this.tail = new ModelPart[3];
		this.tail[0] = new ModelPart(this, 40, 0);
		this.tail[0].addCuboid(-2.0F, 14.0F, 7.0F, 4.0F, 4.0F, 8.0F);
		this.tail[1] = new ModelPart(this, 0, 54);
		this.tail[1].addCuboid(0.0F, 14.0F, 0.0F, 3.0F, 3.0F, 7.0F);
		this.tail[2] = new ModelPart(this);
		this.tail[2].setTextureOffset(41, 32).addCuboid(0.0F, 14.0F, 0.0F, 2.0F, 2.0F, 6.0F);
		this.tail[2].setTextureOffset(25, 19).addCuboid(1.0F, 10.5F, 3.0F, 1.0F, 9.0F, 9.0F);
		this.head.addChild(this.tail[0]);
		this.tail[0].addChild(this.tail[1]);
		this.tail[1].addChild(this.tail[2]);
		this.updateSpikeExtensions(0.0F, 0.0F);
	}

	@Override
	public Iterable<ModelPart> getParts() {
		return ImmutableList.<ModelPart>of(this.head);
	}

	public void setAngles(GuardianEntity guardianEntity, float f, float g, float h, float i, float j) {
		float k = h - (float)guardianEntity.age;
		this.head.yaw = i * (float) (Math.PI / 180.0);
		this.head.pitch = j * (float) (Math.PI / 180.0);
		float l = (1.0F - guardianEntity.getSpikesExtension(k)) * 0.55F;
		this.updateSpikeExtensions(h, l);
		this.eye.pivotZ = -8.25F;
		Entity entity = MinecraftClient.getInstance().getCameraEntity();
		if (guardianEntity.hasBeamTarget()) {
			entity = guardianEntity.getBeamTarget();
		}

		if (entity != null) {
			Vec3d vec3d = entity.getCameraPosVec(0.0F);
			Vec3d vec3d2 = guardianEntity.getCameraPosVec(0.0F);
			double d = vec3d.y - vec3d2.y;
			if (d > 0.0) {
				this.eye.pivotY = 0.0F;
			} else {
				this.eye.pivotY = 1.0F;
			}

			Vec3d vec3d3 = guardianEntity.getRotationVec(0.0F);
			vec3d3 = new Vec3d(vec3d3.x, 0.0, vec3d3.z);
			Vec3d vec3d4 = new Vec3d(vec3d2.x - vec3d.x, 0.0, vec3d2.z - vec3d.z).normalize().rotateY((float) (Math.PI / 2));
			double e = vec3d3.dotProduct(vec3d4);
			this.eye.pivotX = MathHelper.sqrt((float)Math.abs(e)) * 2.0F * (float)Math.signum(e);
		}

		this.eye.visible = true;
		float m = guardianEntity.getTailAngle(k);
		this.tail[0].yaw = MathHelper.sin(m) * (float) Math.PI * 0.05F;
		this.tail[1].yaw = MathHelper.sin(m) * (float) Math.PI * 0.1F;
		this.tail[1].pivotX = -1.5F;
		this.tail[1].pivotY = 0.5F;
		this.tail[1].pivotZ = 14.0F;
		this.tail[2].yaw = MathHelper.sin(m) * (float) Math.PI * 0.15F;
		this.tail[2].pivotX = 0.5F;
		this.tail[2].pivotY = 0.5F;
		this.tail[2].pivotZ = 6.0F;
	}

	private void updateSpikeExtensions(float animationProgress, float extension) {
		for (int i = 0; i < 12; i++) {
			this.spikes[i].pitch = (float) Math.PI * SPIKE_PITCHES[i];
			this.spikes[i].yaw = (float) Math.PI * SPIKE_YAWS[i];
			this.spikes[i].roll = (float) Math.PI * SPIKE_ROLLS[i];
			this.spikes[i].pivotX = SPIKE_PIVOTS_X[i] * (1.0F + MathHelper.cos(animationProgress * 1.5F + (float)i) * 0.01F - extension);
			this.spikes[i].pivotY = 16.0F + SPIKE_PIVOTS_Y[i] * (1.0F + MathHelper.cos(animationProgress * 1.5F + (float)i) * 0.01F - extension);
			this.spikes[i].pivotZ = SPIKE_PIVOTS_Z[i] * (1.0F + MathHelper.cos(animationProgress * 1.5F + (float)i) * 0.01F - extension);
		}
	}
}
