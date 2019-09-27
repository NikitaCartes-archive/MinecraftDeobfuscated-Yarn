/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
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

    public void method_3996(ItemEntity itemEntity, double d, double e, double f, float g, float h, class_4587 arg, class_4597 arg2) {
        float t;
        float s;
        arg.method_22903();
        ItemStack itemStack = itemEntity.getStack();
        int i = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getDamage();
        this.random.setSeed(i);
        BakedModel bakedModel = this.itemRenderer.getHeldItemModel(itemStack, itemEntity.world, null);
        boolean bl = bakedModel.hasDepthInGui();
        int j = this.getRenderedAmount(itemStack);
        float k = 0.25f;
        float l = MathHelper.sin(((float)itemEntity.getAge() + h) / 10.0f + itemEntity.hoverHeight) * 0.1f + 0.1f;
        float m = bakedModel.getTransformation().getTransformation((ModelTransformation.Type)ModelTransformation.Type.GROUND).scale.getY();
        arg.method_22904(0.0, l + 0.25f * m, 0.0);
        float n = ((float)itemEntity.getAge() + h) / 20.0f + itemEntity.hoverHeight;
        arg.method_22907(Vector3f.field_20705.method_23214(n, false));
        float o = bakedModel.getTransformation().ground.scale.getX();
        float p = bakedModel.getTransformation().ground.scale.getY();
        float q = bakedModel.getTransformation().ground.scale.getZ();
        if (!bl) {
            float r = -0.0f * (float)(j - 1) * 0.5f * o;
            s = -0.0f * (float)(j - 1) * 0.5f * p;
            t = -0.09375f * (float)(j - 1) * 0.5f * q;
            arg.method_22904(r, s, t);
        }
        for (int u = 0; u < j; ++u) {
            arg.method_22903();
            if (u > 0) {
                if (bl) {
                    s = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float v = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    arg.method_22904(s, t, v);
                } else {
                    s = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    t = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                    arg.method_22904(s, t, 0.0);
                }
            }
            this.itemRenderer.method_23179(itemStack, ModelTransformation.Type.GROUND, false, arg, arg2, itemEntity.getLightmapCoordinates(), bakedModel);
            arg.method_22909();
            if (bl) continue;
            arg.method_22904(0.0f * o, 0.0f * p, 0.09375f * q);
        }
        arg.method_22909();
        super.render(itemEntity, d, e, f, g, h, arg, arg2);
    }

    public Identifier method_3999(ItemEntity itemEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

