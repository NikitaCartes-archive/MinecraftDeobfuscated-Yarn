package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Nameable;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;

public class BannerBlockEntity extends BlockEntity implements Nameable {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final int MAX_PATTERN_COUNT = 6;
	private static final String PATTERNS_KEY = "patterns";
	@Nullable
	private Text customName;
	private DyeColor baseColor;
	private BannerPatternsComponent patternsComponent = BannerPatternsComponent.DEFAULT;
	private BannerPatternsComponent patternsComponentWithBase = BannerPatternsComponent.DEFAULT;

	public BannerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.BANNER, pos, state);
		this.baseColor = ((AbstractBannerBlock)state.getBlock()).getColor();
		this.setPatterns(BannerPatternsComponent.DEFAULT);
	}

	public BannerBlockEntity(BlockPos pos, BlockState state, DyeColor baseColor) {
		this(pos, state);
		this.baseColor = baseColor;
	}

	public void readFrom(ItemStack stack, DyeColor baseColor) {
		this.baseColor = baseColor;
		this.readComponents(stack.getComponents());
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

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		if (!this.patternsComponent.equals(BannerPatternsComponent.DEFAULT)) {
			nbt.put("patterns", Util.getResult(BannerPatternsComponent.CODEC.encodeStart(NbtOps.INSTANCE, this.patternsComponent), IllegalStateException::new));
		}

		if (this.customName != null) {
			nbt.putString("CustomName", Text.Serialization.toJsonString(this.customName, registryLookup));
		}
	}

	@Override
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
			this.customName = Text.Serialization.fromJson(nbt.getString("CustomName"), registryLookup);
		}

		if (nbt.contains("patterns")) {
			BannerPatternsComponent.CODEC
				.parse(NbtOps.INSTANCE, nbt.get("patterns"))
				.resultOrPartial(string -> LOGGER.error("Failed to parse banner patterns: '{}'", string))
				.ifPresent(this::setPatterns);
		}
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return this.createNbt(registryLookup);
	}

	public BannerPatternsComponent getPatterns() {
		return this.patternsComponentWithBase;
	}

	public ItemStack getPickStack() {
		ItemStack itemStack = new ItemStack(BannerBlock.getForColor(this.baseColor));
		itemStack.applyComponentsFrom(this.createComponentMap());
		return itemStack;
	}

	public DyeColor getColorForState() {
		return this.baseColor;
	}

	@Override
	public void readComponents(ComponentMap components) {
		this.setPatterns(components.getOrDefault(DataComponentTypes.BANNER_PATTERNS, BannerPatternsComponent.DEFAULT));
		this.customName = components.get(DataComponentTypes.CUSTOM_NAME);
	}

	@Override
	public void addComponents(ComponentMap.Builder componentMapBuilder) {
		componentMapBuilder.add(DataComponentTypes.BANNER_PATTERNS, this.patternsComponent);
		componentMapBuilder.add(DataComponentTypes.CUSTOM_NAME, this.customName);
	}

	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		nbt.remove("patterns");
		nbt.remove("CustomName");
	}

	private void setPatterns(BannerPatternsComponent patternsComponent) {
		this.patternsComponent = patternsComponent;
		this.patternsComponentWithBase = this.patternsComponent.withBase(this.baseColor);
	}
}
