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
import net.minecraft.client.render.LayeredVertexConsumerStorage;
import net.minecraft.client.render.VertexConsumer;
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

	public void render(ItemStack itemStack, MatrixStack matrixStack, LayeredVertexConsumerStorage layeredVertexConsumerStorage, int i, int j) {
		Item item = itemStack.getItem();
		if (item instanceof BlockItem) {
			Block block = ((BlockItem)item).getBlock();
			if (block instanceof AbstractBannerBlock) {
				this.renderBanner.readFrom(itemStack, ((AbstractBannerBlock)block).getColor());
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderBanner, matrixStack, layeredVertexConsumerStorage, i, j);
			} else if (block instanceof BedBlock) {
				this.renderBed.setColor(((BedBlock)block).getColor());
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderBed, matrixStack, layeredVertexConsumerStorage, i, j);
			} else if (block instanceof AbstractSkullBlock) {
				GameProfile gameProfile = null;
				if (itemStack.hasTag()) {
					CompoundTag compoundTag = itemStack.getTag();
					if (compoundTag.contains("SkullOwner", 10)) {
						gameProfile = NbtHelper.toGameProfile(compoundTag.getCompound("SkullOwner"));
					} else if (compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
						GameProfile var13 = new GameProfile(null, compoundTag.getString("SkullOwner"));
						gameProfile = SkullBlockEntity.loadProperties(var13);
						compoundTag.remove("SkullOwner");
						compoundTag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
					}
				}

				SkullBlockEntityRenderer.render(null, 180.0F, ((AbstractSkullBlock)block).getSkullType(), gameProfile, 0.0F, matrixStack, layeredVertexConsumerStorage, i);
			} else if (block == Blocks.CONDUIT) {
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderConduit, matrixStack, layeredVertexConsumerStorage, i, j);
			} else if (block == Blocks.CHEST) {
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestNormal, matrixStack, layeredVertexConsumerStorage, i, j);
			} else if (block == Blocks.ENDER_CHEST) {
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestEnder, matrixStack, layeredVertexConsumerStorage, i, j);
			} else if (block == Blocks.TRAPPED_CHEST) {
				BlockEntityRenderDispatcher.INSTANCE.renderEntity(this.renderChestTrapped, matrixStack, layeredVertexConsumerStorage, i, j);
			} else if (block instanceof ShulkerBoxBlock) {
				DyeColor dyeColor = ShulkerBoxBlock.getColor(item);
				if (dyeColor == null) {
					BlockEntityRenderDispatcher.INSTANCE.renderEntity(RENDER_SHULKER_BOX, matrixStack, layeredVertexConsumerStorage, i, j);
				} else {
					BlockEntityRenderDispatcher.INSTANCE.renderEntity(RENDER_SHULKER_BOX_DYED[dyeColor.getId()], matrixStack, layeredVertexConsumerStorage, i, j);
				}
			}
		} else {
			if (item == Items.SHIELD) {
				Identifier identifier;
				if (itemStack.getSubTag("BlockEntityTag") != null) {
					this.renderBanner.readFrom(itemStack, ShieldItem.getColor(itemStack));
					identifier = TextureCache.SHIELD.get(this.renderBanner.getPatternCacheKey(), this.renderBanner.getPatterns(), this.renderBanner.getPatternColors());
				} else {
					identifier = TextureCache.DEFAULT_SHIELD;
				}

				matrixStack.push();
				matrixStack.scale(1.0F, -1.0F, -1.0F);
				VertexConsumer vertexConsumer = ItemRenderer.getArmorVertexConsumer(
					layeredVertexConsumerStorage, this.modelShield.getLayer(identifier), false, itemStack.hasEnchantmentGlint()
				);
				this.modelShield.render(matrixStack, vertexConsumer, i, j, 1.0F, 1.0F, 1.0F);
				matrixStack.pop();
			} else if (item == Items.TRIDENT) {
				matrixStack.push();
				matrixStack.scale(1.0F, -1.0F, -1.0F);
				VertexConsumer vertexConsumer2 = ItemRenderer.getArmorVertexConsumer(
					layeredVertexConsumerStorage, this.modelTrident.getLayer(TridentEntityModel.TEXTURE), false, itemStack.hasEnchantmentGlint()
				);
				this.modelTrident.render(matrixStack, vertexConsumer2, i, j, 1.0F, 1.0F, 1.0F);
				matrixStack.pop();
			}
		}
	}
}
