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
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;

public class BannerBlockEntity extends BlockEntity implements Nameable {
	private TextComponent customName;
	private DyeColor baseColor = DyeColor.WHITE;
	private ListTag patternListTag;
	private boolean field_11770;
	private List<BannerPattern> patternList;
	private List<DyeColor> patternColorList;
	private String field_11775;

	public BannerBlockEntity() {
		super(BlockEntityType.BANNER);
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
			this.patternListTag = compoundTag.getList("Patterns", 10).copy();
		}

		this.baseColor = dyeColor;
		this.patternList = null;
		this.patternColorList = null;
		this.field_11775 = "";
		this.field_11770 = true;
		this.customName = itemStack.hasDisplayName() ? itemStack.getDisplayName() : null;
	}

	@Override
	public TextComponent getName() {
		return (TextComponent)(this.customName != null ? this.customName : new TranslatableTextComponent("block.minecraft.banner"));
	}

	@Override
	public boolean hasCustomName() {
		return this.customName != null;
	}

	@Nullable
	public TextComponent getCustomName() {
		return this.customName;
	}

	public void method_16842(TextComponent textComponent) {
		this.customName = textComponent;
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (this.patternListTag != null) {
			compoundTag.put("Patterns", this.patternListTag);
		}

		if (this.customName != null) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.customName));
		}

		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		if (compoundTag.containsKey("CustomName", 8)) {
			this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}

		if (this.hasWorld()) {
			this.baseColor = ((AbstractBannerBlock)this.getCachedState().getBlock()).getColor();
		} else {
			this.baseColor = null;
		}

		this.patternListTag = compoundTag.getList("Patterns", 10);
		this.patternList = null;
		this.patternColorList = null;
		this.field_11775 = null;
		this.field_11770 = true;
	}

	@Nullable
	@Override
	public BlockEntityUpdateClientPacket toUpdatePacket() {
		return new BlockEntityUpdateClientPacket(this.pos, 6, this.toInitialChunkDataTag());
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
	public List<BannerPattern> getPatternList() {
		this.initPatternLists();
		return this.patternList;
	}

	@Environment(EnvType.CLIENT)
	public List<DyeColor> getPatternColorList() {
		this.initPatternLists();
		return this.patternColorList;
	}

	@Environment(EnvType.CLIENT)
	public String method_10915() {
		this.initPatternLists();
		return this.field_11775;
	}

	@Environment(EnvType.CLIENT)
	private void initPatternLists() {
		if (this.patternList == null || this.patternColorList == null || this.field_11775 == null) {
			if (!this.field_11770) {
				this.field_11775 = "";
			} else {
				this.patternList = Lists.<BannerPattern>newArrayList();
				this.patternColorList = Lists.<DyeColor>newArrayList();
				DyeColor dyeColor = this.getColorForState(this::getCachedState);
				if (dyeColor == null) {
					this.field_11775 = "banner_missing";
				} else {
					this.patternList.add(BannerPattern.BASE);
					this.patternColorList.add(dyeColor);
					this.field_11775 = "b" + dyeColor.getId();
					if (this.patternListTag != null) {
						for (int i = 0; i < this.patternListTag.size(); i++) {
							CompoundTag compoundTag = this.patternListTag.getCompoundTag(i);
							BannerPattern bannerPattern = BannerPattern.byId(compoundTag.getString("Pattern"));
							if (bannerPattern != null) {
								this.patternList.add(bannerPattern);
								int j = compoundTag.getInt("Color");
								this.patternColorList.add(DyeColor.byId(j));
								this.field_11775 = this.field_11775 + bannerPattern.getId() + j;
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
				listTag.remove(listTag.size() - 1);
				if (listTag.isEmpty()) {
					itemStack.removeSubTag("BlockEntityTag");
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public ItemStack method_10907(BlockState blockState) {
		ItemStack itemStack = new ItemStack(StandingBannerBlock.method_9398(this.getColorForState(() -> blockState)));
		if (this.patternListTag != null && !this.patternListTag.isEmpty()) {
			itemStack.getOrCreateSubCompoundTag("BlockEntityTag").put("Patterns", this.patternListTag.copy());
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
