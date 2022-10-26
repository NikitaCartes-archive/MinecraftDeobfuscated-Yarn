/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.item;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class HeldItemRenderer {
    private static final RenderLayer MAP_BACKGROUND = RenderLayer.getText(new Identifier("textures/map/map_background.png"));
    private static final RenderLayer MAP_BACKGROUND_CHECKERBOARD = RenderLayer.getText(new Identifier("textures/map/map_background_checkerboard.png"));
    private static final float field_32735 = -0.4f;
    private static final float field_32736 = 0.2f;
    private static final float field_32737 = -0.2f;
    private static final float field_32738 = -0.6f;
    private static final float EQUIP_OFFSET_TRANSLATE_X = 0.56f;
    private static final float EQUIP_OFFSET_TRANSLATE_Y = -0.52f;
    private static final float EQUIP_OFFSET_TRANSLATE_Z = -0.72f;
    private static final float field_32742 = 45.0f;
    private static final float field_32743 = -80.0f;
    private static final float field_32744 = -20.0f;
    private static final float field_32745 = -20.0f;
    private static final float EAT_OR_DRINK_X_ANGLE_MULTIPLIER = 10.0f;
    private static final float EAT_OR_DRINK_Y_ANGLE_MULTIPLIER = 90.0f;
    private static final float EAT_OR_DRINK_Z_ANGLE_MULTIPLIER = 30.0f;
    private static final float field_32749 = 0.6f;
    private static final float field_32750 = -0.5f;
    private static final float field_32751 = 0.0f;
    private static final double field_32752 = 27.0;
    private static final float field_32753 = 0.8f;
    private static final float field_32754 = 0.1f;
    private static final float field_32755 = -0.3f;
    private static final float field_32756 = 0.4f;
    private static final float field_32757 = -0.4f;
    private static final float ARM_HOLDING_ITEM_SECOND_Y_ANGLE_MULTIPLIER = 70.0f;
    private static final float ARM_HOLDING_ITEM_FIRST_Z_ANGLE_MULTIPLIER = -20.0f;
    private static final float field_32690 = -0.6f;
    private static final float field_32691 = 0.8f;
    private static final float field_32692 = 0.8f;
    private static final float field_32693 = -0.75f;
    private static final float field_32694 = -0.9f;
    private static final float field_32695 = 45.0f;
    private static final float field_32696 = -1.0f;
    private static final float field_32697 = 3.6f;
    private static final float field_32698 = 3.5f;
    private static final float ARM_HOLDING_ITEM_TRANSLATE_X = 5.6f;
    private static final int ARM_HOLDING_ITEM_X_ANGLE_MULTIPLIER = 200;
    private static final int ARM_HOLDING_ITEM_THIRD_Y_ANGLE_MULTIPLIER = -135;
    private static final int ARM_HOLDING_ITEM_SECOND_Z_ANGLE_MULTIPLIER = 120;
    private static final float field_32703 = -0.4f;
    private static final float field_32704 = -0.2f;
    private static final float field_32705 = 0.0f;
    private static final float field_32706 = 0.04f;
    private static final float field_32707 = -0.72f;
    private static final float field_32708 = -1.2f;
    private static final float field_32709 = -0.5f;
    private static final float field_32710 = 45.0f;
    private static final float field_32711 = -85.0f;
    private static final float ARM_X_ANGLE_MULTIPLIER = 45.0f;
    private static final float ARM_Y_ANGLE_MULTIPLIER = 92.0f;
    private static final float ARM_Z_ANGLE_MULTIPLIER = -41.0f;
    private static final float ARM_TRANSLATE_X = 0.3f;
    private static final float ARM_TRANSLATE_Y = -1.1f;
    private static final float ARM_TRANSLATE_Z = 0.45f;
    private static final float field_32718 = 20.0f;
    private static final float FIRST_PERSON_MAP_FIRST_SCALE = 0.38f;
    private static final float FIRST_PERSON_MAP_TRANSLATE_X = -0.5f;
    private static final float FIRST_PERSON_MAP_TRANSLATE_Y = -0.5f;
    private static final float FIRST_PERSON_MAP_TRANSLATE_Z = 0.0f;
    private static final float FIRST_PERSON_MAP_SECOND_SCALE = 0.0078125f;
    private static final int field_32724 = 7;
    private static final int field_32725 = 128;
    private static final int field_32726 = 128;
    private static final float field_32727 = 0.0f;
    private static final float field_32728 = 0.0f;
    private static final float field_32729 = 0.04f;
    private static final float field_32730 = 0.0f;
    private static final float field_32731 = 0.004f;
    private static final float field_32732 = 0.0f;
    private static final float field_32733 = 0.2f;
    private static final float field_32734 = 0.1f;
    private final MinecraftClient client;
    private ItemStack mainHand = ItemStack.EMPTY;
    private ItemStack offHand = ItemStack.EMPTY;
    private float equipProgressMainHand;
    private float prevEquipProgressMainHand;
    private float equipProgressOffHand;
    private float prevEquipProgressOffHand;
    private final EntityRenderDispatcher entityRenderDispatcher;
    private final ItemRenderer itemRenderer;

    public HeldItemRenderer(MinecraftClient client, EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
        this.client = client;
        this.entityRenderDispatcher = entityRenderDispatcher;
        this.itemRenderer = itemRenderer;
    }

    public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (stack.isEmpty()) {
            return;
        }
        this.itemRenderer.renderItem(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, entity.world, light, OverlayTexture.DEFAULT_UV, entity.getId() + renderMode.ordinal());
    }

    private float getMapAngle(float tickDelta) {
        float f = 1.0f - tickDelta / 45.0f + 0.1f;
        f = MathHelper.clamp(f, 0.0f, 1.0f);
        f = -MathHelper.cos(f * (float)Math.PI) * 0.5f + 0.5f;
        return f;
    }

    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm) {
        RenderSystem.setShaderTexture(0, this.client.player.getSkinTexture());
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.entityRenderDispatcher.getRenderer(this.client.player);
        matrices.push();
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(92.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(45.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * -41.0f));
        matrices.translate(f * 0.3f, -1.1f, 0.45f);
        if (arm == Arm.RIGHT) {
            playerEntityRenderer.renderRightArm(matrices, vertexConsumers, light, this.client.player);
        } else {
            playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, this.client.player);
        }
        matrices.pop();
    }

    private void renderMapInOneHand(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, Arm arm, float swingProgress, ItemStack stack) {
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        matrices.translate(f * 0.125f, -0.125f, 0.0f);
        if (!this.client.player.isInvisible()) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 10.0f));
            this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
            matrices.pop();
        }
        matrices.push();
        matrices.translate(f * 0.51f, -0.08f + equipProgress * -1.2f, -0.75f);
        float g = MathHelper.sqrt(swingProgress);
        float h = MathHelper.sin(g * (float)Math.PI);
        float i = -0.5f * h;
        float j = 0.4f * MathHelper.sin(g * ((float)Math.PI * 2));
        float k = -0.3f * MathHelper.sin(swingProgress * (float)Math.PI);
        matrices.translate(f * i, j - 0.3f * h, k);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(h * -45.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * h * -30.0f));
        this.renderFirstPersonMap(matrices, vertexConsumers, light, stack);
        matrices.pop();
    }

    private void renderMapInBothHands(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float pitch, float equipProgress, float swingProgress) {
        float f = MathHelper.sqrt(swingProgress);
        float g = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
        float h = -0.4f * MathHelper.sin(f * (float)Math.PI);
        matrices.translate(0.0f, -g / 2.0f, h);
        float i = this.getMapAngle(pitch);
        matrices.translate(0.0f, 0.04f + equipProgress * -1.2f + i * -0.5f, -0.72f);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(i * -85.0f));
        if (!this.client.player.isInvisible()) {
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0f));
            this.renderArm(matrices, vertexConsumers, light, Arm.RIGHT);
            this.renderArm(matrices, vertexConsumers, light, Arm.LEFT);
            matrices.pop();
        }
        float j = MathHelper.sin(f * (float)Math.PI);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(j * 20.0f));
        matrices.scale(2.0f, 2.0f, 2.0f);
        this.renderFirstPersonMap(matrices, vertexConsumers, light, this.mainHand);
    }

    private void renderFirstPersonMap(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int swingProgress, ItemStack stack) {
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));
        matrices.scale(0.38f, 0.38f, 0.38f);
        matrices.translate(-0.5f, -0.5f, 0.0f);
        matrices.scale(0.0078125f, 0.0078125f, 0.0078125f);
        Integer integer = FilledMapItem.getMapId(stack);
        MapState mapState = FilledMapItem.getMapState(integer, this.client.world);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(mapState == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        vertexConsumer.vertex(matrix4f, -7.0f, 135.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(swingProgress).next();
        vertexConsumer.vertex(matrix4f, 135.0f, 135.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(swingProgress).next();
        vertexConsumer.vertex(matrix4f, 135.0f, -7.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(swingProgress).next();
        vertexConsumer.vertex(matrix4f, -7.0f, -7.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(swingProgress).next();
        if (mapState != null) {
            this.client.gameRenderer.getMapRenderer().draw(matrices, vertexConsumers, integer, mapState, false, swingProgress);
        }
    }

    private void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm) {
        boolean bl = arm != Arm.LEFT;
        float f = bl ? 1.0f : -1.0f;
        float g = MathHelper.sqrt(swingProgress);
        float h = -0.3f * MathHelper.sin(g * (float)Math.PI);
        float i = 0.4f * MathHelper.sin(g * ((float)Math.PI * 2));
        float j = -0.4f * MathHelper.sin(swingProgress * (float)Math.PI);
        matrices.translate(f * (h + 0.64000005f), i + -0.6f + equipProgress * -0.6f, j + -0.71999997f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * 45.0f));
        float k = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float l = MathHelper.sin(g * (float)Math.PI);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * l * 70.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * k * -20.0f));
        ClientPlayerEntity abstractClientPlayerEntity = this.client.player;
        RenderSystem.setShaderTexture(0, abstractClientPlayerEntity.getSkinTexture());
        matrices.translate(f * -1.0f, 3.6f, 3.5f);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(f * 120.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(200.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(f * -135.0f));
        matrices.translate(f * 5.6f, 0.0f, 0.0f);
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.entityRenderDispatcher.getRenderer(abstractClientPlayerEntity);
        if (bl) {
            playerEntityRenderer.renderRightArm(matrices, vertexConsumers, light, abstractClientPlayerEntity);
        } else {
            playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, abstractClientPlayerEntity);
        }
    }

    private void applyEatOrDrinkTransformation(MatrixStack matrices, float tickDelta, Arm arm, ItemStack stack) {
        float h;
        float f = (float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f;
        float g = f / (float)stack.getMaxUseTime();
        if (g < 0.8f) {
            h = MathHelper.abs(MathHelper.cos(f / 4.0f * (float)Math.PI) * 0.1f);
            matrices.translate(0.0f, h, 0.0f);
        }
        h = 1.0f - (float)Math.pow(g, 27.0);
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate(h * 0.6f * (float)i, h * -0.5f, h * 0.0f);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * h * 90.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(h * 10.0f));
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)i * h * 30.0f));
    }

    private void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * (45.0f + f * -20.0f)));
        float g = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)i * g * -20.0f));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(g * -80.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * -45.0f));
    }

    private void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate((float)i * 0.56f, -0.52f + equipProgress * -0.6f, -0.72f);
    }

    public void renderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light) {
        float k;
        float j;
        float f = player.getHandSwingProgress(tickDelta);
        Hand hand = MoreObjects.firstNonNull(player.preferredHand, Hand.MAIN_HAND);
        float g = MathHelper.lerp(tickDelta, player.prevPitch, player.getPitch());
        HandRenderType handRenderType = HeldItemRenderer.getHandRenderType(player);
        float h = MathHelper.lerp(tickDelta, player.lastRenderPitch, player.renderPitch);
        float i = MathHelper.lerp(tickDelta, player.lastRenderYaw, player.renderYaw);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees((player.getPitch(tickDelta) - h) * 0.1f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((player.getYaw(tickDelta) - i) * 0.1f));
        if (handRenderType.renderMainHand) {
            j = hand == Hand.MAIN_HAND ? f : 0.0f;
            k = 1.0f - MathHelper.lerp(tickDelta, this.prevEquipProgressMainHand, this.equipProgressMainHand);
            this.renderFirstPersonItem(player, tickDelta, g, Hand.MAIN_HAND, j, this.mainHand, k, matrices, vertexConsumers, light);
        }
        if (handRenderType.renderOffHand) {
            j = hand == Hand.OFF_HAND ? f : 0.0f;
            k = 1.0f - MathHelper.lerp(tickDelta, this.prevEquipProgressOffHand, this.equipProgressOffHand);
            this.renderFirstPersonItem(player, tickDelta, g, Hand.OFF_HAND, j, this.offHand, k, matrices, vertexConsumers, light);
        }
        vertexConsumers.draw();
    }

    @VisibleForTesting
    static HandRenderType getHandRenderType(ClientPlayerEntity player) {
        boolean bl2;
        ItemStack itemStack = player.getMainHandStack();
        ItemStack itemStack2 = player.getOffHandStack();
        boolean bl = itemStack.isOf(Items.BOW) || itemStack2.isOf(Items.BOW);
        boolean bl3 = bl2 = itemStack.isOf(Items.CROSSBOW) || itemStack2.isOf(Items.CROSSBOW);
        if (!bl && !bl2) {
            return HandRenderType.RENDER_BOTH_HANDS;
        }
        if (player.isUsingItem()) {
            return HeldItemRenderer.getUsingItemHandRenderType(player);
        }
        if (HeldItemRenderer.isChargedCrossbow(itemStack)) {
            return HandRenderType.RENDER_MAIN_HAND_ONLY;
        }
        return HandRenderType.RENDER_BOTH_HANDS;
    }

    private static HandRenderType getUsingItemHandRenderType(ClientPlayerEntity player) {
        ItemStack itemStack = player.getActiveItem();
        Hand hand = player.getActiveHand();
        if (itemStack.isOf(Items.BOW) || itemStack.isOf(Items.CROSSBOW)) {
            return HandRenderType.shouldOnlyRender(hand);
        }
        return hand == Hand.MAIN_HAND && HeldItemRenderer.isChargedCrossbow(player.getOffHandStack()) ? HandRenderType.RENDER_MAIN_HAND_ONLY : HandRenderType.RENDER_BOTH_HANDS;
    }

    private static boolean isChargedCrossbow(ItemStack stack) {
        return stack.isOf(Items.CROSSBOW) && CrossbowItem.isCharged(stack);
    }

    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (player.isUsingSpyglass()) {
            return;
        }
        boolean bl = hand == Hand.MAIN_HAND;
        Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
        matrices.push();
        if (item.isEmpty()) {
            if (bl && !player.isInvisible()) {
                this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
            }
        } else if (item.isOf(Items.FILLED_MAP)) {
            if (bl && this.offHand.isEmpty()) {
                this.renderMapInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
            } else {
                this.renderMapInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
            }
        } else if (item.isOf(Items.CROSSBOW)) {
            int i;
            boolean bl2 = CrossbowItem.isCharged(item);
            boolean bl3 = arm == Arm.RIGHT;
            int n = i = bl3 ? 1 : -1;
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                this.applyEquipOffset(matrices, arm, equipProgress);
                matrices.translate((float)i * -0.4785682f, -0.094387f, 0.05731531f);
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-11.935f));
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * 65.3f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)i * -9.785f));
                float f = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                float g = f / (float)CrossbowItem.getPullTime(item);
                if (g > 1.0f) {
                    g = 1.0f;
                }
                if (g > 0.1f) {
                    float h = MathHelper.sin((f - 0.1f) * 1.3f);
                    float j = g - 0.1f;
                    float k = h * j;
                    matrices.translate(k * 0.0f, k * 0.004f, k * 0.0f);
                }
                matrices.translate(g * 0.0f, g * 0.0f, g * 0.04f);
                matrices.scale(1.0f, 1.0f, 1.0f + g * 0.2f);
                matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)i * 45.0f));
            } else {
                float f = -0.4f * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
                float g = 0.2f * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2));
                float h = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
                matrices.translate((float)i * f, g, h);
                this.applyEquipOffset(matrices, arm, equipProgress);
                this.applySwingOffset(matrices, arm, swingProgress);
                if (bl2 && swingProgress < 0.001f && bl) {
                    matrices.translate((float)i * -0.641864f, 0.0f, 0.0f);
                    matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)i * 10.0f));
                }
            }
            this.renderItem(player, item, bl3 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !bl3, matrices, vertexConsumers, light);
        } else {
            boolean bl2;
            boolean bl3 = bl2 = arm == Arm.RIGHT;
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                int l = bl2 ? 1 : -1;
                switch (item.getUseAction()) {
                    case NONE: {
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        break;
                    }
                    case EAT: 
                    case DRINK: {
                        this.applyEatOrDrinkTransformation(matrices, tickDelta, arm, item);
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        break;
                    }
                    case BLOCK: {
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        break;
                    }
                    case BOW: {
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        matrices.translate((float)l * -0.2785682f, 0.18344387f, 0.15731531f);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-13.935f));
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 35.3f));
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -9.785f));
                        float m = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                        float f = m / 20.0f;
                        f = (f * f + f * 2.0f) / 3.0f;
                        if (f > 1.0f) {
                            f = 1.0f;
                        }
                        if (f > 0.1f) {
                            float g = MathHelper.sin((m - 0.1f) * 1.3f);
                            float h = f - 0.1f;
                            float j = g * h;
                            matrices.translate(j * 0.0f, j * 0.004f, j * 0.0f);
                        }
                        matrices.translate(f * 0.0f, f * 0.0f, f * 0.04f);
                        matrices.scale(1.0f, 1.0f, 1.0f + f * 0.2f);
                        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)l * 45.0f));
                        break;
                    }
                    case SPEAR: {
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        matrices.translate((float)l * -0.5f, 0.7f, 0.1f);
                        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-55.0f));
                        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 35.3f));
                        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -9.785f));
                        float m = (float)item.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - tickDelta + 1.0f);
                        float f = m / 10.0f;
                        if (f > 1.0f) {
                            f = 1.0f;
                        }
                        if (f > 0.1f) {
                            float g = MathHelper.sin((m - 0.1f) * 1.3f);
                            float h = f - 0.1f;
                            float j = g * h;
                            matrices.translate(j * 0.0f, j * 0.004f, j * 0.0f);
                        }
                        matrices.translate(0.0f, 0.0f, f * 0.2f);
                        matrices.scale(1.0f, 1.0f, 1.0f + f * 0.2f);
                        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees((float)l * 45.0f));
                        break;
                    }
                }
            } else if (player.isUsingRiptide()) {
                this.applyEquipOffset(matrices, arm, equipProgress);
                int l = bl2 ? 1 : -1;
                matrices.translate((float)l * -0.4f, 0.8f, 0.3f);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((float)l * 65.0f));
                matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)l * -85.0f));
            } else {
                float n = -0.4f * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
                float m = 0.2f * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2));
                float f = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
                int o = bl2 ? 1 : -1;
                matrices.translate((float)o * n, m, f);
                this.applyEquipOffset(matrices, arm, equipProgress);
                this.applySwingOffset(matrices, arm, swingProgress);
            }
            this.renderItem(player, item, bl2 ? ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND, !bl2, matrices, vertexConsumers, light);
        }
        matrices.pop();
    }

    public void updateHeldItems() {
        this.prevEquipProgressMainHand = this.equipProgressMainHand;
        this.prevEquipProgressOffHand = this.equipProgressOffHand;
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        ItemStack itemStack = clientPlayerEntity.getMainHandStack();
        ItemStack itemStack2 = clientPlayerEntity.getOffHandStack();
        if (ItemStack.areEqual(this.mainHand, itemStack)) {
            this.mainHand = itemStack;
        }
        if (ItemStack.areEqual(this.offHand, itemStack2)) {
            this.offHand = itemStack2;
        }
        if (clientPlayerEntity.isRiding()) {
            this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4f, 0.0f, 1.0f);
            this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4f, 0.0f, 1.0f);
        } else {
            float f = clientPlayerEntity.getAttackCooldownProgress(1.0f);
            this.equipProgressMainHand += MathHelper.clamp((this.mainHand == itemStack ? f * f * f : 0.0f) - this.equipProgressMainHand, -0.4f, 0.4f);
            this.equipProgressOffHand += MathHelper.clamp((float)(this.offHand == itemStack2 ? 1 : 0) - this.equipProgressOffHand, -0.4f, 0.4f);
        }
        if (this.equipProgressMainHand < 0.1f) {
            this.mainHand = itemStack;
        }
        if (this.equipProgressOffHand < 0.1f) {
            this.offHand = itemStack2;
        }
    }

    public void resetEquipProgress(Hand hand) {
        if (hand == Hand.MAIN_HAND) {
            this.equipProgressMainHand = 0.0f;
        } else {
            this.equipProgressOffHand = 0.0f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    @VisibleForTesting
    static enum HandRenderType {
        RENDER_BOTH_HANDS(true, true),
        RENDER_MAIN_HAND_ONLY(true, false),
        RENDER_OFF_HAND_ONLY(false, true);

        final boolean renderMainHand;
        final boolean renderOffHand;

        private HandRenderType(boolean renderMainHand, boolean renderOffHand) {
            this.renderMainHand = renderMainHand;
            this.renderOffHand = renderOffHand;
        }

        public static HandRenderType shouldOnlyRender(Hand hand) {
            return hand == Hand.MAIN_HAND ? RENDER_MAIN_HAND_ONLY : RENDER_OFF_HAND_ONLY;
        }
    }
}

