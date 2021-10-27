package net.minecraft.server.world;

import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.MathHelper;

/**
 * A sleep manager allows easy tracking of whether nights should be skipped
 * on a {@linkplain ServerWorld server world}.
 */
public class SleepManager {
	/**
	 * The total number of players in a server world.
	 */
	private int total;
	/**
	 * The number of players sleeping in a server world.
	 */
	private int sleeping;

	/**
	 * Returns if the number of sleeping players has reached a {@code percentage}
	 * out of all players.
	 * 
	 * <p>This allows initiating the night sleeping process,
	 * but still needs players to have slept long enough (checked in {@linkplain
	 * #canResetTime(int, List) canResetTime}) to actually skip the night.
	 * 
	 * @param percentage the percentage of players required, as obtained from the game rule
	 */
	public boolean canSkipNight(int percentage) {
		return this.sleeping >= this.getNightSkippingRequirement(percentage);
	}

	/**
	 * Returns if the night can actually be skipped at the tick this is called.
	 * 
	 * <p>This is usually tested after {@linkplain #canSkipNight(int) canSkipNight},
	 * which is less performance intensive to check.
	 * 
	 * @param percentage the percentage of players required, as obtained from the game rule
	 * @param players the list of all players in a world where the night would be skipped
	 */
	public boolean canResetTime(int percentage, List<ServerPlayerEntity> players) {
		int i = (int)players.stream().filter(PlayerEntity::canResetTimeBySleeping).count();
		return i >= this.getNightSkippingRequirement(percentage);
	}

	/**
	 * Returns {@linkplain #sleeping the number of sleepers} needed to skip
	 * a night with the given {@code percentage}.
	 * 
	 * @param percentage the percentage of players required, as obtained from the game rule
	 */
	public int getNightSkippingRequirement(int percentage) {
		return Math.max(1, MathHelper.ceil((float)(this.total * percentage) / 100.0F));
	}

	/**
	 * Resets the number of sleeping players to 0.
	 */
	public void clearSleeping() {
		this.sleeping = 0;
	}

	/**
	 * Returns the number of sleeping players.
	 */
	public int getSleeping() {
		return this.sleeping;
	}

	/**
	 * Updates the sleeping player and total player counts.
	 * 
	 * @return {@code true} if the sleeping players or total players have
	 * changed
	 * 
	 * @param players the list of all players in a server world
	 */
	public boolean update(List<ServerPlayerEntity> players) {
		int i = this.total;
		int j = this.sleeping;
		this.total = 0;
		this.sleeping = 0;

		for (ServerPlayerEntity serverPlayerEntity : players) {
			if (!serverPlayerEntity.isSpectator()) {
				this.total++;
				if (serverPlayerEntity.isSleeping()) {
					this.sleeping++;
				}
			}
		}

		return (j > 0 || this.sleeping > 0) && (i != this.total || j != this.sleeping);
	}
}
