package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.yarn.constants.NbtTypeIds;
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
import net.minecraft.util.math.BlockPos;

public class BannerBlockEntity extends BlockEntity implements Nameable {
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
	@Environment(EnvType.CLIENT)
	public static NbtList getPatternListTag(ItemStack stack) {
		NbtList nbtList = null;
		NbtCompound nbtCompound = stack.getSubTag("BlockEntityTag");
		if (nbtCompound != null && nbtCompound.contains("Patterns", NbtTypeIds.LIST)) {
			nbtList = nbtCompound.getList("Patterns", NbtTypeIds.COMPOUND).copy();
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
	public NbtCompound writeNbt(NbtCompound tag) {
		super.writeNbt(tag);
		if (this.patternListTag != null) {
			tag.put("Patterns", this.patternListTag);
		}

		if (this.customName != null) {
			tag.putString("CustomName", Text.Serializer.toJson(this.customName));
		}

		return tag;
	}

	@Override
	public void readNbt(NbtCompound tag) {
		super.readNbt(tag);
		if (tag.contains("CustomName", NbtTypeIds.STRING)) {
			this.customName = Text.Serializer.fromJson(tag.getString("CustomName"));
		}

		this.patternListTag = tag.getList("Patterns", NbtTypeIds.COMPOUND);
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
		return nbtCompound != null && nbtCompound.contains("Patterns") ? nbtCompound.getList("Patterns", NbtTypeIds.COMPOUND).size() : 0;
	}

	@Environment(EnvType.CLIENT)
	public List<Pair<BannerPattern, DyeColor>> getPatterns() {
		if (this.patterns == null && this.patternListTagRead) {
			this.patterns = getPatternsFromNbt(this.baseColor, this.patternListTag);
		}

		return this.patterns;
	}

	@Environment(EnvType.CLIENT)
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
		NbtCompound nbtCompound = stack.getSubTag("BlockEntityTag");
		if (nbtCompound != null && nbtCompound.contains("Patterns", NbtTypeIds.LIST)) {
			NbtList nbtList = nbtCompound.getList("Patterns", NbtTypeIds.COMPOUND);
			if (!nbtList.isEmpty()) {
				nbtList.remove(nbtList.size() - 1);
				if (nbtList.isEmpty()) {
					stack.removeSubTag("BlockEntityTag");
				}
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public ItemStack getPickStack() {
		ItemStack itemStack = new ItemStack(BannerBlock.getForColor(this.baseColor));
		if (this.patternListTag != null && !this.patternListTag.isEmpty()) {
			itemStack.getOrCreateSubTag("BlockEntityTag").put("Patterns", this.patternListTag.copy());
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
