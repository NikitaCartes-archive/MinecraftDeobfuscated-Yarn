package net.minecraft.client.render.item;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ItemRenderer implements SynchronousResourceReloader {
	public static final Identifier ENTITY_ENCHANTMENT_GLINT = Identifier.ofVanilla("textures/misc/enchanted_glint_entity.png");
	public static final Identifier ITEM_ENCHANTMENT_GLINT = Identifier.ofVanilla("textures/misc/enchanted_glint_item.png");
	public static final int field_32937 = 8;
	public static final int field_32938 = 8;
	public static final int field_32934 = 200;
	public static final float COMPASS_WITH_GLINT_GUI_MODEL_MULTIPLIER = 0.5F;
	public static final float COMPASS_WITH_GLINT_FIRST_PERSON_MODEL_MULTIPLIER = 0.75F;
	public static final float field_41120 = 0.0078125F;
	public static final ModelIdentifier TRIDENT = ModelIdentifier.ofInventoryVariant(Identifier.ofVanilla("trident"));
	public static final ModelIdentifier SPYGLASS = ModelIdentifier.ofInventoryVariant(Identifier.ofVanilla("spyglass"));
	private final BakedModelManager bakedModelManager;
	private final ItemModels models;
	private final ItemColors colors;
	private final BuiltinModelItemRenderer builtinModelItemRenderer;

	public ItemRenderer(BakedModelManager bakedModelManager, ItemColors colors, BuiltinModelItemRenderer builtinModelItemRenderer) {
		this.bakedModelManager = bakedModelManager;
		this.models = new ItemModels(bakedModelManager);
		this.builtinModelItemRenderer = builtinModelItemRenderer;
		this.colors = colors;
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

	public void renderItem(
		ItemStack stack,
		ModelTransformationMode transformationMode,
		boolean leftHanded,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		BakedModel model
	) {
		if (!stack.isEmpty()) {
			boolean bl = transformationMode == ModelTransformationMode.GUI
				|| transformationMode == ModelTransformationMode.GROUND
				|| transformationMode == ModelTransformationMode.FIXED;
			if (bl && stack.isOf(Items.BUNDLE) && stack.getItem() instanceof BundleItem bundleItem && BundleItem.hasSelectedStack(stack)) {
				this.renderBundle(bundleItem, stack, transformationMode, leftHanded, matrices, vertexConsumers, light, overlay, bl);
			} else {
				matrices.push();
				this.renderItem(stack, transformationMode, leftHanded, matrices, vertexConsumers, light, overlay, model, bl);
				matrices.pop();
			}
		}
	}

	private void renderBundle(
		BundleItem item,
		ItemStack stack,
		ModelTransformationMode transformationMode,
		boolean leftHanded,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		boolean bl
	) {
		matrices.push();
		BakedModel bakedModel = this.models.getModel(item.getOpenBackTexture());
		this.renderItem(stack, transformationMode, leftHanded, matrices, vertexConsumers, light, overlay, bakedModel, bl, -1.5F);
		matrices.pop();
		matrices.push();
		ItemStack itemStack = BundleItem.getSelectedStack(stack);
		BakedModel bakedModel2 = this.models.getModel(itemStack);
		this.renderItem(itemStack, transformationMode, leftHanded, matrices, vertexConsumers, light, overlay, bakedModel2, bl);
		matrices.pop();
		matrices.push();
		BakedModel bakedModel3 = this.models.getModel(item.getOpenFrontTexture());
		this.renderItem(stack, transformationMode, leftHanded, matrices, vertexConsumers, light, overlay, bakedModel3, bl, 0.5F);
		matrices.pop();
	}

	private void renderItem(
		ItemStack stack,
		ModelTransformationMode transformationMode,
		boolean leftHanded,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		BakedModel model,
		boolean bl
	) {
		if (bl) {
			if (stack.isOf(Items.TRIDENT)) {
				model = this.bakedModelManager.getModel(TRIDENT);
			} else if (stack.isOf(Items.SPYGLASS)) {
				model = this.bakedModelManager.getModel(SPYGLASS);
			}
		}

		this.renderItem(stack, transformationMode, leftHanded, matrices, vertexConsumers, light, overlay, model, bl, -0.5F);
	}

	private void renderItem(
		ItemStack stack,
		ModelTransformationMode transformationMode,
		boolean leftHanded,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		BakedModel model,
		boolean bl,
		float f
	) {
		model.getTransformation().getTransformation(transformationMode).apply(leftHanded, matrices);
		matrices.translate(-0.5F, -0.5F, f);
		this.renderItem(stack, transformationMode, matrices, vertexConsumers, light, overlay, model, bl);
	}

	private void renderItem(
		ItemStack stack,
		ModelTransformationMode transformationMode,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		BakedModel model,
		boolean bl
	) {
		if (!model.isBuiltin() && (!stack.isOf(Items.TRIDENT) || bl)) {
			RenderLayer renderLayer = RenderLayers.getItemLayer(stack);
			VertexConsumer vertexConsumer;
			if (usesDynamicDisplay(stack) && stack.hasGlint()) {
				MatrixStack.Entry entry = matrices.peek().copy();
				if (transformationMode == ModelTransformationMode.GUI) {
					MatrixUtil.scale(entry.getPositionMatrix(), 0.5F);
				} else if (transformationMode.isFirstPerson()) {
					MatrixUtil.scale(entry.getPositionMatrix(), 0.75F);
				}

				vertexConsumer = getDynamicDisplayGlintConsumer(vertexConsumers, renderLayer, entry);
			} else {
				vertexConsumer = getItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
			}

			this.renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);
		} else {
			this.builtinModelItemRenderer.render(stack, transformationMode, matrices, vertexConsumers, light, overlay);
		}
	}

	private static boolean usesDynamicDisplay(ItemStack stack) {
		return stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK);
	}

	public static VertexConsumer getArmorGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean glint) {
		return glint ? VertexConsumers.union(provider.getBuffer(RenderLayer.getArmorEntityGlint()), provider.getBuffer(layer)) : provider.getBuffer(layer);
	}

	public static VertexConsumer getDynamicDisplayGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry) {
		return VertexConsumers.union(new OverlayVertexConsumer(provider.getBuffer(RenderLayer.getGlint()), entry, 0.0078125F), provider.getBuffer(layer));
	}

	public static VertexConsumer getItemGlintConsumer(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean solid, boolean glint) {
		if (glint) {
			return MinecraftClient.isFabulousGraphicsOrBetter() && layer == TexturedRenderLayers.getItemEntityTranslucentCull()
				? VertexConsumers.union(vertexConsumers.getBuffer(RenderLayer.getGlintTranslucent()), vertexConsumers.getBuffer(layer))
				: VertexConsumers.union(vertexConsumers.getBuffer(solid ? RenderLayer.getGlint() : RenderLayer.getEntityGlint()), vertexConsumers.getBuffer(layer));
		} else {
			return vertexConsumers.getBuffer(layer);
		}
	}

	private void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay) {
		boolean bl = !stack.isEmpty();
		MatrixStack.Entry entry = matrices.peek();

		for (BakedQuad bakedQuad : quads) {
			int i = Colors.WHITE;
			if (bl && bakedQuad.hasColor()) {
				i = this.colors.getColor(stack, bakedQuad.getColorIndex());
			}

			float f = (float)ColorHelper.getAlpha(i) / 255.0F;
			float g = (float)ColorHelper.getRed(i) / 255.0F;
			float h = (float)ColorHelper.getGreen(i) / 255.0F;
			float j = (float)ColorHelper.getBlue(i) / 255.0F;
			vertices.quad(entry, bakedQuad, g, h, j, f, light, overlay);
		}
	}

	public BakedModel getModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed) {
		BakedModel bakedModel = this.models.getModel(stack);
		ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld)world : null;
		BakedModel bakedModel2 = bakedModel.getOverrides().getModel(stack, clientWorld, entity, seed);
		return bakedModel2 == null ? bakedModel : bakedModel2;
	}

	public void renderItem(
		ItemStack stack,
		ModelTransformationMode transformationMode,
		int light,
		int overlay,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		@Nullable World world,
		int seed
	) {
		this.renderItem(null, stack, transformationMode, false, matrices, vertexConsumers, world, light, overlay, seed);
	}

	public void renderItem(
		@Nullable LivingEntity entity,
		ItemStack item,
		ModelTransformationMode transformationMode,
		boolean leftHanded,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		@Nullable World world,
		int light,
		int overlay,
		int seed
	) {
		if (!item.isEmpty()) {
			BakedModel bakedModel = this.getModel(item, world, entity, seed);
			this.renderItem(item, transformationMode, leftHanded, matrices, vertexConsumers, light, overlay, bakedModel);
		}
	}

	@Override
	public void reload(ResourceManager manager) {
		this.models.clearModels();
	}

	@Nullable
	public BakedModel getModel(ItemStack stack, LivingEntity entity, ModelTransformationMode transformationMode) {
		return stack.isEmpty() ? null : this.getModel(stack, entity.getWorld(), entity, entity.getId() + transformationMode.ordinal());
	}
}
