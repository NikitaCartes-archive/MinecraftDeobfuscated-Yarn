package net.minecraft;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;

public class class_8301 extends class_8277.class_8280 {
	private final List<GameRules.Key<GameRules.BooleanRule>> field_43732 = List.of(
		GameRules.DO_FIRE_TICK,
		GameRules.DO_MOB_GRIEFING,
		GameRules.KEEP_INVENTORY,
		GameRules.DO_MOB_SPAWNING,
		GameRules.DO_MOB_LOOT,
		GameRules.DO_TILE_DROPS,
		GameRules.DO_ENTITY_DROPS,
		GameRules.NATURAL_REGENERATION,
		GameRules.DO_DAYLIGHT_CYCLE,
		GameRules.SHOW_DEATH_MESSAGES,
		GameRules.REDUCED_DEBUG_INFO,
		GameRules.DISABLE_ELYTRA_MOVEMENT_CHECK,
		GameRules.DO_WEATHER_CYCLE,
		GameRules.DO_LIMITED_CRAFTING,
		GameRules.ANNOUNCE_ADVANCEMENTS,
		GameRules.DISABLE_RAIDS,
		GameRules.DO_INSOMNIA,
		GameRules.DO_IMMEDIATE_RESPAWN,
		GameRules.DROWNING_DAMAGE,
		GameRules.FALL_DAMAGE,
		GameRules.FIRE_DAMAGE,
		GameRules.FREEZE_DAMAGE,
		GameRules.DO_PATROL_SPAWNING,
		GameRules.DO_TRADER_SPAWNING,
		GameRules.DO_WARDEN_SPAWNING,
		GameRules.FORGIVE_DEAD_PLAYERS,
		GameRules.UNIVERSAL_ANGER,
		GameRules.BLOCK_EXPLOSION_DROP_DECAY,
		GameRules.MOB_EXPLOSION_DROP_DECAY,
		GameRules.TNT_EXPLOSION_DROP_DECAY,
		GameRules.WATER_SOURCE_CONVERSION,
		GameRules.LAVA_SOURCE_CONVERSION,
		GameRules.GLOBAL_SOUND_EVENTS,
		GameRules.DO_VINES_SPREAD
	);
	private final Map<String, GameRules.Key<GameRules.BooleanRule>> field_43733 = (Map<String, GameRules.Key<GameRules.BooleanRule>>)this.field_43732
		.stream()
		.collect(Collectors.toMap(GameRules.Key::getName, key -> key));
	private final Codec<class_8301.class_8302> field_43734 = Codecs.idChecked(GameRules.Key::getName, this.field_43733::get)
		.xmap(key -> new class_8301.class_8302(key), arg -> arg.field_43736);

	@Override
	public Optional<class_8291> method_50169(MinecraftServer minecraftServer, Random random) {
		return Util.getRandomOrEmpty(this.field_43732, random).map(key -> new class_8301.class_8302(key));
	}

	@Override
	public Codec<class_8291> method_50120() {
		return class_8289.method_50202(this.field_43734);
	}

	class class_8302 extends class_8277.class_8278 {
		final GameRules.Key<GameRules.BooleanRule> field_43736;
		private final Text field_43737;

		class_8302(GameRules.Key<GameRules.BooleanRule> key) {
			this.field_43736 = key;
			this.field_43737 = Text.translatable("rule.flip_binary_gamerule", Text.translatable(key.getTranslationKey()));
		}

		@Override
		protected Text method_50166() {
			return this.field_43737;
		}

		@Override
		public void method_50165(MinecraftServer minecraftServer) {
			GameRules.BooleanRule booleanRule = minecraftServer.getSaveProperties().getGameRules().get(this.field_43736);
			booleanRule.set(!booleanRule.get(), minecraftServer);
		}
	}
}
