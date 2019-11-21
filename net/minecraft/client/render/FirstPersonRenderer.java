/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
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
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FirstPersonRenderer {
    private final MinecraftClient client;
    private ItemStack mainHand = ItemStack.EMPTY;
    private ItemStack offHand = ItemStack.EMPTY;
    private float equipProgressMainHand;
    private float prevEquipProgressMainHand;
    private float equipProgressOffHand;
    private float prevEquipProgressOffHand;
    private final EntityRenderDispatcher renderManager;
    private final ItemRenderer itemRenderer;

    public FirstPersonRenderer(MinecraftClient minecraftClient) {
        this.client = minecraftClient;
        this.renderManager = minecraftClient.getEntityRenderManager();
        this.itemRenderer = minecraftClient.getItemRenderer();
    }

    public void renderItem(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Type type, boolean bl, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        if (itemStack.isEmpty()) {
            return;
        }
        this.itemRenderer.method_23177(livingEntity, itemStack, type, bl, matrixStack, vertexConsumerProvider, livingEntity.world, i, OverlayTexture.DEFAULT_UV);
    }

    private float getMapAngle(float f) {
        float g = 1.0f - f / 45.0f + 0.1f;
        g = MathHelper.clamp(g, 0.0f, 1.0f);
        g = -MathHelper.cos(g * (float)Math.PI) * 0.5f + 0.5f;
        return g;
    }

    private void renderArm(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, Arm arm) {
        this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.getRenderer(this.client.player);
        matrixStack.push();
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(92.0f));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(45.0f));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(f * -41.0f));
        matrixStack.translate(f * 0.3f, -1.1f, 0.45f);
        if (arm == Arm.RIGHT) {
            playerEntityRenderer.renderRightArm(matrixStack, vertexConsumerProvider, i, this.client.player);
        } else {
            playerEntityRenderer.renderLeftArm(matrixStack, vertexConsumerProvider, i, this.client.player);
        }
        matrixStack.pop();
    }

    private void renderMapInOneHand(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, Arm arm, float g, ItemStack itemStack) {
        float h = arm == Arm.RIGHT ? 1.0f : -1.0f;
        matrixStack.translate(h * 0.125f, -0.125, 0.0);
        if (!this.client.player.isInvisible()) {
            matrixStack.push();
            matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(h * 10.0f));
            this.renderArmHoldingItem(matrixStack, vertexConsumerProvider, i, f, g, arm);
            matrixStack.pop();
        }
        matrixStack.push();
        matrixStack.translate(h * 0.51f, -0.08f + f * -1.2f, -0.75);
        float j = MathHelper.sqrt(g);
        float k = MathHelper.sin(j * (float)Math.PI);
        float l = -0.5f * k;
        float m = 0.4f * MathHelper.sin(j * ((float)Math.PI * 2));
        float n = -0.3f * MathHelper.sin(g * (float)Math.PI);
        matrixStack.translate(h * l, m - 0.3f * k, n);
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(k * -45.0f));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * k * -30.0f));
        this.renderFirstPersonMap(matrixStack, vertexConsumerProvider, i, itemStack);
        matrixStack.pop();
    }

    private void renderMapInBothHands(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, float g, float h) {
        float j = MathHelper.sqrt(h);
        float k = -0.2f * MathHelper.sin(h * (float)Math.PI);
        float l = -0.4f * MathHelper.sin(j * (float)Math.PI);
        matrixStack.translate(0.0, -k / 2.0f, l);
        float m = this.getMapAngle(f);
        matrixStack.translate(0.0, 0.04f + g * -1.2f + m * -0.5f, -0.72f);
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(m * -85.0f));
        if (!this.client.player.isInvisible()) {
            matrixStack.push();
            matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(90.0f));
            this.renderArm(matrixStack, vertexConsumerProvider, i, Arm.RIGHT);
            this.renderArm(matrixStack, vertexConsumerProvider, i, Arm.LEFT);
            matrixStack.pop();
        }
        float n = MathHelper.sin(j * (float)Math.PI);
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(n * 20.0f));
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        this.renderFirstPersonMap(matrixStack, vertexConsumerProvider, i, this.mainHand);
    }

    private void renderFirstPersonMap(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ItemStack itemStack) {
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(180.0f));
        matrixStack.scale(0.38f, 0.38f, 0.38f);
        matrixStack.translate(-0.5, -0.5, 0.0);
        matrixStack.scale(0.0078125f, 0.0078125f, 0.0078125f);
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(MapRenderer.field_21687);
        Matrix4f matrix4f = matrixStack.peek().getModel();
        vertexConsumer.vertex(matrix4f, -7.0f, 135.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(i).next();
        vertexConsumer.vertex(matrix4f, 135.0f, 135.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(i).next();
        vertexConsumer.vertex(matrix4f, 135.0f, -7.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(i).next();
        vertexConsumer.vertex(matrix4f, -7.0f, -7.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(i).next();
        MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, this.client.world);
        if (mapState != null) {
            this.client.gameRenderer.getMapRenderer().draw(matrixStack, vertexConsumerProvider, mapState, false, i);
        }
    }

    private void renderArmHoldingItem(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, float f, float g, Arm arm) {
        boolean bl = arm != Arm.LEFT;
        float h = bl ? 1.0f : -1.0f;
        float j = MathHelper.sqrt(g);
        float k = -0.3f * MathHelper.sin(j * (float)Math.PI);
        float l = 0.4f * MathHelper.sin(j * ((float)Math.PI * 2));
        float m = -0.4f * MathHelper.sin(g * (float)Math.PI);
        matrixStack.translate(h * (k + 0.64000005f), l + -0.6f + f * -0.6f, m + -0.71999997f);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * 45.0f));
        float n = MathHelper.sin(g * g * (float)Math.PI);
        float o = MathHelper.sin(j * (float)Math.PI);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * o * 70.0f));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(h * n * -20.0f));
        ClientPlayerEntity abstractClientPlayerEntity = this.client.player;
        this.client.getTextureManager().bindTexture(abstractClientPlayerEntity.getSkinTexture());
        matrixStack.translate(h * -1.0f, 3.6f, 3.5);
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(h * 120.0f));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(200.0f));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(h * -135.0f));
        matrixStack.translate(h * 5.6f, 0.0, 0.0);
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.getRenderer(abstractClientPlayerEntity);
        if (bl) {
            playerEntityRenderer.renderRightArm(matrixStack, vertexConsumerProvider, i, abstractClientPlayerEntity);
        } else {
            playerEntityRenderer.renderLeftArm(matrixStack, vertexConsumerProvider, i, abstractClientPlayerEntity);
        }
    }

    private void applyEatOrDrinkTransformation(MatrixStack matrixStack, float f, Arm arm, ItemStack itemStack) {
        float i;
        float g = (float)this.client.player.getItemUseTimeLeft() - f + 1.0f;
        float h = g / (float)itemStack.getMaxUseTime();
        if (h < 0.8f) {
            i = MathHelper.abs(MathHelper.cos(g / 4.0f * (float)Math.PI) * 0.1f);
            matrixStack.translate(0.0, i, 0.0);
        }
        i = 1.0f - (float)Math.pow(h, 27.0);
        int j = arm == Arm.RIGHT ? 1 : -1;
        matrixStack.translate(i * 0.6f * (float)j, i * -0.5f, i * 0.0f);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)j * i * 90.0f));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(i * 10.0f));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)j * i * 30.0f));
    }

    private void method_3217(MatrixStack matrixStack, Arm arm, float f) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        float g = MathHelper.sin(f * f * (float)Math.PI);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * (45.0f + g * -20.0f)));
        float h = MathHelper.sin(MathHelper.sqrt(f) * (float)Math.PI);
        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)i * h * -20.0f));
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(h * -80.0f));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)i * -45.0f));
    }

    private void applyHandOffset(MatrixStack matrixStack, Arm arm, float f) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrixStack.translate((float)i * 0.56f, -0.52f + f * -0.6f, -0.72f);
    }

    public void method_22976(float f, MatrixStack matrixStack, VertexConsumerProvider.Immediate immediate, ClientPlayerEntity clientPlayerEntity, int i) {
        float m;
        ItemStack itemStack;
        float g = clientPlayerEntity.getHandSwingProgress(f);
        Hand hand = MoreObjects.firstNonNull(clientPlayerEntity.preferredHand, Hand.MAIN_HAND);
        float h = MathHelper.lerp(f, clientPlayerEntity.prevPitch, clientPlayerEntity.pitch);
        boolean bl = true;
        boolean bl2 = true;
        if (clientPlayerEntity.isUsingItem()) {
            ItemStack itemStack2;
            Hand hand2;
            itemStack = clientPlayerEntity.getActiveItem();
            if (itemStack.getItem() == Items.BOW || itemStack.getItem() == Items.CROSSBOW) {
                bl = clientPlayerEntity.getActiveHand() == Hand.MAIN_HAND;
                boolean bl3 = bl2 = !bl;
            }
            if ((hand2 = clientPlayerEntity.getActiveHand()) == Hand.MAIN_HAND && (itemStack2 = clientPlayerEntity.getOffHandStack()).getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack2)) {
                bl2 = false;
            }
        } else {
            itemStack = clientPlayerEntity.getMainHandStack();
            ItemStack itemStack3 = clientPlayerEntity.getOffHandStack();
            if (itemStack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack)) {
                boolean bl4 = bl2 = !bl;
            }
            if (itemStack3.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack3)) {
                bl = !itemStack.isEmpty();
                bl2 = !bl;
            }
        }
        float j = MathHelper.lerp(f, clientPlayerEntity.lastRenderPitch, clientPlayerEntity.renderPitch);
        float k = MathHelper.lerp(f, clientPlayerEntity.lastRenderYaw, clientPlayerEntity.renderYaw);
        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((clientPlayerEntity.getPitch(f) - j) * 0.1f));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((clientPlayerEntity.getYaw(f) - k) * 0.1f));
        if (bl) {
            float l = hand == Hand.MAIN_HAND ? g : 0.0f;
            m = 1.0f - MathHelper.lerp(f, this.prevEquipProgressMainHand, this.equipProgressMainHand);
            this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.MAIN_HAND, l, this.mainHand, m, matrixStack, immediate, i);
        }
        if (bl2) {
            float l = hand == Hand.OFF_HAND ? g : 0.0f;
            m = 1.0f - MathHelper.lerp(f, this.prevEquipProgressOffHand, this.equipProgressOffHand);
            this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.OFF_HAND, l, this.offHand, m, matrixStack, immediate, i);
        }
        immediate.draw();
    }

    private void renderFirstPersonItem(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, Hand hand, float h, ItemStack itemStack, float i, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int j) {
        boolean bl = hand == Hand.MAIN_HAND;
        Arm arm = bl ? abstractClientPlayerEntity.getMainArm() : abstractClientPlayerEntity.getMainArm().getOpposite();
        matrixStack.push();
        if (itemStack.isEmpty()) {
            if (bl && !abstractClientPlayerEntity.isInvisible()) {
                this.renderArmHoldingItem(matrixStack, vertexConsumerProvider, j, i, h, arm);
            }
        } else if (itemStack.getItem() == Items.FILLED_MAP) {
            if (bl && this.offHand.isEmpty()) {
                this.renderMapInBothHands(matrixStack, vertexConsumerProvider, j, g, i, h);
            } else {
                this.renderMapInOneHand(matrixStack, vertexConsumerProvider, j, i, arm, h, itemStack);
            }
        } else if (itemStack.getItem() == Items.CROSSBOW) {
            int k;
            boolean bl2 = CrossbowItem.isCharged(itemStack);
            boolean bl3 = arm == Arm.RIGHT;
            int n = k = bl3 ? 1 : -1;
            if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
                this.applyHandOffset(matrixStack, arm, i);
                matrixStack.translate((float)k * -0.4785682f, -0.094387f, 0.05731530860066414);
                matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-11.935f));
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)k * 65.3f));
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)k * -9.785f));
                float l = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0f);
                float m = l / (float)CrossbowItem.getPullTime(itemStack);
                if (m > 1.0f) {
                    m = 1.0f;
                }
                if (m > 0.1f) {
                    float n2 = MathHelper.sin((l - 0.1f) * 1.3f);
                    float o = m - 0.1f;
                    float p = n2 * o;
                    matrixStack.translate(p * 0.0f, p * 0.004f, p * 0.0f);
                }
                matrixStack.translate(m * 0.0f, m * 0.0f, m * 0.04f);
                matrixStack.scale(1.0f, 1.0f, 1.0f + m * 0.2f);
                matrixStack.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)k * 45.0f));
            } else {
                float l = -0.4f * MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
                float m = 0.2f * MathHelper.sin(MathHelper.sqrt(h) * ((float)Math.PI * 2));
                float n3 = -0.2f * MathHelper.sin(h * (float)Math.PI);
                matrixStack.translate((float)k * l, m, n3);
                this.applyHandOffset(matrixStack, arm, i);
                this.method_3217(matrixStack, arm, h);
                if (bl2 && h < 0.001f) {
                    matrixStack.translate((float)k * -0.641864f, 0.0, 0.0);
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)k * 10.0f));
                }
            }
            this.renderItem(abstractClientPlayerEntity, itemStack, bl3 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND, !bl3, matrixStack, vertexConsumerProvider, j);
        } else {
            boolean bl2;
            boolean bl3 = bl2 = arm == Arm.RIGHT;
            if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
                int q = bl2 ? 1 : -1;
                switch (itemStack.getUseAction()) {
                    case NONE: {
                        this.applyHandOffset(matrixStack, arm, i);
                        break;
                    }
                    case EAT: 
                    case DRINK: {
                        this.applyEatOrDrinkTransformation(matrixStack, f, arm, itemStack);
                        this.applyHandOffset(matrixStack, arm, i);
                        break;
                    }
                    case BLOCK: {
                        this.applyHandOffset(matrixStack, arm, i);
                        break;
                    }
                    case BOW: {
                        this.applyHandOffset(matrixStack, arm, i);
                        matrixStack.translate((float)q * -0.2785682f, 0.18344387412071228, 0.15731531381607056);
                        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-13.935f));
                        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)q * 35.3f));
                        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)q * -9.785f));
                        float r = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0f);
                        float l = r / 20.0f;
                        l = (l * l + l * 2.0f) / 3.0f;
                        if (l > 1.0f) {
                            l = 1.0f;
                        }
                        if (l > 0.1f) {
                            float m = MathHelper.sin((r - 0.1f) * 1.3f);
                            float n = l - 0.1f;
                            float o = m * n;
                            matrixStack.translate(o * 0.0f, o * 0.004f, o * 0.0f);
                        }
                        matrixStack.translate(l * 0.0f, l * 0.0f, l * 0.04f);
                        matrixStack.scale(1.0f, 1.0f, 1.0f + l * 0.2f);
                        matrixStack.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)q * 45.0f));
                        break;
                    }
                    case SPEAR: {
                        this.applyHandOffset(matrixStack, arm, i);
                        matrixStack.translate((float)q * -0.5f, 0.7f, 0.1f);
                        matrixStack.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(-55.0f));
                        matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)q * 35.3f));
                        matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)q * -9.785f));
                        float r = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0f);
                        float l = r / 10.0f;
                        if (l > 1.0f) {
                            l = 1.0f;
                        }
                        if (l > 0.1f) {
                            float m = MathHelper.sin((r - 0.1f) * 1.3f);
                            float n = l - 0.1f;
                            float o = m * n;
                            matrixStack.translate(o * 0.0f, o * 0.004f, o * 0.0f);
                        }
                        matrixStack.translate(0.0, 0.0, l * 0.2f);
                        matrixStack.scale(1.0f, 1.0f, 1.0f + l * 0.2f);
                        matrixStack.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((float)q * 45.0f));
                        break;
                    }
                }
            } else if (abstractClientPlayerEntity.isUsingRiptide()) {
                this.applyHandOffset(matrixStack, arm, i);
                int q = bl2 ? 1 : -1;
                matrixStack.translate((float)q * -0.4f, 0.8f, 0.3f);
                matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float)q * 65.0f));
                matrixStack.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float)q * -85.0f));
            } else {
                float s = -0.4f * MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
                float r = 0.2f * MathHelper.sin(MathHelper.sqrt(h) * ((float)Math.PI * 2));
                float l = -0.2f * MathHelper.sin(h * (float)Math.PI);
                int t = bl2 ? 1 : -1;
                matrixStack.translate((float)t * s, r, l);
                this.applyHandOffset(matrixStack, arm, i);
                this.method_3217(matrixStack, arm, h);
            }
            this.renderItem(abstractClientPlayerEntity, itemStack, bl2 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND, !bl2, matrixStack, vertexConsumerProvider, j);
        }
        matrixStack.pop();
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
            this.equipProgressMainHand += MathHelper.clamp((Objects.equals(this.mainHand, itemStack) ? f * f * f : 0.0f) - this.equipProgressMainHand, -0.4f, 0.4f);
            this.equipProgressOffHand += MathHelper.clamp((float)(Objects.equals(this.offHand, itemStack2) ? 1 : 0) - this.equipProgressOffHand, -0.4f, 0.4f);
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

