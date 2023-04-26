package net.minecraft.client.sound;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.sound.SoundEvents;
import net.minecraft.world.World;

@Environment(EnvType.CLIENT)
public class BubbleColumnSoundPlayer implements ClientPlayerTickable {
	private final ClientPlayerEntity player;
	private boolean hasPlayedForCurrentColumn;
	private boolean firstTick = true;

	public BubbleColumnSoundPlayer(ClientPlayerEntity player) {
		this.player = player;
	}

	@Override
	public void tick() {
		World world = this.player.getWorld();
		BlockState blockState = (BlockState)world.getStatesInBoxIfLoaded(this.player.getBoundingBox().expand(0.0, -0.4F, 0.0).contract(1.0E-6))
			.filter(state -> state.isOf(Blocks.BUBBLE_COLUMN))
			.findFirst()
			.orElse(null);
		if (blockState != null) {
			if (!this.hasPlayedForCurrentColumn && !this.firstTick && blockState.isOf(Blocks.BUBBLE_COLUMN) && !this.player.isSpectator()) {
				boolean bl = (Boolean)blockState.get(BubbleColumnBlock.DRAG);
				if (bl) {
					this.player.playSound(SoundEvents.BLOCK_BUBBLE_COLUMN_WHIRLPOOL_INSIDE, 1.0F, 1.0F);
				} else {
					this.player.playSound(SoundEvents.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, 1.0F, 1.0F);
				}
			}

			this.hasPlayedForCurrentColumn = true;
		} else {
			this.hasPlayedForCurrentColumn = false;
		}

		this.firstTick = false;
	}
}
