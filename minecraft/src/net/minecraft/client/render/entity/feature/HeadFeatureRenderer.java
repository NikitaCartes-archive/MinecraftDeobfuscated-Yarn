package net.minecraft.client.render.entity.feature;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class HeadFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M> {
	public HeadFeatureRenderer(FeatureRendererContext<T, M> context) {
		super(context);
	}

	public void method_17159(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			matrixStack.push();
			boolean bl = livingEntity instanceof VillagerEntity || livingEntity instanceof ZombieVillagerEntity;
			if (livingEntity.isBaby() && !(livingEntity instanceof VillagerEntity)) {
				float m = 2.0F;
				float n = 1.4F;
				matrixStack.translate(0.0, 0.03125, 0.0);
				matrixStack.scale(0.7F, 0.7F, 0.7F);
				matrixStack.translate(0.0, 1.0, 0.0);
			}

			this.getModel().getHead().rotate(matrixStack);
			if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
				float m = 1.1875F;
				matrixStack.scale(1.1875F, -1.1875F, -1.1875F);
				if (bl) {
					matrixStack.translate(0.0, 0.0625, 0.0);
				}

				GameProfile gameProfile = null;
				if (itemStack.hasTag()) {
					CompoundTag compoundTag = itemStack.getTag();
					if (compoundTag.contains("SkullOwner", 10)) {
						gameProfile = NbtHelper.toGameProfile(compoundTag.getCompound("SkullOwner"));
					} else if (compoundTag.contains("SkullOwner", 8)) {
						String string = compoundTag.getString("SkullOwner");
						if (!StringUtils.isBlank(string)) {
							gameProfile = SkullBlockEntity.loadProperties(new GameProfile(null, string));
							compoundTag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
						}
					}
				}

				matrixStack.translate(-0.5, 0.0, -0.5);
				SkullBlockEntityRenderer.render(
					null, 180.0F, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameProfile, f, matrixStack, vertexConsumerProvider, i
				);
			} else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getSlotType() != EquipmentSlot.HEAD) {
				float mx = 0.625F;
				matrixStack.translate(0.0, -0.25, 0.0);
				matrixStack.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
				matrixStack.scale(0.625F, -0.625F, -0.625F);
				if (bl) {
					matrixStack.translate(0.0, 0.1875, 0.0);
				}

				MinecraftClient.getInstance()
					.getFirstPersonRenderer()
					.renderItem(livingEntity, itemStack, ModelTransformation.Type.HEAD, false, matrixStack, vertexConsumerProvider, i);
			}

			matrixStack.pop();
		}
	}
}
