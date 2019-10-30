/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class ItemEntityRenderer
extends EntityRenderer<ItemEntity> {
    private final ItemRenderer itemRenderer;
    private final Random random = new Random();

    public ItemEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, ItemRenderer itemRenderer) {
        super(entityRenderDispatcher);
        this.itemRenderer = itemRenderer;
        this.field_4673 = 0.15f;
        this.field_4672 = 0.75f;
    }

    private int getRenderedAmount(ItemStack itemStack) {
        int i = 1;
        if (itemStack.getCount() > 48) {
            i = 5;
        } else if (itemStack.getCount() > 32) {
            i = 4;
        } else if (itemStack.getCount() > 16) {
            i = 3;
        } else if (itemStack.getCount() > 1) {
            i = 2;
        }
        return i;
    }

    public void method_3996(ItemEntity itemEntity, double d, double e, double f, float g, float h, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        float t;
        float s;
        matrixStack.push();
        ItemStack itemStack = itemEntity.getStack();
        int i = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getDamage();
        this.random.setSeed(i);
        BakedModel bakedModel = this.itemRenderer.getHeldItemModel(itemStack, itemEntity.world, null);
        boolean bl = bakedModel.hasDepthInGui();
        int j = this.getRenderedAmount(itemStack);
        float k = 0.25f;
        float l = MathHelper.sin(((float)itemEntity.getAge() + h) / 10.0f + itemEntity.hoverHeight) * 0.1f + 0.1f;
        float m = bakedModel.getTransformation().getTransformation((ModelTransformation.Type)ModelTransformation.Type.GROUND).scale.getY();
        matrixStack.translate(0.0, l + 0.25f * m, 0.0);
        float n = ((float)itemEntity.getAge() + h) / 20.0f + itemEntity.hoverHeight;
        matrixStack.multiply(Vector3f.POSITIVE_Y.method_23626(n));
        float o = bakedModel.getTransformation().ground.scale.getX();
        float p = bakedModel.getTransformation().ground.scale.getY();
        float q = bakedModel.getTransformation().ground.scale.getZ();
        if (!bl) {
            float r = -0.0f * (float)(j - 1) * 0.5f * o;
            s = -0.0f * (float)(j - 1) * 0.5f * p;
            t = -0.09375f * (float)(j - 1) * 0.5f * q;
            matrixStack.translate(r, s, t);
        }
        for (int u = 0; u < j; ++u) {
            matrixStack.push();
            if (u > 0) {
                if (bl) {
                    s = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float v = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    matrixStack.translate(s, t, v);
                } else {
                    s = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    matrixStack.translate(s, t, 0.0);
                }
            }
            this.itemRenderer.method_23179(itemStack, ModelTransformation.Type.GROUND, false, matrixStack, vertexConsumerProvider, itemEntity.getLightmapCoordinates(), OverlayTexture.DEFAULT_UV, bakedModel);
            matrixStack.pop();
            if (bl) continue;
            matrixStack.translate(0.0f * o, 0.0f * p, 0.09375f * q);
        }
        matrixStack.pop();
        super.render(itemEntity, d, e, f, g, h, matrixStack, vertexConsumerProvider);
    }

    public Identifier method_3999(ItemEntity itemEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

