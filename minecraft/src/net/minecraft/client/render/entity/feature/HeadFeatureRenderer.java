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
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.Vec3f;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class HeadFeatureRenderer<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M> {
	private final float field_24474;
	private final float field_24475;
	private final float field_24476;

	public HeadFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext) {
		this(featureRendererContext, 1.0F, 1.0F, 1.0F);
	}

	public HeadFeatureRenderer(FeatureRendererContext<T, M> featureRendererContext, float f, float g, float h) {
		super(featureRendererContext);
		this.field_24474 = f;
		this.field_24475 = g;
		this.field_24476 = h;
	}

	public void render(
		MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T livingEntity, float f, float g, float h, float j, float k, float l
	) {
		ItemStack itemStack = livingEntity.getEquippedStack(EquipmentSlot.HEAD);
		if (!itemStack.isEmpty()) {
			Item item = itemStack.getItem();
			matrixStack.push();
			matrixStack.scale(this.field_24474, this.field_24475, this.field_24476);
			boolean bl = livingEntity instanceof VillagerEntity || livingEntity instanceof ZombieVillagerEntity;
			if (livingEntity.isBaby() && !(livingEntity instanceof VillagerEntity)) {
				float m = 2.0F;
				float n = 1.4F;
				matrixStack.translate(0.0, 0.03125, 0.0);
				matrixStack.scale(0.7F, 0.7F, 0.7F);
				matrixStack.translate(0.0, 1.0, 0.0);
			}

			this.getContextModel().getHead().rotate(matrixStack);
			if (item instanceof BlockItem && ((BlockItem)item).getBlock() instanceof AbstractSkullBlock) {
				float m = 1.1875F;
				matrixStack.scale(1.1875F, -1.1875F, -1.1875F);
				if (bl) {
					matrixStack.translate(0.0, 0.0625, 0.0);
				}

				GameProfile gameProfile = null;
				if (itemStack.hasTag()) {
					NbtCompound nbtCompound = itemStack.getTag();
					if (nbtCompound.contains("SkullOwner", 10)) {
						gameProfile = NbtHelper.toGameProfile(nbtCompound.getCompound("SkullOwner"));
					} else if (nbtCompound.contains("SkullOwner", 8)) {
						String string = nbtCompound.getString("SkullOwner");
						if (!StringUtils.isBlank(string)) {
							gameProfile = SkullBlockEntity.loadProperties(new GameProfile(null, string));
							nbtCompound.put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), gameProfile));
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
				matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0F));
				matrixStack.scale(0.625F, -0.625F, -0.625F);
				if (bl) {
					matrixStack.translate(0.0, 0.1875, 0.0);
				}

				MinecraftClient.getInstance()
					.getHeldItemRenderer()
					.renderItem(livingEntity, itemStack, ModelTransformation.Mode.HEAD, false, matrixStack, vertexConsumerProvider, i);
			}

			matrixStack.pop();
		}
	}
}
