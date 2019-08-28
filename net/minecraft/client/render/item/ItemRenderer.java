/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.item;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.ItemDynamicRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.render.model.json.Transformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemRenderer
implements SynchronousResourceReloadListener {
    public static final Identifier ENCHANTMENT_GLINT_TEX = new Identifier("textures/misc/enchanted_item_glint.png");
    private static final Set<Item> WITHOUT_MODELS = Sets.newHashSet(Items.AIR);
    public float zOffset;
    private final ItemModels models;
    private final TextureManager textureManager;
    private final ItemColors colorMap;

    public ItemRenderer(TextureManager textureManager, BakedModelManager bakedModelManager, ItemColors itemColors) {
        this.textureManager = textureManager;
        this.models = new ItemModels(bakedModelManager);
        for (Item item : Registry.ITEM) {
            if (WITHOUT_MODELS.contains(item)) continue;
            this.models.putModel(item, new ModelIdentifier(Registry.ITEM.getId(item), "inventory"));
        }
        this.colorMap = itemColors;
    }

    public ItemModels getModels() {
        return this.models;
    }

    private void renderItemModel(BakedModel bakedModel, ItemStack itemStack) {
        this.renderModel(bakedModel, -1, itemStack);
    }

    private void renderModelWithTint(BakedModel bakedModel, int i) {
        this.renderModel(bakedModel, i, ItemStack.EMPTY);
    }

    private void renderModel(BakedModel bakedModel, int i, ItemStack itemStack) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_UV_NORMAL);
        Random random = new Random();
        long l = 42L;
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            this.renderQuads(bufferBuilder, bakedModel.getQuads(null, direction, random), i, itemStack);
        }
        random.setSeed(42L);
        this.renderQuads(bufferBuilder, bakedModel.getQuads(null, null, random), i, itemStack);
        tessellator.draw();
    }

    public void renderItemAndGlow(ItemStack itemStack, BakedModel bakedModel) {
        if (itemStack.isEmpty()) {
            return;
        }
        RenderSystem.pushMatrix();
        RenderSystem.translatef(-0.5f, -0.5f, -0.5f);
        if (bakedModel.isBuiltin()) {
            RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.enableRescaleNormal();
            ItemDynamicRenderer.INSTANCE.render(itemStack);
        } else {
            this.renderItemModel(bakedModel, itemStack);
            if (itemStack.hasEnchantmentGlint()) {
                ItemRenderer.renderGlint(this.textureManager, () -> this.renderModelWithTint(bakedModel, -8372020), 8);
            }
        }
        RenderSystem.popMatrix();
    }

    public static void renderGlint(TextureManager textureManager, Runnable runnable, int i) {
        RenderSystem.depthMask(false);
        RenderSystem.depthFunc(514);
        RenderSystem.disableLighting();
        RenderSystem.blendFunc(GlStateManager.class_4535.SRC_COLOR, GlStateManager.class_4534.ONE);
        textureManager.bindTexture(ENCHANTMENT_GLINT_TEX);
        RenderSystem.matrixMode(5890);
        RenderSystem.pushMatrix();
        RenderSystem.scalef(i, i, i);
        float f = (float)(SystemUtil.getMeasuringTimeMs() % 3000L) / 3000.0f / (float)i;
        RenderSystem.translatef(f, 0.0f, 0.0f);
        RenderSystem.rotatef(-50.0f, 0.0f, 0.0f, 1.0f);
        runnable.run();
        RenderSystem.popMatrix();
        RenderSystem.pushMatrix();
        RenderSystem.scalef(i, i, i);
        float g = (float)(SystemUtil.getMeasuringTimeMs() % 4873L) / 4873.0f / (float)i;
        RenderSystem.translatef(-g, 0.0f, 0.0f);
        RenderSystem.rotatef(10.0f, 0.0f, 0.0f, 1.0f);
        runnable.run();
        RenderSystem.popMatrix();
        RenderSystem.matrixMode(5888);
        RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
        RenderSystem.enableLighting();
        RenderSystem.depthFunc(515);
        RenderSystem.depthMask(true);
        textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
    }

    private void postNormalQuad(BufferBuilder bufferBuilder, BakedQuad bakedQuad) {
        Vec3i vec3i = bakedQuad.getFace().getVector();
        bufferBuilder.postNormal(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    private void renderQuad(BufferBuilder bufferBuilder, BakedQuad bakedQuad, int i) {
        bufferBuilder.putVertexData(bakedQuad.getVertexData());
        bufferBuilder.setQuadColor(i);
        this.postNormalQuad(bufferBuilder, bakedQuad);
    }

    private void renderQuads(BufferBuilder bufferBuilder, List<BakedQuad> list, int i, ItemStack itemStack) {
        boolean bl = i == -1 && !itemStack.isEmpty();
        int k = list.size();
        for (int j = 0; j < k; ++j) {
            BakedQuad bakedQuad = list.get(j);
            int l = i;
            if (bl && bakedQuad.hasColor()) {
                l = this.colorMap.getColorMultiplier(itemStack, bakedQuad.getColorIndex());
                l |= 0xFF000000;
            }
            this.renderQuad(bufferBuilder, bakedQuad, l);
        }
    }

    public boolean hasDepthInGui(ItemStack itemStack) {
        BakedModel bakedModel = this.models.getModel(itemStack);
        if (bakedModel == null) {
            return false;
        }
        return bakedModel.hasDepthInGui();
    }

    public void renderItem(ItemStack itemStack, ModelTransformation.Type type) {
        if (itemStack.isEmpty()) {
            return;
        }
        BakedModel bakedModel = this.getModel(itemStack);
        this.renderItem(itemStack, bakedModel, type, false);
    }

    public BakedModel getModel(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        BakedModel bakedModel = this.models.getModel(itemStack);
        Item item = itemStack.getItem();
        if (!item.hasPropertyGetters()) {
            return bakedModel;
        }
        return this.getOverriddenModel(bakedModel, itemStack, world, livingEntity);
    }

    public BakedModel getHeldItemModel(ItemStack itemStack, World world, LivingEntity livingEntity) {
        Item item = itemStack.getItem();
        BakedModel bakedModel = item == Items.TRIDENT ? this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident_in_hand#inventory")) : this.models.getModel(itemStack);
        if (!item.hasPropertyGetters()) {
            return bakedModel;
        }
        return this.getOverriddenModel(bakedModel, itemStack, world, livingEntity);
    }

    public BakedModel getModel(ItemStack itemStack) {
        return this.getModel(itemStack, null, null);
    }

    private BakedModel getOverriddenModel(BakedModel bakedModel, ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        BakedModel bakedModel2 = bakedModel.getItemPropertyOverrides().apply(bakedModel, itemStack, world, livingEntity);
        return bakedModel2 == null ? this.models.getModelManager().getMissingModel() : bakedModel2;
    }

    public void renderHeldItem(ItemStack itemStack, LivingEntity livingEntity, ModelTransformation.Type type, boolean bl) {
        if (itemStack.isEmpty() || livingEntity == null) {
            return;
        }
        BakedModel bakedModel = this.getHeldItemModel(itemStack, livingEntity.world, livingEntity);
        this.renderItem(itemStack, bakedModel, type, bl);
    }

    protected void renderItem(ItemStack itemStack, BakedModel bakedModel, ModelTransformation.Type type, boolean bl) {
        if (itemStack.isEmpty()) {
            return;
        }
        this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.enableRescaleNormal();
        RenderSystem.alphaFunc(516, 0.1f);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA, GlStateManager.class_4535.ONE, GlStateManager.class_4534.ZERO);
        RenderSystem.pushMatrix();
        ModelTransformation modelTransformation = bakedModel.getTransformation();
        ModelTransformation.applyGl(modelTransformation.getTransformation(type), bl);
        if (this.areFacesFlippedBy(modelTransformation.getTransformation(type))) {
            RenderSystem.cullFace(GlStateManager.FaceSides.FRONT);
        }
        this.renderItemAndGlow(itemStack, bakedModel);
        RenderSystem.cullFace(GlStateManager.FaceSides.BACK);
        RenderSystem.popMatrix();
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableBlend();
        this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
    }

    private boolean areFacesFlippedBy(Transformation transformation) {
        return transformation.scale.getX() < 0.0f ^ transformation.scale.getY() < 0.0f ^ transformation.scale.getZ() < 0.0f;
    }

    public void renderGuiItemIcon(ItemStack itemStack, int i, int j) {
        this.renderGuiItemModel(itemStack, i, j, this.getModel(itemStack));
    }

    protected void renderGuiItemModel(ItemStack itemStack, int i, int j, BakedModel bakedModel) {
        RenderSystem.pushMatrix();
        this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(516, 0.1f);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        this.prepareGuiItemRender(i, j, bakedModel.hasDepthInGui());
        bakedModel.getTransformation().applyGl(ModelTransformation.Type.GUI);
        this.renderItemAndGlow(itemStack, bakedModel);
        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.disableLighting();
        RenderSystem.popMatrix();
        this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
    }

    private void prepareGuiItemRender(int i, int j, boolean bl) {
        RenderSystem.translatef(i, j, 100.0f + this.zOffset);
        RenderSystem.translatef(8.0f, 8.0f, 0.0f);
        RenderSystem.scalef(1.0f, -1.0f, 1.0f);
        RenderSystem.scalef(16.0f, 16.0f, 16.0f);
        if (bl) {
            RenderSystem.enableLighting();
        } else {
            RenderSystem.disableLighting();
        }
    }

    public void renderGuiItem(ItemStack itemStack, int i, int j) {
        this.renderGuiItem(MinecraftClient.getInstance().player, itemStack, i, j);
    }

    public void renderGuiItem(@Nullable LivingEntity livingEntity, ItemStack itemStack, int i, int j) {
        if (itemStack.isEmpty()) {
            return;
        }
        this.zOffset += 50.0f;
        try {
            this.renderGuiItemModel(itemStack, i, j, this.getModel(itemStack, null, livingEntity));
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Rendering item");
            CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
            crashReportSection.add("Item Type", () -> String.valueOf(itemStack.getItem()));
            crashReportSection.add("Item Damage", () -> String.valueOf(itemStack.getDamage()));
            crashReportSection.add("Item NBT", () -> String.valueOf(itemStack.getTag()));
            crashReportSection.add("Item Foil", () -> String.valueOf(itemStack.hasEnchantmentGlint()));
            throw new CrashException(crashReport);
        }
        this.zOffset -= 50.0f;
    }

    public void renderGuiItemOverlay(TextRenderer textRenderer, ItemStack itemStack, int i, int j) {
        this.renderGuiItemOverlay(textRenderer, itemStack, i, j, null);
    }

    public void renderGuiItemOverlay(TextRenderer textRenderer, ItemStack itemStack, int i, int j, @Nullable String string) {
        ClientPlayerEntity clientPlayerEntity;
        float m;
        if (itemStack.isEmpty()) {
            return;
        }
        if (itemStack.getCount() != 1 || string != null) {
            String string2 = string == null ? String.valueOf(itemStack.getCount()) : string;
            RenderSystem.disableLighting();
            RenderSystem.disableDepthTest();
            RenderSystem.disableBlend();
            textRenderer.drawWithShadow(string2, i + 19 - 2 - textRenderer.getStringWidth(string2), j + 6 + 3, 0xFFFFFF);
            RenderSystem.enableBlend();
            RenderSystem.enableLighting();
            RenderSystem.enableDepthTest();
        }
        if (itemStack.isDamaged()) {
            RenderSystem.disableLighting();
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
            float f = itemStack.getDamage();
            float g = itemStack.getMaxDamage();
            float h = Math.max(0.0f, (g - f) / g);
            int k = Math.round(13.0f - f * 13.0f / g);
            int l = MathHelper.hsvToRgb(h / 3.0f, 1.0f, 1.0f);
            this.renderGuiQuad(bufferBuilder, i + 2, j + 13, 13, 2, 0, 0, 0, 255);
            this.renderGuiQuad(bufferBuilder, i + 2, j + 13, k, 1, l >> 16 & 0xFF, l >> 8 & 0xFF, l & 0xFF, 255);
            RenderSystem.enableBlend();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableTexture();
            RenderSystem.enableLighting();
            RenderSystem.enableDepthTest();
        }
        float f = m = (clientPlayerEntity = MinecraftClient.getInstance().player) == null ? 0.0f : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(itemStack.getItem(), MinecraftClient.getInstance().getTickDelta());
        if (m > 0.0f) {
            RenderSystem.disableLighting();
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            Tessellator tessellator2 = Tessellator.getInstance();
            BufferBuilder bufferBuilder2 = tessellator2.getBufferBuilder();
            this.renderGuiQuad(bufferBuilder2, i, j + MathHelper.floor(16.0f * (1.0f - m)), 16, MathHelper.ceil(16.0f * m), 255, 255, 255, 127);
            RenderSystem.enableTexture();
            RenderSystem.enableLighting();
            RenderSystem.enableDepthTest();
        }
    }

    private void renderGuiQuad(BufferBuilder bufferBuilder, int i, int j, int k, int l, int m, int n, int o, int p) {
        bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(i + 0, j + 0, 0.0).color(m, n, o, p).next();
        bufferBuilder.vertex(i + 0, j + l, 0.0).color(m, n, o, p).next();
        bufferBuilder.vertex(i + k, j + l, 0.0).color(m, n, o, p).next();
        bufferBuilder.vertex(i + k, j + 0, 0.0).color(m, n, o, p).next();
        Tessellator.getInstance().draw();
    }

    @Override
    public void apply(ResourceManager resourceManager) {
        this.models.reloadModels();
    }
}

