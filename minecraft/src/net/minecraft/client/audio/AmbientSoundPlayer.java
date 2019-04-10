package net.minecraft.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import net.minecraft.sound.SoundEvents;

@Environment(EnvType.CLIENT)
public class AmbientSoundPlayer implements ClientPlayerTickable {
	private final ClientPlayerEntity player;
	private final SoundManager soundManager;
	private int ticksUntilPlay = 0;

	public AmbientSoundPlayer(ClientPlayerEntity clientPlayerEntity, SoundManager soundManager) {
		this.player = clientPlayerEntity;
		this.soundManager = soundManager;
	}

	@Override
	public void tick() {
		this.ticksUntilPlay--;
		if (this.ticksUntilPlay <= 0 && this.player.isInWater()) {
			float f = this.player.world.random.nextFloat();
			if (f < 1.0E-4F) {
				this.ticksUntilPlay = 0;
				this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.field_15178));
			} else if (f < 0.001F) {
				this.ticksUntilPlay = 0;
				this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.field_15068));
			} else if (f < 0.01F) {
				this.ticksUntilPlay = 0;
				this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.field_15028));
			}
		}
	}
}
