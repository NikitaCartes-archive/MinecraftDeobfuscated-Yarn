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
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.Matrix4f;
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
import net.minecraft.util.math.MatrixStack;

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

    public void renderItem(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Type type, boolean bl, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        if (itemStack.isEmpty()) {
            return;
        }
        this.itemRenderer.method_23177(livingEntity, itemStack, type, bl, matrixStack, layeredVertexConsumerStorage, livingEntity.world, livingEntity.getLightmapCoordinates());
    }

    private float getMapAngle(float f) {
        float g = 1.0f - f / 45.0f + 0.1f;
        g = MathHelper.clamp(g, 0.0f, 1.0f);
        g = -MathHelper.cos(g * (float)Math.PI) * 0.5f + 0.5f;
        return g;
    }

    private void renderArm(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, Arm arm) {
        this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.getRenderer(this.client.player);
        matrixStack.push();
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(92.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(45.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(f * -41.0f, true));
        matrixStack.translate(f * 0.3f, -1.1f, 0.45f);
        if (arm == Arm.RIGHT) {
            playerEntityRenderer.renderRightArm(matrixStack, layeredVertexConsumerStorage, this.client.player);
        } else {
            playerEntityRenderer.renderLeftArm(matrixStack, layeredVertexConsumerStorage, this.client.player);
        }
        matrixStack.pop();
    }

    private void renderMapInOneHand(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, float f, Arm arm, float g, ItemStack itemStack) {
        float h = arm == Arm.RIGHT ? 1.0f : -1.0f;
        matrixStack.translate(h * 0.125f, -0.125, 0.0);
        if (!this.client.player.isInvisible()) {
            matrixStack.push();
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(h * 10.0f, true));
            this.renderArmHoldingItem(matrixStack, layeredVertexConsumerStorage, f, g, arm);
            matrixStack.pop();
        }
        matrixStack.push();
        matrixStack.translate(h * 0.51f, -0.08f + f * -1.2f, -0.75);
        float i = MathHelper.sqrt(g);
        float j = MathHelper.sin(i * (float)Math.PI);
        float k = -0.5f * j;
        float l = 0.4f * MathHelper.sin(i * ((float)Math.PI * 2));
        float m = -0.3f * MathHelper.sin(g * (float)Math.PI);
        matrixStack.translate(h * k, l - 0.3f * j, m);
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(j * -45.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h * j * -30.0f, true));
        this.renderFirstPersonMap(matrixStack, layeredVertexConsumerStorage, itemStack);
        matrixStack.pop();
    }

    private void renderMapInBothHands(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, float f, float g, float h) {
        float i = MathHelper.sqrt(h);
        float j = -0.2f * MathHelper.sin(h * (float)Math.PI);
        float k = -0.4f * MathHelper.sin(i * (float)Math.PI);
        matrixStack.translate(0.0, -j / 2.0f, k);
        float l = this.getMapAngle(f);
        matrixStack.translate(0.0, 0.04f + g * -1.2f + l * -0.5f, -0.72f);
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(l * -85.0f, true));
        if (!this.client.player.isInvisible()) {
            matrixStack.push();
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(90.0f, true));
            this.renderArm(matrixStack, layeredVertexConsumerStorage, Arm.RIGHT);
            this.renderArm(matrixStack, layeredVertexConsumerStorage, Arm.LEFT);
            matrixStack.pop();
        }
        float m = MathHelper.sin(i * (float)Math.PI);
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(m * 20.0f, true));
        matrixStack.scale(2.0f, 2.0f, 2.0f);
        this.renderFirstPersonMap(matrixStack, layeredVertexConsumerStorage, this.mainHand);
    }

    private void renderFirstPersonMap(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, ItemStack itemStack) {
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(180.0f, true));
        matrixStack.scale(0.38f, 0.38f, 0.38f);
        matrixStack.translate(-0.5, -0.5, 0.0);
        matrixStack.scale(0.0078125f, 0.0078125f, 0.0078125f);
        VertexConsumer vertexConsumer = layeredVertexConsumerStorage.getBuffer(RenderLayer.method_23028(MapRenderer.field_21056));
        Matrix4f matrix4f = matrixStack.peek();
        vertexConsumer.vertex(matrix4f, -7.0f, 135.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 1.0f).light(0xF000F0).next();
        vertexConsumer.vertex(matrix4f, 135.0f, 135.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 1.0f).light(0xF000F0).next();
        vertexConsumer.vertex(matrix4f, 135.0f, -7.0f, 0.0f).color(255, 255, 255, 255).texture(1.0f, 0.0f).light(0xF000F0).next();
        vertexConsumer.vertex(matrix4f, -7.0f, -7.0f, 0.0f).color(255, 255, 255, 255).texture(0.0f, 0.0f).light(0xF000F0).next();
        MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, this.client.world);
        if (mapState != null) {
            this.client.gameRenderer.getMapRenderer().draw(matrixStack, layeredVertexConsumerStorage, mapState, false, 0xF000F0);
        }
    }

    private void renderArmHoldingItem(MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, float f, float g, Arm arm) {
        boolean bl = arm != Arm.LEFT;
        float h = bl ? 1.0f : -1.0f;
        float i = MathHelper.sqrt(g);
        float j = -0.3f * MathHelper.sin(i * (float)Math.PI);
        float k = 0.4f * MathHelper.sin(i * ((float)Math.PI * 2));
        float l = -0.4f * MathHelper.sin(g * (float)Math.PI);
        matrixStack.translate(h * (j + 0.64000005f), k + -0.6f + f * -0.6f, l + -0.71999997f);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h * 45.0f, true));
        float m = MathHelper.sin(g * g * (float)Math.PI);
        float n = MathHelper.sin(i * (float)Math.PI);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h * n * 70.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(h * m * -20.0f, true));
        ClientPlayerEntity abstractClientPlayerEntity = this.client.player;
        this.client.getTextureManager().bindTexture(abstractClientPlayerEntity.getSkinTexture());
        matrixStack.translate(h * -1.0f, 3.6f, 3.5);
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion(h * 120.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(200.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion(h * -135.0f, true));
        matrixStack.translate(h * 5.6f, 0.0, 0.0);
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.getRenderer(abstractClientPlayerEntity);
        if (bl) {
            playerEntityRenderer.renderRightArm(matrixStack, layeredVertexConsumerStorage, abstractClientPlayerEntity);
        } else {
            playerEntityRenderer.renderLeftArm(matrixStack, layeredVertexConsumerStorage, abstractClientPlayerEntity);
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
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)j * i * 90.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(i * 10.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)j * i * 30.0f, true));
    }

    private void method_3217(MatrixStack matrixStack, Arm arm, float f) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        float g = MathHelper.sin(f * f * (float)Math.PI);
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)i * (45.0f + g * -20.0f), true));
        float h = MathHelper.sin(MathHelper.sqrt(f) * (float)Math.PI);
        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)i * h * -20.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(h * -80.0f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)i * -45.0f, true));
    }

    private void applyHandOffset(MatrixStack matrixStack, Arm arm, float f) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrixStack.translate((float)i * 0.56f, -0.52f + f * -0.6f, -0.72f);
    }

    public void method_22976(float f, MatrixStack matrixStack, LayeredVertexConsumerStorage.class_4598 arg) {
        float l;
        ItemStack itemStack;
        ClientPlayerEntity clientPlayerEntity = this.client.player;
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
        float i = MathHelper.lerp(f, clientPlayerEntity.lastRenderPitch, clientPlayerEntity.renderPitch);
        float j = MathHelper.lerp(f, clientPlayerEntity.lastRenderYaw, clientPlayerEntity.renderYaw);
        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion((clientPlayerEntity.getPitch(f) - i) * 0.1f, true));
        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((clientPlayerEntity.getYaw(f) - j) * 0.1f, true));
        if (bl) {
            float k = hand == Hand.MAIN_HAND ? g : 0.0f;
            l = 1.0f - MathHelper.lerp(f, this.prevEquipProgressMainHand, this.equipProgressMainHand);
            this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.MAIN_HAND, k, this.mainHand, l, matrixStack, arg);
        }
        if (bl2) {
            float k = hand == Hand.OFF_HAND ? g : 0.0f;
            l = 1.0f - MathHelper.lerp(f, this.prevEquipProgressOffHand, this.equipProgressOffHand);
            this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.OFF_HAND, k, this.offHand, l, matrixStack, arg);
        }
        arg.method_22993();
    }

    private void renderFirstPersonItem(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, Hand hand, float h, ItemStack itemStack, float i, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage) {
        boolean bl = hand == Hand.MAIN_HAND;
        Arm arm = bl ? abstractClientPlayerEntity.getMainArm() : abstractClientPlayerEntity.getMainArm().getOpposite();
        matrixStack.push();
        if (itemStack.isEmpty()) {
            if (bl && !abstractClientPlayerEntity.isInvisible()) {
                this.renderArmHoldingItem(matrixStack, layeredVertexConsumerStorage, i, h, arm);
            }
        } else if (itemStack.getItem() == Items.FILLED_MAP) {
            if (bl && this.offHand.isEmpty()) {
                this.renderMapInBothHands(matrixStack, layeredVertexConsumerStorage, g, i, h);
            } else {
                this.renderMapInOneHand(matrixStack, layeredVertexConsumerStorage, i, arm, h, itemStack);
            }
        } else if (itemStack.getItem() == Items.CROSSBOW) {
            int j;
            boolean bl2 = CrossbowItem.isCharged(itemStack);
            boolean bl3 = arm == Arm.RIGHT;
            int n = j = bl3 ? 1 : -1;
            if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
                this.applyHandOffset(matrixStack, arm, i);
                matrixStack.translate((float)j * -0.4785682f, -0.094387f, 0.05731530860066414);
                matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-11.935f, true));
                matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)j * 65.3f, true));
                matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)j * -9.785f, true));
                float k = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0f);
                float l = k / (float)CrossbowItem.getPullTime(itemStack);
                if (l > 1.0f) {
                    l = 1.0f;
                }
                if (l > 0.1f) {
                    float m = MathHelper.sin((k - 0.1f) * 1.3f);
                    float n2 = l - 0.1f;
                    float o = m * n2;
                    matrixStack.translate(o * 0.0f, o * 0.004f, o * 0.0f);
                }
                matrixStack.translate(l * 0.0f, l * 0.0f, l * 0.04f);
                matrixStack.scale(1.0f, 1.0f, 1.0f + l * 0.2f);
                matrixStack.multiply(Vector3f.NEGATIVE_Y.getRotationQuaternion((float)j * 45.0f, true));
            } else {
                float k = -0.4f * MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
                float l = 0.2f * MathHelper.sin(MathHelper.sqrt(h) * ((float)Math.PI * 2));
                float m = -0.2f * MathHelper.sin(h * (float)Math.PI);
                matrixStack.translate((float)j * k, l, m);
                this.applyHandOffset(matrixStack, arm, i);
                this.method_3217(matrixStack, arm, h);
                if (bl2 && h < 0.001f) {
                    matrixStack.translate((float)j * -0.641864f, 0.0, 0.0);
                    matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)j * 10.0f, true));
                }
            }
            this.renderItem(abstractClientPlayerEntity, itemStack, bl3 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND, !bl3, matrixStack, layeredVertexConsumerStorage);
        } else {
            boolean bl2;
            boolean bl3 = bl2 = arm == Arm.RIGHT;
            if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
                int p = bl2 ? 1 : -1;
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
                        matrixStack.translate((float)p * -0.2785682f, 0.18344387412071228, 0.15731531381607056);
                        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-13.935f, true));
                        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)p * 35.3f, true));
                        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)p * -9.785f, true));
                        float q = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0f);
                        float k = q / 20.0f;
                        k = (k * k + k * 2.0f) / 3.0f;
                        if (k > 1.0f) {
                            k = 1.0f;
                        }
                        if (k > 0.1f) {
                            float l = MathHelper.sin((q - 0.1f) * 1.3f);
                            float m = k - 0.1f;
                            float n = l * m;
                            matrixStack.translate(n * 0.0f, n * 0.004f, n * 0.0f);
                        }
                        matrixStack.translate(k * 0.0f, k * 0.0f, k * 0.04f);
                        matrixStack.scale(1.0f, 1.0f, 1.0f + k * 0.2f);
                        matrixStack.multiply(Vector3f.NEGATIVE_Y.getRotationQuaternion((float)p * 45.0f, true));
                        break;
                    }
                    case SPEAR: {
                        this.applyHandOffset(matrixStack, arm, i);
                        matrixStack.translate((float)p * -0.5f, 0.7f, 0.1f);
                        matrixStack.multiply(Vector3f.POSITIVE_X.getRotationQuaternion(-55.0f, true));
                        matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)p * 35.3f, true));
                        matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)p * -9.785f, true));
                        float q = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0f);
                        float k = q / 10.0f;
                        if (k > 1.0f) {
                            k = 1.0f;
                        }
                        if (k > 0.1f) {
                            float l = MathHelper.sin((q - 0.1f) * 1.3f);
                            float m = k - 0.1f;
                            float n = l * m;
                            matrixStack.translate(n * 0.0f, n * 0.004f, n * 0.0f);
                        }
                        matrixStack.translate(0.0, 0.0, k * 0.2f);
                        matrixStack.scale(1.0f, 1.0f, 1.0f + k * 0.2f);
                        matrixStack.multiply(Vector3f.NEGATIVE_Y.getRotationQuaternion((float)p * 45.0f, true));
                        break;
                    }
                }
            } else if (abstractClientPlayerEntity.isUsingRiptide()) {
                this.applyHandOffset(matrixStack, arm, i);
                int p = bl2 ? 1 : -1;
                matrixStack.translate((float)p * -0.4f, 0.8f, 0.3f);
                matrixStack.multiply(Vector3f.POSITIVE_Y.getRotationQuaternion((float)p * 65.0f, true));
                matrixStack.multiply(Vector3f.POSITIVE_Z.getRotationQuaternion((float)p * -85.0f, true));
            } else {
                float r = -0.4f * MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
                float q = 0.2f * MathHelper.sin(MathHelper.sqrt(h) * ((float)Math.PI * 2));
                float k = -0.2f * MathHelper.sin(h * (float)Math.PI);
                int s = bl2 ? 1 : -1;
                matrixStack.translate((float)s * r, q, k);
                this.applyHandOffset(matrixStack, arm, i);
                this.method_3217(matrixStack, arm, h);
            }
            this.renderItem(abstractClientPlayerEntity, itemStack, bl2 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND, !bl2, matrixStack, layeredVertexConsumerStorage);
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

