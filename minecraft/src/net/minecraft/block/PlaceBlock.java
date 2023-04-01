package net.minecraft.block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.class_8293;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.PlaceBlockItemPlacementContext;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.tick.TickPriority;

public class PlaceBlock extends WorldModifyingBlock {
	protected PlaceBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected TickPriority getTickPriority() {
		return TickPriority.EXTREMELY_LOW;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (class_8293.field_43551.method_50116()) {
			Direction direction = state.get(FACING);
			Direction direction2 = direction.getOpposite();
			BlockPos blockPos = pos.offset(direction2);
			BlockPos blockPos2 = pos.offset(direction);
			placeBlock(
				world,
				blockPos,
				direction2,
				stack -> {
					if (stack.isEmpty()) {
						return false;
					} else {
						boolean var10000;
						label19: {
							if (stack.getItem() instanceof BlockItem blockItem
								&& blockItem.place(
										new PlaceBlockItemPlacementContext(world, Hand.MAIN_HAND, stack, new BlockHitResult(blockPos2.toCenterPos(), direction2, blockPos2, false))
									)
									.isAccepted()) {
								var10000 = true;
								break label19;
							}

							var10000 = false;
						}

						boolean bl = var10000;
						if (!bl) {
							double d = (double)EntityType.ITEM.getHeight() / 2.0;
							double e = (double)blockPos2.getX() + 0.5;
							double f = (double)blockPos2.getY() + 0.5 - d;
							double g = (double)blockPos2.getZ() + 0.5;
							ItemEntity itemEntity = new ItemEntity(world, e, f, g, stack);
							itemEntity.setToDefaultPickupDelay();
							world.spawnEntity(itemEntity);
						}

						return true;
					}
				}
			);
		}
	}

	public static boolean insertItem(World world, BlockPos pos, Direction side, ItemStack stack) {
		for (Inventory inventory : findInventories(world, pos)) {
			ItemStack itemStack = HopperBlockEntity.transfer(null, inventory, stack, side);
			if (itemStack.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	public static boolean placeBlock(World world, BlockPos pos, Direction side, Function<ItemStack, Boolean> placer) {
		for (Inventory inventory : findInventories(world, pos)) {
			boolean bl = HopperBlockEntity.getAvailableSlots(inventory, side).anyMatch(slot -> {
				ItemStack itemStackx = inventory.removeStack(slot, 1);
				if (!itemStackx.isEmpty()) {
					boolean blx = (Boolean)placer.apply(itemStackx.copy());
					if (blx) {
						inventory.markDirty();
					} else {
						inventory.setStack(slot, itemStackx);
					}

					return true;
				} else {
					return false;
				}
			});
			if (bl) {
				return true;
			}
		}

		ItemEntity itemEntity = findItemEntity(world, pos);
		if (itemEntity != null) {
			ItemStack itemStack = itemEntity.getStack();
			if (!itemStack.isEmpty()) {
				boolean bl = (Boolean)placer.apply(itemStack.copyWithCount(1));
				if (bl) {
					itemStack.decrement(1);
					if (itemStack.getCount() <= 0) {
						itemEntity.discard();
					}
				}

				return true;
			}
		}

		return false;
	}

	public static List<Inventory> findInventories(World world, BlockPos pos) {
		BlockState blockState = world.getBlockState(pos);
		Block block = blockState.getBlock();
		if (block instanceof InventoryProvider) {
			SidedInventory sidedInventory = ((InventoryProvider)block).getInventory(blockState, world, pos);
			if (sidedInventory != null) {
				return List.of(sidedInventory);
			}
		} else if (blockState.hasBlockEntity()) {
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof Inventory) {
				if (!(blockEntity instanceof ChestBlockEntity) || !(block instanceof ChestBlock)) {
					return List.of((Inventory)blockEntity);
				}

				Inventory inventory = ChestBlock.getInventory((ChestBlock)block, blockState, world, pos, true);
				if (inventory != null) {
					return List.of(inventory);
				}
			}
		}

		List<Inventory> list = new ArrayList();

		for (Entity entity : world.getOtherEntities((Entity)null, createSearchBox(pos), EntityPredicates.VALID_INVENTORIES)) {
			if (entity instanceof Inventory inventory2) {
				list.add(inventory2);
			}
		}

		return list;
	}

	@Nullable
	public static ItemEntity findItemEntity(World world, BlockPos pos) {
		List<ItemEntity> list = world.getEntitiesByClass(ItemEntity.class, createSearchBox(pos), EntityPredicates.VALID_ENTITY);
		return list.size() < 1 ? null : (ItemEntity)list.get(0);
	}

	private static Box createSearchBox(BlockPos pos) {
		double d = 0.9999999;
		return Box.of(pos.toCenterPos(), 0.9999999, 0.9999999, 0.9999999);
	}
}
