/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.base.MoreObjects;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FirstPersonRenderer {
    private static final Identifier MAP_BACKGROUND_TEX = new Identifier("textures/map/map_background.png");
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

    public void renderItem(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Type type, boolean bl, class_4587 arg, class_4597 arg2) {
        if (itemStack.isEmpty()) {
            return;
        }
        this.itemRenderer.method_23177(livingEntity, itemStack, type, bl, arg, arg2, livingEntity.world, livingEntity.getLightmapCoordinates());
    }

    private float getMapAngle(float f) {
        float g = 1.0f - f / 45.0f + 0.1f;
        g = MathHelper.clamp(g, 0.0f, 1.0f);
        g = -MathHelper.cos(g * (float)Math.PI) * 0.5f + 0.5f;
        return g;
    }

    private void renderArm(class_4587 arg, class_4597 arg2, Arm arm) {
        this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.getRenderer(this.client.player);
        arg.method_22903();
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        arg.method_22907(Vector3f.field_20705.method_23214(92.0f, true));
        arg.method_22907(Vector3f.field_20703.method_23214(45.0f, true));
        arg.method_22907(Vector3f.field_20707.method_23214(f * -41.0f, true));
        arg.method_22904(f * 0.3f, -1.1f, 0.45f);
        if (arm == Arm.RIGHT) {
            playerEntityRenderer.renderRightArm(arg, arg2, this.client.player);
        } else {
            playerEntityRenderer.renderLeftArm(arg, arg2, this.client.player);
        }
        arg.method_22909();
    }

    private void renderMapInOneHand(class_4587 arg, class_4597 arg2, float f, Arm arm, float g, ItemStack itemStack) {
        float h = arm == Arm.RIGHT ? 1.0f : -1.0f;
        arg.method_22904(h * 0.125f, -0.125, 0.0);
        if (!this.client.player.isInvisible()) {
            arg.method_22903();
            arg.method_22907(Vector3f.field_20707.method_23214(h * 10.0f, true));
            this.renderArmHoldingItem(arg, arg2, f, g, arm);
            arg.method_22909();
        }
        arg.method_22903();
        arg.method_22904(h * 0.51f, -0.08f + f * -1.2f, -0.75);
        float i = MathHelper.sqrt(g);
        float j = MathHelper.sin(i * (float)Math.PI);
        float k = -0.5f * j;
        float l = 0.4f * MathHelper.sin(i * ((float)Math.PI * 2));
        float m = -0.3f * MathHelper.sin(g * (float)Math.PI);
        arg.method_22904(h * k, l - 0.3f * j, m);
        arg.method_22907(Vector3f.field_20703.method_23214(j * -45.0f, true));
        arg.method_22907(Vector3f.field_20705.method_23214(h * j * -30.0f, true));
        this.renderFirstPersonMap(arg, arg2, itemStack);
        arg.method_22909();
    }

    private void renderMapInBothHands(class_4587 arg, class_4597 arg2, float f, float g, float h) {
        float i = MathHelper.sqrt(h);
        float j = -0.2f * MathHelper.sin(h * (float)Math.PI);
        float k = -0.4f * MathHelper.sin(i * (float)Math.PI);
        arg.method_22904(0.0, -j / 2.0f, k);
        float l = this.getMapAngle(f);
        arg.method_22904(0.0, 0.04f + g * -1.2f + l * -0.5f, -0.72f);
        arg.method_22907(Vector3f.field_20703.method_23214(l * -85.0f, true));
        if (!this.client.player.isInvisible()) {
            arg.method_22903();
            arg.method_22907(Vector3f.field_20705.method_23214(90.0f, true));
            this.renderArm(arg, arg2, Arm.RIGHT);
            this.renderArm(arg, arg2, Arm.LEFT);
            arg.method_22909();
        }
        float m = MathHelper.sin(i * (float)Math.PI);
        arg.method_22907(Vector3f.field_20703.method_23214(m * 20.0f, true));
        arg.method_22905(2.0f, 2.0f, 2.0f);
        this.renderFirstPersonMap(arg, arg2, this.mainHand);
    }

    private void renderFirstPersonMap(class_4587 arg, class_4597 arg2, ItemStack itemStack) {
        arg.method_22907(Vector3f.field_20705.method_23214(180.0f, true));
        arg.method_22907(Vector3f.field_20707.method_23214(180.0f, true));
        arg.method_22905(0.38f, 0.38f, 0.38f);
        this.client.getTextureManager().bindTexture(MAP_BACKGROUND_TEX);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        arg.method_22904(-0.5, -0.5, 0.0);
        arg.method_22905(0.0078125f, 0.0078125f, 0.0078125f);
        bufferBuilder.begin(7, VertexFormats.POSITION_UV);
        Matrix4f matrix4f = arg.method_22910();
        bufferBuilder.method_22918(matrix4f, -7.0f, 135.0f, 0.0f).texture(0.0f, 1.0f).next();
        bufferBuilder.method_22918(matrix4f, 135.0f, 135.0f, 0.0f).texture(1.0f, 1.0f).next();
        bufferBuilder.method_22918(matrix4f, 135.0f, -7.0f, 0.0f).texture(1.0f, 0.0f).next();
        bufferBuilder.method_22918(matrix4f, -7.0f, -7.0f, 0.0f).texture(0.0f, 0.0f).next();
        tessellator.draw();
        MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, this.client.world);
        if (mapState != null) {
            this.client.gameRenderer.getMapRenderer().draw(arg, arg2, mapState, false);
        }
    }

    private void renderArmHoldingItem(class_4587 arg, class_4597 arg2, float f, float g, Arm arm) {
        boolean bl = arm != Arm.LEFT;
        float h = bl ? 1.0f : -1.0f;
        float i = MathHelper.sqrt(g);
        float j = -0.3f * MathHelper.sin(i * (float)Math.PI);
        float k = 0.4f * MathHelper.sin(i * ((float)Math.PI * 2));
        float l = -0.4f * MathHelper.sin(g * (float)Math.PI);
        arg.method_22904(h * (j + 0.64000005f), k + -0.6f + f * -0.6f, l + -0.71999997f);
        arg.method_22907(Vector3f.field_20705.method_23214(h * 45.0f, true));
        float m = MathHelper.sin(g * g * (float)Math.PI);
        float n = MathHelper.sin(i * (float)Math.PI);
        arg.method_22907(Vector3f.field_20705.method_23214(h * n * 70.0f, true));
        arg.method_22907(Vector3f.field_20707.method_23214(h * m * -20.0f, true));
        ClientPlayerEntity abstractClientPlayerEntity = this.client.player;
        this.client.getTextureManager().bindTexture(abstractClientPlayerEntity.getSkinTexture());
        arg.method_22904(h * -1.0f, 3.6f, 3.5);
        arg.method_22907(Vector3f.field_20707.method_23214(h * 120.0f, true));
        arg.method_22907(Vector3f.field_20703.method_23214(200.0f, true));
        arg.method_22907(Vector3f.field_20705.method_23214(h * -135.0f, true));
        arg.method_22904(h * 5.6f, 0.0, 0.0);
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.getRenderer(abstractClientPlayerEntity);
        if (bl) {
            playerEntityRenderer.renderRightArm(arg, arg2, abstractClientPlayerEntity);
        } else {
            playerEntityRenderer.renderLeftArm(arg, arg2, abstractClientPlayerEntity);
        }
    }

    private void applyEatOrDrinkTransformation(class_4587 arg, float f, Arm arm, ItemStack itemStack) {
        float i;
        float g = (float)this.client.player.getItemUseTimeLeft() - f + 1.0f;
        float h = g / (float)itemStack.getMaxUseTime();
        if (h < 0.8f) {
            i = MathHelper.abs(MathHelper.cos(g / 4.0f * (float)Math.PI) * 0.1f);
            arg.method_22904(0.0, i, 0.0);
        }
        i = 1.0f - (float)Math.pow(h, 27.0);
        int j = arm == Arm.RIGHT ? 1 : -1;
        arg.method_22904(i * 0.6f * (float)j, i * -0.5f, i * 0.0f);
        arg.method_22907(Vector3f.field_20705.method_23214((float)j * i * 90.0f, true));
        arg.method_22907(Vector3f.field_20703.method_23214(i * 10.0f, true));
        arg.method_22907(Vector3f.field_20707.method_23214((float)j * i * 30.0f, true));
    }

    private void method_3217(class_4587 arg, Arm arm, float f) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        float g = MathHelper.sin(f * f * (float)Math.PI);
        arg.method_22907(Vector3f.field_20705.method_23214((float)i * (45.0f + g * -20.0f), true));
        float h = MathHelper.sin(MathHelper.sqrt(f) * (float)Math.PI);
        arg.method_22907(Vector3f.field_20707.method_23214((float)i * h * -20.0f, true));
        arg.method_22907(Vector3f.field_20703.method_23214(h * -80.0f, true));
        arg.method_22907(Vector3f.field_20705.method_23214((float)i * -45.0f, true));
    }

    private void applyHandOffset(class_4587 arg, Arm arm, float f) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        arg.method_22904((float)i * 0.56f, -0.52f + f * -0.6f, -0.72f);
    }

    public void method_22976(float f, class_4587 arg, class_4597.class_4598 arg2) {
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
        arg.method_22907(Vector3f.field_20703.method_23214((clientPlayerEntity.getPitch(f) - i) * 0.1f, true));
        arg.method_22907(Vector3f.field_20705.method_23214((clientPlayerEntity.getYaw(f) - j) * 0.1f, true));
        if (bl) {
            float k = hand == Hand.MAIN_HAND ? g : 0.0f;
            l = 1.0f - MathHelper.lerp(f, this.prevEquipProgressMainHand, this.equipProgressMainHand);
            this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.MAIN_HAND, k, this.mainHand, l, arg, arg2);
        }
        if (bl2) {
            float k = hand == Hand.OFF_HAND ? g : 0.0f;
            l = 1.0f - MathHelper.lerp(f, this.prevEquipProgressOffHand, this.equipProgressOffHand);
            this.renderFirstPersonItem(clientPlayerEntity, f, h, Hand.OFF_HAND, k, this.offHand, l, arg, arg2);
        }
        arg2.method_22993();
    }

    private void renderFirstPersonItem(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, Hand hand, float h, ItemStack itemStack, float i, class_4587 arg, class_4597 arg2) {
        boolean bl = hand == Hand.MAIN_HAND;
        Arm arm = bl ? abstractClientPlayerEntity.getMainArm() : abstractClientPlayerEntity.getMainArm().getOpposite();
        arg.method_22903();
        if (itemStack.isEmpty()) {
            if (bl && !abstractClientPlayerEntity.isInvisible()) {
                this.renderArmHoldingItem(arg, arg2, i, h, arm);
            }
        } else if (itemStack.getItem() == Items.FILLED_MAP) {
            if (bl && this.offHand.isEmpty()) {
                this.renderMapInBothHands(arg, arg2, g, i, h);
            } else {
                this.renderMapInOneHand(arg, arg2, i, arm, h, itemStack);
            }
        } else if (itemStack.getItem() == Items.CROSSBOW) {
            int j;
            boolean bl2 = CrossbowItem.isCharged(itemStack);
            boolean bl3 = arm == Arm.RIGHT;
            int n = j = bl3 ? 1 : -1;
            if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
                this.applyHandOffset(arg, arm, i);
                arg.method_22904((float)j * -0.4785682f, -0.094387f, 0.05731530860066414);
                arg.method_22907(Vector3f.field_20703.method_23214(-11.935f, true));
                arg.method_22907(Vector3f.field_20705.method_23214((float)j * 65.3f, true));
                arg.method_22907(Vector3f.field_20707.method_23214((float)j * -9.785f, true));
                float k = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0f);
                float l = k / (float)CrossbowItem.getPullTime(itemStack);
                if (l > 1.0f) {
                    l = 1.0f;
                }
                if (l > 0.1f) {
                    float m = MathHelper.sin((k - 0.1f) * 1.3f);
                    float n2 = l - 0.1f;
                    float o = m * n2;
                    arg.method_22904(o * 0.0f, o * 0.004f, o * 0.0f);
                }
                arg.method_22904(l * 0.0f, l * 0.0f, l * 0.04f);
                arg.method_22905(1.0f, 1.0f, 1.0f + l * 0.2f);
                arg.method_22907(Vector3f.field_20704.method_23214((float)j * 45.0f, true));
            } else {
                float k = -0.4f * MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
                float l = 0.2f * MathHelper.sin(MathHelper.sqrt(h) * ((float)Math.PI * 2));
                float m = -0.2f * MathHelper.sin(h * (float)Math.PI);
                arg.method_22904((float)j * k, l, m);
                this.applyHandOffset(arg, arm, i);
                this.method_3217(arg, arm, h);
                if (bl2 && h < 0.001f) {
                    arg.method_22904((float)j * -0.641864f, 0.0, 0.0);
                    arg.method_22907(Vector3f.field_20705.method_23214((float)j * 10.0f, true));
                }
            }
            this.renderItem(abstractClientPlayerEntity, itemStack, bl3 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND, !bl3, arg, arg2);
        } else {
            boolean bl2;
            boolean bl3 = bl2 = arm == Arm.RIGHT;
            if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
                int p = bl2 ? 1 : -1;
                switch (itemStack.getUseAction()) {
                    case NONE: {
                        this.applyHandOffset(arg, arm, i);
                        break;
                    }
                    case EAT: 
                    case DRINK: {
                        this.applyEatOrDrinkTransformation(arg, f, arm, itemStack);
                        this.applyHandOffset(arg, arm, i);
                        break;
                    }
                    case BLOCK: {
                        this.applyHandOffset(arg, arm, i);
                        break;
                    }
                    case BOW: {
                        this.applyHandOffset(arg, arm, i);
                        arg.method_22904((float)p * -0.2785682f, 0.18344387412071228, 0.15731531381607056);
                        arg.method_22907(Vector3f.field_20703.method_23214(-13.935f, true));
                        arg.method_22907(Vector3f.field_20705.method_23214((float)p * 35.3f, true));
                        arg.method_22907(Vector3f.field_20707.method_23214((float)p * -9.785f, true));
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
                            arg.method_22904(n * 0.0f, n * 0.004f, n * 0.0f);
                        }
                        arg.method_22904(k * 0.0f, k * 0.0f, k * 0.04f);
                        arg.method_22905(1.0f, 1.0f, 1.0f + k * 0.2f);
                        arg.method_22907(Vector3f.field_20704.method_23214((float)p * 45.0f, true));
                        break;
                    }
                    case SPEAR: {
                        this.applyHandOffset(arg, arm, i);
                        arg.method_22904((float)p * -0.5f, 0.7f, 0.1f);
                        arg.method_22907(Vector3f.field_20703.method_23214(-55.0f, true));
                        arg.method_22907(Vector3f.field_20705.method_23214((float)p * 35.3f, true));
                        arg.method_22907(Vector3f.field_20707.method_23214((float)p * -9.785f, true));
                        float q = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0f);
                        float k = q / 10.0f;
                        if (k > 1.0f) {
                            k = 1.0f;
                        }
                        if (k > 0.1f) {
                            float l = MathHelper.sin((q - 0.1f) * 1.3f);
                            float m = k - 0.1f;
                            float n = l * m;
                            arg.method_22904(n * 0.0f, n * 0.004f, n * 0.0f);
                        }
                        arg.method_22904(0.0, 0.0, k * 0.2f);
                        arg.method_22905(1.0f, 1.0f, 1.0f + k * 0.2f);
                        arg.method_22907(Vector3f.field_20704.method_23214((float)p * 45.0f, true));
                        break;
                    }
                }
            } else if (abstractClientPlayerEntity.isUsingRiptide()) {
                this.applyHandOffset(arg, arm, i);
                int p = bl2 ? 1 : -1;
                arg.method_22904((float)p * -0.4f, 0.8f, 0.3f);
                arg.method_22907(Vector3f.field_20705.method_23214((float)p * 65.0f, true));
                arg.method_22907(Vector3f.field_20707.method_23214((float)p * -85.0f, true));
            } else {
                float r = -0.4f * MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
                float q = 0.2f * MathHelper.sin(MathHelper.sqrt(h) * ((float)Math.PI * 2));
                float k = -0.2f * MathHelper.sin(h * (float)Math.PI);
                int s = bl2 ? 1 : -1;
                arg.method_22904((float)s * r, q, k);
                this.applyHandOffset(arg, arm, i);
                this.method_3217(arg, arm, h);
            }
            this.renderItem(abstractClientPlayerEntity, itemStack, bl2 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND, !bl2, arg, arg2);
        }
        arg.method_22909();
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

