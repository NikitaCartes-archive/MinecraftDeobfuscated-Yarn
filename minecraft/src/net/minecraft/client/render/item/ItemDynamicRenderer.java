package net.minecraft.client.render.item;

import com.mojang.authlib.GameProfile;
import java.util.Arrays;
import java.util.Comparator;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_4587;
import net.minecraft.class_4588;
import net.minecraft.class_4597;
import net.minecraft.class_4608;
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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.SkullBlockEntityRenderer;
import net.minecraft.client.render.entity.model.ShieldEntityModel;
import net.minecraft.client.render.entity.model.TridentEntityModel;
import net.minecraft.client.texture.TextureCache;
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
public class ItemDynamicRenderer {
	private static final ShulkerBoxBlockEntity[] RENDER_SHULKER_BOX_DYED = (ShulkerBoxBlockEntity[])Arrays.stream(DyeColor.values())
		.sorted(Comparator.comparingInt(DyeColor::getId))
		.map(ShulkerBoxBlockEntity::new)
		.toArray(ShulkerBoxBlockEntity[]::new);
	private static final ShulkerBoxBlockEntity RENDER_SHULKER_BOX = new ShulkerBoxBlockEntity(null);
	public static final ItemDynamicRenderer INSTANCE = new ItemDynamicRenderer();
	private final ChestBlockEntity renderChestNormal = new ChestBlockEntity();
	private final ChestBlockEntity renderChestTrapped = new TrappedChestBlockEntity();
	private final EnderChestBlockEntity renderChestEnder = new EnderChestBlockEntity();
	private final BannerBlockEntity renderBanner = new BannerBlockEntity();
	private final BedBlockEntity renderBed = new BedBlockEntity();
	private final ConduitBlockEntity renderConduit = new ConduitBlockEntity();
	private final ShieldEntityModel modelShield = new ShieldEntityModel();
	private final TridentEntityModel modelTrident = new TridentEntityModel();

	public void render(ItemStack itemStack, class_4587 arg, class_4597 arg2, int i) {
		Item item = itemStack.getItem();
		if (item instanceof BlockItem) {
			Block block = ((BlockItem)item).getBlock();
			if (block instanceof AbstractBannerBlock) {
				this.renderBanner.deserialize(itemStack, ((AbstractBannerBlock)block).getColor());
				BlockEntityRenderDispatcher.INSTANCE.method_23077(this.renderBanner, arg, arg2, i);
			} else if (block instanceof BedBlock) {
				this.renderBed.setColor(((BedBlock)block).getColor());
				BlockEntityRenderDispatcher.INSTANCE.method_23077(this.renderBed, arg, arg2, i);
			} else if (block instanceof AbstractSkullBlock) {
				GameProfile gameProfile = null;
				if (itemStack.hasTag()) {
					CompoundTag compoundTag = itemStack.getTag();
					if (compoundTag.contains("SkullOwner", 10)) {
						gameProfile = NbtHelper.toGameProfile(compoundTag.getCompound("SkullOwner"));
					} else if (compoundTag.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundTag.getString("SkullOwner"))) {
						GameProfile var12 = new GameProfile(null, compoundTag.getString("SkullOwner"));
						gameProfile = SkullBlockEntity.loadProperties(var12);
						compoundTag.remove("SkullOwner");
						compoundTag.put("SkullOwner", NbtHelper.fromGameProfile(new CompoundTag(), gameProfile));
					}
				}

				SkullBlockEntityRenderer.render(null, 180.0F, ((AbstractSkullBlock)block).getSkullType(), gameProfile, 0.0F, arg, arg2, i);
			} else if (block == Blocks.CONDUIT) {
				BlockEntityRenderDispatcher.INSTANCE.method_23077(this.renderConduit, arg, arg2, i);
			} else if (block == Blocks.CHEST) {
				BlockEntityRenderDispatcher.INSTANCE.method_23077(this.renderChestNormal, arg, arg2, i);
			} else if (block == Blocks.ENDER_CHEST) {
				BlockEntityRenderDispatcher.INSTANCE.method_23077(this.renderChestEnder, arg, arg2, i);
			} else if (block == Blocks.TRAPPED_CHEST) {
				BlockEntityRenderDispatcher.INSTANCE.method_23077(this.renderChestTrapped, arg, arg2, i);
			} else if (block instanceof ShulkerBoxBlock) {
				DyeColor dyeColor = ShulkerBoxBlock.getColor(item);
				if (dyeColor == null) {
					BlockEntityRenderDispatcher.INSTANCE.method_23077(RENDER_SHULKER_BOX, arg, arg2, i);
				} else {
					BlockEntityRenderDispatcher.INSTANCE.method_23077(RENDER_SHULKER_BOX_DYED[dyeColor.getId()], arg, arg2, i);
				}
			}
		} else {
			if (item == Items.SHIELD) {
				Identifier identifier;
				if (itemStack.getSubTag("BlockEntityTag") != null) {
					this.renderBanner.deserialize(itemStack, ShieldItem.getColor(itemStack));
					identifier = TextureCache.SHIELD.get(this.renderBanner.getPatternCacheKey(), this.renderBanner.getPatterns(), this.renderBanner.getPatternColors());
				} else {
					identifier = TextureCache.DEFAULT_SHIELD;
				}

				arg.method_22903();
				arg.method_22905(1.0F, -1.0F, -1.0F);
				class_4588 lv = ItemRenderer.method_23181(arg2, identifier, false, itemStack.hasEnchantmentGlint(), false);
				class_4608.method_23211(lv);
				this.modelShield.renderItem(arg, lv, i);
				lv.method_22923();
				arg.method_22909();
			} else if (item == Items.TRIDENT) {
				MinecraftClient.getInstance().getTextureManager().bindTexture(TridentEntityModel.TEXTURE);
				arg.method_22903();
				arg.method_22905(1.0F, -1.0F, -1.0F);
				class_4588 lv2 = ItemRenderer.method_23181(arg2, TridentEntityModel.TEXTURE, false, itemStack.hasEnchantmentGlint(), false);
				class_4608.method_23211(lv2);
				this.modelTrident.renderItem(arg, lv2, i);
				lv2.method_22923();
				arg.method_22909();
			}
		}
	}
}
