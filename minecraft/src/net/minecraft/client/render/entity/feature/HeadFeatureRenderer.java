package net.minecraft.client.render.entity.feature;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.model.json.ModelTransformation;
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
	public HeadFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_17159(T livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			RenderSystem.pushMatrix();
			if (livingEntity.isInSneakingPose()) {
				RenderSystem.translatef(0.0F, 0.2F, 0.0F);
			}

			boolean bl = livingEntity instanceof VillagerEntity || livingEntity instanceof ZombieVillagerEntity;
			if (livingEntity.isBaby() && !(livingEntity instanceof VillagerEntity)) {
				float m = 2.0F;
				float n = 1.4F;
				RenderSystem.translatef(0.0F, 0.5F * l, 0.0F);
				RenderSystem.scalef(0.7F, 0.7F, 0.7F);
				RenderSystem.translatef(0.0F, 16.0F * l, 0.0F);
			}

			this.getModel().setHeadAngle(0.0625F);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
				float m = 1.1875F;
				RenderSystem.scalef(1.1875F, -1.1875F, -1.1875F);
				if (bl) {
					RenderSystem.translatef(0.0F, 0.0625F, 0.0F);
				}

				GameProfile gameProfile = null;
				if (itemStack.hasTag()) {
					CompoundTag compoundTag = itemStack.getTag();
					if (compoundTag.containsKey("SkullOwner", 10)) {
						gameProfile = NbtHelper.toGameProfile(compoundTag.getCompound("SkullOwner"));
					} else if (compoundTag.containsKey("SkullOwner", 8)) {
						String string = compoundTag.getString("SkullOwner");
						if (!StringUtils.isBlank(string)) {
							gameProfile = SkullBlockEntity.loadProperties(new GameProfile(null, string));
							compoundTag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
						}
					}
				}

				SkullBlockEntityRenderer.INSTANCE
					.render(-0.5F, 0.0F, -0.5F, null, 180.0F, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameProfile, -1, f);
			} else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getSlotType() != EquipmentSlot.HEAD) {
				float mx = 0.625F;
				RenderSystem.translatef(0.0F, -0.25F, 0.0F);
				RenderSystem.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				RenderSystem.scalef(0.625F, -0.625F, -0.625F);
				if (bl) {
					RenderSystem.translatef(0.0F, 0.1875F, 0.0F);
				}

				MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(livingEntity, itemStack, ModelTransformation.Type.HEAD);
			}

			RenderSystem.popMatrix();
		}
	}

	@Override
	public boolean hasHurtOverlay() {
		return false;
	}
}
