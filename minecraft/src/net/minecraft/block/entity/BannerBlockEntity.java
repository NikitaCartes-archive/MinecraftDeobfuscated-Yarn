package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;

public class BannerBlockEntity extends BlockEntity implements Nameable {
	@Nullable
	private Text customName;
	@Nullable
	private DyeColor baseColor = DyeColor.WHITE;
	@Nullable
	private NbtList patternListTag;
	private boolean patternListTagRead;
	@Nullable
	private List<Pair<BannerPattern, DyeColor>> patterns;

	public BannerBlockEntity() {
		super(BlockEntityType.BANNER);
	}

	public BannerBlockEntity(DyeColor baseColor) {
		this();
		this.baseColor = baseColor;
	}

	@Nullable
	@Environment(EnvType.CLIENT)
	public static NbtList getPatternListTag(ItemStack stack) {
		NbtList nbtList = null;
		NbtCompound nbtCompound = stack.getSubTag("BlockEntityTag");
		if (nbtCompound != null && nbtCompound.contains("Patterns", 9)) {
			nbtList = nbtCompound.getList("Patterns", 10).copy();
		}

		return nbtList;
	}

	@Environment(EnvType.CLIENT)
	public void readFrom(ItemStack stack, DyeColor baseColor) {
		this.patternListTag = getPatternListTag(stack);
		this.baseColor = baseColor;
		this.patterns = null;
		this.patternListTagRead = true;
		this.customName = stack.hasCustomName() ? stack.getName() : null;
	}

	@Override
	public Text getName() {
		return (Text)(this.customName != null ? this.customName : new TranslatableText("block.minecraft.banner"));
	}

	@Nullable
	@Override
	public Text getCustomName() {
		return this.customName;
	}

	public void setCustomName(Text customName) {
		this.customName = customName;
	}

	@Override
	public NbtCompound writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if (this.patternListTag != null) {
			nbt.put("Patterns", this.patternListTag);
		}

		if (this.customName != null) {
			nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
		}

		return nbt;
	}

	@Override
	public void fromTag(BlockState state, NbtCompound tag) {
		super.fromTag(state, tag);
		if (tag.contains("CustomName", 8)) {
			this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
		}

		if (this.hasWorld()) {
			this.baseColor = ((AbstractBannerBlock)this.getCachedState().getBlock()).getColor();
		} else {
			this.baseColor = null;
		}

		this.patternListTag = tag.getList("Patterns", 10);
		this.patterns = null;
		this.patternListTagRead = true;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 6, this.toInitialChunkDataNbt());
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.writeNbt(new NbtCompound());
	}

	public static int getPatternCount(ItemStack stack) {
		NbtCompound nbtCompound = stack.getSubTag("BlockEntityTag");
		return nbtCompound != null && nbtCompound.contains("Patterns") ? nbtCompound.getList("Patterns", 10).size() : 0;
	}

	@Environment(EnvType.CLIENT)
	public List<Pair<BannerPattern, DyeColor>> getPatterns() {
		if (this.patterns == null && this.patternListTagRead) {
			this.patterns = method_24280(this.getColorForState(this::getCachedState), this.patternListTag);
		}

		return this.patterns;
	}

	@Environment(EnvType.CLIENT)
	public static List<Pair<BannerPattern, DyeColor>> method_24280(DyeColor dyeColor, @Nullable NbtList nbtList) {
		List<Pair<BannerPattern, DyeColor>> list = Lists.<Pair<BannerPattern, DyeColor>>newArrayList();
		list.add(Pair.of(BannerPattern.BASE, dyeColor));
		if (nbtList != null) {
			for (int i = 0; i < nbtList.size(); i++) {
				NbtCompound nbtCompound = nbtList.getCompound(i);
				BannerPattern bannerPattern = BannerPattern.byId(nbtCompound.getString("Pattern"));
				if (bannerPattern != null) {
					int j = nbtCompound.getInt("Color");
					list.add(Pair.of(bannerPattern, DyeColor.byId(j)));
				}
			}
		}

		return list;
	}

	public static void loadFromItemStack(ItemStack stack) {
		NbtCompound nbtCompound = stack.getSubTag("BlockEntityTag");
		if (nbtCompound != null && nbtCompound.contains("Patterns", 9)) {
			NbtList nbtList = nbtCompound.getList("Patterns", 10);
			if (!nbtList.isEmpty()) {
				nbtList.remove(nbtList.size() - 1);
				if (nbtList.isEmpty()) {
					stack.removeSubTag("BlockEntityTag");
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getPickStack(BlockState state) {
		ItemStack itemStack = new ItemStack(BannerBlock.getForColor(this.getColorForState(() -> state)));
		if (this.patternListTag != null && !this.patternListTag.isEmpty()) {
			itemStack.getOrCreateSubTag("BlockEntityTag").put("Patterns", this.patternListTag.copy());
		}

		if (this.customName != null) {
			itemStack.setCustomName(this.customName);
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
