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
        this.shadowSize = f;
    }

    protected final boolean addFeature(FeatureRenderer<T, M> featureRenderer) {
        return this.features.add(featureRenderer);
    }

    @Override
    public M getModel() {
        return this.model;
    }

    @Override
    public void render(T livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        float n;
        Direction direction;
        matrixStack.push();
        ((EntityModel)this.model).handSwingProgress = this.getHandSwingProgress(livingEntity, g);
        ((EntityModel)this.model).riding = ((Entity)livingEntity).hasVehicle();
        ((EntityModel)this.model).child = ((LivingEntity)livingEntity).isBaby();
        float h = MathHelper.lerpAngleDegrees(g, ((LivingEntity)livingEntity).prevBodyYaw, ((LivingEntity)livingEntity).bodyYaw);
        float j = MathHelper.lerpAngleDegrees(g, ((LivingEntity)livingEntity).prevHeadYaw, ((LivingEntity)livingEntity).headYaw);
        float k = j - h;
        if (((Entity)livingEntity).hasVehicle() && ((Entity)livingEntity).getVehicle() instanceof LivingEntity) {
            LivingEntity livingEntity2 = (LivingEntity)((Entity)livingEntity).getVehicle();
            h = MathHelper.lerpAngleDegrees(g, livingEntity2.prevBodyYaw, livingEntity2.bodyYaw);
            k = j - h;
            float l = MathHelper.wrapDegrees(k);
            if (l < -85.0f) {
                l = -85.0f;
            }
            if (l >= 85.0f) {
                l = 85.0f;
            }
            h = j - l;
            if (l * l > 2500.0f) {
                h += l * 0.2f;
            }
            k = j - h;
        }
        float m = MathHelper.lerp(g, ((LivingEntity)livingEntity).prevPitch, ((LivingEntity)livingEntity).pitch);
        if (((Entity)livingEntity).getPose() == EntityPose.SLEEPING && (direction = ((LivingEntity)livingEntity).getSleepingDirection()) != null) {
            n = ((Entity)livingEntity).getEyeHeight(EntityPose.STANDING) - 0.1f;
            matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0, (float)(-direction.getOffsetZ()) * n);
        }
        float l = this.getCustomAngle(livingEntity, g);
        this.setupTransforms(livingEntity, matrixStack, l, h, g);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        this.scale(livingEntity, matrixStack, g);
        matrixStack.translate(0.0, -1.501f, 0.0);
        n = 0.0f;
        float o = 0.0f;
        if (!((Entity)livingEntity).hasVehicle() && ((LivingEntity)livingEntity).isAlive()) {
            n = MathHelper.lerp(g, ((LivingEntity)livingEntity).lastLimbDistance, ((LivingEntity)livingEntity).limbDistance);
            o = ((LivingEntity)livingEntity).limbAngle - ((LivingEntity)livingEntity).limbDistance * (1.0f - g);
            if (((LivingEntity)livingEntity).isBaby()) {
                o *= 3.0f;
            }
            if (n > 1.0f) {
                n = 1.0f;
            }
        }
        ((EntityModel)this.model).animateModel(livingEntity, o, n, g);
        boolean bl = ((Entity)livingEntity).isGlowing();
        boolean bl2 = this.method_4056(livingEntity, false);
        boolean bl3 = !bl2 && !((Entity)livingEntity).canSeePlayer(MinecraftClient.getInstance().player);
        ((EntityModel)this.model).setAngles(livingEntity, o, n, l, k, m);
        Identifier identifier = this.getTexture(livingEntity);
        RenderLayer renderLayer = bl3 ? RenderLayer.getEntityTranslucent(identifier) : (bl2 ? ((Model)this.model).getLayer(identifier) : RenderLayer.getOutline(identifier));
        if (bl2 || bl3 || bl) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
            int p = LivingEntityRenderer.getOverlay(livingEntity, this.getWhiteOverlayProgress(livingEntity, g));
            ((Model)this.model).render(matrixStack, vertexConsumer, i, p, 1.0f, 1.0f, 1.0f, bl3 ? 0.15f : 1.0f);
        }
        if (!((Entity)livingEntity).isSpectator()) {
            for (FeatureRenderer<T, M> featureRenderer : this.features) {
                featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntity, o, n, g, l, k, m);
            }
        }
        matrixStack.pop();
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    public static int getOverlay(LivingEntity livingEntity, float f) {
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
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f - g));
        }
        if (((LivingEntity)livingEntity).deathTime > 0) {
            float i = ((float)((LivingEntity)livingEntity).deathTime + h - 1.0f) / 20.0f * 1.6f;
            if ((i = MathHelper.sqrt(i)) > 1.0f) {
                i = 1.0f;
            }
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(i * this.getLyingAngle(livingEntity)));
        } else if (((LivingEntity)livingEntity).isUsingRiptide()) {
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-90.0f - ((LivingEntity)livingEntity).pitch));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(((float)((LivingEntity)livingEntity).age + h) * -75.0f));
        } else if (entityPose == EntityPose.SLEEPING) {
            Direction direction = ((LivingEntity)livingEntity).getSleepingDirection();
            float j = direction != null ? LivingEntityRenderer.getYaw(direction) : g;
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(j));
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(this.getLyingAngle(livingEntity)));
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(270.0f));
        } else if ((((Entity)livingEntity).hasCustomName() || livingEntity instanceof PlayerEntity) && ("Dinnerbone".equals(string = Formatting.strip(((Entity)livingEntity).getName().getString())) || "Grumm".equals(string)) && (!(livingEntity instanceof PlayerEntity) || ((PlayerEntity)livingEntity).isPartVisible(PlayerModelPart.CAPE))) {
            matrixStack.translate(0.0, ((Entity)livingEntity).getHeight() + 0.1f, 0.0);
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        }
    }

    protected float getHandSwingProgress(T livingEntity, float f) {
        return ((LivingEntity)livingEntity).getHandSwingProgress(f);
    }

    protected float getCustomAngle(T livingEntity, float f) {
        return (float)((LivingEntity)livingEntity).age + f;
    }

    protected float getLyingAngle(T livingEntity) {
        return 90.0f;
    }

    protected float getWhiteOverlayProgress(T livingEntity, float f) {
        return 0.0f;
    }

    protected void scale(T livingEntity, MatrixStack matrixStack, float f) {
    }

    @Override
    protected boolean hasLabel(T livingEntity) {
        boolean bl;
        float f;
        double d = this.renderManager.getSquaredDistanceToCamera((Entity)livingEntity);
        float f2 = f = ((Entity)livingEntity).isSneaky() ? 32.0f : 64.0f;
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
        return this.hasLabel((T)((LivingEntity)entity));
    }
}

