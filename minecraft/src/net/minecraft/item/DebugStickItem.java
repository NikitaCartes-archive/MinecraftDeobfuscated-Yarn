package net.minecraft.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.DebugStickStateComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class DebugStickItem extends Item {
	public DebugStickItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
		if (!world.isClient) {
			this.use(miner, state, world, pos, false, miner.getStackInHand(Hand.MAIN_HAND));
		}

		return false;
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		PlayerEntity playerEntity = context.getPlayer();
		World world = context.getWorld();
		if (!world.isClient && playerEntity != null) {
			BlockPos blockPos = context.getBlockPos();
			if (!this.use(playerEntity, world.getBlockState(blockPos), world, blockPos, true, context.getStack())) {
				return ActionResult.FAIL;
			}
		}

		return ActionResult.SUCCESS;
	}

	private boolean use(PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos, boolean update, ItemStack stack) {
		if (!player.isCreativeLevelTwoOp()) {
			return false;
		} else {
			RegistryEntry<Block> registryEntry = state.getRegistryEntry();
			StateManager<Block, BlockState> stateManager = registryEntry.value().getStateManager();
			Collection<Property<?>> collection = stateManager.getProperties();
			if (collection.isEmpty()) {
				sendMessage(player, Text.translatable(this.getTranslationKey() + ".empty", registryEntry.getIdAsString()));
				return false;
			} else {
				DebugStickStateComponent debugStickStateComponent = stack.get(DataComponentTypes.DEBUG_STICK_STATE);
				if (debugStickStateComponent == null) {
					return false;
				} else {
					Property<?> property = (Property<?>)debugStickStateComponent.properties().get(registryEntry);
					if (update) {
						if (property == null) {
							property = (Property<?>)collection.iterator().next();
						}

						BlockState blockState = cycle(state, property, player.shouldCancelInteraction());
						world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
						sendMessage(player, Text.translatable(this.getTranslationKey() + ".update", property.getName(), getValueString(blockState, property)));
					} else {
						property = cycle(collection, property, player.shouldCancelInteraction());
						stack.set(DataComponentTypes.DEBUG_STICK_STATE, debugStickStateComponent.with(registryEntry, property));
						sendMessage(player, Text.translatable(this.getTranslationKey() + ".select", property.getName(), getValueString(state, property)));
					}

					return true;
				}
			}
		}
	}

	private static <T extends Comparable<T>> BlockState cycle(BlockState state, Property<T> property, boolean inverse) {
		return state.with(property, cycle(property.getValues(), state.get(property), inverse));
	}

	private static <T> T cycle(Iterable<T> elements, @Nullable T current, boolean inverse) {
		return inverse ? Util.previous(elements, current) : Util.next(elements, current);
	}

	private static void sendMessage(PlayerEntity player, Text message) {
		((ServerPlayerEntity)player).sendMessageToClient(message, true);
	}

	private static <T extends Comparable<T>> String getValueString(BlockState state, Property<T> property) {
		return property.name(state.get(property));
	}
}
