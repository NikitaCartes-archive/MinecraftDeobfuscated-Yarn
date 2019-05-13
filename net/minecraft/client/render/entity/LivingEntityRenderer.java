/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import java.nio.FloatBuffer;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormat;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(value=EnvType.CLIENT)
public abstract class LivingEntityRenderer<T extends LivingEntity, M extends EntityModel<T>>
extends EntityRenderer<T>
implements FeatureRendererContext<T, M> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final NativeImageBackedTexture colorOverlayTexture = SystemUtil.consume(new NativeImageBackedTexture(16, 16, false), nativeImageBackedTexture -> {
        nativeImageBackedTexture.getImage().untrack();
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                nativeImageBackedTexture.getImage().setPixelRGBA(j, i, -1);
            }
        }
        nativeImageBackedTexture.upload();
    });
    protected M model;
    protected final FloatBuffer colorOverlayBuffer = GlAllocationUtils.allocateFloatBuffer(4);
    protected final List<FeatureRenderer<T, M>> features = Lists.newArrayList();
    protected boolean disableOutlineRender;

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

    public void method_4054(T livingEntity, double d, double e, double f, float g, float h) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        ((EntityModel)this.model).handSwingProgress = this.getHandSwingProgress(livingEntity, h);
        ((EntityModel)this.model).isRiding = ((Entity)livingEntity).hasVehicle();
        ((EntityModel)this.model).isChild = ((LivingEntity)livingEntity).isBaby();
        try {
            float l;
            float i = MathHelper.lerpAngleDegrees(h, ((LivingEntity)livingEntity).field_6220, ((LivingEntity)livingEntity).field_6283);
            float j = MathHelper.lerpAngleDegrees(h, ((LivingEntity)livingEntity).prevHeadYaw, ((LivingEntity)livingEntity).headYaw);
            float k = j - i;
            if (((Entity)livingEntity).hasVehicle() && ((Entity)livingEntity).getVehicle() instanceof LivingEntity) {
                LivingEntity livingEntity2 = (LivingEntity)((Entity)livingEntity).getVehicle();
                i = MathHelper.lerpAngleDegrees(h, livingEntity2.field_6220, livingEntity2.field_6283);
                k = j - i;
                l = MathHelper.wrapDegrees(k);
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
            this.method_4048(livingEntity, d, e, f);
            l = this.getAge(livingEntity, h);
            this.setupTransforms(livingEntity, l, i, h);
            float n = this.scaleAndTranslate(livingEntity, h);
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
            GlStateManager.enableAlphaTest();
            ((EntityModel)this.model).animateModel(livingEntity, p, o, h);
            ((EntityModel)this.model).setAngles(livingEntity, p, o, l, k, m, n);
            if (this.renderOutlines) {
                boolean bl = this.beforeOutlineRender(livingEntity);
                GlStateManager.enableColorMaterial();
                GlStateManager.setupSolidRenderingTextureCombine(this.getOutlineColor(livingEntity));
                if (!this.disableOutlineRender) {
                    this.render(livingEntity, p, o, l, k, m, n);
                }
                if (!((Entity)livingEntity).isSpectator()) {
                    this.renderFeatures(livingEntity, p, o, h, l, k, m, n);
                }
                GlStateManager.tearDownSolidRenderingTextureCombine();
                GlStateManager.disableColorMaterial();
                if (bl) {
                    this.afterOutlineRender();
                }
            } else {
                boolean bl = this.applyOverlayColor(livingEntity, h);
                this.render(livingEntity, p, o, l, k, m, n);
                if (bl) {
                    this.disableOverlayColor();
                }
                GlStateManager.depthMask(true);
                if (!((Entity)livingEntity).isSpectator()) {
                    this.renderFeatures(livingEntity, p, o, h, l, k, m, n);
                }
            }
            GlStateManager.disableRescaleNormal();
        } catch (Exception exception) {
            LOGGER.error("Couldn't render entity", (Throwable)exception);
        }
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.enableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        super.render(livingEntity, d, e, f, g, h);
    }

    public float scaleAndTranslate(T livingEntity, float f) {
        GlStateManager.enableRescaleNormal();
        GlStateManager.scalef(-1.0f, -1.0f, 1.0f);
        this.scale(livingEntity, f);
        float g = 0.0625f;
        GlStateManager.translatef(0.0f, -1.501f, 0.0f);
        return 0.0625f;
    }

    protected boolean beforeOutlineRender(T livingEntity) {
        GlStateManager.disableLighting();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.disableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        return true;
    }

    protected void afterOutlineRender() {
        GlStateManager.enableLighting();
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.enableTexture();
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }

    protected void render(T livingEntity, float f, float g, float h, float i, float j, float k) {
        boolean bl2;
        boolean bl = this.method_4056(livingEntity);
        boolean bl3 = bl2 = !bl && !((Entity)livingEntity).canSeePlayer(MinecraftClient.getInstance().player);
        if (bl || bl2) {
            if (!this.bindEntityTexture(livingEntity)) {
                return;
            }
            if (bl2) {
                GlStateManager.setProfile(GlStateManager.RenderMode.TRANSPARENT_MODEL);
            }
            ((EntityModel)this.model).render(livingEntity, f, g, h, i, j, k);
            if (bl2) {
                GlStateManager.unsetProfile(GlStateManager.RenderMode.TRANSPARENT_MODEL);
            }
        }
    }

    protected boolean method_4056(T livingEntity) {
        return !((Entity)livingEntity).isInvisible() || this.renderOutlines;
    }

    protected boolean applyOverlayColor(T livingEntity, float f) {
        return this.applyOverlayColor(livingEntity, f, true);
    }

    protected boolean applyOverlayColor(T livingEntity, float f, boolean bl) {
        boolean bl3;
        float g = ((Entity)livingEntity).getBrightnessAtEyes();
        int i = this.getOverlayColor(livingEntity, g, f);
        boolean bl2 = (i >> 24 & 0xFF) > 0;
        boolean bl4 = bl3 = ((LivingEntity)livingEntity).hurtTime > 0 || ((LivingEntity)livingEntity).deathTime > 0;
        if (bl2 || bl3) {
            if (!bl2 && !bl) {
                return false;
            }
            GlStateManager.activeTexture(GLX.GL_TEXTURE0);
            GlStateManager.enableTexture();
            GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
            GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
            GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
            GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
            GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
            GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
            GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
            GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
            GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
            GlStateManager.activeTexture(GLX.GL_TEXTURE1);
            GlStateManager.enableTexture();
            GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
            GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, GLX.GL_INTERPOLATE);
            GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_CONSTANT);
            GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
            GlStateManager.texEnv(8960, GLX.GL_SOURCE2_RGB, GLX.GL_CONSTANT);
            GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
            GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
            GlStateManager.texEnv(8960, GLX.GL_OPERAND2_RGB, 770);
            GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
            GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
            GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
            this.colorOverlayBuffer.position(0);
            if (bl3) {
                this.colorOverlayBuffer.put(1.0f);
                this.colorOverlayBuffer.put(0.0f);
                this.colorOverlayBuffer.put(0.0f);
                this.colorOverlayBuffer.put(0.3f);
            } else {
                float h = (float)(i >> 24 & 0xFF) / 255.0f;
                float j = (float)(i >> 16 & 0xFF) / 255.0f;
                float k = (float)(i >> 8 & 0xFF) / 255.0f;
                float l = (float)(i & 0xFF) / 255.0f;
                this.colorOverlayBuffer.put(j);
                this.colorOverlayBuffer.put(k);
                this.colorOverlayBuffer.put(l);
                this.colorOverlayBuffer.put(1.0f - h);
            }
            this.colorOverlayBuffer.flip();
            GlStateManager.texEnv(8960, 8705, this.colorOverlayBuffer);
            GlStateManager.activeTexture(GLX.GL_TEXTURE2);
            GlStateManager.enableTexture();
            GlStateManager.bindTexture(colorOverlayTexture.getGlId());
            GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
            GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
            GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_PREVIOUS);
            GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_TEXTURE1);
            GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
            GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
            GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
            GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
            GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
            GlStateManager.activeTexture(GLX.GL_TEXTURE0);
            return true;
        }
        return false;
    }

    protected void disableOverlayColor() {
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
        GlStateManager.enableTexture();
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_ALPHA, GLX.GL_PRIMARY_COLOR);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_ALPHA, 770);
        GlStateManager.activeTexture(GLX.GL_TEXTURE1);
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.activeTexture(GLX.GL_TEXTURE2);
        GlStateManager.disableTexture();
        GlStateManager.bindTexture(0);
        GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
        GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
        GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
        GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
        GlStateManager.activeTexture(GLX.GL_TEXTURE0);
    }

    protected void method_4048(T livingEntity, double d, double e, double f) {
        Direction direction;
        if (((Entity)livingEntity).getPose() == EntityPose.SLEEPING && (direction = ((LivingEntity)livingEntity).getSleepingDirection()) != null) {
            float g = ((Entity)livingEntity).getEyeHeight(EntityPose.STANDING) - 0.1f;
            GlStateManager.translatef((float)d - (float)direction.getOffsetX() * g, (float)e, (float)f - (float)direction.getOffsetZ() * g);
            return;
        }
        GlStateManager.translatef((float)d, (float)e, (float)f);
    }

    private static float method_18656(Direction direction) {
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

    protected void setupTransforms(T livingEntity, float f, float g, float h) {
        String string;
        EntityPose entityPose = ((Entity)livingEntity).getPose();
        if (entityPose != EntityPose.SLEEPING) {
            GlStateManager.rotatef(180.0f - g, 0.0f, 1.0f, 0.0f);
        }
        if (((LivingEntity)livingEntity).deathTime > 0) {
            float i = ((float)((LivingEntity)livingEntity).deathTime + h - 1.0f) / 20.0f * 1.6f;
            if ((i = MathHelper.sqrt(i)) > 1.0f) {
                i = 1.0f;
            }
            GlStateManager.rotatef(i * this.getLyingAngle(livingEntity), 0.0f, 0.0f, 1.0f);
        } else if (((LivingEntity)livingEntity).isUsingRiptide()) {
            GlStateManager.rotatef(-90.0f - ((LivingEntity)livingEntity).pitch, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotatef(((float)((LivingEntity)livingEntity).age + h) * -75.0f, 0.0f, 1.0f, 0.0f);
        } else if (entityPose == EntityPose.SLEEPING) {
            Direction direction = ((LivingEntity)livingEntity).getSleepingDirection();
            GlStateManager.rotatef(direction != null ? LivingEntityRenderer.method_18656(direction) : g, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotatef(this.getLyingAngle(livingEntity), 0.0f, 0.0f, 1.0f);
            GlStateManager.rotatef(270.0f, 0.0f, 1.0f, 0.0f);
        } else if ((((Entity)livingEntity).hasCustomName() || livingEntity instanceof PlayerEntity) && (string = ChatFormat.stripFormatting(((Entity)livingEntity).getName().getString())) != null && ("Dinnerbone".equals(string) || "Grumm".equals(string)) && (!(livingEntity instanceof PlayerEntity) || ((PlayerEntity)livingEntity).isSkinOverlayVisible(PlayerModelPart.CAPE))) {
            GlStateManager.translatef(0.0f, ((Entity)livingEntity).getHeight() + 0.1f, 0.0f);
            GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
        }
    }

    protected float getHandSwingProgress(T livingEntity, float f) {
        return ((LivingEntity)livingEntity).getHandSwingProgress(f);
    }

    protected float getAge(T livingEntity, float f) {
        return (float)((LivingEntity)livingEntity).age + f;
    }

    protected void renderFeatures(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
        for (FeatureRenderer<T, M> featureRenderer : this.features) {
            boolean bl = this.applyOverlayColor(livingEntity, h, featureRenderer.hasHurtOverlay());
            featureRenderer.render(livingEntity, f, g, h, i, j, k, l);
            if (!bl) continue;
            this.disableOverlayColor();
        }
    }

    protected float getLyingAngle(T livingEntity) {
        return 90.0f;
    }

    protected int getOverlayColor(T livingEntity, float f, float g) {
        return 0;
    }

    protected void scale(T livingEntity, float f) {
    }

    public void method_4041(T livingEntity, double d, double e, double f) {
        float h;
        if (!this.method_4055(livingEntity)) {
            return;
        }
        double g = ((Entity)livingEntity).squaredDistanceTo(this.renderManager.camera.getPos());
        float f2 = h = ((Entity)livingEntity).isInSneakingPose() ? 32.0f : 64.0f;
        if (g >= (double)(h * h)) {
            return;
        }
        String string = ((Entity)livingEntity).getDisplayName().getFormattedText();
        GlStateManager.alphaFunc(516, 0.1f);
        this.renderLabel(livingEntity, d, e, f, string, g);
    }

    protected boolean method_4055(T livingEntity) {
        boolean bl;
        ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
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
        return MinecraftClient.isHudEnabled() && livingEntity != this.renderManager.camera.getFocusedEntity() && bl && !((Entity)livingEntity).hasPassengers();
    }

    @Override
    protected /* synthetic */ boolean hasLabel(Entity entity) {
        return this.method_4055((LivingEntity)entity);
    }

    @Override
    public /* synthetic */ void renderLabelIfPresent(Entity entity, double d, double e, double f) {
        this.method_4041((LivingEntity)entity, d, e, f);
    }
}

