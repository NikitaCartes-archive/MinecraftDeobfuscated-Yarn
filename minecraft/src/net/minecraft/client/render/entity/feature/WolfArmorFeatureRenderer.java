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
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;

@Environment(EnvType.CLIENT)
public class WolfArmorFeatureRenderer extends FeatureRenderer<WolfEntity, WolfEntityModel<WolfEntity>> {
	private final WolfEntityModel<WolfEntity> model;
	private static final Map<Cracks.CrackLevel, Identifier> CRACK_TEXTURES = Map.of(
		Cracks.CrackLevel.LOW,
		Identifier.ofVanilla("textures/entity/wolf/wolf_armor_crackiness_low.png"),
		Cracks.CrackLevel.MEDIUM,
		Identifier.ofVanilla("textures/entity/wolf/wolf_armor_crackiness_medium.png"),
		Cracks.CrackLevel.HIGH,
		Identifier.ofVanilla("textures/entity/wolf/wolf_armor_crackiness_high.png")
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
			if (itemStack.getItem() instanceof AnimalArmorItem animalArmorItem && animalArmorItem.getType() == AnimalArmorItem.Type.CANINE) {
				this.getContextModel().copyStateTo(this.model);
				this.model.animateModel(wolfEntity, f, g, h);
				this.model.setAngles(wolfEntity, f, g, j, k, l);
				VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityCutoutNoCull(animalArmorItem.getEntityTexture()));
				this.model.render(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
				this.renderDyed(matrixStack, vertexConsumerProvider, i, itemStack, animalArmorItem);
				this.renderCracks(matrixStack, vertexConsumerProvider, i, itemStack);
				return;
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

			this.model
				.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(identifier)), light, OverlayTexture.DEFAULT_UV, ColorHelper.Argb.fullAlpha(i));
		}
	}

	private void renderCracks(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, ItemStack stack) {
		Cracks.CrackLevel crackLevel = Cracks.WOLF_ARMOR.getCrackLevel(stack);
		if (crackLevel != Cracks.CrackLevel.NONE) {
			Identifier identifier = (Identifier)CRACK_TEXTURES.get(crackLevel);
			VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(identifier));
			this.model.render(matrices, vertexConsumer, light, OverlayTexture.DEFAULT_UV);
		}
	}
}
