package net.minecraft.client.render.item;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormats;
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
import net.minecraft.util.Util;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ItemRenderer implements SynchronousResourceReloadListener {
	public static final Identifier ENCHANTMENT_GLINT_TEX = new Identifier("textures/misc/enchanted_item_glint.png");
	private static final Set<Item> WITHOUT_MODELS = Sets.<Item>newHashSet(Items.AIR);
	public float zOffset;
	private final ItemModels models;
	private final TextureManager textureManager;
	private final ItemColors colorMap;

	public ItemRenderer(TextureManager manager, BakedModelManager bakery, ItemColors colorMap) {
		this.textureManager = manager;
		this.models = new ItemModels(bakery);

		for (Item item : Registry.ITEM) {
			if (!WITHOUT_MODELS.contains(item)) {
				this.models.putModel(item, new ModelIdentifier(Registry.ITEM.getId(item), "inventory"));
			}
		}

		this.colorMap = colorMap;
	}

	public ItemModels getModels() {
		return this.models;
	}

	private void renderItemModel(BakedModel bakedModel, ItemStack stack) {
		this.renderModel(bakedModel, -1, stack);
	}

	private void renderModelWithTint(BakedModel bakedModel, int i) {
		this.renderModel(bakedModel, i, ItemStack.EMPTY);
	}

	private void renderModel(BakedModel bakedModel, int i, ItemStack stack) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL);
		Random random = new Random();
		long l = 42L;

		for (Direction direction : Direction.values()) {
			random.setSeed(42L);
			this.renderQuads(bufferBuilder, bakedModel.getQuads(null, direction, random), i, stack);
		}

		random.setSeed(42L);
		this.renderQuads(bufferBuilder, bakedModel.getQuads(null, null, random), i, stack);
		tessellator.draw();
	}

	public void renderItemAndGlow(ItemStack stack, BakedModel bakedModel) {
		if (!stack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
			if (bakedModel.isBuiltin()) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableRescaleNormal();
				BuiltinModelItemRenderer.INSTANCE.render(stack);
			} else {
				this.renderItemModel(bakedModel, stack);
				if (stack.hasEnchantmentGlint()) {
					renderGlint(this.textureManager, () -> this.renderModelWithTint(bakedModel, -8372020), 8);
				}
			}

			GlStateManager.popMatrix();
		}
	}

	public static void renderGlint(TextureManager textureManager, Runnable renderer, int i) {
		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
		textureManager.bindTexture(ENCHANTMENT_GLINT_TEX);
		GlStateManager.matrixMode(5890);
		GlStateManager.pushMatrix();
		GlStateManager.scalef((float)i, (float)i, (float)i);
		float f = (float)(Util.getMeasuringTimeMs() % 3000L) / 3000.0F / (float)i;
		GlStateManager.translatef(f, 0.0F, 0.0F);
		GlStateManager.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
		renderer.run();
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.scalef((float)i, (float)i, (float)i);
		float g = (float)(Util.getMeasuringTimeMs() % 4873L) / 4873.0F / (float)i;
		GlStateManager.translatef(-g, 0.0F, 0.0F);
		GlStateManager.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
		renderer.run();
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
	}

	private void postNormalQuad(BufferBuilder buffer, BakedQuad bakedQuad) {
		Vec3i vec3i = bakedQuad.getFace().getVector();
		buffer.postNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
	}

	private void renderQuad(BufferBuilder bufferBuilder, BakedQuad bakedQuad, int color) {
		bufferBuilder.putVertexData(bakedQuad.getVertexData());
		bufferBuilder.setQuadColor(color);
		this.postNormalQuad(bufferBuilder, bakedQuad);
	}

	private void renderQuads(BufferBuilder bufferBuilder, List<BakedQuad> quads, int forcedColor, ItemStack stack) {
		boolean bl = forcedColor == -1 && !stack.isEmpty();
		int i = 0;

		for (int j = quads.size(); i < j; i++) {
			BakedQuad bakedQuad = (BakedQuad)quads.get(i);
			int k = forcedColor;
			if (bl && bakedQuad.hasColor()) {
				k = this.colorMap.getColorMultiplier(stack, bakedQuad.getColorIndex());
				k |= -16777216;
			}

			this.renderQuad(bufferBuilder, bakedQuad, k);
		}
	}

	public boolean hasDepthInGui(ItemStack stack) {
		BakedModel bakedModel = this.models.getModel(stack);
		return bakedModel == null ? false : bakedModel.hasDepth();
	}

	public void renderItem(ItemStack stack, ModelTransformation.Type type) {
		if (!stack.isEmpty()) {
			BakedModel bakedModel = this.getModel(stack);
			this.renderItem(stack, bakedModel, type, false);
		}
	}

	public BakedModel getModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		BakedModel bakedModel = this.models.getModel(stack);
		Item item = stack.getItem();
		return !item.hasPropertyGetters() ? bakedModel : this.getOverriddenModel(bakedModel, stack, world, livingEntity);
	}

	public BakedModel getHeldItemModel(ItemStack stack, World world, LivingEntity entity) {
		Item item = stack.getItem();
		BakedModel bakedModel;
		if (item == Items.TRIDENT) {
			bakedModel = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
		} else {
			bakedModel = this.models.getModel(stack);
		}

		return !item.hasPropertyGetters() ? bakedModel : this.getOverriddenModel(bakedModel, stack, world, entity);
	}

	public BakedModel getModel(ItemStack stack) {
		return this.getModel(stack, null, null);
	}

	private BakedModel getOverriddenModel(BakedModel model, ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
		BakedModel bakedModel = model.getItemPropertyOverrides().apply(model, stack, world, entity);
		return bakedModel == null ? this.models.getModelManager().getMissingModel() : bakedModel;
	}

	public void renderHeldItem(ItemStack stack, LivingEntity entity, ModelTransformation.Type type, boolean bl) {
		if (!stack.isEmpty() && entity != null) {
			BakedModel bakedModel = this.getHeldItemModel(stack, entity.world, entity);
			this.renderItem(stack, bakedModel, type, bl);
		}
	}

	protected void renderItem(ItemStack stack, BakedModel model, ModelTransformation.Type type, boolean bl) {
		if (!stack.isEmpty()) {
			this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.enableRescaleNormal();
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.enableBlend();
			GlStateManager.blendFuncSeparate(
				GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
			);
			GlStateManager.pushMatrix();
			ModelTransformation modelTransformation = model.getTransformation();
			ModelTransformation.applyGl(modelTransformation.getTransformation(type), bl);
			if (this.areFacesFlippedBy(modelTransformation.getTransformation(type))) {
				GlStateManager.cullFace(GlStateManager.FaceSides.FRONT);
			}

			this.renderItemAndGlow(stack, model);
			GlStateManager.cullFace(GlStateManager.FaceSides.BACK);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
		}
	}

	private boolean areFacesFlippedBy(Transformation transformation) {
		return transformation.scale.getX() < 0.0F ^ transformation.scale.getY() < 0.0F ^ transformation.scale.getZ() < 0.0F;
	}

	public void renderGuiItemIcon(ItemStack stack, int x, int y) {
		this.renderGuiItemModel(stack, x, y, this.getModel(stack));
	}

	protected void renderGuiItemModel(ItemStack stack, int x, int y, BakedModel model) {
		GlStateManager.pushMatrix();
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.prepareGuiItemRender(x, y, model.hasDepth());
		model.getTransformation().applyGl(ModelTransformation.Type.GUI);
		this.renderItemAndGlow(stack, model);
		GlStateManager.disableAlphaTest();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
	}

	private void prepareGuiItemRender(int x, int y, boolean depth) {
		GlStateManager.translatef((float)x, (float)y, 100.0F + this.zOffset);
		GlStateManager.translatef(8.0F, 8.0F, 0.0F);
		GlStateManager.scalef(1.0F, -1.0F, 1.0F);
		GlStateManager.scalef(16.0F, 16.0F, 16.0F);
		if (depth) {
			GlStateManager.enableLighting();
		} else {
			GlStateManager.disableLighting();
		}
	}

	public void renderGuiItem(ItemStack stack, int x, int y) {
		this.renderGuiItem(MinecraftClient.getInstance().player, stack, x, y);
	}

	public void renderGuiItem(@Nullable LivingEntity entity, ItemStack itemStack, int x, int y) {
		if (!itemStack.isEmpty()) {
			this.zOffset += 50.0F;

			try {
				this.renderGuiItemModel(itemStack, x, y, this.getModel(itemStack, null, entity));
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Rendering item");
				CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
				crashReportSection.add("Item Type", (CrashCallable<String>)(() -> String.valueOf(itemStack.getItem())));
				crashReportSection.add("Item Damage", (CrashCallable<String>)(() -> String.valueOf(itemStack.getDamage())));
				crashReportSection.add("Item NBT", (CrashCallable<String>)(() -> String.valueOf(itemStack.getTag())));
				crashReportSection.add("Item Foil", (CrashCallable<String>)(() -> String.valueOf(itemStack.hasEnchantmentGlint())));
				throw new CrashException(crashReport);
			}

			this.zOffset -= 50.0F;
		}
	}

	public void renderGuiItemOverlay(TextRenderer fontRenderer, ItemStack stack, int x, int y) {
		this.renderGuiItemOverlay(fontRenderer, stack, x, y, null);
	}

	public void renderGuiItemOverlay(TextRenderer fontRenderer, ItemStack stack, int x, int y, @Nullable String amountText) {
		if (!stack.isEmpty()) {
			if (stack.getCount() != 1 || amountText != null) {
				String string = amountText == null ? String.valueOf(stack.getCount()) : amountText;
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableBlend();
				fontRenderer.drawWithShadow(string, (float)(x + 19 - 2 - fontRenderer.getStringWidth(string)), (float)(y + 6 + 3), 16777215);
				GlStateManager.enableBlend();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}

			if (stack.isDamaged()) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableTexture();
				GlStateManager.disableAlphaTest();
				GlStateManager.disableBlend();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				float f = (float)stack.getDamage();
				float g = (float)stack.getMaxDamage();
				float h = Math.max(0.0F, (g - f) / g);
				int i = Math.round(13.0F - f * 13.0F / g);
				int j = MathHelper.hsvToRgb(h / 3.0F, 1.0F, 1.0F);
				this.renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
				this.renderGuiQuad(bufferBuilder, x + 2, y + 13, i, 1, j >> 16 & 0xFF, j >> 8 & 0xFF, j & 0xFF, 255);
				GlStateManager.enableBlend();
				GlStateManager.enableAlphaTest();
				GlStateManager.enableTexture();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}

			ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
			float k = clientPlayerEntity == null
				? 0.0F
				: clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
			if (k > 0.0F) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableTexture();
				Tessellator tessellator2 = Tessellator.getInstance();
				BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
				this.renderGuiQuad(bufferBuilder2, x, y + MathHelper.floor(16.0F * (1.0F - k)), 16, MathHelper.ceil(16.0F * k), 255, 255, 255, 127);
				GlStateManager.enableTexture();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}
		}
	}

	private void renderGuiQuad(BufferBuilder buffer, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		buffer.begin(7, VertexFormats.POSITION_COLOR);
		buffer.vertex((double)(x + 0), (double)(y + 0), 0.0).color(red, green, blue, alpha).next();
		buffer.vertex((double)(x + 0), (double)(y + height), 0.0).color(red, green, blue, alpha).next();
		buffer.vertex((double)(x + width), (double)(y + height), 0.0).color(red, green, blue, alpha).next();
		buffer.vertex((double)(x + width), (double)(y + 0), 0.0).color(red, green, blue, alpha).next();
		Tessellator.getInstance().draw();
	}

	@Override
	public void apply(ResourceManager manager) {
		this.models.reloadModels();
	}
}
