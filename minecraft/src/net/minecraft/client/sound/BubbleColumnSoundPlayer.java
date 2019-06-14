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

	public BubbleColumnSoundPlayer(ClientPlayerEntity clientPlayerEntity) {
		this.player = clientPlayerEntity;
	}

	@Override
	public void tick() {
		World world = this.player.field_6002;
		BlockState blockState = world.method_8475(this.player.method_5829().expand(0.0, -0.4F, 0.0).contract(0.001), Blocks.field_10422);
		if (blockState != null) {
			if (!this.hasPlayedForCurrentColumn && !this.firstTick && blockState.getBlock() == Blocks.field_10422 && !this.player.isSpectator()) {
				boolean bl = (Boolean)blockState.method_11654(BubbleColumnBlock.field_10680);
				if (bl) {
					this.player.playSound(SoundEvents.field_19196, 1.0F, 1.0F);
				} else {
					this.player.playSound(SoundEvents.field_19195, 1.0F, 1.0F);
				}
			}

			this.hasPlayedForCurrentColumn = true;
		} else {
			this.hasPlayedForCurrentColumn = false;
		}

		this.firstTick = false;
	}
}
