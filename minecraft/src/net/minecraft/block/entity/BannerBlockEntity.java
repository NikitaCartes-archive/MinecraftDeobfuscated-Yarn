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
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;

public class BannerBlockEntity extends BlockEntity implements Nameable {
	public static final int MAX_PATTERN_COUNT = 6;
	public static final String PATTERNS_KEY = "Patterns";
	public static final String PATTERN_KEY = "Pattern";
	public static final String COLOR_KEY = "Color";
	@Nullable
	private Text customName;
	private DyeColor baseColor;
	@Nullable
	private NbtList patternListNbt;
	@Nullable
	private List<Pair<RegistryEntry<BannerPattern>, DyeColor>> patterns;

	public BannerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.BANNER, pos, state);
		this.baseColor = ((AbstractBannerBlock)state.getBlock()).getColor();
	}

	public BannerBlockEntity(BlockPos pos, BlockState state, DyeColor baseColor) {
		this(pos, state);
		this.baseColor = baseColor;
	}

	@Nullable
	public static NbtList getPatternListNbt(ItemStack stack) {
		NbtList nbtList = null;
		NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
		if (nbtCompound != null && nbtCompound.contains("Patterns", NbtElement.LIST_TYPE)) {
			nbtList = nbtCompound.getList("Patterns", NbtElement.COMPOUND_TYPE).copy();
		}

		return nbtList;
	}

	public void readFrom(ItemStack stack, DyeColor baseColor) {
		this.baseColor = baseColor;
		this.readFrom(stack);
	}

	public void readFrom(ItemStack stack) {
		this.patternListNbt = getPatternListNbt(stack);
		this.patterns = null;
		this.customName = stack.hasCustomName() ? stack.getName() : null;
	}

	@Override
	public Text getName() {
		return (Text)(this.customName != null ? this.customName : Text.translatable("block.minecraft.banner"));
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
		if (this.patternListNbt != null) {
			nbt.put("Patterns", this.patternListNbt);
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

		this.patternListNbt = nbt.getList("Patterns", NbtElement.COMPOUND_TYPE);
		this.patterns = null;
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

	public List<Pair<RegistryEntry<BannerPattern>, DyeColor>> getPatterns() {
		if (this.patterns == null) {
			this.patterns = getPatternsFromNbt(this.baseColor, this.patternListNbt);
		}

		return this.patterns;
	}

	public static List<Pair<RegistryEntry<BannerPattern>, DyeColor>> getPatternsFromNbt(DyeColor baseColor, @Nullable NbtList patternListNbt) {
		List<Pair<RegistryEntry<BannerPattern>, DyeColor>> list = Lists.<Pair<RegistryEntry<BannerPattern>, DyeColor>>newArrayList();
		list.add(Pair.of(Registry.BANNER_PATTERN.entryOf(BannerPatterns.BASE), baseColor));
		if (patternListNbt != null) {
			for (int i = 0; i < patternListNbt.size(); i++) {
				NbtCompound nbtCompound = patternListNbt.getCompound(i);
				RegistryEntry<BannerPattern> registryEntry = BannerPattern.byId(nbtCompound.getString("Pattern"));
				if (registryEntry != null) {
					int j = nbtCompound.getInt("Color");
					list.add(Pair.of(registryEntry, DyeColor.byId(j)));
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
		if (this.patternListNbt != null && !this.patternListNbt.isEmpty()) {
			NbtCompound nbtCompound = new NbtCompound();
			nbtCompound.put("Patterns", this.patternListNbt.copy());
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
