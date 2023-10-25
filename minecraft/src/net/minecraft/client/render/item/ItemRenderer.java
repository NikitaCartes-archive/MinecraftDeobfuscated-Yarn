package net.minecraft.client.render.item;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.block.TranslucentBlock;
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
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ItemRenderer implements SynchronousResourceReloader {
	public static final Identifier ENTITY_ENCHANTMENT_GLINT = new Identifier("textures/misc/enchanted_glint_entity.png");
	public static final Identifier ITEM_ENCHANTMENT_GLINT = new Identifier("textures/misc/enchanted_glint_item.png");
	private static final Set<Item> WITHOUT_MODELS = Sets.<Item>newHashSet(Items.AIR);
	public static final int field_32937 = 8;
	public static final int field_32938 = 8;
	public static final int field_32934 = 200;
	public static final float COMPASS_WITH_GLINT_GUI_MODEL_MULTIPLIER = 0.5F;
	public static final float COMPASS_WITH_GLINT_FIRST_PERSON_MODEL_MULTIPLIER = 0.75F;
	public static final float field_41120 = 0.0078125F;
	private static final ModelIdentifier TRIDENT = ModelIdentifier.ofVanilla("trident", "inventory");
	public static final ModelIdentifier TRIDENT_IN_HAND = ModelIdentifier.ofVanilla("trident_in_hand", "inventory");
	private static final ModelIdentifier SPYGLASS = ModelIdentifier.ofVanilla("spyglass", "inventory");
	public static final ModelIdentifier SPYGLASS_IN_HAND = ModelIdentifier.ofVanilla("spyglass_in_hand", "inventory");
	private final MinecraftClient client;
	private final ItemModels models;
	private final TextureManager textureManager;
	private final ItemColors colors;
	private final BuiltinModelItemRenderer builtinModelItemRenderer;

	public ItemRenderer(
		MinecraftClient client, TextureManager manager, BakedModelManager bakery, ItemColors colors, BuiltinModelItemRenderer builtinModelItemRenderer
	) {
		this.client = client;
		this.textureManager = manager;
		this.models = new ItemModels(bakery);
		this.builtinModelItemRenderer = builtinModelItemRenderer;

		for (Item item : Registries.ITEM) {
			if (!WITHOUT_MODELS.contains(item)) {
				this.models.putModel(item, new ModelIdentifier(Registries.ITEM.getId(item), "inventory"));
			}
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

	public void renderItem(
		ItemStack stack,
		ModelTransformationMode renderMode,
		boolean leftHanded,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		BakedModel model
	) {
		if (!stack.isEmpty()) {
			matrices.push();
			boolean bl = renderMode == ModelTransformationMode.GUI || renderMode == ModelTransformationMode.GROUND || renderMode == ModelTransformationMode.FIXED;
			if (bl) {
				if (stack.isOf(Items.TRIDENT)) {
					model = this.models.getModelManager().getModel(TRIDENT);
				} else if (stack.isOf(Items.SPYGLASS)) {
					model = this.models.getModelManager().getModel(SPYGLASS);
				}
			}

			model.getTransformation().getTransformation(renderMode).apply(leftHanded, matrices);
			matrices.translate(-0.5F, -0.5F, -0.5F);
			if (!model.isBuiltin() && (!stack.isOf(Items.TRIDENT) || bl)) {
				boolean bl2;
				if (renderMode != ModelTransformationMode.GUI && !renderMode.isFirstPerson() && stack.getItem() instanceof BlockItem) {
					Block block = ((BlockItem)stack.getItem()).getBlock();
					bl2 = !(block instanceof TranslucentBlock) && !(block instanceof StainedGlassPaneBlock);
				} else {
					bl2 = true;
				}

				RenderLayer renderLayer = RenderLayers.getItemLayer(stack, bl2);
				VertexConsumer vertexConsumer;
				if (usesDynamicDisplay(stack) && stack.hasGlint()) {
					matrices.push();
					MatrixStack.Entry entry = matrices.peek();
					if (renderMode == ModelTransformationMode.GUI) {
						MatrixUtil.scale(entry.getPositionMatrix(), 0.5F);
					} else if (renderMode.isFirstPerson()) {
						MatrixUtil.scale(entry.getPositionMatrix(), 0.75F);
					}

					if (bl2) {
						vertexConsumer = getDirectDynamicDisplayGlintConsumer(vertexConsumers, renderLayer, entry);
					} else {
						vertexConsumer = getDynamicDisplayGlintConsumer(vertexConsumers, renderLayer, entry);
					}

					matrices.pop();
				} else if (bl2) {
					vertexConsumer = getDirectItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
				} else {
					vertexConsumer = getItemGlintConsumer(vertexConsumers, renderLayer, true, stack.hasGlint());
				}

				this.renderBakedItemModel(model, stack, light, overlay, matrices, vertexConsumer);
			} else {
				this.builtinModelItemRenderer.render(stack, renderMode, matrices, vertexConsumers, light, overlay);
			}

			matrices.pop();
		}
	}

	private static boolean usesDynamicDisplay(ItemStack stack) {
		return stack.isIn(ItemTags.COMPASSES) || stack.isOf(Items.CLOCK);
	}

	public static VertexConsumer getArmorGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean solid, boolean glint) {
		return glint
			? VertexConsumers.union(provider.getBuffer(solid ? RenderLayer.getArmorGlint() : RenderLayer.getArmorEntityGlint()), provider.getBuffer(layer))
			: provider.getBuffer(layer);
	}

	public static VertexConsumer getDynamicDisplayGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry) {
		return VertexConsumers.union(
			new OverlayVertexConsumer(provider.getBuffer(RenderLayer.getGlint()), entry.getPositionMatrix(), entry.getNormalMatrix(), 0.0078125F),
			provider.getBuffer(layer)
		);
	}

	public static VertexConsumer getDirectDynamicDisplayGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry) {
		return VertexConsumers.union(
			new OverlayVertexConsumer(provider.getBuffer(RenderLayer.getDirectGlint()), entry.getPositionMatrix(), entry.getNormalMatrix(), 0.0078125F),
			provider.getBuffer(layer)
		);
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

	public static VertexConsumer getDirectItemGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean solid, boolean glint) {
		return glint
			? VertexConsumers.union(provider.getBuffer(solid ? RenderLayer.getDirectGlint() : RenderLayer.getDirectEntityGlint()), provider.getBuffer(layer))
			: provider.getBuffer(layer);
	}

	private void renderBakedItemQuads(MatrixStack matrices, VertexConsumer vertices, List<BakedQuad> quads, ItemStack stack, int light, int overlay) {
		boolean bl = !stack.isEmpty();
		MatrixStack.Entry entry = matrices.peek();

		for (BakedQuad bakedQuad : quads) {
			int i = -1;
			if (bl && bakedQuad.hasColor()) {
				i = this.colors.getColor(stack, bakedQuad.getColorIndex());
			}

			float f = (float)(i >> 16 & 0xFF) / 255.0F;
			float g = (float)(i >> 8 & 0xFF) / 255.0F;
			float h = (float)(i & 0xFF) / 255.0F;
			vertices.quad(entry, bakedQuad, f, g, h, light, overlay);
		}
	}

	public BakedModel getModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity entity, int seed) {
		BakedModel bakedModel;
		if (stack.isOf(Items.TRIDENT)) {
			bakedModel = this.models.getModelManager().getModel(TRIDENT_IN_HAND);
		} else if (stack.isOf(Items.SPYGLASS)) {
			bakedModel = this.models.getModelManager().getModel(SPYGLASS_IN_HAND);
		} else {
			bakedModel = this.models.getModel(stack);
		}

		ClientWorld clientWorld = world instanceof ClientWorld ? (ClientWorld)world : null;
		BakedModel bakedModel2 = bakedModel.getOverrides().apply(bakedModel, stack, clientWorld, entity, seed);
		return bakedModel2 == null ? this.models.getModelManager().getMissingModel() : bakedModel2;
	}

	public void renderItem(
		ItemStack stack,
		ModelTransformationMode transformationType,
		int light,
		int overlay,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		@Nullable World world,
		int seed
	) {
		this.renderItem(null, stack, transformationType, false, matrices, vertexConsumers, world, light, overlay, seed);
	}

	public void renderItem(
		@Nullable LivingEntity entity,
		ItemStack item,
		ModelTransformationMode renderMode,
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
			this.renderItem(item, renderMode, leftHanded, matrices, vertexConsumers, light, overlay, bakedModel);
		}
	}

	@Override
	public void reload(ResourceManager manager) {
		this.models.reloadModels();
	}
}
