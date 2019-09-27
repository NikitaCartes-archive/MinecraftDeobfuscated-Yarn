package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>> extends EntityRenderer<T> implements FeatureRendererContext<T, M> {
	private static final Logger field_21011 = LogManager.getLogger();
	protected M model;
	protected final List<FeatureRenderer<T, M>> features = Lists.<FeatureRenderer<T, M>>newArrayList();

	public LivingEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, M entityModel, float f) {
		super(entityRenderDispatcher);
		this.model = entityModel;
		this.field_4673 = f;
	}

	protected final boolean addFeature(FeatureRenderer<T, M> featureRenderer) {
		return this.features.add(featureRenderer);
	}

	@Override
	public M getModel() {
		return this.model;
	}

	public void method_4054(T livingEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
		arg.method_22903();
		this.model.handSwingProgress = this.getHandSwingProgress(livingEntity, h);
		this.model.isRiding = livingEntity.hasVehicle();
		this.model.isChild = livingEntity.isBaby();
		float i = MathHelper.lerpAngleDegrees(h, livingEntity.prevBodyYaw, livingEntity.bodyYaw);
		float j = MathHelper.lerpAngleDegrees(h, livingEntity.prevHeadYaw, livingEntity.headYaw);
		float k = j - i;
		if (livingEntity.hasVehicle() && livingEntity.getVehicle() instanceof LivingEntity) {
			LivingEntity livingEntity2 = (LivingEntity)livingEntity.getVehicle();
			i = MathHelper.lerpAngleDegrees(h, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
			k = j - i;
			float l = MathHelper.wrapDegrees(k);
			if (l < -85.0F) {
				l = -85.0F;
			}

			if (l >= 85.0F) {
				l = 85.0F;
			}

			i = j - l;
			if (l * l > 2500.0F) {
				i += l * 0.2F;
			}

			k = j - i;
		}

		float m = MathHelper.lerp(h, livingEntity.prevPitch, livingEntity.pitch);
		if (livingEntity.getPose() == EntityPose.SLEEPING) {
			Direction direction = livingEntity.getSleepingDirection();
			if (direction != null) {
				float n = livingEntity.getEyeHeight(EntityPose.STANDING) - 0.1F;
				arg.method_22904((double)((float)(-direction.getOffsetX()) * n), 0.0, (double)((float)(-direction.getOffsetZ()) * n));
			}
		}

		float lx = this.getAge(livingEntity, h);
		this.setupTransforms(livingEntity, arg, lx, i, h);
		arg.method_22905(-1.0F, -1.0F, 1.0F);
		this.scale(livingEntity, arg, h);
		float n = 0.0625F;
		arg.method_22904(0.0, -1.501F, 0.0);
		float o = 0.0F;
		float p = 0.0F;
		if (!livingEntity.hasVehicle() && livingEntity.isAlive()) {
			o = MathHelper.lerp(h, livingEntity.lastLimbDistance, livingEntity.limbDistance);
			p = livingEntity.limbAngle - livingEntity.limbDistance * (1.0F - h);
			if (livingEntity.isBaby()) {
				p *= 3.0F;
			}

			if (o > 1.0F) {
				o = 1.0F;
			}
		}

		this.model.animateModel(livingEntity, p, o, h);
		boolean bl = this.method_4056(livingEntity, false);
		boolean bl2 = !bl && !livingEntity.canSeePlayer(MinecraftClient.getInstance().player);
		int q = livingEntity.getLightmapCoordinates();
		if (bl || bl2) {
			this.model.setAngles(livingEntity, p, o, lx, k, m, 0.0625F);
			class_4588 lv = arg2.getBuffer(
				bl2 ? BlockRenderLayer.method_23019(this.getTexture(livingEntity), true, true, false) : BlockRenderLayer.method_23017(this.getTexture(livingEntity))
			);
			method_23184(livingEntity, lv, this.method_23185(livingEntity, h));
			this.model.method_22957(arg, lv, q);
			lv.method_22923();
		}

		if (!livingEntity.isSpectator()) {
			for (FeatureRenderer<T, M> featureRenderer : this.features) {
				featureRenderer.render(arg, arg2, q, livingEntity, p, o, h, lx, k, m, 0.0625F);
			}
		}

		arg.method_22909();
		super.render(livingEntity, d, e, f, g, h, arg, arg2);
	}

	public static void method_23184(LivingEntity livingEntity, class_4588 arg, float f) {
		arg.method_22922(class_4608.method_23210(f), class_4608.method_23212(livingEntity.hurtTime > 0 || livingEntity.deathTime > 0));
	}

	protected boolean method_4056(T livingEntity, boolean bl) {
		return !livingEntity.isInvisible() || bl;
	}

	private static float method_18656(Direction direction) {
		switch (direction) {
			case SOUTH:
				return 90.0F;
			case WEST:
				return 0.0F;
			case NORTH:
				return 270.0F;
			case EAST:
				return 180.0F;
			default:
				return 0.0F;
		}
	}

	protected void setupTransforms(T livingEntity, class_4587 arg, float f, float g, float h) {
		EntityPose entityPose = livingEntity.getPose();
		if (entityPose != EntityPose.SLEEPING) {
			arg.method_22907(Vector3f.field_20705.method_23214(180.0F - g, true));
		}

		if (livingEntity.deathTime > 0) {
			float i = ((float)livingEntity.deathTime + h - 1.0F) / 20.0F * 1.6F;
			i = MathHelper.sqrt(i);
			if (i > 1.0F) {
				i = 1.0F;
			}

			arg.method_22907(Vector3f.field_20707.method_23214(i * this.getLyingAngle(livingEntity), true));
		} else if (livingEntity.isUsingRiptide()) {
			arg.method_22907(Vector3f.field_20703.method_23214(-90.0F - livingEntity.pitch, true));
			arg.method_22907(Vector3f.field_20705.method_23214(((float)livingEntity.age + h) * -75.0F, true));
		} else if (entityPose == EntityPose.SLEEPING) {
			Direction direction = livingEntity.getSleepingDirection();
			arg.method_22907(Vector3f.field_20705.method_23214(direction != null ? method_18656(direction) : g, true));
			arg.method_22907(Vector3f.field_20707.method_23214(this.getLyingAngle(livingEntity), true));
			arg.method_22907(Vector3f.field_20705.method_23214(270.0F, true));
		} else if (livingEntity.hasCustomName() || livingEntity instanceof PlayerEntity) {
			String string = Formatting.strip(livingEntity.getName().getString());
			if (("Dinnerbone".equals(string) || "Grumm".equals(string))
				&& (!(livingEntity instanceof PlayerEntity) || ((PlayerEntity)livingEntity).isSkinOverlayVisible(PlayerModelPart.CAPE))) {
				arg.method_22904(0.0, (double)(livingEntity.getHeight() + 0.1F), 0.0);
				arg.method_22907(Vector3f.field_20707.method_23214(180.0F, true));
			}
		}
	}

	protected float getHandSwingProgress(T livingEntity, float f) {
		return livingEntity.getHandSwingProgress(f);
	}

	protected float getAge(T livingEntity, float f) {
		return (float)livingEntity.age + f;
	}

	protected float getLyingAngle(T livingEntity) {
		return 90.0F;
	}

	protected float method_23185(T livingEntity, float f) {
		return 0.0F;
	}

	protected void scale(T livingEntity, class_4587 arg, float f) {
	}

	protected boolean method_4055(T livingEntity) {
		double d = this.renderManager.method_23168(livingEntity);
		float f = livingEntity.method_21751() ? 32.0F : 64.0F;
		if (d >= (double)(f * f)) {
			return false;
		} else {
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
			boolean bl = !livingEntity.canSeePlayer(clientPlayerEntity);
			if (livingEntity != clientPlayerEntity) {
				AbstractTeam abstractTeam = livingEntity.getScoreboardTeam();
				AbstractTeam abstractTeam2 = clientPlayerEntity.getScoreboardTeam();
				if (abstractTeam != null) {
					AbstractTeam.VisibilityRule visibilityRule = abstractTeam.getNameTagVisibilityRule();
					switch (visibilityRule) {
						case ALWAYS:
							return bl;
						case NEVER:
							return false;
						case HIDE_FOR_OTHER_TEAMS:
							return abstractTeam2 == null ? bl : abstractTeam.isEqual(abstractTeam2) && (abstractTeam.shouldShowFriendlyInvisibles() || bl);
						case HIDE_FOR_OWN_TEAM:
							return abstractTeam2 == null ? bl : !abstractTeam.isEqual(abstractTeam2) && bl;
						default:
							return true;
					}
				}
			}

			return MinecraftClient.isHudEnabled() && livingEntity != minecraftClient.getCameraEntity() && bl && !livingEntity.hasPassengers();
		}
	}
}
