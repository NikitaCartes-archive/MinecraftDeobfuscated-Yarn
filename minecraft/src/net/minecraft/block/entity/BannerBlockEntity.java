package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;

public class BannerBlockEntity extends BlockEntity implements Nameable {
	private Component customName;
	private DyeColor baseColor = DyeColor.field_7952;
	private ListTag patternListTag;
	private boolean patternListTagRead;
	private List<BannerPattern> patterns;
	private List<DyeColor> patternColors;
	private String patternCacheKey;

	public BannerBlockEntity() {
		super(BlockEntityType.field_11905);
	}

	public BannerBlockEntity(DyeColor dyeColor) {
		this();
		this.baseColor = dyeColor;
	}

	@Environment(EnvType.CLIENT)
	public void deserialize(ItemStack itemStack, DyeColor dyeColor) {
		this.patternListTag = null;
		CompoundTag compoundTag = itemStack.getSubCompoundTag("BlockEntityTag");
		if (compoundTag != null && compoundTag.containsKey("Patterns", 9)) {
			this.patternListTag = compoundTag.getList("Patterns", 10).method_10612();
		}

		this.baseColor = dyeColor;
		this.patterns = null;
		this.patternColors = null;
		this.patternCacheKey = "";
		this.patternListTagRead = true;
		this.customName = itemStack.hasDisplayName() ? itemStack.getDisplayName() : null;
	}

	@Override
	public Component getName() {
		return (Component)(this.customName != null ? this.customName : new TranslatableComponent("block.minecraft.banner"));
	}

	@Nullable
	@Override
	public Component getCustomName() {
		return this.customName;
	}

	public void setCustomName(Component component) {
		this.customName = component;
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (this.patternListTag != null) {
			compoundTag.put("Patterns", this.patternListTag);
		}

		if (this.customName != null) {
			compoundTag.putString("CustomName", Component.Serializer.toJsonString(this.customName));
		}

		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		if (compoundTag.containsKey("CustomName", 8)) {
			this.customName = Component.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}

		if (this.hasWorld()) {
			this.baseColor = ((AbstractBannerBlock)this.getCachedState().getBlock()).getColor();
		} else {
			this.baseColor = null;
		}

		this.patternListTag = compoundTag.getList("Patterns", 10);
		this.patterns = null;
		this.patternColors = null;
		this.patternCacheKey = null;
		this.patternListTagRead = true;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 6, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
	}

	public static int getPatternCount(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("BlockEntityTag");
		return compoundTag != null && compoundTag.containsKey("Patterns") ? compoundTag.getList("Patterns", 10).size() : 0;
	}

	@Environment(EnvType.CLIENT)
	public List<BannerPattern> getPatterns() {
		this.readPattern();
		return this.patterns;
	}

	@Environment(EnvType.CLIENT)
	public List<DyeColor> getPatternColors() {
		this.readPattern();
		return this.patternColors;
	}

	@Environment(EnvType.CLIENT)
	public String getPatternCacheKey() {
		this.readPattern();
		return this.patternCacheKey;
	}

	@Environment(EnvType.CLIENT)
	private void readPattern() {
		if (this.patterns == null || this.patternColors == null || this.patternCacheKey == null) {
			if (!this.patternListTagRead) {
				this.patternCacheKey = "";
			} else {
				this.patterns = Lists.<BannerPattern>newArrayList();
				this.patternColors = Lists.<DyeColor>newArrayList();
				DyeColor dyeColor = this.getColorForState(this::getCachedState);
				if (dyeColor == null) {
					this.patternCacheKey = "banner_missing";
				} else {
					this.patterns.add(BannerPattern.BASE);
					this.patternColors.add(dyeColor);
					this.patternCacheKey = "b" + dyeColor.getId();
					if (this.patternListTag != null) {
						for (int i = 0; i < this.patternListTag.size(); i++) {
							CompoundTag compoundTag = this.patternListTag.getCompoundTag(i);
							BannerPattern bannerPattern = BannerPattern.byId(compoundTag.getString("Pattern"));
							if (bannerPattern != null) {
								this.patterns.add(bannerPattern);
								int j = compoundTag.getInt("Color");
								this.patternColors.add(DyeColor.byId(j));
								this.patternCacheKey = this.patternCacheKey + bannerPattern.getId() + j;
							}
						}
					}
				}
			}
		}
	}

	public static void loadFromItemStack(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.getSubCompoundTag("BlockEntityTag");
		if (compoundTag != null && compoundTag.containsKey("Patterns", 9)) {
			ListTag listTag = compoundTag.getList("Patterns", 10);
			if (!listTag.isEmpty()) {
				listTag.method_10536(listTag.size() - 1);
				if (listTag.isEmpty()) {
					itemStack.removeSubTag("BlockEntityTag");
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getPickStack(BlockState blockState) {
		ItemStack itemStack = new ItemStack(BannerBlock.getForColor(this.getColorForState(() -> blockState)));
		if (this.patternListTag != null && !this.patternListTag.isEmpty()) {
			itemStack.getOrCreateSubCompoundTag("BlockEntityTag").put("Patterns", this.patternListTag.method_10612());
		}

		if (this.customName != null) {
			itemStack.setDisplayName(this.customName);
		}

		return itemStack;
	}

	public DyeColor getColorForState(Supplier<BlockState> supplier) {
		if (this.baseColor == null) {
			this.baseColor = ((AbstractBannerBlock)((BlockState)supplier.get()).getBlock()).getColor();
		}

		return this.baseColor;
	}
}
