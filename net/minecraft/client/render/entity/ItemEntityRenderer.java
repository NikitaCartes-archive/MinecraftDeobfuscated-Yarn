/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.entity;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4493;
import net.minecraft.client.render.GuiLighting;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
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

    private int method_3997(ItemEntity itemEntity, double d, double e, double f, float g, BakedModel bakedModel) {
        ItemStack itemStack = itemEntity.getStack();
        Item item = itemStack.getItem();
        if (item == null) {
            return 0;
        }
        boolean bl = bakedModel.hasDepthInGui();
        int i = this.getRenderedAmount(itemStack);
        float h = 0.25f;
        float j = MathHelper.sin(((float)itemEntity.getAge() + g) / 10.0f + itemEntity.hoverHeight) * 0.1f + 0.1f;
        float k = bakedModel.getTransformation().getTransformation((ModelTransformation.Type)ModelTransformation.Type.GROUND).scale.getY();
        RenderSystem.translatef((float)d, (float)e + j + 0.25f * k, (float)f);
        if (bl || this.renderManager.gameOptions != null) {
            float l = (((float)itemEntity.getAge() + g) / 20.0f + itemEntity.hoverHeight) * 57.295776f;
            RenderSystem.rotatef(l, 0.0f, 1.0f, 0.0f);
        }
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        return i;
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

    public void method_3996(ItemEntity itemEntity, double d, double e, double f, float g, float h) {
        float p;
        float o;
        ItemStack itemStack = itemEntity.getStack();
        int i = itemStack.isEmpty() ? 187 : Item.getRawId(itemStack.getItem()) + itemStack.getDamage();
        this.random.setSeed(i);
        boolean bl = false;
        if (this.bindEntityTexture(itemEntity)) {
            this.renderManager.textureManager.getTexture(this.method_3999(itemEntity)).pushFilter(false, false);
            bl = true;
        }
        RenderSystem.enableRescaleNormal();
        RenderSystem.alphaFunc(516, 0.1f);
        RenderSystem.enableBlend();
        GuiLighting.enable();
        RenderSystem.blendFuncSeparate(class_4493.class_4535.SRC_ALPHA, class_4493.class_4534.ONE_MINUS_SRC_ALPHA, class_4493.class_4535.ONE, class_4493.class_4534.ZERO);
        RenderSystem.pushMatrix();
        BakedModel bakedModel = this.itemRenderer.getModel(itemStack, itemEntity.world, null);
        int j = this.method_3997(itemEntity, d, e, f, h, bakedModel);
        float k = bakedModel.getTransformation().ground.scale.getX();
        float l = bakedModel.getTransformation().ground.scale.getY();
        float m = bakedModel.getTransformation().ground.scale.getZ();
        boolean bl2 = bakedModel.hasDepthInGui();
        if (!bl2) {
            float n = -0.0f * (float)(j - 1) * 0.5f * k;
            o = -0.0f * (float)(j - 1) * 0.5f * l;
            p = -0.09375f * (float)(j - 1) * 0.5f * m;
            RenderSystem.translatef(n, o, p);
        }
        if (this.renderOutlines) {
            RenderSystem.enableColorMaterial();
            RenderSystem.setupSolidRenderingTextureCombine(this.getOutlineColor(itemEntity));
        }
        for (int q = 0; q < j; ++q) {
            if (bl2) {
                RenderSystem.pushMatrix();
                if (q > 0) {
                    o = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    p = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    float r = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f;
                    RenderSystem.translatef(o, p, r);
                }
                bakedModel.getTransformation().applyGl(ModelTransformation.Type.GROUND);
                this.itemRenderer.renderItemAndGlow(itemStack, bakedModel);
                RenderSystem.popMatrix();
                continue;
            }
            RenderSystem.pushMatrix();
            if (q > 0) {
                o = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                p = (this.random.nextFloat() * 2.0f - 1.0f) * 0.15f * 0.5f;
                RenderSystem.translatef(o, p, 0.0f);
            }
            bakedModel.getTransformation().applyGl(ModelTransformation.Type.GROUND);
            this.itemRenderer.renderItemAndGlow(itemStack, bakedModel);
            RenderSystem.popMatrix();
            RenderSystem.translatef(0.0f * k, 0.0f * l, 0.09375f * m);
        }
        if (this.renderOutlines) {
            RenderSystem.tearDownSolidRenderingTextureCombine();
            RenderSystem.disableColorMaterial();
        }
        RenderSystem.popMatrix();
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
        this.bindEntityTexture(itemEntity);
        if (bl) {
            this.renderManager.textureManager.getTexture(this.method_3999(itemEntity)).popFilter();
        }
        super.render(itemEntity, d, e, f, g, h);
    }

    protected Identifier method_3999(ItemEntity itemEntity) {
        return SpriteAtlasTexture.BLOCK_ATLAS_TEX;
    }
}

