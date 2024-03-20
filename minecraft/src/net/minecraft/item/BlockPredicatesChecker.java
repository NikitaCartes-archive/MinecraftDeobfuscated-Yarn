package net.minecraft.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.predicate.BlockPredicate;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;

/**
 * Checks if a block predicate stored inside {@link ItemStack}'s NBT
 * matches the block in a world. The predicate must be stored inside
 * the {@code key} sub NBT of the item stack.
 * 
 * <p>The result is cached to reduce cost for successive lookups
 * on the same block.
 * 
 * @apiNote This is used to implement checks for restrictions specified
 * using {@code CanPlaceOn} or {@code CanDestroy}.
 */
public class BlockPredicatesChecker {
	private static final Codec<BlockPredicatesChecker> SINGLE_CODEC = BlockPredicate.CODEC
		.flatComapMap(predicate -> new BlockPredicatesChecker(List.of(predicate), true), checker -> DataResult.error(() -> "Cannot encode"));
	private static final Codec<BlockPredicatesChecker> FULL_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.nonEmptyList(BlockPredicate.CODEC.listOf()).fieldOf("predicates").forGetter(checker -> checker.predicates),
					Codecs.createStrictOptionalFieldCodec(Codec.BOOL, "show_in_tooltip", true).forGetter(BlockPredicatesChecker::showInTooltip)
				)
				.apply(instance, BlockPredicatesChecker::new)
	);
	public static final Codec<BlockPredicatesChecker> CODEC = Codecs.alternatively(FULL_CODEC, SINGLE_CODEC);
	public static final PacketCodec<RegistryByteBuf, BlockPredicatesChecker> PACKET_CODEC = PacketCodec.tuple(
		BlockPredicate.PACKET_CODEC.collect(PacketCodecs.toList()),
		blockPredicatesChecker -> blockPredicatesChecker.predicates,
		PacketCodecs.BOOL,
		BlockPredicatesChecker::showInTooltip,
		BlockPredicatesChecker::new
	);
	public static final Text CAN_BREAK_TEXT = Text.translatable("item.canBreak").formatted(Formatting.GRAY);
	public static final Text CAN_PLACE_TEXT = Text.translatable("item.canPlace").formatted(Formatting.GRAY);
	private static final Text CAN_USE_UNKNOWN_TEXT = Text.translatable("item.canUse.unknown").formatted(Formatting.GRAY);
	private final List<BlockPredicate> predicates;
	private final boolean showInTooltip;
	private final List<Text> tooltipText;
	@Nullable
	private CachedBlockPosition cachedPos;
	private boolean lastResult;
	private boolean nbtAware;

	private BlockPredicatesChecker(List<BlockPredicate> predicates, boolean showInTooltip, List<Text> tooltipText) {
		this.predicates = predicates;
		this.showInTooltip = showInTooltip;
		this.tooltipText = tooltipText;
	}

	public BlockPredicatesChecker(List<BlockPredicate> predicates, boolean showInTooltip) {
		this.predicates = predicates;
		this.showInTooltip = showInTooltip;
		this.tooltipText = getTooltipText(predicates);
	}

	private static boolean canUseCache(CachedBlockPosition pos, @Nullable CachedBlockPosition cachedPos, boolean nbtAware) {
		if (cachedPos == null || pos.getBlockState() != cachedPos.getBlockState()) {
			return false;
		} else if (!nbtAware) {
			return true;
		} else if (pos.getBlockEntity() == null && cachedPos.getBlockEntity() == null) {
			return true;
		} else if (pos.getBlockEntity() != null && cachedPos.getBlockEntity() != null) {
			DynamicRegistryManager dynamicRegistryManager = pos.getWorld().getRegistryManager();
			return Objects.equals(pos.getBlockEntity().createNbtWithId(dynamicRegistryManager), cachedPos.getBlockEntity().createNbtWithId(dynamicRegistryManager));
		} else {
			return false;
		}
	}

	/**
	 * {@return true if any of the predicates in the {@code stack}'s NBT
	 * matched against the block at {@code pos}, false otherwise}
	 */
	public boolean check(CachedBlockPosition cachedPos) {
		if (canUseCache(cachedPos, this.cachedPos, this.nbtAware)) {
			return this.lastResult;
		} else {
			this.cachedPos = cachedPos;
			this.nbtAware = false;

			for (BlockPredicate blockPredicate : this.predicates) {
				if (blockPredicate.test(cachedPos)) {
					this.nbtAware = this.nbtAware | blockPredicate.hasNbt();
					this.lastResult = true;
					return true;
				}
			}

			this.lastResult = false;
			return false;
		}
	}

	public void addTooltips(Consumer<Text> adder) {
		this.tooltipText.forEach(adder);
	}

	public BlockPredicatesChecker withShowInTooltip(boolean showInTooltip) {
		return new BlockPredicatesChecker(this.predicates, showInTooltip, this.tooltipText);
	}

	private static List<Text> getTooltipText(List<BlockPredicate> blockPredicates) {
		for (BlockPredicate blockPredicate : blockPredicates) {
			if (blockPredicate.blocks().isEmpty()) {
				return List.of(CAN_USE_UNKNOWN_TEXT);
			}
		}

		return blockPredicates.stream()
			.flatMap(predicate -> ((RegistryEntryList)predicate.blocks().orElseThrow()).stream())
			.distinct()
			.map(registryEntry -> ((Block)registryEntry.value()).getName().formatted(Formatting.DARK_GRAY))
			.toList();
	}

	public boolean showInTooltip() {
		return this.showInTooltip;
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else {
			return !(o instanceof BlockPredicatesChecker blockPredicatesChecker)
				? false
				: this.predicates.equals(blockPredicatesChecker.predicates) && this.showInTooltip == blockPredicatesChecker.showInTooltip;
		}
	}

	public int hashCode() {
		return this.predicates.hashCode() * 31 + (this.showInTooltip ? 1 : 0);
	}

	public String toString() {
		return "AdventureModePredicate{predicates=" + this.predicates + ", showInTooltip=" + this.showInTooltip + "}";
	}
}
