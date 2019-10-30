package net.minecraft.client.render.item;

import com.mojang.authlib.GameProfile;
import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BedBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.SkullBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.texture.TextureCache;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.StringUtils;

@Environment(EnvType.CLIENT)
public class BuiltinModelItemRenderer {
	private static final ShulkerBoxBlockEntity[] RENDER_SHULKER_BOX_DYED = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(ShulkerBoxBlockEntity::new)
		.toArray(ShulkerBoxBlockEntity[]::new);
	private static final ShulkerBoxBlockEntity RENDER_SHULKER_BOX = new ShulkerBoxBlockEntity(null);
	public static final BuiltinModelItemRenderer INSTANCE = new BuiltinModelItemRenderer();
	private final ChestBlockEntity renderChestNormal = new ChestBlockEntity();
	private final ChestBlockEntity renderChestTrapped = new TrappedChestBlockEntity();
	private final EnderChestBlockEntity renderChestEnder = new EnderChestBlockEntity();
	private final BannerBlockEntity renderBanner = new BannerBlockEntity();
	private final BedBlockEntity renderBed = new BedBlockEntity();
	private final ConduitBlockEntity renderConduit = new ConduitBlockEntity();
	private final ShieldEntityModel modelShield = new ShieldEntityModel();
	private final TridentEntityModel modelTrident = new TridentEntityModel();

	public void render(ItemStack stack, MatrixStack matrix, VertexConsumerProvider vertexConsumerProvider, int light, int overlay) {
		Item item = stack.getItem();
		if (item instanceof BlockItem) {
			Block block = ((BlockItem)item).getBlock();
			if (block instanceof AbstractBannerBlock) {
				this.renderBanner.readFrom(stack, ((AbstractBannerBlock)block).getColor());
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderBanner, matrix, vertexConsumerProvider, light, overlay);
			} else if (block instanceof BedBlock) {
				this.renderBed.setColor(((BedBlock)block).getColor());
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderBed, matrix, vertexConsumerProvider, light, overlay);
			} else if (block instanceof AbstractSkullBlock) {
				GameProfile gameProfile = null;
				if (stack.hasTag()) {
					CompoundTag compoundTag = stack.getTag();
					if (compoundTag.contains("SkullOwner", 10)) {
						gameProfile = NbtHelper.toGameProfile(compoundTag.getCompound("SkullOwner"));
					} else if (compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
						GameProfile var13 = new GameProfile(null, compoundTag.getString("SkullOwner"));
						gameProfile = SkullBlockEntity.loadProperties(var13);
						compoundTag.remove("SkullOwner");
						compoundTag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
					}
				}

				SkullBlockEntityRenderer.render(null, 180.0F, ((AbstractSkullBlock)block).getSkullType(), gameProfile, 0.0F, matrix, vertexConsumerProvider, light);
			} else if (block == Blocks.CONDUIT) {
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderConduit, matrix, vertexConsumerProvider, light, overlay);
			} else if (block == Blocks.CHEST) {
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestNormal, matrix, vertexConsumerProvider, light, overlay);
			} else if (block == Blocks.ENDER_CHEST) {
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestEnder, matrix, vertexConsumerProvider, light, overlay);
			} else if (block == Blocks.TRAPPED_CHEST) {
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestTrapped, matrix, vertexConsumerProvider, light, overlay);
			} else if (block instanceof ShulkerBoxBlock) {
				DyeColor dyeColor = ShulkerBoxBlock.getColor(item);
				if (dyeColor == null) {
					BlockEntityRenderDispatcher.INSTANCE.renderEntity(RENDER_SHULKER_BOX, matrix, vertexConsumerProvider, light, overlay);
				} else {
					BlockEntityRenderDispatcher.INSTANCE.renderEntity(RENDER_SHULKER_BOX_DYED[dyeColor.getId()], matrix, vertexConsumerProvider, light, overlay);
				}
			}
		} else {
			if (item == Items.SHIELD) {
				Identifier identifier;
				if (stack.getSubTag("BlockEntityTag") != null) {
					this.renderBanner.readFrom(stack, ShieldItem.getColor(stack));
					identifier = TextureCache.SHIELD.get(this.renderBanner.getPatternCacheKey(), this.renderBanner.getPatterns(), this.renderBanner.getPatternColors());
				} else {
					identifier = TextureCache.DEFAULT_SHIELD;
				}

				matrix.push();
				matrix.scale(1.0F, -1.0F, -1.0F);
				VertexConsumer vertexConsumer = ItemRenderer.getArmorVertexConsumer(
					vertexConsumerProvider, this.modelShield.getLayer(identifier), false, stack.hasEnchantmentGlint()
				);
				this.modelShield.render(matrix, vertexConsumer, light, overlay, 1.0F, 1.0F, 1.0F);
				matrix.pop();
			} else if (item == Items.TRIDENT) {
				matrix.push();
				matrix.scale(1.0F, -1.0F, -1.0F);
				VertexConsumer vertexConsumer2 = ItemRenderer.getArmorVertexConsumer(
					vertexConsumerProvider, this.modelTrident.getLayer(TridentEntityModel.TEXTURE), false, stack.hasEnchantmentGlint()
				);
				this.modelTrident.render(matrix, vertexConsumer2, light, overlay, 1.0F, 1.0F, 1.0F);
				matrix.pop();
			}
		}
	}
}
