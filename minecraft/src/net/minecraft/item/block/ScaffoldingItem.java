package net.minecraft.item.block;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.ChatMessageS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sortme.ChatMessageType;
import net.minecraft.text.TextFormat;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class ScaffoldingItem extends BlockItem {
	public ScaffoldingItem(Block block, Item.Settings settings) {
		super(block, settings);
	}

	@Nullable
	@Override
	public ItemPlacementContext method_16356(ItemPlacementContext itemPlacementContext) {
		BlockPos blockPos = itemPlacementContext.method_8037();
		World world = itemPlacementContext.method_8045();
		BlockState blockState = world.method_8320(blockPos);
		Block block = this.method_7711();
		if (blockState.getBlock() != block) {
			return itemPlacementContext;
		} else {
			Direction direction;
			if (itemPlacementContext.isPlayerSneaking()) {
				direction = itemPlacementContext.method_17699() ? itemPlacementContext.method_8038().getOpposite() : itemPlacementContext.method_8038();
			} else {
				direction = itemPlacementContext.method_8038() == Direction.UP ? itemPlacementContext.method_8042() : Direction.UP;
			}

			int i = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable(blockPos).method_10098(direction);

			while (i < 7) {
				if (!world.isClient && !World.method_8558(mutable)) {
					PlayerEntity playerEntity = itemPlacementContext.getPlayer();
					int j = world.getHeight();
					if (playerEntity instanceof ServerPlayerEntity && mutable.getY() >= j) {
						ChatMessageS2CPacket chatMessageS2CPacket = new ChatMessageS2CPacket(
							new TranslatableTextComponent("build.tooHigh", j).applyFormat(TextFormat.field_1061), ChatMessageType.field_11733
						);
						((ServerPlayerEntity)playerEntity).field_13987.sendPacket(chatMessageS2CPacket);
					}
					break;
				}

				blockState = world.method_8320(mutable);
				if (blockState.getBlock() != this.method_7711()) {
					if (blockState.method_11587(itemPlacementContext)) {
						return ItemPlacementContext.method_16355(itemPlacementContext, mutable, direction);
					}
					break;
				}

				mutable.method_10098(direction);
				if (direction.getAxis().isHorizontal()) {
					i++;
				}
			}

			return null;
		}
	}
}
