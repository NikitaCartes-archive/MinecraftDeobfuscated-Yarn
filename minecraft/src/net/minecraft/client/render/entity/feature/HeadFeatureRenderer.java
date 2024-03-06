package net.minecraft.client.render.entity.feature;

import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityModel;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LimbAnimator;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class HeadFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M> {
	private final float scaleX;
	private final float scaleY;
	private final float scaleZ;
	private final Map<SkullBlock.SkullType, SkullBlockEntityModel> headModels;
	private final HeldItemRenderer heldItemRenderer;

	public HeadFeatureRenderer(FeatureRendererContext<T, M> context, EntityModelLoader loader, HeldItemRenderer heldItemRenderer) {
		this(context, loader, 1.0F, 1.0F, 1.0F, heldItemRenderer);
	}

	public HeadFeatureRenderer(
		FeatureRendererContext<T, M> context, EntityModelLoader loader, float scaleX, float scaleY, float scaleZ, HeldItemRenderer heldItemRenderer
	) {
		super(context);
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		this.headModels = SkullBlockEntityRenderer.getModels(loader);
		this.heldItemRenderer = heldItemRenderer;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			matrixStack.push();
			matrixStack.scale(this.scaleX, this.scaleY, this.scaleZ);
			boolean bl = livingEntity instanceof VillagerEntity || livingEntity instanceof ZombieVillagerEntity;
			if (livingEntity.isBaby() && !(livingEntity instanceof VillagerEntity)) {
				float m = 2.0F;
				float n = 1.4F;
				matrixStack.translate(0.0F, 0.03125F, 0.0F);
				matrixStack.scale(0.7F, 0.7F, 0.7F);
				matrixStack.translate(0.0F, 1.0F, 0.0F);
			}

			this.getContextModel().getHead().rotate(matrixStack);
			if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
				float n = 1.1875F;
				matrixStack.scale(1.1875F, -1.1875F, -1.1875F);
				if (bl) {
					matrixStack.translate(0.0F, 0.0625F, 0.0F);
				}

				ProfileComponent profileComponent = itemStack.get(DataComponentTypes.PROFILE);
				matrixStack.translate(-0.5, 0.0, -0.5);
				SkullBlock.SkullType skullType = ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType();
				SkullBlockEntityModel skullBlockEntityModel = (SkullBlockEntityModel)this.headModels.get(skullType);
				RenderLayer renderLayer = SkullBlockEntityRenderer.getRenderLayer(skullType, profileComponent);
				LimbAnimator limbAnimator;
				if (livingEntity.getVehicle() instanceof LivingEntity livingEntity2) {
					limbAnimator = livingEntity2.limbAnimator;
				} else {
					limbAnimator = livingEntity.limbAnimator;
				}

				float o = limbAnimator.getPos(h);
				SkullBlockEntityRenderer.renderSkull(null, 180.0F, o, matrixStack, vertexConsumerProvider, i, skullBlockEntityModel, renderLayer);
			} else if (!(item instanceof ArmorItem armorItem) || armorItem.getSlotType() != EquipmentSlot.HEAD) {
				translate(matrixStack, bl);
				this.heldItemRenderer.renderItem(livingEntity, itemStack, ModelTransformationMode.HEAD, false, matrixStack, vertexConsumerProvider, i);
			}

			matrixStack.pop();
		}
	}

	public static void translate(MatrixStack matrices, boolean villager) {
		float f = 0.625F;
		matrices.translate(0.0F, -0.25F, 0.0F);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
		matrices.scale(0.625F, -0.625F, -0.625F);
		if (villager) {
			matrices.translate(0.0F, 0.1875F, 0.0F);
		}
	}
}
