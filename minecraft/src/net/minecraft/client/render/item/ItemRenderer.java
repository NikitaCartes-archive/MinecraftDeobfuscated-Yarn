package net.minecraft.client.render.item;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
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
import net.minecraft.client.render.DelegatingVertexConsumer;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
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

	public ItemRenderer(TextureManager textureManager, BakedModelManager bakedModelManager, ItemColors colorMap) {
		this.textureManager = textureManager;
		this.models = new ItemModels(bakedModelManager);

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

	public void method_23179(
		ItemStack itemStack,
		ModelTransformation.Type type,
		boolean bl,
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		int i,
		int j,
		BakedModel bakedModel
	) {
		if (!itemStack.isEmpty()) {
			matrixStack.push();
			boolean bl2 = type == ModelTransformation.Type.GUI;
			boolean bl3 = bl2 || type == ModelTransformation.Type.GROUND || type == ModelTransformation.Type.FIXED;
			if (itemStack.getItem() == Items.TRIDENT && bl3) {
				bakedModel = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident#inventory"));
			}

			bakedModel.getTransformation().getTransformation(type).method_23075(bl, matrixStack);
			matrixStack.translate(-0.5, -0.5, -0.5);
			if (!bakedModel.isBuiltin() && (itemStack.getItem() != Items.TRIDENT || bl3)) {
				RenderLayer renderLayer = RenderLayers.getItemLayer(itemStack);
				RenderLayer renderLayer2;
				if (bl2 && Objects.equals(renderLayer, RenderLayer.getEntityTranslucent(SpriteAtlasTexture.BLOCK_ATLAS_TEX))) {
					renderLayer2 = RenderLayer.getEntityTranslucentCull(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
				} else {
					renderLayer2 = renderLayer;
				}

				VertexConsumer vertexConsumer = getArmorVertexConsumer(vertexConsumerProvider, renderLayer2, true, itemStack.hasEnchantmentGlint());
				this.method_23182(bakedModel, itemStack, i, j, matrixStack, vertexConsumer);
			} else {
				BuiltinModelItemRenderer.INSTANCE.render(itemStack, matrixStack, vertexConsumerProvider, i, j);
			}

			matrixStack.pop();
		}
	}

	public static VertexConsumer getArmorVertexConsumer(VertexConsumerProvider vertexConsumers, RenderLayer layer, boolean bl, boolean glint) {
		return (VertexConsumer)(glint
			? new DelegatingVertexConsumer(
				ImmutableList.of(vertexConsumers.getBuffer(bl ? RenderLayer.getGlint() : RenderLayer.getEntityGlint()), vertexConsumers.getBuffer(layer))
			)
			: vertexConsumers.getBuffer(layer));
	}

	private void method_23180(MatrixStack matrixStack, VertexConsumer vertexConsumer, List<BakedQuad> list, ItemStack itemStack, int i, int j) {
		boolean bl = !itemStack.isEmpty();
		MatrixStack.Entry entry = matrixStack.peek();

		for (BakedQuad bakedQuad : list) {
			int k = -1;
			if (bl && bakedQuad.hasColor()) {
				k = this.colorMap.getColorMultiplier(itemStack, bakedQuad.getColorIndex());
			}

			float f = (float)(k >> 16 & 0xFF) / 255.0F;
			float g = (float)(k >> 8 & 0xFF) / 255.0F;
			float h = (float)(k & 0xFF) / 255.0F;
			vertexConsumer.quad(entry, bakedQuad, f, g, h, i, j);
		}
	}

	public BakedModel getHeldItemModel(ItemStack stack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		Item item = stack.getItem();
		BakedModel bakedModel;
		if (item == Items.TRIDENT) {
			bakedModel = this.models.getModelManager().getModel(new ModelIdentifier("minecraft:trident_in_hand#inventory"));
		} else {
			bakedModel = this.models.getModel(stack);
		}

		return !item.hasPropertyGetters() ? bakedModel : this.getOverriddenModel(bakedModel, stack, world, livingEntity);
	}

	private BakedModel getOverriddenModel(BakedModel bakedModel, ItemStack stack, @Nullable World world, @Nullable LivingEntity livingEntity) {
		BakedModel bakedModel2 = bakedModel.getItemPropertyOverrides().apply(bakedModel, stack, world, livingEntity);
		return bakedModel2 == null ? this.models.getModelManager().getMissingModel() : bakedModel2;
	}

	public void method_23178(
		ItemStack itemStack, ModelTransformation.Type type, int i, int j, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider
	) {
		this.method_23177(null, itemStack, type, false, matrixStack, vertexConsumerProvider, null, i, j);
	}

	public void method_23177(
		@Nullable LivingEntity livingEntity,
		ItemStack itemStack,
		ModelTransformation.Type type,
		boolean bl,
		MatrixStack matrixStack,
		VertexConsumerProvider vertexConsumerProvider,
		@Nullable World world,
		int i,
		int j
	) {
		if (!itemStack.isEmpty()) {
			BakedModel bakedModel = this.getHeldItemModel(itemStack, world, livingEntity);
			this.method_23179(itemStack, type, bl, matrixStack, vertexConsumerProvider, i, j, bakedModel);
		}
	}

	public void renderGuiItemIcon(ItemStack stack, int x, int y) {
		this.renderGuiItemModel(stack, x, y, this.getHeldItemModel(stack, null, null));
	}

	protected void renderGuiItemModel(ItemStack itemStack, int x, int y, BakedModel bakedModel) {
		RenderSystem.pushMatrix();
		this.textureManager.bindTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX);
		this.textureManager.getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEX).setFilter(false, false);
		RenderSystem.enableRescaleNormal();
		RenderSystem.enableAlphaTest();
		RenderSystem.defaultAlphaFunc();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.translatef((float)x, (float)y, 100.0F + this.zOffset);
		RenderSystem.translatef(8.0F, 8.0F, 0.0F);
		RenderSystem.scalef(1.0F, -1.0F, 1.0F);
		RenderSystem.scalef(16.0F, 16.0F, 16.0F);
		MatrixStack matrixStack = new MatrixStack();
		VertexConsumerProvider.Immediate immediate = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		this.method_23179(itemStack, ModelTransformation.Type.GUI, false, matrixStack, immediate, 15728880, OverlayTexture.DEFAULT_UV, bakedModel);
		immediate.draw();
		RenderSystem.enableDepthTest();
		RenderSystem.disableAlphaTest();
		RenderSystem.disableRescaleNormal();
		RenderSystem.popMatrix();
	}

	public void renderGuiItem(ItemStack stack, int x, int y) {
		this.renderGuiItem(MinecraftClient.getInstance().player, stack, x, y);
	}

	public void renderGuiItem(@Nullable LivingEntity livingEntity, ItemStack itemStack, int x, int y) {
		if (!itemStack.isEmpty()) {
			this.zOffset += 50.0F;

			try {
				this.renderGuiItemModel(itemStack, x, y, this.getHeldItemModel(itemStack, null, livingEntity));
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
			MatrixStack matrixStack = new MatrixStack();
			if (stack.getCount() != 1 || amountText != null) {
				String string = amountText == null ? String.valueOf(stack.getCount()) : amountText;
				matrixStack.translate(0.0, 0.0, (double)(this.zOffset + 200.0F));
				VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
				fontRenderer.draw(
					string,
					(float)(x + 19 - 2 - fontRenderer.getStringWidth(string)),
					(float)(y + 6 + 3),
					16777215,
					true,
					matrixStack.peek().getModel(),
					immediate,
					false,
					0,
					15728880
				);
				immediate.draw();
			}

			if (stack.isDamaged()) {
				RenderSystem.disableDepthTest();
				RenderSystem.disableTexture();
				RenderSystem.disableAlphaTest();
				RenderSystem.disableBlend();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder bufferBuilder = tessellator.getBuffer();
				float f = (float)stack.getDamage();
				float g = (float)stack.getMaxDamage();
				float h = Math.max(0.0F, (g - f) / g);
				int i = Math.round(13.0F - f * 13.0F / g);
				int j = MathHelper.hsvToRgb(h / 3.0F, 1.0F, 1.0F);
				this.renderGuiQuad(bufferBuilder, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
				this.renderGuiQuad(bufferBuilder, x + 2, y + 13, i, 1, j >> 16 & 0xFF, j >> 8 & 0xFF, j & 0xFF, 255);
				RenderSystem.enableBlend();
				RenderSystem.enableAlphaTest();
				RenderSystem.enableTexture();
				RenderSystem.enableDepthTest();
			}

			ClientPlayerEntity clientPlayerEntity = MinecraftClient.getInstance().player;
			float k = clientPlayerEntity == null
				? 0.0F
				: clientPlayerEntity.getItemCooldownManager().getCooldownProgress(stack.getItem(), MinecraftClient.getInstance().getTickDelta());
			if (k > 0.0F) {
				RenderSystem.disableDepthTest();
				RenderSystem.disableTexture();
				Tessellator tessellator2 = Tessellator.getInstance();
				BufferBuilder bufferBuilder2 = tessellator2.getBuffer();
				this.renderGuiQuad(bufferBuilder2, x, y + MathHelper.floor(16.0F * (1.0F - k)), 16, MathHelper.ceil(16.0F * k), 255, 255, 255, 127);
				RenderSystem.enableTexture();
				RenderSystem.enableDepthTest();
			}
		}
	}

	private void renderGuiQuad(BufferBuilder bufferBuilder, int x, int y, int width, int height, int red, int green, int blue, int alpha) {
		bufferBuilder.begin(7, VertexFormats.POSITION_COLOR);
		bufferBuilder.vertex((double)(x + 0), (double)(y + 0), 0.0).color(red, green, blue, alpha).next();
		bufferBuilder.vertex((double)(x + 0), (double)(y + height), 0.0).color(red, green, blue, alpha).next();
		bufferBuilder.vertex((double)(x + width), (double)(y + height), 0.0).color(red, green, blue, alpha).next();
		bufferBuilder.vertex((double)(x + width), (double)(y + 0), 0.0).color(red, green, blue, alpha).next();
		Tessellator.getInstance().draw();
	}

	@Override
	public void apply(ResourceManager manager) {
		this.models.reloadModels();
	}
}
