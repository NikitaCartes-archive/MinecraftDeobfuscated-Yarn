package net.minecraft.client.render.entity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.Cuboid;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformations;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.block.BlockItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.TagHelper;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class HeadEntityRenderer implements LayerEntityRenderer<LivingEntity> {
	private final Cuboid model;

	public HeadEntityRenderer(Cuboid cuboid) {
		this.model = cuboid;
	}

	@Override
	public void render(LivingEntity livingEntity, float f, float g, float h, float i, float j, float k, float l) {
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			MinecraftClient minecraftClient = MinecraftClient.getInstance();
			GlStateManager.pushMatrix();
			if (livingEntity.isSneaking()) {
				GlStateManager.translatef(0.0F, 0.2F, 0.0F);
			}

			boolean bl = livingEntity instanceof VillagerEntity || livingEntity instanceof ZombieVillagerEntity;
			if (livingEntity.isChild() && !(livingEntity instanceof VillagerEntity)) {
				float m = 2.0F;
				float n = 1.4F;
				GlStateManager.translatef(0.0F, 0.5F * l, 0.0F);
				GlStateManager.scalef(0.7F, 0.7F, 0.7F);
				GlStateManager.translatef(0.0F, 16.0F * l, 0.0F);
			}

			this.model.method_2847(0.0625F);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
				float m = 1.1875F;
				GlStateManager.scalef(1.1875F, -1.1875F, -1.1875F);
				if (bl) {
					GlStateManager.translatef(0.0F, 0.0625F, 0.0F);
				}

				GameProfile gameProfile = null;
				if (itemStack.hasTag()) {
					CompoundTag compoundTag = itemStack.getTag();
					if (compoundTag.containsKey("SkullOwner", 10)) {
						gameProfile = TagHelper.deserializeProfile(compoundTag.getCompound("SkullOwner"));
					} else if (compoundTag.containsKey("SkullOwner", 8)) {
						String string = compoundTag.getString("SkullOwner");
						if (!StringUtils.isBlank(string)) {
							gameProfile = SkullBlockEntity.loadProperties(new GameProfile(null, string));
							compoundTag.put("SkullOwner", TagHelper.serializeProfile(new CompoundTag(), gameProfile));
						}
					}
				}

				SkullBlockEntityRenderer.INSTANCE
					.method_3581(-0.5F, 0.0F, -0.5F, null, 180.0F, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameProfile, -1, f);
			} else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getSlotType() != EquipmentSlot.HEAD) {
				float mx = 0.625F;
				GlStateManager.translatef(0.0F, -0.25F, 0.0F);
				GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
				GlStateManager.scalef(0.625F, -0.625F, -0.625F);
				if (bl) {
					GlStateManager.translatef(0.0F, 0.1875F, 0.0F);
				}

				minecraftClient.getFirstPersonRenderer().renderItem(livingEntity, itemStack, ModelTransformations.Type.HEAD);
			}

			GlStateManager.popMatrix();
		}
	}

	@Override
	public boolean shouldMergeTextures() {
		return false;
	}
}
