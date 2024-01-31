package net.minecraft.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.command.argument.BlockPredicateArgumentType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;

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
	private final String key;
	@Nullable
	private CachedBlockPosition cachedPos;
	private boolean lastResult;
	private boolean nbtAware;

	/**
	 * @param key the sub NBT key that stores the predicate, for
	 * example {@code "CanPlaceOn"}
	 */
	public BlockPredicatesChecker(String key) {
		this.key = key;
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
	public boolean check(ItemStack stack, Registry<Block> blockRegistry, CachedBlockPosition pos) {
		if (canUseCache(pos, this.cachedPos, this.nbtAware)) {
			return this.lastResult;
		} else {
			this.cachedPos = pos;
			this.nbtAware = false;
			NbtCompound nbtCompound = stack.getNbt();
			if (nbtCompound != null && nbtCompound.contains(this.key, NbtElement.LIST_TYPE)) {
				NbtList nbtList = nbtCompound.getList(this.key, NbtElement.STRING_TYPE);

				for (int i = 0; i < nbtList.size(); i++) {
					String string = nbtList.getString(i);

					try {
						BlockPredicateArgumentType.BlockPredicate blockPredicate = BlockPredicateArgumentType.parse(blockRegistry.getReadOnlyWrapper(), new StringReader(string));
						this.nbtAware = this.nbtAware | blockPredicate.hasNbt();
						if (blockPredicate.test(pos)) {
							this.lastResult = true;
							return true;
						}
					} catch (CommandSyntaxException var9) {
					}
				}
			}

			this.lastResult = false;
			return false;
		}
	}
}
