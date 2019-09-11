/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderLayer;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.map.MapState;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class FirstPersonRenderer {
    private static final Identifier MAP_BACKGROUND_TEX = new Identifier("textures/map/map_background.png");
    private static final Identifier UNDERWATER_TEX = new Identifier("textures/misc/underwater.png");
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

    public void renderItem(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Type type) {
        this.renderItemFromSide(livingEntity, itemStack, type, false);
    }

    public void renderItemFromSide(LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Type type, boolean bl) {
        boolean bl2;
        if (itemStack.isEmpty()) {
            return;
        }
        Item item = itemStack.getItem();
        Block block = Block.getBlockFromItem(item);
        RenderSystem.pushMatrix();
        boolean bl3 = bl2 = this.itemRenderer.hasDepthInGui(itemStack) && block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT;
        if (bl2) {
            RenderSystem.depthMask(false);
        }
        this.itemRenderer.renderHeldItem(itemStack, livingEntity, type, bl);
        if (bl2) {
            RenderSystem.depthMask(true);
        }
        RenderSystem.popMatrix();
    }

    private void rotate(float f, float g) {
        RenderSystem.pushMatrix();
        RenderSystem.rotatef(f, 1.0f, 0.0f, 0.0f);
        RenderSystem.rotatef(g, 0.0f, 1.0f, 0.0f);
        GuiLighting.enable();
        RenderSystem.popMatrix();
    }

    private void applyLightmap() {
        ClientPlayerEntity abstractClientPlayerEntity = this.client.player;
        int i = this.client.world.getLightmapIndex(new BlockPos(abstractClientPlayerEntity.x, abstractClientPlayerEntity.y + (double)abstractClientPlayerEntity.getStandingEyeHeight(), abstractClientPlayerEntity.z));
        float f = i & 0xFFFF;
        float g = i >> 16;
        RenderSystem.glMultiTexCoord2f(33985, f, g);
    }

    private void applyCameraAngles(float f) {
        ClientPlayerEntity clientPlayerEntity = this.client.player;
        float g = MathHelper.lerp(f, clientPlayerEntity.lastRenderPitch, clientPlayerEntity.renderPitch);
        float h = MathHelper.lerp(f, clientPlayerEntity.lastRenderYaw, clientPlayerEntity.renderYaw);
        RenderSystem.rotatef((clientPlayerEntity.getPitch(f) - g) * 0.1f, 1.0f, 0.0f, 0.0f);
        RenderSystem.rotatef((clientPlayerEntity.getYaw(f) - h) * 0.1f, 0.0f, 1.0f, 0.0f);
    }

    private float getMapAngle(float f) {
        float g = 1.0f - f / 45.0f + 0.1f;
        g = MathHelper.clamp(g, 0.0f, 1.0f);
        g = -MathHelper.cos(g * (float)Math.PI) * 0.5f + 0.5f;
        return g;
    }

    private void renderArms() {
        if (this.client.player.isInvisible()) {
            return;
        }
        RenderSystem.disableCull();
        RenderSystem.pushMatrix();
        RenderSystem.rotatef(90.0f, 0.0f, 1.0f, 0.0f);
        this.renderArm(Arm.RIGHT);
        this.renderArm(Arm.LEFT);
        RenderSystem.popMatrix();
        RenderSystem.enableCull();
    }

    private void renderArm(Arm arm) {
        this.client.getTextureManager().bindTexture(this.client.player.getSkinTexture());
        Object entityRenderer = this.renderManager.getRenderer(this.client.player);
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)entityRenderer;
        RenderSystem.pushMatrix();
        float f = arm == Arm.RIGHT ? 1.0f : -1.0f;
        RenderSystem.rotatef(92.0f, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(45.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.rotatef(f * -41.0f, 0.0f, 0.0f, 1.0f);
        RenderSystem.translatef(f * 0.3f, -1.1f, 0.45f);
        if (arm == Arm.RIGHT) {
            playerEntityRenderer.renderRightArm(this.client.player);
        } else {
            playerEntityRenderer.renderLeftArm(this.client.player);
        }
        RenderSystem.popMatrix();
    }

    private void renderMapInOneHand(float f, Arm arm, float g, ItemStack itemStack) {
        float h = arm == Arm.RIGHT ? 1.0f : -1.0f;
        RenderSystem.translatef(h * 0.125f, -0.125f, 0.0f);
        if (!this.client.player.isInvisible()) {
            RenderSystem.pushMatrix();
            RenderSystem.rotatef(h * 10.0f, 0.0f, 0.0f, 1.0f);
            this.renderArmHoldingItem(f, g, arm);
            RenderSystem.popMatrix();
        }
        RenderSystem.pushMatrix();
        RenderSystem.translatef(h * 0.51f, -0.08f + f * -1.2f, -0.75f);
        float i = MathHelper.sqrt(g);
        float j = MathHelper.sin(i * (float)Math.PI);
        float k = -0.5f * j;
        float l = 0.4f * MathHelper.sin(i * ((float)Math.PI * 2));
        float m = -0.3f * MathHelper.sin(g * (float)Math.PI);
        RenderSystem.translatef(h * k, l - 0.3f * j, m);
        RenderSystem.rotatef(j * -45.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.rotatef(h * j * -30.0f, 0.0f, 1.0f, 0.0f);
        this.renderFirstPersonMap(itemStack);
        RenderSystem.popMatrix();
    }

    private void renderMapInBothHands(float f, float g, float h) {
        float i = MathHelper.sqrt(h);
        float j = -0.2f * MathHelper.sin(h * (float)Math.PI);
        float k = -0.4f * MathHelper.sin(i * (float)Math.PI);
        RenderSystem.translatef(0.0f, -j / 2.0f, k);
        float l = this.getMapAngle(f);
        RenderSystem.translatef(0.0f, 0.04f + g * -1.2f + l * -0.5f, -0.72f);
        RenderSystem.rotatef(l * -85.0f, 1.0f, 0.0f, 0.0f);
        this.renderArms();
        float m = MathHelper.sin(i * (float)Math.PI);
        RenderSystem.rotatef(m * 20.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.scalef(2.0f, 2.0f, 2.0f);
        this.renderFirstPersonMap(this.mainHand);
    }

    private void renderFirstPersonMap(ItemStack itemStack) {
        RenderSystem.rotatef(180.0f, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
        RenderSystem.scalef(0.38f, 0.38f, 0.38f);
        RenderSystem.disableLighting();
        this.client.getTextureManager().bindTexture(MAP_BACKGROUND_TEX);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        RenderSystem.translatef(-0.5f, -0.5f, 0.0f);
        RenderSystem.scalef(0.0078125f, 0.0078125f, 0.0078125f);
        bufferBuilder.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder.vertex(-7.0, 135.0, 0.0).texture(0.0, 1.0).next();
        bufferBuilder.vertex(135.0, 135.0, 0.0).texture(1.0, 1.0).next();
        bufferBuilder.vertex(135.0, -7.0, 0.0).texture(1.0, 0.0).next();
        bufferBuilder.vertex(-7.0, -7.0, 0.0).texture(0.0, 0.0).next();
        tessellator.draw();
        MapState mapState = FilledMapItem.getOrCreateMapState(itemStack, this.client.world);
        if (mapState != null) {
            this.client.gameRenderer.getMapRenderer().draw(mapState, false);
        }
        RenderSystem.enableLighting();
    }

    private void renderArmHoldingItem(float f, float g, Arm arm) {
        boolean bl = arm != Arm.LEFT;
        float h = bl ? 1.0f : -1.0f;
        float i = MathHelper.sqrt(g);
        float j = -0.3f * MathHelper.sin(i * (float)Math.PI);
        float k = 0.4f * MathHelper.sin(i * ((float)Math.PI * 2));
        float l = -0.4f * MathHelper.sin(g * (float)Math.PI);
        RenderSystem.translatef(h * (j + 0.64000005f), k + -0.6f + f * -0.6f, l + -0.71999997f);
        RenderSystem.rotatef(h * 45.0f, 0.0f, 1.0f, 0.0f);
        float m = MathHelper.sin(g * g * (float)Math.PI);
        float n = MathHelper.sin(i * (float)Math.PI);
        RenderSystem.rotatef(h * n * 70.0f, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(h * m * -20.0f, 0.0f, 0.0f, 1.0f);
        ClientPlayerEntity abstractClientPlayerEntity = this.client.player;
        this.client.getTextureManager().bindTexture(abstractClientPlayerEntity.getSkinTexture());
        RenderSystem.translatef(h * -1.0f, 3.6f, 3.5f);
        RenderSystem.rotatef(h * 120.0f, 0.0f, 0.0f, 1.0f);
        RenderSystem.rotatef(200.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.rotatef(h * -135.0f, 0.0f, 1.0f, 0.0f);
        RenderSystem.translatef(h * 5.6f, 0.0f, 0.0f);
        PlayerEntityRenderer playerEntityRenderer = (PlayerEntityRenderer)this.renderManager.getRenderer(abstractClientPlayerEntity);
        RenderSystem.disableCull();
        if (bl) {
            playerEntityRenderer.renderRightArm(abstractClientPlayerEntity);
        } else {
            playerEntityRenderer.renderLeftArm(abstractClientPlayerEntity);
        }
        RenderSystem.enableCull();
    }

    private void applyEatOrDrinkTransformation(float f, Arm arm, ItemStack itemStack) {
        float i;
        float g = (float)this.client.player.getItemUseTimeLeft() - f + 1.0f;
        float h = g / (float)itemStack.getMaxUseTime();
        if (h < 0.8f) {
            i = MathHelper.abs(MathHelper.cos(g / 4.0f * (float)Math.PI) * 0.1f);
            RenderSystem.translatef(0.0f, i, 0.0f);
        }
        i = 1.0f - (float)Math.pow(h, 27.0);
        int j = arm == Arm.RIGHT ? 1 : -1;
        RenderSystem.translatef(i * 0.6f * (float)j, i * -0.5f, i * 0.0f);
        RenderSystem.rotatef((float)j * i * 90.0f, 0.0f, 1.0f, 0.0f);
        RenderSystem.rotatef(i * 10.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.rotatef((float)j * i * 30.0f, 0.0f, 0.0f, 1.0f);
    }

    private void method_3217(Arm arm, float f) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        float g = MathHelper.sin(f * f * (float)Math.PI);
        RenderSystem.rotatef((float)i * (45.0f + g * -20.0f), 0.0f, 1.0f, 0.0f);
        float h = MathHelper.sin(MathHelper.sqrt(f) * (float)Math.PI);
        RenderSystem.rotatef((float)i * h * -20.0f, 0.0f, 0.0f, 1.0f);
        RenderSystem.rotatef(h * -80.0f, 1.0f, 0.0f, 0.0f);
        RenderSystem.rotatef((float)i * -45.0f, 0.0f, 1.0f, 0.0f);
    }

    private void applyHandOffset(Arm arm, float f) {
        int i = arm == Arm.RIGHT ? 1 : -1;
        RenderSystem.translatef((float)i * 0.56f, -0.52f + f * -0.6f, -0.72f);
    }

    public void renderFirstPersonItem(float f) {
        ItemStack itemStack;
        ClientPlayerEntity abstractClientPlayerEntity = this.client.player;
        float g = abstractClientPlayerEntity.getHandSwingProgress(f);
        Hand hand = MoreObjects.firstNonNull(abstractClientPlayerEntity.preferredHand, Hand.MAIN_HAND);
        float h = MathHelper.lerp(f, abstractClientPlayerEntity.prevPitch, abstractClientPlayerEntity.pitch);
        float i = MathHelper.lerp(f, abstractClientPlayerEntity.prevYaw, abstractClientPlayerEntity.yaw);
        boolean bl = true;
        boolean bl2 = true;
        if (((LivingEntity)abstractClientPlayerEntity).isUsingItem()) {
            ItemStack itemStack2;
            Hand hand2;
            itemStack = abstractClientPlayerEntity.getActiveItem();
            if (itemStack.getItem() == Items.BOW || itemStack.getItem() == Items.CROSSBOW) {
                bl = ((LivingEntity)abstractClientPlayerEntity).getActiveHand() == Hand.MAIN_HAND;
                boolean bl3 = bl2 = !bl;
            }
            if ((hand2 = ((LivingEntity)abstractClientPlayerEntity).getActiveHand()) == Hand.MAIN_HAND && (itemStack2 = abstractClientPlayerEntity.getOffHandStack()).getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack2)) {
                bl2 = false;
            }
        } else {
            itemStack = abstractClientPlayerEntity.getMainHandStack();
            ItemStack itemStack3 = abstractClientPlayerEntity.getOffHandStack();
            if (itemStack.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack)) {
                boolean bl4 = bl2 = !bl;
            }
            if (itemStack3.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(itemStack3)) {
                bl = !itemStack.isEmpty();
                bl2 = !bl;
            }
        }
        this.rotate(h, i);
        this.applyLightmap();
        this.applyCameraAngles(f);
        RenderSystem.enableRescaleNormal();
        if (bl) {
            float j = hand == Hand.MAIN_HAND ? g : 0.0f;
            float k = 1.0f - MathHelper.lerp(f, this.prevEquipProgressMainHand, this.equipProgressMainHand);
            this.renderFirstPersonItem(abstractClientPlayerEntity, f, h, Hand.MAIN_HAND, j, this.mainHand, k);
        }
        if (bl2) {
            float j = hand == Hand.OFF_HAND ? g : 0.0f;
            float k = 1.0f - MathHelper.lerp(f, this.prevEquipProgressOffHand, this.equipProgressOffHand);
            this.renderFirstPersonItem(abstractClientPlayerEntity, f, h, Hand.OFF_HAND, j, this.offHand, k);
        }
        RenderSystem.disableRescaleNormal();
        GuiLighting.disable();
    }

    public void renderFirstPersonItem(AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, Hand hand, float h, ItemStack itemStack, float i) {
        boolean bl = hand == Hand.MAIN_HAND;
        Arm arm = bl ? abstractClientPlayerEntity.getMainArm() : abstractClientPlayerEntity.getMainArm().getOpposite();
        RenderSystem.pushMatrix();
        if (itemStack.isEmpty()) {
            if (bl && !abstractClientPlayerEntity.isInvisible()) {
                this.renderArmHoldingItem(i, h, arm);
            }
        } else if (itemStack.getItem() == Items.FILLED_MAP) {
            if (bl && this.offHand.isEmpty()) {
                this.renderMapInBothHands(g, i, h);
            } else {
                this.renderMapInOneHand(i, arm, h, itemStack);
            }
        } else if (itemStack.getItem() == Items.CROSSBOW) {
            int j;
            boolean bl2 = CrossbowItem.isCharged(itemStack);
            boolean bl3 = arm == Arm.RIGHT;
            int n = j = bl3 ? 1 : -1;
            if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
                this.applyHandOffset(arm, i);
                RenderSystem.translatef((float)j * -0.4785682f, -0.094387f, 0.05731531f);
                RenderSystem.rotatef(-11.935f, 1.0f, 0.0f, 0.0f);
                RenderSystem.rotatef((float)j * 65.3f, 0.0f, 1.0f, 0.0f);
                RenderSystem.rotatef((float)j * -9.785f, 0.0f, 0.0f, 1.0f);
                float k = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0f);
                float l = k / (float)CrossbowItem.getPullTime(itemStack);
                if (l > 1.0f) {
                    l = 1.0f;
                }
                if (l > 0.1f) {
                    float m = MathHelper.sin((k - 0.1f) * 1.3f);
                    float n2 = l - 0.1f;
                    float o = m * n2;
                    RenderSystem.translatef(o * 0.0f, o * 0.004f, o * 0.0f);
                }
                RenderSystem.translatef(l * 0.0f, l * 0.0f, l * 0.04f);
                RenderSystem.scalef(1.0f, 1.0f, 1.0f + l * 0.2f);
                RenderSystem.rotatef((float)j * 45.0f, 0.0f, -1.0f, 0.0f);
            } else {
                float k = -0.4f * MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
                float l = 0.2f * MathHelper.sin(MathHelper.sqrt(h) * ((float)Math.PI * 2));
                float m = -0.2f * MathHelper.sin(h * (float)Math.PI);
                RenderSystem.translatef((float)j * k, l, m);
                this.applyHandOffset(arm, i);
                this.method_3217(arm, h);
                if (bl2 && h < 0.001f) {
                    RenderSystem.translatef((float)j * -0.641864f, 0.0f, 0.0f);
                    RenderSystem.rotatef((float)j * 10.0f, 0.0f, 1.0f, 0.0f);
                }
            }
            this.renderItemFromSide(abstractClientPlayerEntity, itemStack, bl3 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND, !bl3);
        } else {
            boolean bl2;
            boolean bl3 = bl2 = arm == Arm.RIGHT;
            if (abstractClientPlayerEntity.isUsingItem() && abstractClientPlayerEntity.getItemUseTimeLeft() > 0 && abstractClientPlayerEntity.getActiveHand() == hand) {
                int p = bl2 ? 1 : -1;
                switch (itemStack.getUseAction()) {
                    case NONE: {
                        this.applyHandOffset(arm, i);
                        break;
                    }
                    case EAT: 
                    case DRINK: {
                        this.applyEatOrDrinkTransformation(f, arm, itemStack);
                        this.applyHandOffset(arm, i);
                        break;
                    }
                    case BLOCK: {
                        this.applyHandOffset(arm, i);
                        break;
                    }
                    case BOW: {
                        this.applyHandOffset(arm, i);
                        RenderSystem.translatef((float)p * -0.2785682f, 0.18344387f, 0.15731531f);
                        RenderSystem.rotatef(-13.935f, 1.0f, 0.0f, 0.0f);
                        RenderSystem.rotatef((float)p * 35.3f, 0.0f, 1.0f, 0.0f);
                        RenderSystem.rotatef((float)p * -9.785f, 0.0f, 0.0f, 1.0f);
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
                            RenderSystem.translatef(n * 0.0f, n * 0.004f, n * 0.0f);
                        }
                        RenderSystem.translatef(k * 0.0f, k * 0.0f, k * 0.04f);
                        RenderSystem.scalef(1.0f, 1.0f, 1.0f + k * 0.2f);
                        RenderSystem.rotatef((float)p * 45.0f, 0.0f, -1.0f, 0.0f);
                        break;
                    }
                    case SPEAR: {
                        this.applyHandOffset(arm, i);
                        RenderSystem.translatef((float)p * -0.5f, 0.7f, 0.1f);
                        RenderSystem.rotatef(-55.0f, 1.0f, 0.0f, 0.0f);
                        RenderSystem.rotatef((float)p * 35.3f, 0.0f, 1.0f, 0.0f);
                        RenderSystem.rotatef((float)p * -9.785f, 0.0f, 0.0f, 1.0f);
                        float q = (float)itemStack.getMaxUseTime() - ((float)this.client.player.getItemUseTimeLeft() - f + 1.0f);
                        float k = q / 10.0f;
                        if (k > 1.0f) {
                            k = 1.0f;
                        }
                        if (k > 0.1f) {
                            float l = MathHelper.sin((q - 0.1f) * 1.3f);
                            float m = k - 0.1f;
                            float n = l * m;
                            RenderSystem.translatef(n * 0.0f, n * 0.004f, n * 0.0f);
                        }
                        RenderSystem.translatef(0.0f, 0.0f, k * 0.2f);
                        RenderSystem.scalef(1.0f, 1.0f, 1.0f + k * 0.2f);
                        RenderSystem.rotatef((float)p * 45.0f, 0.0f, -1.0f, 0.0f);
                        break;
                    }
                }
            } else if (abstractClientPlayerEntity.isUsingRiptide()) {
                this.applyHandOffset(arm, i);
                int p = bl2 ? 1 : -1;
                RenderSystem.translatef((float)p * -0.4f, 0.8f, 0.3f);
                RenderSystem.rotatef((float)p * 65.0f, 0.0f, 1.0f, 0.0f);
                RenderSystem.rotatef((float)p * -85.0f, 0.0f, 0.0f, 1.0f);
            } else {
                float r = -0.4f * MathHelper.sin(MathHelper.sqrt(h) * (float)Math.PI);
                float q = 0.2f * MathHelper.sin(MathHelper.sqrt(h) * ((float)Math.PI * 2));
                float k = -0.2f * MathHelper.sin(h * (float)Math.PI);
                int s = bl2 ? 1 : -1;
                RenderSystem.translatef((float)s * r, q, k);
                this.applyHandOffset(arm, i);
                this.method_3217(arm, h);
            }
            this.renderItemFromSide(abstractClientPlayerEntity, itemStack, bl2 ? ModelTransformation.Type.FIRST_PERSON_RIGHT_HAND : ModelTransformation.Type.FIRST_PERSON_LEFT_HAND, !bl2);
        }
        RenderSystem.popMatrix();
    }

    public void renderOverlays(float f) {
        RenderSystem.disableAlphaTest();
        if (this.client.player.isInsideWall()) {
            BlockState blockState = this.client.world.getBlockState(new BlockPos(this.client.player));
            ClientPlayerEntity playerEntity = this.client.player;
            for (int i = 0; i < 8; ++i) {
                double d = playerEntity.x + (double)(((float)((i >> 0) % 2) - 0.5f) * playerEntity.getWidth() * 0.8f);
                double e = playerEntity.y + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f);
                double g = playerEntity.z + (double)(((float)((i >> 2) % 2) - 0.5f) * playerEntity.getWidth() * 0.8f);
                BlockPos blockPos = new BlockPos(d, e + (double)playerEntity.getStandingEyeHeight(), g);
                BlockState blockState2 = this.client.world.getBlockState(blockPos);
                if (!blockState2.canSuffocate(this.client.world, blockPos)) continue;
                blockState = blockState2;
            }
            if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
                this.renderBlock(this.client.getBlockRenderManager().getModels().getSprite(blockState));
            }
        }
        if (!this.client.player.isSpectator()) {
            if (this.client.player.isInFluid(FluidTags.WATER)) {
                this.renderWaterOverlay(f);
            }
            if (this.client.player.isOnFire()) {
                this.renderFireOverlay();
            }
        }
        RenderSystem.enableAlphaTest();
    }

    private void renderBlock(Sprite sprite) {
        this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        float f = 0.1f;
        RenderSystem.color4f(0.1f, 0.1f, 0.1f, 0.5f);
        RenderSystem.pushMatrix();
        float g = -1.0f;
        float h = 1.0f;
        float i = -1.0f;
        float j = 1.0f;
        float k = -0.5f;
        float l = sprite.getMinU();
        float m = sprite.getMaxU();
        float n = sprite.getMinV();
        float o = sprite.getMaxV();
        bufferBuilder.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder.vertex(-1.0, -1.0, -0.5).texture(m, o).next();
        bufferBuilder.vertex(1.0, -1.0, -0.5).texture(l, o).next();
        bufferBuilder.vertex(1.0, 1.0, -0.5).texture(l, n).next();
        bufferBuilder.vertex(-1.0, 1.0, -0.5).texture(m, n).next();
        tessellator.draw();
        RenderSystem.popMatrix();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    private void renderWaterOverlay(float f) {
        this.client.getTextureManager().bindTexture(UNDERWATER_TEX);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        float g = this.client.player.getBrightnessAtEyes();
        RenderSystem.color4f(g, g, g, 0.1f);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);
        RenderSystem.pushMatrix();
        float h = 4.0f;
        float i = -1.0f;
        float j = 1.0f;
        float k = -1.0f;
        float l = 1.0f;
        float m = -0.5f;
        float n = -this.client.player.yaw / 64.0f;
        float o = this.client.player.pitch / 64.0f;
        bufferBuilder.begin(7, VertexFormats.POSITION_UV);
        bufferBuilder.vertex(-1.0, -1.0, -0.5).texture(4.0f + n, 4.0f + o).next();
        bufferBuilder.vertex(1.0, -1.0, -0.5).texture(0.0f + n, 4.0f + o).next();
        bufferBuilder.vertex(1.0, 1.0, -0.5).texture(0.0f + n, 0.0f + o).next();
        bufferBuilder.vertex(-1.0, 1.0, -0.5).texture(4.0f + n, 0.0f + o).next();
        tessellator.draw();
        RenderSystem.popMatrix();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }

    private void renderFireOverlay() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 0.9f);
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);
        float f = 1.0f;
        for (int i = 0; i < 2; ++i) {
            RenderSystem.pushMatrix();
            Sprite sprite = this.client.getSpriteAtlas().getSprite(ModelLoader.FIRE_1);
            this.client.getTextureManager().bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
            float g = sprite.getMinU();
            float h = sprite.getMaxU();
            float j = sprite.getMinV();
            float k = sprite.getMaxV();
            float l = -0.5f;
            float m = 0.5f;
            float n = -0.5f;
            float o = 0.5f;
            float p = -0.5f;
            RenderSystem.translatef((float)(-(i * 2 - 1)) * 0.24f, -0.3f, 0.0f);
            RenderSystem.rotatef((float)(i * 2 - 1) * 10.0f, 0.0f, 1.0f, 0.0f);
            bufferBuilder.begin(7, VertexFormats.POSITION_UV);
            bufferBuilder.vertex(-0.5, -0.5, -0.5).texture(h, k).next();
            bufferBuilder.vertex(0.5, -0.5, -0.5).texture(g, k).next();
            bufferBuilder.vertex(0.5, 0.5, -0.5).texture(g, j).next();
            bufferBuilder.vertex(-0.5, 0.5, -0.5).texture(h, j).next();
            tessellator.draw();
            RenderSystem.popMatrix();
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
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

