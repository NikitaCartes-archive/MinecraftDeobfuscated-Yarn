package net.minecraft.client.render.item;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
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
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.OverlayVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloader;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MatrixUtil;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ItemRenderer implements SynchronousResourceReloader {
	public static final Identifier ENCHANTED_ITEM_GLINT = new Identifier("textures/misc/enchanted_item_glint.png");
	private static final Set<Item> WITHOUT_MODELS = Sets.<Item>newHashSet(Items.AIR);
	private static final int field_32937 = 8;
	private static final int field_32938 = 8;
	public static final int field_32934 = 200;
	public static final float COMPASS_WITH_GLINT_GUI_MODEL_MULTIPLIER = 0.5F;
	public static final float COMPASS_WITH_GLINT_FIRST_PERSON_MODEL_MULTIPLIER = 0.75F;
	private static final ModelIdentifier TRIDENT = ModelIdentifier.ofVanilla("trident", "inventory");
	public static final ModelIdentifier TRIDENT_IN_HAND = ModelIdentifier.ofVanilla("trident_in_hand", "inventory");
	private static final ModelIdentifier SPYGLASS = ModelIdentifier.ofVanilla("spyglass", "inventory");
	public static final ModelIdentifier SPYGLASS_IN_HAND = ModelIdentifier.ofVanilla("spyglass_in_hand", "inventory");
	public float zOffset;
	private final ItemModels models;
	private final TextureManager textureManager;
	private final ItemColors colors;
	private final BuiltinModelItemRenderer builtinModelItemRenderer;

	public ItemRenderer(TextureManager manager, BakedModelManager bakery, ItemColors colors, BuiltinModelItemRenderer builtinModelItemRenderer) {
		this.textureManager = manager;
		this.models = new ItemModels(bakery);
		this.builtinModelItemRenderer = builtinModelItemRenderer;

		for (Item item : Registry.ITEM) {
			if (!WITHOUT_MODELS.contains(item)) {
				this.models.putModel(item, new ModelIdentifier(Registry.ITEM.getId(item), "inventory"));
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
		ModelTransformation.Mode renderMode,
		boolean leftHanded,
		MatrixStack matrices,
		VertexConsumerProvider vertexConsumers,
		int light,
		int overlay,
		BakedModel model
	) {
		if (!stack.isEmpty()) {
			matrices.push();
			boolean bl = renderMode == ModelTransformation.Mode.GUI || renderMode == ModelTransformation.Mode.GROUND || renderMode == ModelTransformation.Mode.FIXED;
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
				if (renderMode != ModelTransformation.Mode.GUI && !renderMode.isFirstPerson() && stack.getItem() instanceof BlockItem) {
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
					if (renderMode == ModelTransformation.Mode.GUI) {
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
			new OverlayVertexConsumer(provider.getBuffer(RenderLayer.getGlint()), entry.getPositionMatrix(), entry.getNormalMatrix()), provider.getBuffer(layer)
		);
	}

	public static VertexConsumer getDirectCompassGlintConsumer(VertexConsumerProvider provider, RenderLayer layer, MatrixStack.Entry entry) {
		return VertexConsumers.union(
			new OverlayVertexConsumer(provider.getBuffer(RenderLayer.getDirectGlint()), entry.getPositionMatrix(), entry.getNormalMatrix()), provider.getBuffer(layer)
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
		ItemStack stack, ModelTransformation.Mode transformationType, int light, int overlay, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int seed
	) {
		this.renderItem(null, stack, transformationType, false, matrices, vertexConsumers, null, light, overlay, seed);
	}

	public void renderItem(
		@Nullable LivingEntity entity,
		ItemStack item,
		ModelTransformation.Mode renderMode,
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

	public void renderGuiItemIcon(ItemStack stack, int x, int y) {
		this.renderGuiItemModel(stack, x, y, this.getModel(stack, null, null, 0));
	}

	protected void renderGuiItemModel(ItemStack stack, int x, int y, BakedModel model) {
		this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).setFilter(false, false);
		RenderSystem.setShaderTexture(0, SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		MatrixStack matrixStack = RenderSystem.getModelViewStack();
		matrixStack.push();
		matrixStack.translate((float)x, (float)y, 100.0F + this.zOffset);
		matrixStack.translate(8.0F, 8.0F, 0.0F);
		matrixStack.scale(1.0F, -1.0F, 1.0F);
		matrixStack.scale(16.0F, 16.0F, 16.0F);
		RenderSystem.applyModelViewMatrix();
		MatrixStack matrixStack2 = new MatrixStack();
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		boolean bl = !model.isSideLit();
		if (bl) {
			DiffuseLighting.disableGuiDepthLighting();
		}

		this.renderItem(
			stack, ModelTransformation.Mode.GUI, false, matrixStack2, immediate, LightmapTextureManager.MAX_LIGHT_COORDINATE, OverlayTexture.DEFAULT_UV, model
		);
		immediate.draw();
		RenderSystem.enableDepthTest();
		if (bl) {
			DiffuseLighting.enableGuiDepthLighting();
		}

		matrixStack.pop();
		RenderSystem.applyModelViewMatrix();
	}

	/**
	 * Renders an item in a GUI with the player as the attached entity
	 * for calculating model overrides.
	 */
	public void renderInGuiWithOverrides(ItemStack stack, int x, int y) {
		this.innerRenderInGui(MinecraftClient.getInstance().player, stack, x, y, 0);
	}

	public void renderInGuiWithOverrides(ItemStack stack, int x, int y, int seed) {
		this.innerRenderInGui(MinecraftClient.getInstance().player, stack, x, y, seed);
	}

	public void renderInGuiWithOverrides(ItemStack stack, int x, int y, int seed, int depth) {
		this.innerRenderInGui(MinecraftClient.getInstance().player, stack, x, y, seed, depth);
	}

	/**
	 * Renders an item in a GUI without an attached entity.
	 */
	public void renderInGui(ItemStack stack, int x, int y) {
		this.innerRenderInGui(null, stack, x, y, 0);
	}

	/**
	 * Renders an item in a GUI with an attached entity.
	 * 
	 * <p>The entity is used to calculate model overrides for the item.
	 */
	public void renderInGuiWithOverrides(LivingEntity entity, ItemStack stack, int x, int y, int seed) {
		this.innerRenderInGui(entity, stack, x, y, seed);
	}

	private void innerRenderInGui(@Nullable LivingEntity entity, ItemStack stack, int x, int y, int seed) {
		this.innerRenderInGui(entity, stack, x, y, seed, 0);
	}

	private void innerRenderInGui(@Nullable LivingEntity entity, ItemStack itemStack, int x, int y, int seed, int depth) {
		if (!itemStack.isEmpty()) {
			BakedModel bakedModel = this.getModel(itemStack, null, entity, seed);
			this.zOffset = bakedModel.hasDepth() ? this.zOffset + 50.0F + (float)depth : this.zOffset + 50.0F;

			try {
				this.renderGuiItemModel(itemStack, x, y, bakedModel);
			} catch (Throwable var11) {
				CrashReport crashReport = CrashReport.create(var11, "Rendering item");
				CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
				crashReportSection.add("Item Type", (CrashCallable<String>)(() -> String.valueOf(itemStack.getItem())));
				crashReportSection.add("Item Damage", (CrashCallable<String>)(() -> String.valueOf(itemStack.getDamage())));
				crashReportSection.add("Item NBT", (CrashCallable<String>)(() -> String.valueOf(itemStack.getNbt())));
				crashReportSection.add("Item Foil", (CrashCallable<String>)(() -> String.valueOf(itemStack.hasGlint())));
				throw new CrashException(crashReport);
			}

			this.zOffset = bakedModel.hasDepth() ? this.zOffset - 50.0F - (float)depth : this.zOffset - 50.0F;
		}
	}

	/**
	 * Renders the overlay for items in GUIs, including the damage bar and the item count.
	 */
	public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y) {
		this.renderGuiItemOverlay(renderer, stack, x, y, null);
	}

	/**
	 * Renders the overlay for items in GUIs, including the damage bar and the item count.
	 * 
	 * @param countLabel a label for the stack; if null, the stack count is drawn instead
	 */
	public void renderGuiItemOverlay(TextRenderer renderer, ItemStack stack, int x, int y, @Nullable String countLabel) {
		if (!stack.isEmpty()) {
			MatrixStack matrixStack = new MatrixStack();
			if (stack.getCount() != 1 || countLabel != null) {
				String string = countLabel == null ? String.valueOf(stack.getCount()) : countLabel;
				matrixStack.translate(0.0F, 0.0F, this.zOffset + 200.0F);
				VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
				renderer.draw(
					string,
					(float)(x + 19 - 2 - renderer.getWidth(string)),
					(float)(y + 6 + 3),
					16777215,
					true,
					matrixStack.peek().getPositionMatrix(),
					immediate,
					false,
					0,
					15728880
				);
				immediate.draw();
			}

			if (stack.isItemBarVisible()) {
				RenderSystem.disableDepthTest();
				RenderSystem.disableTexture();
				RenderSystem.disableBlend();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				int i = stack.getItemBarStep();
				int j = stack.getItemBarColor();
				this.renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
				this.renderGuiQuad(bufferBuilder, x + 2, y + 13, i, 1, j >> 16 & 0xFF, j >> 8 & 0xFF, j & 0xFF, 255);
				RenderSystem.enableBlend();
				RenderSystem.enableTexture();
				RenderSystem.enableDepthTest();
			}

			ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
			float f = clientPlayerEntity == null
				? 0.0F
				: clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
			if (f > 0.0F) {
				RenderSystem.disableDepthTest();
				RenderSystem.disableTexture();
				RenderSystem.enableBlend();
				RenderSystem.defaultBlendFunc();
				Tessellator tessellator2 = Tessellator.getInstance();
				BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
				this.renderGuiQuad(bufferBuilder2, x, y + MathHelper.floor(16.0F * (1.0F - f)), 16, MathHelper.ceil(16.0F * f), 255, 255, 255, 127);
				RenderSystem.enableTexture();
				RenderSystem.enableDepthTest();
			}
		}
	}

	private void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
		buffer.vertex((double)(x + 0), (double)(y + 0), 0.0).color(red, green, blue, alpha).next();
		buffer.vertex((double)(x + 0), (double)(y + height), 0.0).color(red, green, blue, alpha).next();
		buffer.vertex((double)(x + width), (double)(y + height), 0.0).color(red, green, blue, alpha).next();
		buffer.vertex((double)(x + width), (double)(y + 0), 0.0).color(red, green, blue, alpha).next();
		BufferRenderer.drawWithShader(buffer.end());
	}

	@Override
	public void reload(ResourceManager manager) {
		this.models.reloadModels();
	}
}
