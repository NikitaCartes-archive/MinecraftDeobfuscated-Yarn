/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.item;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ItemRenderer
implements SynchronousResourceReloadListener {
    public static final Identifier ENCHANTED_ITEM_GLINT = new Identifier("textures/misc/enchanted_item_glint.png");
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

    private void method_23182(BakedModel bakedModel, ItemStack itemStack, int i, int j, MatrixStack matrixStack, VertexConsumer vertexConsumer) {
        Random random = new Random();
        long l = 42L;
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            this.method_23180(matrixStack, vertexConsumer, bakedModel.getQuads(null, direction, random), itemStack, i, j);
        }
        random.setSeed(42L);
        this.method_23180(matrixStack, vertexConsumer, bakedModel.getQuads(null, null, random), itemStack, i, j);
    }

    public void method_23179(ItemStack itemStack, ModelTransformation.Type type, boolean bl, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, int j, BakedModel bakedModel) {
        boolean bl3;
        if (itemStack.isEmpty()) {
            return;
        }
        matrixStack.push();
        boolean bl2 = type == ModelTransformation.Type.GUI;
        boolean bl4 = bl3 = bl2 || type == ModelTransformation.Type.GROUND || type == ModelTransformation.Type.FIXED;
        if (itemStack.getItem() == Items.TRIDENT && bl3) {
            bakedModel = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident#inventory"));
        }
        bakedModel.getTransformation().getTransformation(type).method_23075(bl, matrixStack);
        matrixStack.translate(-0.5, -0.5, -0.5);
        if (bakedModel.isBuiltin() || itemStack.getItem() == Items.TRIDENT && !bl3) {
            BuiltinModelItemRenderer.INSTANCE.render(itemStack, matrixStack, vertexConsumerProvider, i, j);
        } else {
            RenderLayer renderLayer = RenderLayers.getItemLayer(itemStack);
            RenderLayer renderLayer2 = bl2 && Objects.equals(renderLayer, TexturedRenderLayers.getEntityTranslucent()) ? TexturedRenderLayers.getEntityTranslucentCull() : renderLayer;
            VertexConsumer vertexConsumer = ItemRenderer.getArmorVertexConsumer(vertexConsumerProvider, renderLayer2, true, itemStack.hasEnchantmentGlint());
            this.method_23182(bakedModel, itemStack, i, j, matrixStack, vertexConsumer);
        }
        matrixStack.pop();
    }

    public static VertexConsumer getArmorVertexConsumer(VertexConsumerProvider vertexConsumerProvider, RenderLayer renderLayer, boolean bl, boolean bl2) {
        if (bl2) {
            return VertexConsumers.dual(vertexConsumerProvider.getBuffer(bl ? RenderLayer.getGlint() : RenderLayer.getEntityGlint()), vertexConsumerProvider.getBuffer(renderLayer));
        }
        return vertexConsumerProvider.getBuffer(renderLayer);
    }

    private void method_23180(MatrixStack matrixStack, VertexConsumer vertexConsumer, List<BakedQuad> list, ItemStack itemStack, int i, int j) {
        boolean bl = !itemStack.isEmpty();
        MatrixStack.Entry entry = matrixStack.peek();
        for (BakedQuad bakedQuad : list) {
            int k = -1;
            if (bl && bakedQuad.hasColor()) {
                k = this.colorMap.getColorMultiplier(itemStack, bakedQuad.getColorIndex());
            }
            float f = (float)(k >> 16 & 0xFF) / 255.0f;
            float g = (float)(k >> 8 & 0xFF) / 255.0f;
            float h = (float)(k & 0xFF) / 255.0f;
            vertexConsumer.quad(entry, bakedQuad, f, g, h, i, j);
        }
    }

    public BakedModel getHeldItemModel(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        Item item = itemStack.getItem();
        BakedModel bakedModel = item == Items.TRIDENT ? this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident_in_hand#inventory")) : this.models.getModel(itemStack);
        if (!item.hasPropertyGetters()) {
            return bakedModel;
        }
        return this.getOverriddenModel(bakedModel, itemStack, world, livingEntity);
    }

    private BakedModel getOverriddenModel(BakedModel bakedModel, ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        BakedModel bakedModel2 = bakedModel.getItemPropertyOverrides().apply(bakedModel, itemStack, world, livingEntity);
        return bakedModel2 == null ? this.models.getModelManager().getMissingModel() : bakedModel2;
    }

    public void method_23178(ItemStack itemStack, ModelTransformation.Type type, int i, int j, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
        this.method_23177(null, itemStack, type, false, matrixStack, vertexConsumerProvider, null, i, j);
    }

    public void method_23177(@Nullable LivingEntity livingEntity, ItemStack itemStack, ModelTransformation.Type type, boolean bl, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, @Nullable World world, int i, int j) {
        if (itemStack.isEmpty()) {
            return;
        }
        BakedModel bakedModel = this.getHeldItemModel(itemStack, world, livingEntity);
        this.method_23179(itemStack, type, bl, matrixStack, vertexConsumerProvider, i, j, bakedModel);
    }

    public void renderGuiItemIcon(ItemStack itemStack, int i, int j) {
        this.renderGuiItemModel(itemStack, i, j, this.getHeldItemModel(itemStack, null, null));
    }

    protected void renderGuiItemModel(ItemStack itemStack, int i, int j, BakedModel bakedModel) {
        RenderSystem.pushMatrix();
        this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
        this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);
        RenderSystem.enableRescaleNormal();
        RenderSystem.enableAlphaTest();
        RenderSystem.defaultAlphaFunc();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.translatef(i, j, 100.0f + this.zOffset);
        RenderSystem.translatef(8.0f, 8.0f, 0.0f);
        RenderSystem.scalef(1.0f, -1.0f, 1.0f);
        RenderSystem.scalef(16.0f, 16.0f, 16.0f);
        MatrixStack matrixStack = new MatrixStack();
        VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
        this.method_23179(itemStack, ModelTransformation.Type.GUI, false, matrixStack, immediate, 0xF000F0, OverlayTexture.DEFAULT_UV, bakedModel);
        immediate.draw();
        RenderSystem.enableDepthTest();
        RenderSystem.disableAlphaTest();
        RenderSystem.disableRescaleNormal();
        RenderSystem.popMatrix();
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
            this.renderGuiItemModel(itemStack, i, j, this.getHeldItemModel(itemStack, null, livingEntity));
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
        MatrixStack matrixStack = new MatrixStack();
        if (itemStack.getCount() != 1 || string != null) {
            String string2 = string == null ? String.valueOf(itemStack.getCount()) : string;
            matrixStack.translate(0.0, 0.0, this.zOffset + 200.0f);
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            textRenderer.draw(string2, i + 19 - 2 - textRenderer.getStringWidth(string2), j + 6 + 3, 0xFFFFFF, true, matrixStack.peek().getModel(), immediate, false, 0, 0xF000F0);
            immediate.draw();
        }
        if (itemStack.isDamaged()) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableAlphaTest();
            RenderSystem.disableBlend();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
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
            RenderSystem.enableDepthTest();
        }
        float f = m = (clientPlayerEntity = MinecraftClient.getInstance().player) == null ? 0.0f : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(itemStack.getItem(), MinecraftClient.getInstance().getTickDelta());
        if (m > 0.0f) {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            Tessellator tessellator2 = Tessellator.getInstance();
            BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
            this.renderGuiQuad(bufferBuilder2, i, j + MathHelper.floor(16.0f * (1.0f - m)), 16, MathHelper.ceil(16.0f * m), 255, 255, 255, 127);
            RenderSystem.enableTexture();
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

