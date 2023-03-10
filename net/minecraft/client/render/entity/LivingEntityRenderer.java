/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
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
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends EntityRenderer<T>
implements FeatureRendererContext<T, M> {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final float field_32939 = 0.1f;
    protected M model;
    protected final List<FeatureRenderer<T, M>> features = Lists.newArrayList();

    public LivingEntityRenderer(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
        super(ctx);
        this.model = model;
        this.shadowRadius = shadowRadius;
    }

    protected final boolean addFeature(FeatureRenderer<T, M> feature) {
        return this.features.add(feature);
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
        float m = MathHelper.lerp(g, ((LivingEntity)livingEntity).prevPitch, ((Entity)livingEntity).getPitch());
        if (LivingEntityRenderer.shouldFlipUpsideDown(livingEntity)) {
            m *= -1.0f;
            k *= -1.0f;
        }
        if (((Entity)livingEntity).isInPose(EntityPose.SLEEPING) && (direction = ((LivingEntity)livingEntity).getSleepingDirection()) != null) {
            n = ((Entity)livingEntity).getEyeHeight(EntityPose.STANDING) - 0.1f;
            matrixStack.translate((float)(-direction.getOffsetX()) * n, 0.0f, (float)(-direction.getOffsetZ()) * n);
        }
        float l = this.getAnimationProgress(livingEntity, g);
        this.setupTransforms(livingEntity, matrixStack, l, h, g);
        matrixStack.scale(-1.0f, -1.0f, 1.0f);
        this.scale(livingEntity, matrixStack, g);
        matrixStack.translate(0.0f, -1.501f, 0.0f);
        n = 0.0f;
        float o = 0.0f;
        if (!((Entity)livingEntity).hasVehicle() && ((LivingEntity)livingEntity).isAlive()) {
            n = ((LivingEntity)livingEntity).limbAnimator.getSpeed(g);
            o = ((LivingEntity)livingEntity).limbAnimator.getPos(g);
            if (((LivingEntity)livingEntity).isBaby()) {
                o *= 3.0f;
            }
            if (n > 1.0f) {
                n = 1.0f;
            }
        }
        ((EntityModel)this.model).animateModel(livingEntity, o, n, g);
        ((EntityModel)this.model).setAngles(livingEntity, o, n, l, k, m);
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        boolean bl = this.isVisible(livingEntity);
        boolean bl2 = !bl && !((Entity)livingEntity).isInvisibleTo(minecraftClient.player);
        boolean bl3 = minecraftClient.hasOutline((Entity)livingEntity);
        RenderLayer renderLayer = this.getRenderLayer(livingEntity, bl, bl2, bl3);
        if (renderLayer != null) {
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
            int p = LivingEntityRenderer.getOverlay(livingEntity, this.getAnimationCounter(livingEntity, g));
            ((Model)this.model).render(matrixStack, vertexConsumer, i, p, 1.0f, 1.0f, 1.0f, bl2 ? 0.15f : 1.0f);
        }
        if (!((Entity)livingEntity).isSpectator()) {
            for (FeatureRenderer<T, M> featureRenderer : this.features) {
                featureRenderer.render(matrixStack, vertexConsumerProvider, i, livingEntity, o, n, g, l, k, m);
            }
        }
        matrixStack.pop();
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    /**
     * Gets the render layer appropriate for rendering the passed entity. Returns null if the entity should not be rendered.
     */
    @Nullable
    protected RenderLayer getRenderLayer(T entity, boolean showBody, boolean translucent, boolean showOutline) {
        Identifier identifier = this.getTexture(entity);
        if (translucent) {
            return RenderLayer.getItemEntityTranslucentCull(identifier);
        }
        if (showBody) {
            return ((Model)this.model).getLayer(identifier);
        }
        if (showOutline) {
            return RenderLayer.getOutline(identifier);
        }
        return null;
    }

    /**
     * {@return the packed overlay color for an entity} It is determined by the entity's death progress and whether the entity is flashing.
     */
    public static int getOverlay(LivingEntity entity, float whiteOverlayProgress) {
        return OverlayTexture.packUv(OverlayTexture.getU(whiteOverlayProgress), OverlayTexture.getV(entity.hurtTime > 0 || entity.deathTime > 0));
    }

    protected boolean isVisible(T entity) {
        return !((Entity)entity).isInvisible();
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

    /**
     * {@return if this entity is shaking} Specifically, in the way a zombie villager,
     * zombie, husk, or piglin undergoing conversion shakes.
     */
    protected boolean isShaking(T entity) {
        return ((Entity)entity).isFrozen();
    }

    protected void setupTransforms(T entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        if (this.isShaking(entity)) {
            bodyYaw += (float)(Math.cos((double)((LivingEntity)entity).age * 3.25) * Math.PI * (double)0.4f);
        }
        if (!((Entity)entity).isInPose(EntityPose.SLEEPING)) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - bodyYaw));
        }
        if (((LivingEntity)entity).deathTime > 0) {
            float f = ((float)((LivingEntity)entity).deathTime + tickDelta - 1.0f) / 20.0f * 1.6f;
            if ((f = MathHelper.sqrt(f)) > 1.0f) {
                f = 1.0f;
            }
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * this.getLyingAngle(entity)));
        } else if (((LivingEntity)entity).isUsingRiptide()) {
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f - ((Entity)entity).getPitch()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(((float)((LivingEntity)entity).age + tickDelta) * -75.0f));
        } else if (((Entity)entity).isInPose(EntityPose.SLEEPING)) {
            Direction direction = ((LivingEntity)entity).getSleepingDirection();
            float g = direction != null ? LivingEntityRenderer.getYaw(direction) : bodyYaw;
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(g));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.getLyingAngle(entity)));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(270.0f));
        } else if (LivingEntityRenderer.shouldFlipUpsideDown(entity)) {
            matrices.translate(0.0f, ((Entity)entity).getHeight() + 0.1f, 0.0f);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        }
    }

    protected float getHandSwingProgress(T entity, float tickDelta) {
        return ((LivingEntity)entity).getHandSwingProgress(tickDelta);
    }

    /**
     * This value is passed to other methods when calculating angles for animation.
     * It's typically just the sum of the entity's age (in ticks) and the passed in tickDelta.
     */
    protected float getAnimationProgress(T entity, float tickDelta) {
        return (float)((LivingEntity)entity).age + tickDelta;
    }

    protected float getLyingAngle(T entity) {
        return 90.0f;
    }

    protected float getAnimationCounter(T entity, float tickDelta) {
        return 0.0f;
    }

    protected void scale(T entity, MatrixStack matrices, float amount) {
    }

    @Override
    protected boolean hasLabel(T livingEntity) {
        boolean bl;
        float f;
        double d = this.dispatcher.getSquaredDistanceToCamera((Entity)livingEntity);
        float f2 = f = ((Entity)livingEntity).isSneaky() ? 32.0f : 64.0f;
        if (d >= (double)(f * f)) {
            return false;
        }
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        ClientPlayerEntity clientPlayerEntity = minecraftClient.player;
        boolean bl2 = bl = !((Entity)livingEntity).isInvisibleTo(clientPlayerEntity);
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

    public static boolean shouldFlipUpsideDown(LivingEntity entity) {
        String string;
        if ((entity instanceof PlayerEntity || entity.hasCustomName()) && ("Dinnerbone".equals(string = Formatting.strip(entity.getName().getString())) || "Grumm".equals(string))) {
            return !(entity instanceof PlayerEntity) || ((PlayerEntity)entity).isPartVisible(PlayerModelPart.CAPE);
        }
        return false;
    }
}

