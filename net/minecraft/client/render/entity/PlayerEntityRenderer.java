/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5617;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.Deadmau5FeatureRenderer;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeadFeatureRenderer;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.feature.ShoulderParrotFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckStingersFeatureRenderer;
import net.minecraft.client.render.entity.feature.TridentRiptideFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

@Environment(value=EnvType.CLIENT)
public class PlayerEntityRenderer
extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
    public PlayerEntityRenderer(class_5617.class_5618 arg, boolean bl) {
        super(arg, new PlayerEntityModel(arg.method_32167(bl ? EntityModelLayers.PLAYER_SLIM : EntityModelLayers.PLAYER), bl), 0.5f);
        this.addFeature(new ArmorFeatureRenderer(this, new BipedEntityModel(arg.method_32167(bl ? EntityModelLayers.PLAYER_SLIM_INNER_ARMOR : EntityModelLayers.PLAYER_INNER_ARMOR)), new BipedEntityModel(arg.method_32167(bl ? EntityModelLayers.PLAYER_SLIM_OUTER_ARMOR : EntityModelLayers.PLAYER_OUTER_ARMOR))));
        this.addFeature(new HeldItemFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
        this.addFeature(new StuckArrowsFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(arg, this));
        this.addFeature(new Deadmau5FeatureRenderer(this));
        this.addFeature(new CapeFeatureRenderer(this));
        this.addFeature(new HeadFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this, arg.method_32170()));
        this.addFeature(new ElytraFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this, arg.method_32170()));
        this.addFeature(new ShoulderParrotFeatureRenderer<AbstractClientPlayerEntity>(this, arg.method_32170()));
        this.addFeature(new TridentRiptideFeatureRenderer<AbstractClientPlayerEntity>(this, arg.method_32170()));
        this.addFeature(new StuckStingersFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>>(this));
    }

    @Override
    public void render(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        this.setModelPose(abstractClientPlayerEntity);
        super.render(abstractClientPlayerEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public Vec3d getPositionOffset(AbstractClientPlayerEntity abstractClientPlayerEntity, float f) {
        if (abstractClientPlayerEntity.isInSneakingPose()) {
            return new Vec3d(0.0, -0.125, 0.0);
        }
        return super.getPositionOffset(abstractClientPlayerEntity, f);
    }

    private void setModelPose(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        PlayerEntityModel playerEntityModel = (PlayerEntityModel)this.getModel();
        if (abstractClientPlayerEntity.isSpectator()) {
            playerEntityModel.setVisible(false);
            playerEntityModel.head.visible = true;
            playerEntityModel.helmet.visible = true;
        } else {
            playerEntityModel.setVisible(true);
            playerEntityModel.helmet.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.HAT);
            playerEntityModel.jacket.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.JACKET);
            playerEntityModel.leftPantLeg.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_PANTS_LEG);
            playerEntityModel.rightPantLeg.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.RIGHT_PANTS_LEG);
            playerEntityModel.leftSleeve.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.LEFT_SLEEVE);
            playerEntityModel.rightSleeve.visible = abstractClientPlayerEntity.isPartVisible(PlayerModelPart.RIGHT_SLEEVE);
            playerEntityModel.sneaking = abstractClientPlayerEntity.isInSneakingPose();
            BipedEntityModel.ArmPose armPose = PlayerEntityRenderer.getArmPose(abstractClientPlayerEntity, Hand.MAIN_HAND);
            BipedEntityModel.ArmPose armPose2 = PlayerEntityRenderer.getArmPose(abstractClientPlayerEntity, Hand.OFF_HAND);
            if (armPose.method_30156()) {
                BipedEntityModel.ArmPose armPose3 = armPose2 = abstractClientPlayerEntity.getOffHandStack().isEmpty() ? BipedEntityModel.ArmPose.EMPTY : BipedEntityModel.ArmPose.ITEM;
            }
            if (abstractClientPlayerEntity.getMainArm() == Arm.RIGHT) {
                playerEntityModel.rightArmPose = armPose;
                playerEntityModel.leftArmPose = armPose2;
            } else {
                playerEntityModel.rightArmPose = armPose2;
                playerEntityModel.leftArmPose = armPose;
            }
        }
    }

    private static BipedEntityModel.ArmPose getArmPose(AbstractClientPlayerEntity abstractClientPlayerEntity, Hand hand) {
        ItemStack itemStack = abstractClientPlayerEntity.getStackInHand(hand);
        if (itemStack.isEmpty()) {
            return BipedEntityModel.ArmPose.EMPTY;
        }
        if (abstractClientPlayerEntity.getActiveHand() == hand && abstractClientPlayerEntity.getItemUseTimeLeft() > 0) {
            UseAction useAction = itemStack.getUseAction();
            if (useAction == UseAction.BLOCK) {
                return BipedEntityModel.ArmPose.BLOCK;
            }
            if (useAction == UseAction.BOW) {
                return BipedEntityModel.ArmPose.BOW_AND_ARROW;
            }
            if (useAction == UseAction.SPEAR) {
                return BipedEntityModel.ArmPose.THROW_SPEAR;
            }
            if (useAction == UseAction.CROSSBOW && hand == abstractClientPlayerEntity.getActiveHand()) {
                return BipedEntityModel.ArmPose.CROSSBOW_CHARGE;
            }
            if (useAction == UseAction.SPYGLASS) {
                return BipedEntityModel.ArmPose.SPYGLASS;
            }
        } else if (!abstractClientPlayerEntity.handSwinging && itemStack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
            return BipedEntityModel.ArmPose.CROSSBOW_HOLD;
        }
        return BipedEntityModel.ArmPose.ITEM;
    }

    @Override
    public Identifier getTexture(AbstractClientPlayerEntity abstractClientPlayerEntity) {
        return abstractClientPlayerEntity.getSkinTexture();
    }

    @Override
    protected void scale(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f) {
        float g = 0.9375f;
        matrixStack.scale(0.9375f, 0.9375f, 0.9375f);
    }

    @Override
    protected void renderLabelIfPresent(AbstractClientPlayerEntity abstractClientPlayerEntity, Text text, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        Scoreboard scoreboard;
        ScoreboardObjective scoreboardObjective;
        double d = this.dispatcher.getSquaredDistanceToCamera(abstractClientPlayerEntity);
        matrixStack.push();
        if (d < 100.0 && (scoreboardObjective = (scoreboard = abstractClientPlayerEntity.getScoreboard()).getObjectiveForSlot(2)) != null) {
            ScoreboardPlayerScore scoreboardPlayerScore = scoreboard.getPlayerScore(abstractClientPlayerEntity.getEntityName(), scoreboardObjective);
            super.renderLabelIfPresent(abstractClientPlayerEntity, new LiteralText(Integer.toString(scoreboardPlayerScore.getScore())).append(" ").append(scoreboardObjective.getDisplayName()), matrixStack, vertexConsumerProvider, i);
            this.getFontRenderer().getClass();
            matrixStack.translate(0.0, 9.0f * 1.15f * 0.025f, 0.0);
        }
        super.renderLabelIfPresent(abstractClientPlayerEntity, text, matrixStack, vertexConsumerProvider, i);
        matrixStack.pop();
    }

    public void renderRightArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player) {
        this.renderArm(matrices, vertexConsumers, light, player, ((PlayerEntityModel)this.model).rightArm, ((PlayerEntityModel)this.model).rightSleeve);
    }

    public void renderLeftArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player) {
        this.renderArm(matrices, vertexConsumers, light, player, ((PlayerEntityModel)this.model).field_27433, ((PlayerEntityModel)this.model).leftSleeve);
    }

    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve) {
        PlayerEntityModel playerEntityModel = (PlayerEntityModel)this.getModel();
        this.setModelPose(player);
        playerEntityModel.handSwingProgress = 0.0f;
        playerEntityModel.sneaking = false;
        playerEntityModel.leaningPitch = 0.0f;
        playerEntityModel.setAngles(player, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        arm.pitch = 0.0f;
        arm.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntitySolid(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
        sleeve.pitch = 0.0f;
        sleeve.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(player.getSkinTexture())), light, OverlayTexture.DEFAULT_UV);
    }

    @Override
    protected void setupTransforms(AbstractClientPlayerEntity abstractClientPlayerEntity, MatrixStack matrixStack, float f, float g, float h) {
        float i = abstractClientPlayerEntity.getLeaningPitch(h);
        if (abstractClientPlayerEntity.isFallFlying()) {
            super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            float j = (float)abstractClientPlayerEntity.getRoll() + h;
            float k = MathHelper.clamp(j * j / 100.0f, 0.0f, 1.0f);
            if (!abstractClientPlayerEntity.isUsingRiptide()) {
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(k * (-90.0f - abstractClientPlayerEntity.pitch)));
            }
            Vec3d vec3d = abstractClientPlayerEntity.getRotationVec(h);
            Vec3d vec3d2 = abstractClientPlayerEntity.getVelocity();
            double d = Entity.squaredHorizontalLength(vec3d2);
            double e = Entity.squaredHorizontalLength(vec3d);
            if (d > 0.0 && e > 0.0) {
                double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
                double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion((float)(Math.signum(m) * Math.acos(l))));
            }
        } else if (i > 0.0f) {
            super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
            float j = abstractClientPlayerEntity.isTouchingWater() ? -90.0f - abstractClientPlayerEntity.pitch : -90.0f;
            float k = MathHelper.lerp(i, 0.0f, j);
            matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(k));
            if (abstractClientPlayerEntity.isInSwimmingPose()) {
                matrixStack.translate(0.0, -1.0, 0.3f);
            }
        } else {
            super.setupTransforms(abstractClientPlayerEntity, matrixStack, f, g, h);
        }
    }
}

