/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Model;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends EntityRenderer<T>
implements FeatureRendererContext<T, M> {
    private static final Logger LOGGER = LogManager.getLogger();
    protected M model;
    protected final List<FeatureRenderer<T, M>> features = Lists.newArrayList();

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

    public void method_4054(T livingEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        float n;
        Direction direction;
        matrixStack.push();
        ((EntityModel)this.model).handSwingProgress = this.getHandSwingProgress(livingEntity, h);
        ((EntityModel)this.model).isRiding = ((Entity)livingEntity).hasVehicle();
        ((EntityModel)this.model).isChild = ((LivingEntity)livingEntity).isBaby();
        float i = MathHelper.lerpAngleDegrees(h, ((LivingEntity)livingEntity).prevBodyYaw, ((LivingEntity)livingEntity).bodyYaw);
        float j = MathHelper.lerpAngleDegrees(h, ((LivingEntity)livingEntity).prevHeadYaw, ((LivingEntity)livingEntity).headYaw);
        float k = j - i;
        if (((Entity)livingEntity).hasVehicle() && ((Entity)livingEntity).getVehicle() instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)((Entity)livingEntity).getVehicle();
            i = MathHelper.lerpAngleDegrees(h, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
            k = j - i;
            float l = MathHelper.wrapDegrees(k);
            if (l < -85.0f) {
                l = -85.0f;
            }
            if (l >= 85.0f) {
                l = 85.0f;
            }
            i = j - l;
            if (l * l > 2500.0f) {
                i += l * 0.2f;
            }
            k = j - i;
        }
        float m = MathHelper.lerp(h, ((LivingEntity)livingEntity).prevPitch, ((LivingEntity)livingEntity).pitch);
        if (((Entity)livingEntity).getPose() == EntityPose.SLEEPING && (direction = ((LivingEntity)livingEntity).getSleepingDirection()) != null) {
            n = ((Entity)livingEntity).getEyeHeight(EntityPose.STANDING) - 0.1f;
            matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0, (float)(-direction.getOffsetZ()) * n);
        }
        float l = this.getAge(livingEntity, h);
        this.setupTransforms(livingEntity, matrixStack, l, i, h);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        this.scale(livingEntity, matrixStack, h);
        n = 0.0625f;
        matrixStack.translate(0.0, -1.501f, 0.0);
        float o = 0.0f;
        float p = 0.0f;
        if (!((Entity)livingEntity).hasVehicle() && ((LivingEntity)livingEntity).isAlive()) {
            o = MathHelper.lerp(h, ((LivingEntity)livingEntity).lastLimbDistance, ((LivingEntity)livingEntity).limbDistance);
            p = ((LivingEntity)livingEntity).limbAngle - ((LivingEntity)livingEntity).limbDistance * (1.0f - h);
            if (((LivingEntity)livingEntity).isBaby()) {
                p *= 3.0f;
            }
            if (o > 1.0f) {
                o = 1.0f;
            }
        }
        ((EntityModel)this.model).animateModel(livingEntity, p, o, h);
        boolean bl = this.method_4056(livingEntity, false);
        boolean bl2 = !bl && !((Entity)livingEntity).canSeePlayer(MinecraftClient.getInstance().player);
        int q = ((Entity)livingEntity).getLightmapCoordinates();
        ((EntityModel)this.model).setAngles(livingEntity, p, o, l, k, m, 0.0625f);
        if (bl || bl2) {
            Identifier identifier = this.getTexture(livingEntity);
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(bl2 ? RenderLayer.getEntityForceTranslucent(identifier) : ((Model)this.model).getLayer(identifier));
            ((Model)this.model).render(matrixStack, vertexConsumer, q, LivingEntityRenderer.method_23622(livingEntity, this.method_23185(livingEntity, h)), 1.0f, 1.0f, 1.0f);
        }
        if (!((Entity)livingEntity).isSpectator()) {
            for (FeatureRenderer<T, M> featureRenderer : this.features) {
                featureRenderer.render(matrixStack, vertexConsumerProvider, q, livingEntity, p, o, h, l, k, m, 0.0625f);
            }
        }
        matrixStack.pop();
        super.render(livingEntity, d, e, f, g, h, matrixStack, vertexConsumerProvider);
    }

    public static int method_23622(LivingEntity livingEntity, float f) {
        return OverlayTexture.packUv(OverlayTexture.getU(f), OverlayTexture.getV(livingEntity.hurtTime > 0 || livingEntity.deathTime > 0));
    }

    protected boolean method_4056(T livingEntity, boolean bl) {
        return !((Entity)livingEntity).isInvisible() || bl;
    }

    private static float getYaw(Direction direction) {
        switch (direction) {
            case SOUTH: {
                return 90.0f;
            }
            case WEST: {
                return 0.0f;
            }
            case NORTH: {
                return 270.0f;
            }
            case EAST: {
                return 180.0f;
            }
        }
        return 0.0f;
    }

    protected void setupTransforms(T livingEntity, MatrixStack matrixStack, float f, float g, float h) {
        String string;
        EntityPose entityPose = ((Entity)livingEntity).getPose();
        if (entityPose != EntityPose.SLEEPING) {
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0f - g));
        }
        if (((LivingEntity)livingEntity).deathTime > 0) {
            float i = ((float)((LivingEntity)livingEntity).deathTime + h - 1.0f) / 20.0f * 1.6f;
            if ((i = MathHelper.sqrt(i)) > 1.0f) {
                i = 1.0f;
            }
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(i * this.getLyingAngle(livingEntity)));
        } else if (((LivingEntity)livingEntity).isUsingRiptide()) {
            matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-90.0f - ((LivingEntity)livingEntity).pitch));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(((float)((LivingEntity)livingEntity).age + h) * -75.0f));
        } else if (entityPose == EntityPose.SLEEPING) {
            Direction direction = ((LivingEntity)livingEntity).getSleepingDirection();
            float j = direction != null ? LivingEntityRenderer.getYaw(direction) : g;
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(j));
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(this.getLyingAngle(livingEntity)));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(270.0f));
        } else if ((((Entity)livingEntity).hasCustomName() || livingEntity instanceof PlayerEntity) && ("Dinnerbone".equals(string = Formatting.strip(((Entity)livingEntity).getName().getString())) || "Grumm".equals(string)) && (!(livingEntity instanceof PlayerEntity) || ((PlayerEntity)livingEntity).isSkinOverlayVisible(PlayerModelPart.CAPE))) {
            matrixStack.translate(0.0, ((Entity)livingEntity).getHeight() + 0.1f, 0.0);
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(180.0f));
        }
    }

    protected float getHandSwingProgress(T livingEntity, float f) {
        return ((LivingEntity)livingEntity).getHandSwingProgress(f);
    }

    protected float getAge(T livingEntity, float f) {
        return (float)((LivingEntity)livingEntity).age + f;
    }

    protected float getLyingAngle(T livingEntity) {
        return 90.0f;
    }

    protected float method_23185(T livingEntity, float f) {
        return 0.0f;
    }

    protected void scale(T livingEntity, MatrixStack matrixStack, float f) {
    }

    protected boolean method_4055(T livingEntity) {
        boolean bl;
        float f;
        double d = this.renderManager.getSquaredDistanceToCamera((Entity)livingEntity);
        float f2 = f = ((Entity)livingEntity).method_21751() ? 32.0f : 64.0f;
        if (d >= (double)(f * f)) {
            return false;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        boolean bl2 = bl = !((Entity)livingEntity).canSeePlayer(clientPlayerEntity);
        if (livingEntity != clientPlayerEntity) {
            AbstractTeam abstractTeam = ((Entity)livingEntity).getScoreboardTeam();
            AbstractTeam abstractTeam2 = clientPlayerEntity.getScoreboardTeam();
            if (abstractTeam != null) {
                AbstractTeam.VisibilityRule visibilityRule = abstractTeam.getNameTagVisibilityRule();
                switch (visibilityRule) {
                    case ALWAYS: {
                        return bl;
                    }
                    case NEVER: {
                        return false;
                    }
                    case HIDE_FOR_OTHER_TEAMS: {
                        return abstractTeam2 == null ? bl : abstractTeam.isEqual(abstractTeam2) && (abstractTeam.shouldShowFriendlyInvisibles() || bl);
                    }
                    case HIDE_FOR_OWN_TEAM: {
                        return abstractTeam2 == null ? bl : !abstractTeam.isEqual(abstractTeam2) && bl;
                    }
                }
                return true;
            }
        }
        return MinecraftClient.isHudEnabled() && livingEntity != minecraftClient.getCameraEntity() && bl && !((Entity)livingEntity).hasPassengers();
    }

    @Override
    protected /* synthetic */ boolean hasLabel(Entity entity) {
        return this.method_4055((LivingEntity)entity);
    }
}

