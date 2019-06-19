package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ScaffoldingBlock;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
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
				direction = itemPlacementContext.method_17699() ? itemPlacementContext.getSide().getOpposite() : itemPlacementContext.getSide();
			} else {
				direction = itemPlacementContext.getSide() == Direction.field_11036 ? itemPlacementContext.getPlayerFacing() : Direction.field_11036;
			}

			int i = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos).setOffset(direction);

			while (i < 7) {
				if (!world.isClient && !World.isValid(mutable)) {
					PlayerEntity playerEntity = itemPlacementContext.getPlayer();
					int j = world.getHeight();
					if (playerEntity instanceof ServerPlayerEntity && mutable.getY() >= j) {
						ChatMessageS2CPacket chatMessageS2CPacket = new ChatMessageS2CPacket(
							new TranslatableText("build.tooHigh", j).formatted(Formatting.field_1061), MessageType.field_11733
						);
						((ServerPlayerEntity)playerEntity).networkHandler.sendPacket(chatMessageS2CPacket);
					}
					break;
				}

				blockState = world.getBlockState(mutable);
				if (blockState.getBlock() != this.getBlock()) {
					if (blockState.canReplace(itemPlacementContext)) {
						return ItemPlacementContext.offset(itemPlacementContext, mutable, direction);
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
	protected boolean checkStatePlacement() {
		return false;
	}
}
