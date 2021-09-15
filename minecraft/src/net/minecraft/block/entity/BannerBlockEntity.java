package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;

public class BannerBlockEntity extends BlockEntity implements Nameable {
	public static final int field_31296 = 6;
	public static final String PATTERNS_KEY = "Patterns";
	public static final String PATTERN_KEY = "Pattern";
	public static final String COLOR_KEY = "Color";
	@Nullable
	private Text customName;
	private DyeColor baseColor;
	@Nullable
	private NbtList patternListTag;
	private boolean patternListTagRead;
	@Nullable
	private List<Pair<BannerPattern, DyeColor>> patterns;

	public BannerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.BANNER, pos, state);
		this.baseColor = ((AbstractBannerBlock)state.getBlock()).getColor();
	}

	public BannerBlockEntity(BlockPos pos, BlockState state, DyeColor baseColor) {
		this(pos, state);
		this.baseColor = baseColor;
	}

	@Nullable
	public static NbtList getPatternListTag(ItemStack stack) {
		NbtList nbtList = null;
		NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
		if (nbtCompound != null && nbtCompound.contains("Patterns", NbtElement.LIST_TYPE)) {
			nbtList = nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE).copy();
		}

		return nbtList;
	}

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
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		if (this.patternListTag != null) {
			nbt.put("Patterns", this.patternListTag);
		}

		if (this.customName != null) {
			nbt.putString("CustomName", Text.Serializer.toJson(this.customName));
		}
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
			this.customName = Text.Serializer.fromJson(nbt.getString("CustomName"));
		}

		this.patternListTag = nbt.getList("Patterns", NbtElement.COMPOUND_TYPE);
		this.patterns = null;
		this.patternListTagRead = true;
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.createNbt();
	}

	public static int getPatternCount(ItemStack stack) {
		NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
		return nbtCompound != null && nbtCompound.contains("Patterns") ? nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE).size() : 0;
	}

	public List<Pair<BannerPattern, DyeColor>> getPatterns() {
		if (this.patterns == null && this.patternListTagRead) {
			this.patterns = getPatternsFromNbt(this.baseColor, this.patternListTag);
		}

		return this.patterns;
	}

	public static List<Pair<BannerPattern, DyeColor>> getPatternsFromNbt(DyeColor baseColor, @Nullable NbtList patternListTag) {
		List<Pair<BannerPattern, DyeColor>> list = Lists.<Pair<BannerPattern, DyeColor>>newArrayList();
		list.add(Pair.of(BannerPattern.BASE, baseColor));
		if (patternListTag != null) {
			for (int i = 0; i < patternListTag.size(); i++) {
				NbtCompound nbtCompound = patternListTag.getCompound(i);
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
		NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
		if (nbtCompound != null && nbtCompound.contains("Patterns", NbtElement.LIST_TYPE)) {
			NbtList nbtList = nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE);
			if (!nbtList.isEmpty()) {
				nbtList.remove(nbtList.size() - 1);
				if (nbtList.isEmpty()) {
					nbtCompound.remove("Patterns");
				}

				BlockItem.setBlockEntityNbt(stack, BlockEntityType.BANNER, nbtCompound);
			}
		}
	}

	public ItemStack getPickStack() {
		ItemStack itemStack = new ItemStack(BannerBlock.getForColor(this.baseColor));
		if (this.patternListTag != null && !this.patternListTag.isEmpty()) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.put("Patterns", this.patternListTag.copy());
			BlockItem.setBlockEntityNbt(itemStack, this.getType(), nbtCompound);
		}

		if (this.customName != null) {
			itemStack.setCustomName(this.customName);
		}

		return itemStack;
	}

	public DyeColor getColorForState() {
		return this.baseColor;
	}
}
