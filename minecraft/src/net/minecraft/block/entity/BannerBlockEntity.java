package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.StandingBannerBlock;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;

public class BannerBlockEntity extends BlockEntity implements Nameable {
	private TextComponent field_11772;
	private DyeColor baseColor = DyeColor.field_7952;
	private ListTag field_11773;
	private boolean patternListTagRead;
	private List<BannerPattern> patterns;
	private List<DyeColor> patternColors;
	private String patternCacheKey;

	public BannerBlockEntity() {
		super(BlockEntityType.BANNER);
	}

	public BannerBlockEntity(DyeColor dyeColor) {
		this();
		this.baseColor = dyeColor;
	}

	@Environment(EnvType.CLIENT)
	public void deserialize(ItemStack itemStack, DyeColor dyeColor) {
		this.field_11773 = null;
		CompoundTag compoundTag = itemStack.method_7941("BlockEntityTag");
		if (compoundTag != null && compoundTag.containsKey("Patterns", 9)) {
			this.field_11773 = compoundTag.method_10554("Patterns", 10).method_10612();
		}

		this.baseColor = dyeColor;
		this.patterns = null;
		this.patternColors = null;
		this.patternCacheKey = "";
		this.patternListTagRead = true;
		this.field_11772 = itemStack.hasDisplayName() ? itemStack.method_7964() : null;
	}

	@Override
	public TextComponent method_5477() {
		return (TextComponent)(this.field_11772 != null ? this.field_11772 : new TranslatableTextComponent("block.minecraft.banner"));
	}

	@Nullable
	@Override
	public TextComponent method_5797() {
		return this.field_11772;
	}

	public void method_16842(TextComponent textComponent) {
		this.field_11772 = textComponent;
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		if (this.field_11773 != null) {
			compoundTag.method_10566("Patterns", this.field_11773);
		}

		if (this.field_11772 != null) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.field_11772));
		}

		return compoundTag;
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		if (compoundTag.containsKey("CustomName", 8)) {
			this.field_11772 = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}

		if (this.hasWorld()) {
			this.baseColor = ((AbstractBannerBlock)this.method_11010().getBlock()).getColor();
		} else {
			this.baseColor = null;
		}

		this.field_11773 = compoundTag.method_10554("Patterns", 10);
		this.patterns = null;
		this.patternColors = null;
		this.patternCacheKey = null;
		this.patternListTagRead = true;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket method_16886() {
		return new BlockEntityUpdateS2CPacket(this.field_11867, 6, this.method_16887());
	}

	@Override
	public CompoundTag method_16887() {
		return this.method_11007(new CompoundTag());
	}

	public static int getPatternCount(ItemStack itemStack) {
		CompoundTag compoundTag = itemStack.method_7941("BlockEntityTag");
		return compoundTag != null && compoundTag.containsKey("Patterns") ? compoundTag.method_10554("Patterns", 10).size() : 0;
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
				DyeColor dyeColor = this.getColorForState(this::method_11010);
				if (dyeColor == null) {
					this.patternCacheKey = "banner_missing";
				} else {
					this.patterns.add(BannerPattern.BASE);
					this.patternColors.add(dyeColor);
					this.patternCacheKey = "b" + dyeColor.getId();
					if (this.field_11773 != null) {
						for (int i = 0; i < this.field_11773.size(); i++) {
							CompoundTag compoundTag = this.field_11773.getCompoundTag(i);
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
		CompoundTag compoundTag = itemStack.method_7941("BlockEntityTag");
		if (compoundTag != null && compoundTag.containsKey("Patterns", 9)) {
			ListTag listTag = compoundTag.method_10554("Patterns", 10);
			if (!listTag.isEmpty()) {
				listTag.method_10536(listTag.size() - 1);
				if (listTag.isEmpty()) {
					itemStack.removeSubTag("BlockEntityTag");
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public ItemStack method_10907(BlockState blockState) {
		ItemStack itemStack = new ItemStack(StandingBannerBlock.method_9398(this.getColorForState(() -> blockState)));
		if (this.field_11773 != null && !this.field_11773.isEmpty()) {
			itemStack.method_7911("BlockEntityTag").method_10566("Patterns", this.field_11773.method_10612());
		}

		if (this.field_11772 != null) {
			itemStack.method_7977(this.field_11772);
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
