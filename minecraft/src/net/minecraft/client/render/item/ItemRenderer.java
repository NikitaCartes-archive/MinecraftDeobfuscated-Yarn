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
import net.minecraft.client.font.FontRenderer;
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
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.crash.ICrashCallable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ItemRenderer implements ResourceReloadListener {
	public static final Identifier ENCHANTMENT_GLINT_TEX = new Identifier("textures/misc/enchanted_item_glint.png");
	private static final Set<Item> WITHOUT_MODELS = Sets.<Item>newHashSet(Items.AIR);
	public float zOffset;
	private final ItemModels models;
	private final TextureManager textureManager;
	private final ItemColorMap colorMap;

	public ItemRenderer(TextureManager textureManager, BakedModelManager bakedModelManager, ItemColorMap itemColorMap) {
		this.textureManager = textureManager;
		this.models = new ItemModels(bakedModelManager);

		for (Item item : Registry.ITEM) {
			if (!WITHOUT_MODELS.contains(item)) {
				this.models.putModel(item, new ModelIdentifier(Registry.ITEM.getId(item), "inventory"));
			}
		}

		this.colorMap = itemColorMap;
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
		if (!itemStack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
			if (bakedModel.isBuiltin()) {
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.enableRescaleNormal();
				ItemDynamicRenderer.INSTANCE.render(itemStack);
			} else {
				this.renderItemModel(bakedModel, itemStack);
				if (itemStack.hasEnchantmentGlint()) {
					renderGlint(this.textureManager, () -> this.renderModelWithTint(bakedModel, -8372020), 8);
				}
			}

			GlStateManager.popMatrix();
		}
	}

	public static void renderGlint(TextureManager textureManager, Runnable runnable, int i) {
		GlStateManager.depthMask(false);
		GlStateManager.depthFunc(514);
		GlStateManager.disableLighting();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
		textureManager.bindTexture(ENCHANTMENT_GLINT_TEX);
		GlStateManager.matrixMode(5890);
		GlStateManager.pushMatrix();
		GlStateManager.scalef((float)i, (float)i, (float)i);
		float f = (float)(SystemUtil.getMeasuringTimeMs() % 3000L) / 3000.0F / (float)i;
		GlStateManager.translatef(f, 0.0F, 0.0F);
		GlStateManager.rotatef(-50.0F, 0.0F, 0.0F, 1.0F);
		runnable.run();
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		GlStateManager.scalef((float)i, (float)i, (float)i);
		float g = (float)(SystemUtil.getMeasuringTimeMs() % 4873L) / 4873.0F / (float)i;
		GlStateManager.translatef(-g, 0.0F, 0.0F);
		GlStateManager.rotatef(10.0F, 0.0F, 0.0F, 1.0F);
		runnable.run();
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(5888);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableLighting();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);
		textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
	}

	private void postNormalQuad(BufferBuilder bufferBuilder, BakedQuad bakedQuad) {
		Vec3i vec3i = bakedQuad.getFace().getVector();
		bufferBuilder.postNormal((float)vec3i.getX(), (float)vec3i.getY(), (float)vec3i.getZ());
	}

	private void renderQuad(BufferBuilder bufferBuilder, BakedQuad bakedQuad, int i) {
		bufferBuilder.putVertexData(bakedQuad.getVertexData());
		bufferBuilder.setQuadColor(i);
		this.postNormalQuad(bufferBuilder, bakedQuad);
	}

	private void renderQuads(BufferBuilder bufferBuilder, List<BakedQuad> list, int i, ItemStack itemStack) {
		boolean bl = i == -1 && !itemStack.isEmpty();
		int j = 0;

		for (int k = list.size(); j < k; j++) {
			BakedQuad bakedQuad = (BakedQuad)list.get(j);
			int l = i;
			if (bl && bakedQuad.hasColor()) {
				l = this.colorMap.getRenderColor(itemStack, bakedQuad.getColorIndex());
				l |= -16777216;
			}

			this.renderQuad(bufferBuilder, bakedQuad, l);
		}
	}

	public boolean hasDepthInGui(ItemStack itemStack) {
		BakedModel bakedModel = this.models.getModel(itemStack);
		return bakedModel == null ? false : bakedModel.hasDepthInGui();
	}

	public void renderItem(ItemStack itemStack, ModelTransformation.Type type) {
		if (!itemStack.isEmpty()) {
			BakedModel bakedModel = this.getModel(itemStack);
			this.renderItem(itemStack, bakedModel, type, false);
		}
	}

	public BakedModel getModel(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		BakedModel bakedModel = this.models.getModel(itemStack);
		Item item = itemStack.getItem();
		return !item.hasProperties() ? bakedModel : this.getOverriddenModel(bakedModel, itemStack, world, livingEntity);
	}

	public BakedModel getHeldItemModel(ItemStack itemStack, World world, LivingEntity livingEntity) {
		Item item = itemStack.getItem();
		BakedModel bakedModel;
		if (item == Items.field_8547) {
			bakedModel = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
		} else {
			bakedModel = this.models.getModel(itemStack);
		}

		return !item.hasProperties() ? bakedModel : this.getOverriddenModel(bakedModel, itemStack, world, livingEntity);
	}

	public BakedModel getModel(ItemStack itemStack) {
		return this.getModel(itemStack, null, null);
	}

	private BakedModel getOverriddenModel(BakedModel bakedModel, ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		BakedModel bakedModel2 = bakedModel.getItemPropertyOverrides().apply(bakedModel, itemStack, world, livingEntity);
		return bakedModel2 == null ? this.models.getModelManager().getMissingModel() : bakedModel2;
	}

	public void renderHeldItem(ItemStack itemStack, LivingEntity livingEntity, ModelTransformation.Type type, boolean bl) {
		if (!itemStack.isEmpty() && livingEntity != null) {
			BakedModel bakedModel = this.getHeldItemModel(itemStack, livingEntity.world, livingEntity);
			this.renderItem(itemStack, bakedModel, type, bl);
		}
	}

	protected void renderItem(ItemStack itemStack, BakedModel bakedModel, ModelTransformation.Type type, boolean bl) {
		if (!itemStack.isEmpty()) {
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
			ModelTransformation modelTransformation = bakedModel.getTransformation();
			ModelTransformation.applyGl(modelTransformation.getTransformation(type), bl);
			if (this.areFacesFlippedBy(modelTransformation.getTransformation(type))) {
				GlStateManager.cullFace(GlStateManager.FaceSides.field_5068);
			}

			this.renderItemAndGlow(itemStack, bakedModel);
			GlStateManager.cullFace(GlStateManager.FaceSides.field_5070);
			GlStateManager.popMatrix();
			GlStateManager.disableRescaleNormal();
			GlStateManager.disableBlend();
			this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
			this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
		}
	}

	private boolean areFacesFlippedBy(Transformation transformation) {
		return transformation.scale.x() < 0.0F ^ transformation.scale.y() < 0.0F ^ transformation.scale.z() < 0.0F;
	}

	public void renderGuiItemIcon(ItemStack itemStack, int i, int j) {
		this.renderGuiItemModel(itemStack, i, j, this.getModel(itemStack));
	}

	protected void renderGuiItemModel(ItemStack itemStack, int i, int j, BakedModel bakedModel) {
		GlStateManager.pushMatrix();
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.prepareGuiItemRender(i, j, bakedModel.hasDepthInGui());
		bakedModel.getTransformation().applyGl(ModelTransformation.Type.GUI);
		this.renderItemAndGlow(itemStack, bakedModel);
		GlStateManager.disableAlphaTest();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableLighting();
		GlStateManager.popMatrix();
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
	}

	private void prepareGuiItemRender(int i, int j, boolean bl) {
		GlStateManager.translatef((float)i, (float)j, 100.0F + this.zOffset);
		GlStateManager.translatef(8.0F, 8.0F, 0.0F);
		GlStateManager.scalef(1.0F, -1.0F, 1.0F);
		GlStateManager.scalef(16.0F, 16.0F, 16.0F);
		if (bl) {
			GlStateManager.enableLighting();
		} else {
			GlStateManager.disableLighting();
		}
	}

	public void renderGuiItem(ItemStack itemStack, int i, int j) {
		this.renderGuiItem(MinecraftClient.getInstance().player, itemStack, i, j);
	}

	public void renderGuiItem(@Nullable LivingEntity livingEntity, ItemStack itemStack, int i, int j) {
		if (!itemStack.isEmpty()) {
			this.zOffset += 50.0F;

			try {
				this.renderGuiItemModel(itemStack, i, j, this.getModel(itemStack, null, livingEntity));
			} catch (Throwable var8) {
				CrashReport crashReport = CrashReport.create(var8, "Rendering item");
				CrashReportSection crashReportSection = crashReport.addElement("Item being rendered");
				crashReportSection.add("Item Type", (ICrashCallable<String>)(() -> String.valueOf(itemStack.getItem())));
				crashReportSection.add("Item Damage", (ICrashCallable<String>)(() -> String.valueOf(itemStack.getDamage())));
				crashReportSection.add("Item NBT", (ICrashCallable<String>)(() -> String.valueOf(itemStack.getTag())));
				crashReportSection.add("Item Foil", (ICrashCallable<String>)(() -> String.valueOf(itemStack.hasEnchantmentGlint())));
				throw new CrashException(crashReport);
			}

			this.zOffset -= 50.0F;
		}
	}

	public void renderGuiItemOverlay(FontRenderer fontRenderer, ItemStack itemStack, int i, int j) {
		this.renderGuiItemOverlay(fontRenderer, itemStack, i, j, null);
	}

	public void renderGuiItemOverlay(FontRenderer fontRenderer, ItemStack itemStack, int i, int j, @Nullable String string) {
		if (!itemStack.isEmpty()) {
			if (itemStack.getAmount() != 1 || string != null) {
				String string2 = string == null ? String.valueOf(itemStack.getAmount()) : string;
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableBlend();
				fontRenderer.drawWithShadow(string2, (float)(i + 19 - 2 - fontRenderer.getStringWidth(string2)), (float)(j + 6 + 3), 16777215);
				GlStateManager.enableBlend();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}

			if (itemStack.isDamaged()) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableTexture();
				GlStateManager.disableAlphaTest();
				GlStateManager.disableBlend();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
				float f = (float)itemStack.getDamage();
				float g = (float)itemStack.getDurability();
				float h = Math.max(0.0F, (g - f) / g);
				int k = Math.round(13.0F - f * 13.0F / g);
				int l = MathHelper.hsvToRgb(h / 3.0F, 1.0F, 1.0F);
				this.renderGuiQuad(bufferBuilder, i + 2, j + 13, 13, 2, 0, 0, 0, 255);
				this.renderGuiQuad(bufferBuilder, i + 2, j + 13, k, 1, l >> 16 & 0xFF, l >> 8 & 0xFF, l & 0xFF, 255);
				GlStateManager.enableBlend();
				GlStateManager.enableAlphaTest();
				GlStateManager.enableTexture();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}

			ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
			float m = clientPlayerEntity == null
				? 0.0F
				: clientPlayerEntity.getItemCooldownManager().getCooldownProgress(itemStack.getItem(), MinecraftClient.getInstance().getTickDelta());
			if (m > 0.0F) {
				GlStateManager.disableLighting();
				GlStateManager.disableDepthTest();
				GlStateManager.disableTexture();
				Tessellator tessellator2 = Tessellator.getInstance();
				BufferBuilder bufferBuilder2 = tessellator2.getBufferBuilder();
				this.renderGuiQuad(bufferBuilder2, i, j + MathHelper.floor(16.0F * (1.0F - m)), 16, MathHelper.ceil(16.0F * m), 255, 255, 255, 127);
				GlStateManager.enableTexture();
				GlStateManager.enableLighting();
				GlStateManager.enableDepthTest();
			}
		}
	}

	private void renderGuiQuad(BufferBuilder bufferBuilder, int i, int j, int k, int l, int m, int n, int o, int p) {
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex((double)(i + 0), (double)(j + 0), 0.0).color(m, n, o, p).next();
		bufferBuilder.vertex((double)(i + 0), (double)(j + l), 0.0).color(m, n, o, p).next();
		bufferBuilder.vertex((double)(i + k), (double)(j + l), 0.0).color(m, n, o, p).next();
		bufferBuilder.vertex((double)(i + k), (double)(j + 0), 0.0).color(m, n, o, p).next();
		Tessellator.getInstance().draw();
	}

	@Override
	public void onResourceReload(ResourceManager resourceManager) {
		this.models.reloadModels();
	}
}
