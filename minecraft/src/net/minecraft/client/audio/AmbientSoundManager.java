package net.minecraft.client.audio;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ClientPlayerTickable;

@Environment(EnvType.CLIENT)
public class AmbientSoundManager implements ClientPlayerTickable {
	private final ClientPlayerEntity player;
	private final SoundManager soundManager;
	private int field_5480 = 0;

	public AmbientSoundManager(ClientPlayerEntity clientPlayerEntity, SoundManager soundManager) {
		this.player = clientPlayerEntity;
		this.soundManager = soundManager;
	}

	@Override
	public void tick() {
		this.field_5480--;
		if (this.field_5480 <= 0 && this.player.isInWater()) {
			float f = this.player.world.random.nextFloat();
			if (f < 1.0E-4F) {
				this.field_5480 = 0;
				this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.field_15178));
			} else if (f < 0.001F) {
				this.field_5480 = 0;
				this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.field_15068));
			} else if (f < 0.01F) {
				this.field_5480 = 0;
				this.soundManager.play(new AmbientSoundLoops.MusicLoop(this.player, SoundEvents.field_15028));
			}
		}
	}
}
