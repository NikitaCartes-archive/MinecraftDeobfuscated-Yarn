package net.minecraft.client.render.entity.feature;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.WolfEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.type.DyedColorComponent;
import net.minecraft.entity.passive.Cracks;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.AnimalArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public class WolfArmorFeatureRenderer extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
	private final WolfEntityModel<WolfEntity> model;
	private static final Map<Cracks.CrackLevel, Identifier> CRACK_TEXTURES = Map.of(
		Cracks.CrackLevel.LOW,
		new Identifier("textures/entity/wolf/wolf_armor_crackiness_low.png"),
		Cracks.CrackLevel.MEDIUM,
		new Identifier("textures/entity/wolf/wolf_armor_crackiness_medium.png"),
		Cracks.CrackLevel.HIGH,
		new Identifier("textures/entity/wolf/wolf_armor_crackiness_high.png")
	);

	public WolfArmorFeatureRenderer(FeatureRendererContext<WolfEntity, WolfEntityModel<WolfEntity>> context, EntityModelLoader loader) {
		super(context);
		this.model = new WolfEntityModel<>(loader.getModelPart(EntityModelLayers.WOLF_ARMOR));
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, WolfEntity wolfEntity, float f, float g, float h, float j, float k, float l
	) {
		if (wolfEntity.hasArmor()) {
			ItemStack itemStack = wolfEntity.getBodyArmor();
			Item vertexConsumer = itemStack.getItem();
			if (vertexConsumer instanceof AnimalArmorItem animalArmorItem) {
				this.getContextModel().copyStateTo(this.model);
				this.model.animateModel(wolfEntity, f, g, h);
				this.model.setAngles(wolfEntity, f, g, j, k, l);
				VertexConsumer var14 = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(animalArmorItem.getEntityTexture()));
				this.model.render(matrixStack, var14, i, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
				this.renderDyed(matrixStack, vertexConsumerProvider, i, itemStack, animalArmorItem);
				this.renderCracks(matrixStack, vertexConsumerProvider, i, itemStack);
			}
		}
	}

	private void renderDyed(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack, AnimalArmorItem item) {
		if (stack.isIn(ItemTags.DYEABLE)) {
			int i = DyedColorComponent.getColor(stack, 0);
			if (ColorHelper.Argb.getAlpha(i) == 0) {
				return;
			}

			Identifier identifier = item.getOverlayTexture();
			if (identifier == null) {
				return;
			}

			float f = (float)ColorHelper.Argb.getRed(i) / 255.0F;
			float g = (float)ColorHelper.Argb.getGreen(i) / 255.0F;
			float h = (float)ColorHelper.Argb.getBlue(i) / 255.0F;
			this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(identifier)), light, OverlayTexture.DEFAULT_UV, f, g, h, 1.0F);
		}
	}

	private void renderCracks(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack) {
		Cracks.CrackLevel crackLevel = Cracks.WOLF_ARMOR.getCrackLevel(stack);
		if (crackLevel != Cracks.CrackLevel.NONE) {
			Identifier identifier = (Identifier)CRACK_TEXTURES.get(crackLevel);
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(identifier));
			this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
