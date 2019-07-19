/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.ArmorBipedFeatureRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityRenderer
extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        this(entityRenderDispatcher, false);
    }

    public PlayerEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, boolean bl) {
        super(entityRenderDispatcher, new PlayerEntityModel(0.0f, bl), 0.5f);
        this.addFeature(new ArmorBipedFeatureRenderer(this, new BipedEntityModel(0.5f), new BipedEntityModel(1.0f)));
        this.addFeature(new HeldItemFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new StuckArrowsFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new Deadmau5FeatureRenderer(this));
        this.addFeature(new CapeFeatureRenderer(this));
        this.addFeature(new HeadFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new ElytraFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new ShoulderParrotFeatureRenderer<AbstractClientPlayerEntity>(this));
        this.addFeature(new TridentRiptideFeatureRenderer<AbstractClientPlayerEntity>(this));
    }

    @Override
    public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, float g, float h) {
        if (abstractClientPlayerEntity.isMainPlayer() && this.renderManager.camera.getFocusedEntity() != abstractClientPlayerEntity) {
            return;
        }
        double i = e;
        if (abstractClientPlayerEntity.isInSneakingPose()) {
            i -= 0.125;
        }
        this.setModelPose(abstractClientPlayerEntity);
        GlStateManager.setProfile(GlStateManager.RenderMode.PLAYER_SKIN);
        super.render(abstractClientPlayerEntity, d, i, f, g, h);
        GlStateManager.unsetProfile(GlStateManager.RenderMode.PLAYER_SKIN);
    }

    private void setModelPose(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        PlayerEntityModel playerEntityModel = (PlayerEntityModel)this.getModel();
        if (abstractClientPlayerEntity.isSpectator()) {
            playerEntityModel.setVisible(false);
            playerEntityModel.head.visible = true;
            playerEntityModel.helmet.visible = true;
        } else {
            ItemStack itemStack = abstractClientPlayerEntity.getMainHandStack();
            ItemStack itemStack2 = abstractClientPlayerEntity.getOffHandStack();
            playerEntityModel.setVisible(true);
            playerEntityModel.helmet.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.HAT);
            playerEntityModel.jacket.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.JACKET);
            playerEntityModel.leftPantLeg.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
            playerEntityModel.rightPantLeg.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
            playerEntityModel.leftSleeve.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
            playerEntityModel.rightSleeve.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
            playerEntityModel.isSneaking = abstractClientPlayerEntity.isInSneakingPose();
            BipedEntityModel.ArmPose armPose = this.method_4210(abstractClientPlayerEntity, itemStack, itemStack2, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose armPose2 = this.method_4210(abstractClientPlayerEntity, itemStack, itemStack2, Hand.OFF_HAND);
            if (abstractClientPlayerEntity.getMainArm() == Arm.RIGHT) {
                playerEntityModel.rightArmPose = armPose;
                playerEntityModel.leftArmPose = armPose2;
            } else {
                playerEntityModel.rightArmPose = armPose2;
                playerEntityModel.leftArmPose = armPose;
            }
        }
    }

    private BipedEntityModel.ArmPose method_4210(AbstractClientPlayerEntity abstractClientPlayerEntity, ItemStack itemStack, ItemStack itemStack2, Hand hand) {
        ItemStack itemStack3;
        BipedEntityModel.ArmPose armPose = BipedEntityModel.ArmPose.EMPTY;
        ItemStack itemStack4 = itemStack3 = hand == Hand.MAIN_HAND ? itemStack : itemStack2;
        if (!itemStack3.isEmpty()) {
            armPose = BipedEntityModel.ArmPose.ITEM;
            if (abstractClientPlayerEntity.getItemUseTimeLeft() > 0) {
                UseAction useAction = itemStack3.getUseAction();
                if (useAction == UseAction.BLOCK) {
                    armPose = BipedEntityModel.ArmPose.BLOCK;
                } else if (useAction == UseAction.BOW) {
                    armPose = BipedEntityModel.ArmPose.BOW_AND_ARROW;
                } else if (useAction == UseAction.SPEAR) {
                    armPose = BipedEntityModel.ArmPose.THROW_SPEAR;
                } else if (useAction == UseAction.CROSSBOW && hand == abstractClientPlayerEntity.getActiveHand()) {
                    armPose = BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
                }
            } else {
                boolean bl = itemStack.getItem() == Items.CROSSBOW;
                boolean bl2 = CrossbowItem.isCharged(itemStack);
                boolean bl3 = itemStack2.getItem() == Items.CROSSBOW;
                boolean bl4 = CrossbowItem.isCharged(itemStack2);
                if (bl && bl2) {
                    armPose = BipedEntityModel.ArmPose.CROSSBOW_HOLD;
                }
                if (bl3 && bl4 && itemStack.getItem().getUseAction(itemStack) == UseAction.NONE) {
                    armPose = BipedEntityModel.ArmPose.CROSSBOW_HOLD;
                }
            }
        }
        return armPose;
    }

    @Override
    public Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        return abstractClientPlayerEntity.getSkinTexture();
    }

    @Override
    protected void scale(AbstractClientPlayerEntity abstractClientPlayerEntity, float f) {
        float g = 0.9375f;
        GlStateManager.scalef(0.9375f, 0.9375f, 0.9375f);
    }

    @Override
    protected void renderLabel(AbstractClientPlayerEntity abstractClientPlayerEntity, double d, double e, double f, String string, double g) {
        Scoreboard scoreboard;
        ScoreboardObjective scoreboardObjective;
        if (g < 100.0 && (scoreboardObjective = (scoreboard = abstractClientPlayerEntity.getScoreboard()).getObjectiveForSlot(2)) != null) {
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(abstractClientPlayerEntity.getEntityName(), scoreboardObjective);
            this.renderLabel(abstractClientPlayerEntity, scoreboardPlayerScore.getScore() + " " + scoreboardObjective.getDisplayName().asFormattedString(), d, e, f, 64);
            this.getFontRenderer().getClass();
            e += (double)(9.0f * 1.15f * 0.025f);
        }
        super.renderLabel(abstractClientPlayerEntity, d, e, f, string, g);
    }

    public void renderRightArm(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        float f = 1.0f;
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        float g = 0.0625f;
        PlayerEntityModel playerEntityModel = (PlayerEntityModel)this.getModel();
        this.setModelPose(abstractClientPlayerEntity);
        GlStateManager.enableBlend();
        playerEntityModel.handSwingProgress = 0.0f;
        playerEntityModel.isSneaking = false;
        playerEntityModel.field_3396 = 0.0f;
        playerEntityModel.setAngles(abstractClientPlayerEntity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        playerEntityModel.rightArm.pitch = 0.0f;
        playerEntityModel.rightArm.render(0.0625f);
        playerEntityModel.rightSleeve.pitch = 0.0f;
        playerEntityModel.rightSleeve.render(0.0625f);
        GlStateManager.disableBlend();
    }

    public void renderLeftArm(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        float f = 1.0f;
        GlStateManager.color3f(1.0f, 1.0f, 1.0f);
        float g = 0.0625f;
        PlayerEntityModel playerEntityModel = (PlayerEntityModel)this.getModel();
        this.setModelPose(abstractClientPlayerEntity);
        GlStateManager.enableBlend();
        playerEntityModel.isSneaking = false;
        playerEntityModel.handSwingProgress = 0.0f;
        playerEntityModel.field_3396 = 0.0f;
        playerEntityModel.setAngles(abstractClientPlayerEntity, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        playerEntityModel.leftArm.pitch = 0.0f;
        playerEntityModel.leftArm.render(0.0625f);
        playerEntityModel.leftSleeve.pitch = 0.0f;
        playerEntityModel.leftSleeve.render(0.0625f);
        GlStateManager.disableBlend();
    }

    @Override
    protected void setupTransforms(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h) {
        float i = abstractClientPlayerEntity.method_6024(h);
        if (abstractClientPlayerEntity.isFallFlying()) {
            super.setupTransforms(abstractClientPlayerEntity, f, g, h);
            float j = (float)abstractClientPlayerEntity.method_6003() + h;
            float k = MathHelper.clamp(j * j / 100.0f, 0.0f, 1.0f);
            if (!abstractClientPlayerEntity.isUsingRiptide()) {
                GlStateManager.rotatef(k * (-90.0f - abstractClientPlayerEntity.pitch), 1.0f, 0.0f, 0.0f);
            }
            Vec3d vec3d = abstractClientPlayerEntity.getRotationVec(h);
            Vec3d vec3d2 = abstractClientPlayerEntity.getVelocity();
            double d = Entity.squaredHorizontalLength(vec3d2);
            double e = Entity.squaredHorizontalLength(vec3d);
            if (d > 0.0 && e > 0.0) {
                double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / (Math.sqrt(d) * Math.sqrt(e));
                double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                GlStateManager.rotatef((float)(Math.signum(m) * Math.acos(l)) * 180.0f / (float)Math.PI, 0.0f, 1.0f, 0.0f);
            }
        } else if (i > 0.0f) {
            super.setupTransforms(abstractClientPlayerEntity, f, g, h);
            float j = abstractClientPlayerEntity.isTouchingWater() ? -90.0f - abstractClientPlayerEntity.pitch : -90.0f;
            float k = MathHelper.lerp(i, 0.0f, j);
            GlStateManager.rotatef(k, 1.0f, 0.0f, 0.0f);
            if (abstractClientPlayerEntity.isInSwimmingPose()) {
                GlStateManager.translatef(0.0f, -1.0f, 0.3f);
            }
        } else {
            super.setupTransforms(abstractClientPlayerEntity, f, g, h);
        }
    }
}

