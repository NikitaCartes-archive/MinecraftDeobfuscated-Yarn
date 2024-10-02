package net.minecraft.client.render.entity.feature;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class HeadFeatureRenderer<S extends LivingEntityRenderState, M extends EntityModel<S> & ModelWithHead> extends FeatureRenderer<S, M> {
	private static final float field_53209 = 0.625F;
	private static final float field_53210 = 1.1875F;
	private final HeadFeatureRenderer.HeadTransformation headTransformation;
	private final Map<SkullBlock.SkullType, SkullBlockEntityModel> headModels;
	private final ItemRenderer heldItemRenderer;

	public HeadFeatureRenderer(FeatureRendererContext<S, M> context, EntityModelLoader loader, ItemRenderer itemRenderer) {
		this(context, loader, HeadFeatureRenderer.HeadTransformation.DEFAULT, itemRenderer);
	}

	public HeadFeatureRenderer(
		FeatureRendererContext<S, M> context, EntityModelLoader loader, HeadFeatureRenderer.HeadTransformation headTransformation, ItemRenderer itemRenderer
	) {
		super(context);
		this.headTransformation = headTransformation;
		this.headModels = SkullBlockEntityRenderer.getModels(loader);
		this.heldItemRenderer = itemRenderer;
	}

	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, S livingEntityRenderState, float f, float g) {
		ItemStack itemStack = livingEntityRenderState.equippedHeadStack;
		BakedModel bakedModel = livingEntityRenderState.equippedHeadItemModel;
		if (!itemStack.isEmpty() && bakedModel != null) {
			label17: {
				Item item = itemStack.getItem();
				matrixStack.push();
				matrixStack.scale(this.headTransformation.horizontalScale(), 1.0F, this.headTransformation.horizontalScale());
				M entityModel = this.getContextModel();
				entityModel.getRootPart().rotate(matrixStack);
				entityModel.getHead().rotate(matrixStack);
				if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof AbstractSkullBlock abstractSkullBlock) {
					matrixStack.translate(0.0F, this.headTransformation.skullYOffset(), 0.0F);
					matrixStack.scale(1.1875F, -1.1875F, -1.1875F);
					ProfileComponent profileComponent = itemStack.get(DataComponentTypes.PROFILE);
					matrixStack.translate(-0.5, 0.0, -0.5);
					SkullBlock.SkullType skullType = abstractSkullBlock.getSkullType();
					SkullBlockEntityModel skullBlockEntityModel = (SkullBlockEntityModel)this.headModels.get(skullType);
					RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(skullType, profileComponent);
					SkullBlockEntityRenderer.renderSkull(
						null, 180.0F, livingEntityRenderState.headItemAnimationProgress, matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, renderLayer
					);
					break label17;
				}

				if (!ArmorFeatureRenderer.hasModel(itemStack, EquipmentSlot.HEAD)) {
					translate(matrixStack, this.headTransformation);
					this.heldItemRenderer
						.renderItem(itemStack, ModelTransformationMode.HEAD, false, matrixStack, vertexConsumerProvider, i, OverlayTexture.DEFAULT_UV, bakedModel);
				}
			}

			matrixStack.pop();
		}
	}

	public static void translate(MatrixStack matrices, HeadFeatureRenderer.HeadTransformation transformation) {
		matrices.translate(0.0F, -0.25F + transformation.yOffset(), 0.0F);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
		matrices.scale(0.625F, -0.625F, -0.625F);
	}

	@Environment(EnvType.CLIENT)
	public static record HeadTransformation(float yOffset, float skullYOffset, float horizontalScale) {
		public static final HeadFeatureRenderer.HeadTransformation DEFAULT = new HeadFeatureRenderer.HeadTransformation(0.0F, 0.0F, 1.0F);
	}
}
