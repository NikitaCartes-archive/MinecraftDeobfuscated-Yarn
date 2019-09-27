package net.minecraft.client.render.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4589;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
import net.minecraft.block.BlockRenderLayer;
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
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.SynchronousResourceReloadListener;
import net.minecraft.util.Identifier;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class ItemRenderer implements SynchronousResourceReloadListener {
	public static final Identifier field_21010 = new Identifier("textures/misc/enchanted_item_glint.png");
	private static final Set<Item> WITHOUT_MODELS = Sets.<Item>newHashSet(Items.AIR);
	public float zOffset;
	private final ItemModels models;
	private final TextureManager textureManager;
	private final ItemColors colorMap;

	public ItemRenderer(TextureManager textureManager, BakedModelManager bakedModelManager, ItemColors itemColors) {
		this.textureManager = textureManager;
		this.models = new ItemModels(bakedModelManager);

		for (Item item : Registry.ITEM) {
			if (!WITHOUT_MODELS.contains(item)) {
				this.models.putModel(item, new ModelIdentifier(Registry.ITEM.getId(item), "inventory"));
			}
		}

		this.colorMap = itemColors;
	}

	public ItemModels getModels() {
		return this.models;
	}

	private void method_23182(BakedModel bakedModel, ItemStack itemStack, int i, class_4587 arg, class_4588 arg2) {
		Random random = new Random();
		long l = 42L;

		for (Direction direction : Direction.values()) {
			random.setSeed(42L);
			this.method_23180(arg, arg2, bakedModel.getQuads(null, direction, random), itemStack, i);
		}

		random.setSeed(42L);
		this.method_23180(arg, arg2, bakedModel.getQuads(null, null, random), itemStack, i);
	}

	public void method_23179(ItemStack itemStack, ModelTransformation.Type type, boolean bl, class_4587 arg, class_4597 arg2, int i, BakedModel bakedModel) {
		if (!itemStack.isEmpty()) {
			arg.method_22903();
			if (itemStack.getItem() == Items.TRIDENT && type == ModelTransformation.Type.GUI) {
				bakedModel = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident#inventory"));
			}

			bakedModel.getTransformation().getTransformation(type).method_23075(bl, arg);
			arg.method_22904(-0.5, -0.5, -0.5);
			if (!bakedModel.isBuiltin() && (itemStack.getItem() != Items.TRIDENT || type == ModelTransformation.Type.GUI)) {
				class_4588 lv = method_23181(arg2, SpriteAtlasTexture.BLOCK_ATLAS_TEX, true, itemStack.hasEnchantmentGlint(), bakedModel.hasDepthInGui());
				class_4608.method_23211(lv);
				this.method_23182(bakedModel, itemStack, i, arg, lv);
				lv.method_22923();
			} else {
				ItemDynamicRenderer.INSTANCE.render(itemStack, arg, arg2, i);
			}

			arg.method_22909();
		}
	}

	public static class_4588 method_23181(class_4597 arg, Identifier identifier, boolean bl, boolean bl2, boolean bl3) {
		return (class_4588)(bl2
			? new class_4589(
				ImmutableList.of(arg.getBuffer(bl ? BlockRenderLayer.GLINT : BlockRenderLayer.ENTITY_GLINT), arg.getBuffer(BlockRenderLayer.method_23017(identifier)))
			)
			: arg.getBuffer(BlockRenderLayer.method_23017(identifier)));
	}

	private void method_23180(class_4587 arg, class_4588 arg2, List<BakedQuad> list, ItemStack itemStack, int i) {
		boolean bl = !itemStack.isEmpty();
		Matrix4f matrix4f = arg.method_22910();

		for (BakedQuad bakedQuad : list) {
			int j = -1;
			if (bl && bakedQuad.hasColor()) {
				j = this.colorMap.getColorMultiplier(itemStack, bakedQuad.getColorIndex());
			}

			float f = (float)(j >> 16 & 0xFF) / 255.0F;
			float g = (float)(j >> 8 & 0xFF) / 255.0F;
			float h = (float)(j & 0xFF) / 255.0F;
			arg2.method_22919(matrix4f, bakedQuad, f, g, h, i);
		}
	}

	public BakedModel getHeldItemModel(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		Item item = itemStack.getItem();
		BakedModel bakedModel;
		if (item == Items.TRIDENT) {
			bakedModel = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
		} else {
			bakedModel = this.models.getModel(itemStack);
		}

		return !item.hasPropertyGetters() ? bakedModel : this.getOverriddenModel(bakedModel, itemStack, world, livingEntity);
	}

	private BakedModel getOverriddenModel(BakedModel bakedModel, ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		BakedModel bakedModel2 = bakedModel.getItemPropertyOverrides().apply(bakedModel, itemStack, world, livingEntity);
		return bakedModel2 == null ? this.models.getModelManager().getMissingModel() : bakedModel2;
	}

	public void method_23178(ItemStack itemStack, ModelTransformation.Type type, int i, class_4587 arg, class_4597 arg2) {
		this.method_23177(null, itemStack, type, false, arg, arg2, null, i);
	}

	public void method_23177(
		@Nullable LivingEntity livingEntity,
		ItemStack itemStack,
		ModelTransformation.Type type,
		boolean bl,
		class_4587 arg,
		class_4597 arg2,
		@Nullable World world,
		int i
	) {
		if (!itemStack.isEmpty()) {
			BakedModel bakedModel = this.getHeldItemModel(itemStack, world, livingEntity);
			this.method_23179(itemStack, type, bl, arg, arg2, i, bakedModel);
		}
	}

	public void renderGuiItemIcon(ItemStack itemStack, int i, int j) {
		this.renderGuiItemModel(itemStack, i, j, this.getHeldItemModel(itemStack, null, null));
	}

	protected void renderGuiItemModel(ItemStack itemStack, int i, int j, BakedModel bakedModel) {
		RenderSystem.pushMatrix();
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).pushFilter(false, false);
		RenderSystem.enableRescaleNormal();
		RenderSystem.enableAlphaTest();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.class_4535.SRC_ALPHA, GlStateManager.class_4534.ONE_MINUS_SRC_ALPHA);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.translatef((float)i, (float)j, 100.0F + this.zOffset);
		RenderSystem.translatef(8.0F, 8.0F, 0.0F);
		RenderSystem.scalef(1.0F, -1.0F, 1.0F);
		RenderSystem.scalef(16.0F, 16.0F, 16.0F);
		class_4587 lv = new class_4587();
		class_4597.class_4598 lv2 = MinecraftClient.getInstance().method_22940().method_23000();
		this.method_23179(itemStack, ModelTransformation.Type.GUI, false, lv, lv2, 15728880, bakedModel);
		lv2.method_22993();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableRescaleNormal();
		RenderSystem.popMatrix();
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).popFilter();
	}

	public void renderGuiItem(ItemStack itemStack, int i, int j) {
		this.renderGuiItem(MinecraftClient.getInstance().player, itemStack, i, j);
	}

	public void renderGuiItem(@Nullable LivingEntity livingEntity, ItemStack itemStack, int i, int j) {
		if (!itemStack.isEmpty()) {
			this.zOffset += 50.0F;

			try {
				this.renderGuiItemModel(itemStack, i, j, this.getHeldItemModel(itemStack, null, livingEntity));
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

	public void renderGuiItemOverlay(TextRenderer textRenderer, ItemStack itemStack, int i, int j) {
		this.renderGuiItemOverlay(textRenderer, itemStack, i, j, null);
	}

	public void renderGuiItemOverlay(TextRenderer textRenderer, ItemStack itemStack, int i, int j, @Nullable String string) {
		if (!itemStack.isEmpty()) {
			if (itemStack.getCount() != 1 || string != null) {
				String string2 = string == null ? String.valueOf(itemStack.getCount()) : string;
				RenderSystem.disableDepthTest();
				RenderSystem.disableBlend();
				textRenderer.drawWithShadow(string2, (float)(i + 19 - 2 - textRenderer.getStringWidth(string2)), (float)(j + 6 + 3), 16777215);
				RenderSystem.enableBlend();
				RenderSystem.enableDepthTest();
			}

			if (itemStack.isDamaged()) {
				RenderSystem.disableDepthTest();
				RenderSystem.disableTexture();
				RenderSystem.disableAlphaTest();
				RenderSystem.disableBlend();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBufferBuilder();
				float f = (float)itemStack.getDamage();
				float g = (float)itemStack.getMaxDamage();
				float h = Math.max(0.0F, (g - f) / g);
				int k = Math.round(13.0F - f * 13.0F / g);
				int l = MathHelper.hsvToRgb(h / 3.0F, 1.0F, 1.0F);
				this.renderGuiQuad(bufferBuilder, i + 2, j + 13, 13, 2, 0, 0, 0, 255);
				this.renderGuiQuad(bufferBuilder, i + 2, j + 13, k, 1, l >> 16 & 0xFF, l >> 8 & 0xFF, l & 0xFF, 255);
				RenderSystem.enableBlend();
				RenderSystem.enableAlphaTest();
				RenderSystem.enableTexture();
				RenderSystem.enableDepthTest();
			}

			ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
			float m = clientPlayerEntity == null
				? 0.0F
				: clientPlayerEntity.getItemCooldownManager().getCooldownProgress(itemStack.getItem(), MinecraftClient.getInstance().getTickDelta());
			if (m > 0.0F) {
				RenderSystem.disableDepthTest();
				RenderSystem.disableTexture();
				Tessellator tessellator2 = Tessellator.getInstance();
				BufferBuilder bufferBuilder2 = tessellator2.getBufferBuilder();
				this.renderGuiQuad(bufferBuilder2, i, j + MathHelper.floor(16.0F * (1.0F - m)), 16, MathHelper.ceil(16.0F * m), 255, 255, 255, 127);
				RenderSystem.enableTexture();
				RenderSystem.enableDepthTest();
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
	public void apply(ResourceManager resourceManager) {
		this.models.reloadModels();
	}
}
