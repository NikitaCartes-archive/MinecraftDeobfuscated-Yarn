package net.minecraft.world;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;

public record GameModeList(List<GameMode> gameModes) {
	public static final GameModeList ALL = of(GameMode.values());
	public static final GameModeList SURVIVAL_LIKE = of(GameMode.SURVIVAL, GameMode.ADVENTURE);
	public static final Codec<GameModeList> CODEC = GameMode.CODEC.listOf().xmap(GameModeList::new, GameModeList::gameModes);

	public static GameModeList of(GameMode... gameModes) {
		return new GameModeList(Arrays.stream(gameModes).toList());
	}

	public boolean contains(GameMode gameMode) {
		return this.gameModes.contains(gameMode);
	}
}
