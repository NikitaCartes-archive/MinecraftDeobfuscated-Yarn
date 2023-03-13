package net.minecraft.client.render.item;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
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
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class ItemRenderer implements SynchronousResourceReloader {
	public static final Identifier ENTITY_ENCHANTMENT_GLINT = new Identifier("textures/misc/enchanted_glint_entity.png");
	public static final Identifier ITEM_ENCHANTMENT_GLINT = new Identifier("textures/misc/enchanted_glint_item.png");
	private static final Set<Item> WITHOUT_MODELS = Sets.<Item>newHashSet(Items.AIR);
	private static final int field_32937 = 8;
	private static final int field_32938 = 8;
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
					bl2 = !(block instanceof TransparentBlock) && !(block instanceof StainedGlassPaneBlock);
				} else {
					bl2 = true;
				}

				RenderLayer renderLayer = RenderLayers.getItemLayer(stack, bl2);
				VertexConsumer vertexConsumer;
				if (stack.isIn(ItemTags.COMPASSES) && stack.hasGlint()) {
					matrices.push();
					MatrixStack.Entry entry = matrices.peek();
					if (renderMode == ModelTransformationMode.GUI) {
						MatrixUtil.scale(entry.getPositionMatrix(), 0.5F);
					} else if (renderMode.isFirstPerson()) {
						MatrixUtil.scale(entry.getPositionMatrix(), 0.75F);
					}

					if (bl2) {
						vertexConsumer = getDirectCompassGlintConsumer(vertexConsumers, renderLayer, entry);
					} else {
						vertexConsumer = getCompassGlintConsumer(vertexConsumers, renderLayer, entry);
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

	public static VertexConsumer getArmorGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, boolean solid, boolean glint) {
		return glint
			? VertexConsumers.union(provider.getBuffer(solid ? RenderLayer.getArmorGlint() : RenderLayer.getArmorEntityGlint()), provider.getBuffer(layer))
			: provider.getBuffer(layer);
	}

	public static VertexConsumer getCompassGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry) {
		return VertexConsumers.union(
			new OverlayVertexConsumer(provider.getBuffer(RenderLayer.getGlint()), entry.getPositionMatrix(), entry.getNormalMatrix(), 0.0078125F),
			provider.getBuffer(layer)
		);
	}

	public static VertexConsumer getDirectCompassGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry) {
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

	public void renderGuiItemIcon(MatrixStack matrices, ItemStack stack, int x, int y) {
		this.renderGuiItemModel(matrices, stack, x, y, this.getModel(stack, null, null, 0));
	}

	protected void renderGuiItemModel(MatrixStack matrices, ItemStack stack, int x, int y, BakedModel model) {
		matrices.push();
		matrices.translate((float)x, (float)y, 100.0F);
		matrices.translate(8.0F, 8.0F, 0.0F);
		matrices.multiplyPositionMatrix(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
		matrices.scale(16.0F, 16.0F, 16.0F);
		VertexConsumerProvider.Immediate immediate = this.client.getBufferBuilders().getEntityVertexConsumers();
		boolean bl = !model.isSideLit();
		if (bl) {
			DiffuseLighting.disableGuiDepthLighting();
		}

		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.push();
		matrixStack.multiplyPositionMatrix(matrices.peek().getPositionMatrix());
		RenderSystem.applyModelViewMatrix();
		this.renderItem(stack, ModelTransformationMode.GUI, false, new MatrixStack(), immediate, 15728880, OverlayTexture.DEFAULT_UV, model);
		immediate.draw();
		RenderSystem.enableDepthTest();
		if (bl) {
			DiffuseLighting.enableGuiDepthLighting();
		}

		matrices.pop();
		matrixStack.pop();
		RenderSystem.applyModelViewMatrix();
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
		if (!stack.isEmpty()) {
			BakedModel bakedModel = this.getModel(stack, world, entity, seed);
			matrices.push();
			matrices.translate(0.0F, 0.0F, (float)(50 + (bakedModel.hasDepth() ? depth : 0)));

			try {
				this.renderGuiItemModel(matrices, stack, x, y, bakedModel);
			} catch (Throwable var13) {
				CrashReport crashReport = CrashReport.create(var13, "Rendering item");
				CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
				crashReportSection.add("Item Type", (CrashCallable<String>)(() -> String.valueOf(stack.getItem())));
				crashReportSection.add("Item Damage", (CrashCallable<String>)(() -> String.valueOf(stack.getDamage())));
				crashReportSection.add("Item NBT", (CrashCallable<String>)(() -> String.valueOf(stack.getNbt())));
				crashReportSection.add("Item Foil", (CrashCallable<String>)(() -> String.valueOf(stack.hasGlint())));
				throw new CrashException(crashReport);
			}

			matrices.pop();
		}
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
		if (!stack.isEmpty()) {
			matrices.push();
			if (stack.getCount() != 1 || countLabel != null) {
				String string = countLabel == null ? String.valueOf(stack.getCount()) : countLabel;
				matrices.translate(0.0F, 0.0F, 200.0F);
				VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
				textRenderer.draw(
					string,
					(float)(x + 19 - 2 - textRenderer.getWidth(string)),
					(float)(y + 6 + 3),
					16777215,
					true,
					matrices.peek().getPositionMatrix(),
					immediate,
					TextRenderer.TextLayerType.NORMAL,
					0,
					15728880
				);
				immediate.draw();
			}

			if (stack.isItemBarVisible()) {
				RenderSystem.disableDepthTest();
				int i = stack.getItemBarStep();
				int j = stack.getItemBarColor();
				int k = x + 2;
				int l = y + 13;
				DrawableHelper.fill(matrices, k, l, k + 13, l + 2, -16777216);
				DrawableHelper.fill(matrices, k, l, k + i, l + 1, j | 0xFF000000);
				RenderSystem.enableDepthTest();
			}

			ClientPlayerEntity clientPlayerEntity = this.client.player;
			float f = clientPlayerEntity == null ? 0.0F : clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), this.client.getTickDelta());
			if (f > 0.0F) {
				RenderSystem.disableDepthTest();
				int k = y + MathHelper.floor(16.0F * (1.0F - f));
				int l = k + MathHelper.ceil(16.0F * f);
				DrawableHelper.fill(matrices, x, k, x + 16, l, Integer.MAX_VALUE);
				RenderSystem.enableDepthTest();
			}

			matrices.pop();
		}
	}

	@Override
	public void reload(ResourceManager manager) {
		this.models.reloadModels();
	}
}
