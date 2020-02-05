/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.item;

import com.google.common.base.MoreObjects;
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
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
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

@Environment(value=EnvType.CLIENT)
public class HeldItemRenderer {
    private static final RenderLayer MAP_BACKGROUND = RenderLayer.getText(new Identifier("textures/map/map_background.png"));
    private static final RenderLayer MAP_BACKGROUND_CHECKERBOARD = RenderLayer.getText(new Identifier("textures/map/map_background_checkerboard.png"));
    private final MinecraftClient client;
    private ItemStack mainHand = ItemStack.EMPTY;
    private ItemStack offHand = ItemStack.EMPTY;
    private float equipProgressMainHand;
    private float prevEquipProgressMainHand;
    private float equipProgressOffHand;
    private float prevEquipProgressOffHand;
    private final EntityRenderDispatcher renderManager;
    private final ItemRenderer itemRenderer;

    public HeldItemRenderer(MinecraftClient client) {
        this.client = client;
        this.renderManager = client.getEntityRenderManager();
        this.itemRenderer = client.getItemRenderer();
    }

    public void renderItem(LivingEntity entity, ItemStack stack, ModelTransformation.Mode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        if (stack.isEmpty()) {
            return;
        }
        this.itemRenderer.renderItem(entity, stack, renderMode, leftHanded, matrices, vertexConsumers, entity.world, light, OverlayTexture.DEFAULT_UV);
    }

    private float getMapAngle(float tickDelta) {
        float f = 1.0f - tickDelta / 45.0f + 0.1f;
        f = MathHelper.clamp(f, 0.0f, 1.0f);
        f = -MathHelper.cos(f * (float)Math.PI) * 0.5f + 0.5f;
        return f;
    }

    private void renderArm(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm) {
        this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.getRenderer(this.client.player);
        matrices.push();
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(92.0f));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(45.0f));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f * -41.0f));
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
        matrices.translate(f * 0.125f, -0.125, 0.0);
        if (!this.client.player.isInvisible()) {
            matrices.push();
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f * 10.0f));
            this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
            matrices.pop();
        }
        matrices.push();
        matrices.translate(f * 0.51f, -0.08f + equipProgress * -1.2f, -0.75);
        float g = MathHelper.sqrt(swingProgress);
        float h = MathHelper.sin(g * (float)Math.PI);
        float i = -0.5f * h;
        float j = 0.4f * MathHelper.sin(g * ((float)Math.PI * 2));
        float k = -0.3f * MathHelper.sin(swingProgress * (float)Math.PI);
        matrices.translate(f * i, j - 0.3f * h, k);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(h * -45.0f));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(f * h * -30.0f));
        this.renderFirstPersonMap(matrices, vertexConsumers, light, stack);
        matrices.pop();
    }

    private void renderMapInBothHands(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float pitch, float equipProgress, float swingProgress) {
        float f = MathHelper.sqrt(swingProgress);
        float g = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
        float h = -0.4f * MathHelper.sin(f * (float)Math.PI);
        matrices.translate(0.0, -g / 2.0f, h);
        float i = this.getMapAngle(pitch);
        matrices.translate(0.0, 0.04f + equipProgress * -1.2f + i * -0.5f, -0.72f);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(i * -85.0f));
        if (!this.client.player.isInvisible()) {
            matrices.push();
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
            this.renderArm(matrices, vertexConsumers, light, Arm.RIGHT);
            this.renderArm(matrices, vertexConsumers, light, Arm.LEFT);
            matrices.pop();
        }
        float j = MathHelper.sin(f * (float)Math.PI);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(j * 20.0f));
        matrices.scale(2.0f, 2.0f, 2.0f);
        this.renderFirstPersonMap(matrices, vertexConsumers, light, this.mainHand);
    }

    private void renderFirstPersonMap(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int swingProgress, ItemStack stack) {
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        matrices.scale(0.38f, 0.38f, 0.38f);
        matrices.translate(-0.5, -0.5, 0.0);
        matrices.scale(0.0078125f, 0.0078125f, 0.0078125f);
        MapState mapState = FilledMapItem.getOrCreateMapState(stack, this.client.world);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(mapState == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
        Matrix4f matrix4f = matrices.peek().getModel();
        vertexConsumer.vertex(matrix4f, -7.0f, 135.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(swingProgress).next();
        vertexConsumer.vertex(matrix4f, 135.0f, 135.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(swingProgress).next();
        vertexConsumer.vertex(matrix4f, 135.0f, -7.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(swingProgress).next();
        vertexConsumer.vertex(matrix4f, -7.0f, -7.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(swingProgress).next();
        if (mapState != null) {
            this.client.gameRenderer.getMapRenderer().draw(matrices, vertexConsumers, mapState, false, swingProgress);
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
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(f * 45.0f));
        float k = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        float l = MathHelper.sin(g * (float)Math.PI);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(f * l * 70.0f));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f * k * -20.0f));
        ClientPlayerEntity abstractClientPlayerEntity = this.client.player;
        this.client.getTextureManager().bindTexture(abstractClientPlayerEntity.getSkinTexture());
        matrices.translate(f * -1.0f, 3.6f, 3.5);
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f * 120.0f));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(200.0f));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(f * -135.0f));
        matrices.translate(f * 5.6f, 0.0, 0.0);
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.getRenderer(abstractClientPlayerEntity);
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
            matrices.translate(0.0, h, 0.0);
        }
        h = 1.0f - (float)Math.pow(g, 27.0);
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate(h * 0.6f * (float)i, h * -0.5f, h * 0.0f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * h * 90.0f));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(h * 10.0f));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)i * h * 30.0f));
    }

    private void applySwingOffset(MatrixStack matrices, Arm arm, float swingProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * (45.0f + f * -20.0f)));
        float g = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)i * g * -20.0f));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(g * -80.0f));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * -45.0f));
    }

    private void applyEquipOffset(MatrixStack matrices, Arm arm, float equipProgress) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate((float)i * 0.56f, -0.52f + equipProgress * -0.6f, -0.72f);
    }

    public void renderItem(float tickDelta, MatrixStack matrices, VertexConsumerProvider.Immediate vertexConsumers, ClientPlayerEntity player, int light) {
        float k;
        ItemStack itemStack;
        float f = player.getHandSwingProgress(tickDelta);
        Hand hand = MoreObjects.firstNonNull(player.preferredHand, Hand.MAIN_HAND);
        float g = MathHelper.lerp(tickDelta, player.prevPitch, player.pitch);
        boolean bl = true;
        boolean bl2 = true;
        if (player.isUsingItem()) {
            ItemStack itemStack2;
            Hand hand2;
            itemStack = player.getActiveItem();
            if (itemStack.getItem() == Items.BOW || itemStack.getItem() == Items.CROSSBOW) {
                bl = player.getActiveHand() == Hand.MAIN_HAND;
                boolean bl3 = bl2 = !bl;
            }
            if ((hand2 = player.getActiveHand()) == Hand.MAIN_HAND && (itemStack2 = player.getOffHandStack()).getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack2)) {
                bl2 = false;
            }
        } else {
            itemStack = player.getMainHandStack();
            ItemStack itemStack3 = player.getOffHandStack();
            if (itemStack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack)) {
                boolean bl4 = bl2 = !bl;
            }
            if (itemStack3.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack3)) {
                bl = !itemStack.isEmpty();
                bl2 = !bl;
            }
        }
        float h = MathHelper.lerp(tickDelta, player.lastRenderPitch, player.renderPitch);
        float i = MathHelper.lerp(tickDelta, player.lastRenderYaw, player.renderYaw);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((player.getPitch(tickDelta) - h) * 0.1f));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((player.getYaw(tickDelta) - i) * 0.1f));
        if (bl) {
            float j = hand == Hand.MAIN_HAND ? f : 0.0f;
            k = 1.0f - MathHelper.lerp(tickDelta, this.prevEquipProgressMainHand, this.equipProgressMainHand);
            this.renderFirstPersonItem(player, tickDelta, g, Hand.MAIN_HAND, j, this.mainHand, k, matrices, vertexConsumers, light);
        }
        if (bl2) {
            float j = hand == Hand.OFF_HAND ? f : 0.0f;
            k = 1.0f - MathHelper.lerp(tickDelta, this.prevEquipProgressOffHand, this.equipProgressOffHand);
            this.renderFirstPersonItem(player, tickDelta, g, Hand.OFF_HAND, j, this.offHand, k, matrices, vertexConsumers, light);
        }
        vertexConsumers.draw();
    }

    private void renderFirstPersonItem(AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand, float swingProgress, ItemStack item, float equipProgress, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        boolean bl = hand == Hand.MAIN_HAND;
        Arm arm = bl ? player.getMainArm() : player.getMainArm().getOpposite();
        matrices.push();
        if (item.isEmpty()) {
            if (bl && !player.isInvisible()) {
                this.renderArmHoldingItem(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
            }
        } else if (item.getItem() == Items.FILLED_MAP) {
            if (bl && this.offHand.isEmpty()) {
                this.renderMapInBothHands(matrices, vertexConsumers, light, pitch, equipProgress, swingProgress);
            } else {
                this.renderMapInOneHand(matrices, vertexConsumers, light, equipProgress, arm, swingProgress, item);
            }
        } else if (item.getItem() == Items.CROSSBOW) {
            int i;
            boolean bl2 = CrossbowItem.isCharged(item);
            boolean bl3 = arm == Arm.RIGHT;
            int n = i = bl3 ? 1 : -1;
            if (player.isUsingItem() && player.getItemUseTimeLeft() > 0 && player.getActiveHand() == hand) {
                this.applyEquipOffset(matrices, arm, equipProgress);
                matrices.translate((float)i * -0.4785682f, -0.094387f, 0.05731530860066414);
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-11.935f));
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * 65.3f));
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)i * -9.785f));
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
                matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)i * 45.0f));
            } else {
                float f = -0.4f * MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
                float g = 0.2f * MathHelper.sin(MathHelper.sqrt(swingProgress) * ((float)Math.PI * 2));
                float h = -0.2f * MathHelper.sin(swingProgress * (float)Math.PI);
                matrices.translate((float)i * f, g, h);
                this.applyEquipOffset(matrices, arm, equipProgress);
                this.applySwingOffset(matrices, arm, swingProgress);
                if (bl2 && swingProgress < 0.001f) {
                    matrices.translate((float)i * -0.641864f, 0.0, 0.0);
                    matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * 10.0f));
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
                        matrices.translate((float)l * -0.2785682f, 0.18344387412071228, 0.15731531381607056);
                        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-13.935f));
                        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)l * 35.3f));
                        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)l * -9.785f));
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
                        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)l * 45.0f));
                        break;
                    }
                    case SPEAR: {
                        this.applyEquipOffset(matrices, arm, equipProgress);
                        matrices.translate((float)l * -0.5f, 0.7f, 0.1f);
                        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-55.0f));
                        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)l * 35.3f));
                        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)l * -9.785f));
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
                        matrices.translate(0.0, 0.0, f * 0.2f);
                        matrices.scale(1.0f, 1.0f, 1.0f + f * 0.2f);
                        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)l * 45.0f));
                        break;
                    }
                }
            } else if (player.isUsingRiptide()) {
                this.applyEquipOffset(matrices, arm, equipProgress);
                int l = bl2 ? 1 : -1;
                matrices.translate((float)l * -0.4f, 0.8f, 0.3f);
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)l * 65.0f));
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)l * -85.0f));
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
        if (clientPlayerEntity.isRiding()) {
            this.equipProgressMainHand = MathHelper.clamp(this.equipProgressMainHand - 0.4f, 0.0f, 1.0f);
            this.equipProgressOffHand = MathHelper.clamp(this.equipProgressOffHand - 0.4f, 0.0f, 1.0f);
        } else {
            float f = clientPlayerEntity.getAttackCooldownProgress(1.0f);
            this.equipProgressMainHand += MathHelper.clamp((ItemStack.areEqualIgnoreDamage(this.mainHand, itemStack) ? f * f * f : 0.0f) - this.equipProgressMainHand, -0.4f, 0.4f);
            this.equipProgressOffHand += MathHelper.clamp((float)(ItemStack.areEqualIgnoreDamage(this.offHand, itemStack2) ? 1 : 0) - this.equipProgressOffHand, -0.4f, 0.4f);
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
}

