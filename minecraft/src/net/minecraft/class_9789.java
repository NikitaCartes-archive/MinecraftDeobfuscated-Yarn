package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.List;
import net.minecraft.world.GameMode;

public record class_9789(List<GameMode> types) {
	public static final class_9789 field_52012 = method_60724(GameMode.values());
	public static final class_9789 field_52013 = method_60724(GameMode.SURVIVAL, GameMode.ADVENTURE);
	public static final Codec<class_9789> field_52014 = GameMode.CODEC.listOf().xmap(class_9789::new, class_9789::types);

	public static class_9789 method_60724(GameMode... gameModes) {
		return new class_9789(Arrays.stream(gameModes).toList());
	}

	public boolean method_60723(GameMode gameMode) {
		return this.types.contains(gameMode);
	}
}
