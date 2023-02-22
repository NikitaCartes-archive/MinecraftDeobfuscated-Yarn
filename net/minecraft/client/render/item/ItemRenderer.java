/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render.item;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TransparentBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.item.BuiltinModelItemRenderer;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

@Environment(value=EnvType.CLIENT)
public class ItemRenderer
implements SynchronousResourceReloader {
    public static final Identifier ENTITY_ENCHANTMENT_GLINT = new Identifier("textures/misc/enchanted_glint_entity.png");
    public static final Identifier ITEM_ENCHANTMENT_GLINT = new Identifier("textures/misc/enchanted_glint_item.png");
    private static final Set<Item> WITHOUT_MODELS = Sets.newHashSet(Items.AIR);
    private static final int field_32937 = 8;
    private static final int field_32938 = 8;
    public static final int field_32934 = 200;
    public static final float COMPASS_WITH_GLINT_GUI_MODEL_MULTIPLIER = 0.5f;
    public static final float COMPASS_WITH_GLINT_FIRST_PERSON_MODEL_MULTIPLIER = 0.75f;
    public static final float field_41120 = 0.0078125f;
    private static final ModelIdentifier TRIDENT = ModelIdentifier.ofVanilla("trident", "inventory");
    public static final ModelIdentifier TRIDENT_IN_HAND = ModelIdentifier.ofVanilla("trident_in_hand", "inventory");
    private static final ModelIdentifier SPYGLASS = ModelIdentifier.ofVanilla("spyglass", "inventory");
    public static final ModelIdentifier SPYGLASS_IN_HAND = ModelIdentifier.ofVanilla("spyglass_in_hand", "inventory");
    private final MinecraftClient client;
    private final ItemModels models;
    private final TextureManager textureManager;
    private final ItemColors colors;
    private final BuiltinModelItemRenderer builtinModelItemRenderer;

    public ItemRenderer(MinecraftClient client, TextureManager manager, BakedModelManager bakery, ItemColors colors, BuiltinModelItemRenderer builtinModelItemRenderer) {
        this.client = client;
        this.textureManager = manager;
        this.models = new ItemModels(bakery);
        this.builtinModelItemRenderer = builtinModelItemRenderer;
        for (Item item : Registries.ITEM) {
            if (WITHOUT_MODELS.contains(item)) continue;
            this.models.putModel(item, new ModelIdentifier(Registries.ITEM.getId(item), "inventory"));
        }
        this.colors = colors;
    }

    public ItemModels getModels() {
        return this.models;
    }

    private void renderBakedItemModel(BakedModel model, ItemStack stack, int light, int overlay, MatrixStack matrices, VertexConsumer vertices) {
        Random random = Random.create();
        long l = 42L;
        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            this.renderBakedItemQuads(matrices, vertices, model.getQuads(null, direction, random), stack, light, overlay);
        }
        random.setSeed(42L);
        this.renderBakedItemQuads(matrices, vertices, model.getQuads(null, null, random), stack, light, overlay);
    }

    public void renderItem(ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, BakedModel model) {
        boolean bl;
        if (stack.isEmpty()) {
            return;
        }
        matrices.push();
        boolean bl2 = bl = renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND || renderMode == ModelTransformationMode.FIXED;
        if (bl) {
            if (stack.isOf(Items.TRIDENT)) {
                model = this.models.getModelManager().getModel(TRIDENT);
            } else if (stack.isOf(Items.SPYGLASS)) {
                model = this.models.getModelManager().getModel(SPYGLASS);
            }
        }
        model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
        matrices.translate(-0.5f, -0.5f, -0.5f);
        if (model.isBuiltin() || stack.isOf(Items.TRIDENT) && !bl) {
            this.builtinModelItemRenderer.render(stack, renderMode, matrices, vertexConsumers, light, overlay);
        } else {
            VertexConsumer vertexConsumer;
            Block block;
            boolean bl22 = renderMode != ModelTransformationMode.GUI && !renderMode.isFirstPerson() && stack.getItem() instanceof BlockItem ? !((block = ((BlockItem)stack.getItem()).getBlock()) instanceof TransparentBlock) && !(block instanceof StainedGlassPaneBlock) : true;
            RenderLayer renderLayer = RenderLayers.getItemLayer(stack, bl22);
            if (stack.isIn(ItemTags.COMPASSES) && stack.hasGlint()) {
                matrices.push();
                MatrixStack.Entry entry = matrices.peek();
                if (renderMode == ModelTransformationMode.GUI) {
                    MatrixUtil.scale(entry.getPositionMatrix(), 0.5f);
                } else if (renderMode.isFirstPerson()) {
                    MatrixUtil.scale(entry.getPositionMatrix(), 0.75f);
                }
                vertexConsumer = bl22 ? ItemRenderer.getDirectCompassGlintConsumer(vertexConsumers, renderLayer, entry) : ItemRenderer.getCompassGlintConsumer(vertexConsumers, renderLayer, entry);
                matrices.pop();
            } else {
                vertexConsumer = bl22 ? ItemRenderer.getDirectItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint()) : ItemRenderer.getItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
            }
            this.renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);
        }
        matrices.pop();
    }

    public static VertexConsumer getArmorGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean solid, boolean glint) {
        if (glint) {
            return VertexConsumers.union(provider.getBuffer(solid ? RenderLayer.getArmorGlint() : RenderLayer.getArmorEntityGlint()), provider.getBuffer(layer));
        }
        return provider.getBuffer(layer);
    }

    public static VertexConsumer getCompassGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry) {
        return VertexConsumers.union((VertexConsumer)new OverlayVertexConsumer(provider.getBuffer(RenderLayer.getGlint()), entry.getPositionMatrix(), entry.getNormalMatrix(), 0.0078125f), provider.getBuffer(layer));
    }

    public static VertexConsumer getDirectCompassGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry) {
        return VertexConsumers.union((VertexConsumer)new OverlayVertexConsumer(provider.getBuffer(RenderLayer.getDirectGlint()), entry.getPositionMatrix(), entry.getNormalMatrix(), 0.0078125f), provider.getBuffer(layer));
    }

    public static VertexConsumer getItemGlintConsumer(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid, boolean glint) {
        if (glint) {
            if (MinecraftClient.isFabulousGraphicsOrBetter() && layer == TexturedRenderLayers.getItemEntityTranslucentCull()) {
                return VertexConsumers.union(vertexConsumers.getBuffer(RenderLayer.getGlintTranslucent()), vertexConsumers.getBuffer(layer));
            }
            return VertexConsumers.union(vertexConsumers.getBuffer(solid ? RenderLayer.getGlint() : RenderLayer.getEntityGlint()), vertexConsumers.getBuffer(layer));
        }
        return vertexConsumers.getBuffer(layer);
    }

    public static VertexConsumer getDirectItemGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean solid, boolean glint) {
        if (glint) {
            return VertexConsumers.union(provider.getBuffer(solid ? RenderLayer.getDirectGlint() : RenderLayer.getDirectEntityGlint()), provider.getBuffer(layer));
        }
        return provider.getBuffer(layer);
    }

    private void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay) {
        boolean bl = !stack.isEmpty();
        MatrixStack.Entry entry = matrices.peek();
        for (BakedQuad bakedQuad : quads) {
            int i = -1;
            if (bl && bakedQuad.hasColor()) {
                i = this.colors.getColor(stack, bakedQuad.getColorIndex());
            }
            float f = (float)(i >> 16 & 0xFF) / 255.0f;
            float g = (float)(i >> 8 & 0xFF) / 255.0f;
            float h = (float)(i & 0xFF) / 255.0f;
            vertices.quad(entry, bakedQuad, f, g, h, light, overlay);
        }
    }

    public BakedModel getModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed) {
        BakedModel bakedModel = stack.isOf(Items.TRIDENT) ? this.models.getModelManager().getModel(TRIDENT_IN_HAND) : (stack.isOf(Items.SPYGLASS) ? this.models.getModelManager().getModel(SPYGLASS_IN_HAND) : this.models.getModel(stack));
        ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld)world : null;
        BakedModel bakedModel2 = bakedModel.getOverrides().apply(bakedModel, stack, clientWorld, entity, seed);
        return bakedModel2 == null ? this.models.getModelManager().getMissingModel() : bakedModel2;
    }

    public void renderItem(ItemStack stack, ModelTransformationMode transformationType, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int seed) {
        this.renderItem(null, stack, transformationType, false, matrices, vertexConsumers, world, light, overlay, seed);
    }

    public void renderItem(@Nullable LivingEntity entity, ItemStack item, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, @Nullable World world, int light, int overlay, int seed) {
        if (item.isEmpty()) {
            return;
        }
        BakedModel bakedModel = this.getModel(item, world, entity, seed);
        this.renderItem(item, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, bakedModel);
    }

    public void renderGuiItemIcon(MatrixStack matrices, ItemStack stack, int x, int y) {
        this.renderGuiItemModel(matrices, stack, x, y, this.getModel(stack, null, null, 0));
    }

    protected void renderGuiItemModel(MatrixStack matrices, ItemStack stack, int x, int y, BakedModel model) {
        boolean bl;
        matrices.push();
        matrices.translate(x, y, 100.0f);
        matrices.translate(8.0f, 8.0f, 0.0f);
        matrices.multiplyPositionMatrix(new Matrix4f().scaling(1.0f, -1.0f, 1.0f));
        matrices.scale(16.0f, 16.0f, 16.0f);
        VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
        boolean bl2 = bl = !model.isSideLit();
        if (bl) {
            DiffuseLighting.disableGuiDepthLighting();
        }
        this.renderItem(stack, ModelTransformationMode.GUI, false, matrices, immediate, 0xF000F0, OverlayTexture.DEFAULT_UV, model);
        immediate.draw();
        RenderSystem.enableDepthTest();
        if (bl) {
            DiffuseLighting.enableGuiDepthLighting();
        }
        matrices.pop();
    }

    /**
     * Renders an item in a GUI with the player as the attached entity
     * for calculating model overrides.
     */
    public void renderInGuiWithOverrides(MatrixStack matrices, ItemStack stack, int x, int y) {
        this.innerRenderInGui(matrices, this.client.player, this.client.world, stack, x, y, 0);
    }

    public void renderInGuiWithOverrides(MatrixStack matrices, ItemStack stack, int x, int y, int seed) {
        this.innerRenderInGui(matrices, this.client.player, this.client.world, stack, x, y, seed);
    }

    public void renderInGuiWithOverrides(MatrixStack matrices, ItemStack stack, int x, int y, int seed, int depth) {
        this.innerRenderInGui(matrices, this.client.player, this.client.world, stack, x, y, seed, depth);
    }

    /**
     * Renders an item in a GUI without an attached entity.
     */
    public void renderInGui(MatrixStack matrices, ItemStack stack, int x, int y) {
        this.innerRenderInGui(matrices, null, this.client.world, stack, x, y, 0);
    }

    /**
     * Renders an item in a GUI with an attached entity.
     * 
     * <p>The entity is used to calculate model overrides for the item.
     */
    public void renderInGuiWithOverrides(MatrixStack matrices, LivingEntity entity, ItemStack stack, int x, int y, int seed) {
        this.innerRenderInGui(matrices, entity, entity.world, stack, x, y, seed);
    }

    private void innerRenderInGui(MatrixStack matrices, @Nullable LivingEntity entity, @Nullable World world, ItemStack stack, int x, int y, int seed) {
        this.innerRenderInGui(matrices, entity, world, stack, x, y, seed, 0);
    }

    private void innerRenderInGui(MatrixStack matrices, @Nullable LivingEntity entity, @Nullable World world, ItemStack stack, int x, int y, int seed, int depth) {
        if (stack.isEmpty()) {
            return;
        }
        BakedModel bakedModel = this.getModel(stack, world, entity, seed);
        matrices.push();
        matrices.translate(0.0f, 0.0f, 50 + (bakedModel.hasDepth() ? depth : 0));
        try {
            this.renderGuiItemModel(matrices, stack, x, y, bakedModel);
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Rendering item");
            CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
            crashReportSection.add("Item Type", () -> String.valueOf(stack.getItem()));
            crashReportSection.add("Item Damage", () -> String.valueOf(stack.getDamage()));
            crashReportSection.add("Item NBT", () -> String.valueOf(stack.getNbt()));
            crashReportSection.add("Item Foil", () -> String.valueOf(stack.hasGlint()));
            throw new CrashException(crashReport);
        }
        matrices.pop();
    }

    /**
     * Renders the overlay for items in GUIs, including the damage bar and the item count.
     */
    public void renderGuiItemOverlay(MatrixStack matrices, TextRenderer textRenderer, ItemStack stack, int x, int y) {
        this.renderGuiItemOverlay(matrices, textRenderer, stack, x, y, null);
    }

    /**
     * Renders the overlay for items in GUIs, including the damage bar and the item count.
     */
    public void renderGuiItemOverlay(MatrixStack matrices, TextRenderer textRenderer, ItemStack stack, int x, int y, @Nullable String countLabel) {
        ClientPlayerEntity clientPlayerEntity;
        float f;
        int l;
        int k;
        if (stack.isEmpty()) {
            return;
        }
        matrices.push();
        if (stack.getCount() != 1 || countLabel != null) {
            String string = countLabel == null ? String.valueOf(stack.getCount()) : countLabel;
            matrices.translate(0.0f, 0.0f, 200.0f);
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
            textRenderer.draw(string, (float)(x + 19 - 2 - textRenderer.getWidth(string)), (float)(y + 6 + 3), 0xFFFFFF, true, matrices.peek().getPositionMatrix(), (VertexConsumerProvider)immediate, TextRenderer.TextLayerType.NORMAL, 0, 0xF000F0);
            immediate.draw();
        }
        if (stack.isItemBarVisible()) {
            RenderSystem.disableDepthTest();
            int i = stack.getItemBarStep();
            int j = stack.getItemBarColor();
            k = x + 2;
            l = y + 13;
            DrawableHelper.fill(matrices, k, l, k + 13, l + 2, -16777216);
            DrawableHelper.fill(matrices, k, l, k + i, l + 1, j | 0xFF000000);
            RenderSystem.enableDepthTest();
        }
        float f2 = f = (clientPlayerEntity = this.client.player) == null ? 0.0f : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), this.client.getTickDelta());
        if (f > 0.0f) {
            RenderSystem.disableDepthTest();
            k = y + MathHelper.floor(16.0f * (1.0f - f));
            l = k + MathHelper.ceil(16.0f * f);
            DrawableHelper.fill(matrices, x, k, x + 16, l, Integer.MAX_VALUE);
            RenderSystem.enableDepthTest();
        }
        matrices.pop();
    }

    @Override
    public void reload(ResourceManager manager) {
        this.models.reloadModels();
    }
}

