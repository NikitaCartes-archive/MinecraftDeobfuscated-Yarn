package net.minecraft.client.render.entity.feature;

import com.mojang.authlib.GameProfile;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.render.model.json.ModelTransformation;
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
	public HeadFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		super(featureRendererContext);
	}

	public void method_17159(class_4587 arg, class_4597 arg2, int i, T livingEntity, float f, float g, float h, float j, float k, float l, float m) {
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			arg.method_22903();
			if (livingEntity.isInSneakingPose()) {
				arg.method_22904(0.0, 0.2F, 0.0);
			}

			boolean bl = livingEntity instanceof VillagerEntity || livingEntity instanceof ZombieVillagerEntity;
			if (livingEntity.isBaby() && !(livingEntity instanceof VillagerEntity)) {
				float n = 2.0F;
				float o = 1.4F;
				arg.method_22904(0.0, (double)(0.5F * m), 0.0);
				arg.method_22905(0.7F, 0.7F, 0.7F);
				arg.method_22904(0.0, (double)(16.0F * m), 0.0);
			}

			this.getModel().getHead().method_22703(arg, 0.0625F);
			if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
				float n = 1.1875F;
				arg.method_22905(1.1875F, -1.1875F, -1.1875F);
				if (bl) {
					arg.method_22904(0.0, 0.0625, 0.0);
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

				arg.method_22904(-0.5, 0.0, -0.5);
				SkullBlockEntityRenderer.render(null, 180.0F, ((AbstractSkullBlock)((BlockItem)item).getBlock()).getSkullType(), gameProfile, f, arg, arg2, i);
			} else if (!(item instanceof ArmorItem) || ((ArmorItem)item).getSlotType() != EquipmentSlot.HEAD) {
				float nx = 0.625F;
				arg.method_22904(0.0, -0.25, 0.0);
				arg.method_22907(Vector3f.field_20705.method_23214(180.0F, true));
				arg.method_22905(0.625F, -0.625F, -0.625F);
				if (bl) {
					arg.method_22904(0.0, 0.1875, 0.0);
				}

				MinecraftClient.getInstance().getFirstPersonRenderer().renderItem(livingEntity, itemStack, ModelTransformation.Type.HEAD, false, arg, arg2);
			}

			arg.method_22909();
		}
	}
}
