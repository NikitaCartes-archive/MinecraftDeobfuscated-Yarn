package net.minecraft.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class DebugStickItem extends Item {
	public DebugStickItem(Item.Settings settings) {
		super(settings);
	}

	@Override
	public boolean hasGlint(ItemStack stack) {
		return true;
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
			this.use(playerEntity, world.getBlockState(blockPos), world, blockPos, true, context.getStack());
		}

		return ActionResult.success(world.isClient);
	}

	private void use(PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos, boolean update, ItemStack stack) {
		if (player.isCreativeLevelTwoOp()) {
			Block block = state.getBlock();
			StateManager<Block, BlockState> stateManager = block.getStateManager();
			Collection<Property<?>> collection = stateManager.getProperties();
			String string = Registry.BLOCK.getId(block).toString();
			if (collection.isEmpty()) {
				sendMessage(player, new TranslatableText(this.getTranslationKey() + ".empty", string));
			} else {
				CompoundTag compoundTag = stack.getOrCreateSubTag("DebugProperty");
				String string2 = compoundTag.getString(string);
				Property<?> property = stateManager.getProperty(string2);
				if (update) {
					if (property == null) {
						property = (Property<?>)collection.iterator().next();
					}

					BlockState blockState = cycle(state, property, player.shouldCancelInteraction());
					world.setBlockState(pos, blockState, 18);
					sendMessage(player, new TranslatableText(this.getTranslationKey() + ".update", property.getName(), getValueString(blockState, property)));
				} else {
					property = cycle(collection, property, player.shouldCancelInteraction());
					String string3 = property.getName();
					compoundTag.putString(string, string3);
					sendMessage(player, new TranslatableText(this.getTranslationKey() + ".select", string3, getValueString(state, property)));
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
		((ServerPlayerEntity)player).sendMessage(message, MessageType.GAME_INFO, Util.NIL_UUID);
	}

	private static <T extends Comparable<T>> String getValueString(BlockState state, Property<T> property) {
		return property.name(state.get(property));
	}
}
