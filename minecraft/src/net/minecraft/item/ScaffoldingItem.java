package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.ChatFormat;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ScaffoldingItem extends BlockItem {
	public ScaffoldingItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Nullable
	@Override
	public ItemPlacementContext getPlacementContext(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.getBlockPos();
		World world = itemPlacementContext.getWorld();
		BlockState blockState = world.getBlockState(blockPos);
		Block block = this.getBlock();
		if (blockState.getBlock() != block) {
			return ScaffoldingBlock.calculateDistance(world, blockPos) == 7 ? null : itemPlacementContext;
		} else {
			Direction direction;
			if (itemPlacementContext.isPlayerSneaking()) {
				direction = itemPlacementContext.method_17699() ? itemPlacementContext.getFacing().getOpposite() : itemPlacementContext.getFacing();
			} else {
				direction = itemPlacementContext.getFacing() == Direction.field_11036 ? itemPlacementContext.getPlayerHorizontalFacing() : Direction.field_11036;
			}

			int i = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos).setOffset(direction);

			while (i < 7) {
				if (!world.isClient && !World.isValid(mutable)) {
					PlayerEntity playerEntity = itemPlacementContext.getPlayer();
					int j = world.getHeight();
					if (playerEntity instanceof ServerPlayerEntity && mutable.getY() >= j) {
						ChatMessageS2CPacket chatMessageS2CPacket = new ChatMessageS2CPacket(
							new TranslatableComponent("build.tooHigh", j).applyFormat(ChatFormat.field_1061), ChatMessageType.field_11733
						);
						((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(chatMessageS2CPacket);
					}
					break;
				}

				blockState = world.getBlockState(mutable);
				if (blockState.getBlock() != this.getBlock()) {
					if (blockState.canReplace(itemPlacementContext)) {
						return ItemPlacementContext.create(itemPlacementContext, mutable, direction);
					}
					break;
				}

				mutable.setOffset(direction);
				if (direction.getAxis().isHorizontal()) {
					i++;
				}
			}

			return null;
		}
	}

	@Override
	protected boolean shouldCheckIfStateAllowsPlacement() {
		return false;
	}
}
