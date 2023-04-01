package net.minecraft;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.intprovider.ClampedIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.GameRules;

public class class_8329 extends class_8277 {
	private final List<Pair<GameRules.Key<GameRules.IntRule>, IntProvider>> field_43872 = List.of(
		Pair.of(GameRules.SNOW_ACCUMULATION_HEIGHT, UniformIntProvider.create(0, 8)),
		Pair.of(GameRules.PLAYERS_SLEEPING_PERCENTAGE, ClampedIntProvider.create(UniformIntProvider.create(-20, 120), 0, 100)),
		Pair.of(GameRules.MAX_ENTITY_CRAMMING, UniformIntProvider.create(0, 100)),
		Pair.of(GameRules.SPAWN_RADIUS, UniformIntProvider.create(1, 100)),
		Pair.of(GameRules.RANDOM_TICK_SPEED, UniformIntProvider.create(0, 20))
	);
	private final Map<String, GameRules.Key<GameRules.IntRule>> field_43873 = (Map<String, GameRules.Key<GameRules.IntRule>>)this.field_43872
		.stream()
		.collect(Collectors.toMap(pair -> ((GameRules.Key)pair.getFirst()).getName(), Pair::getFirst));
	private final Codec<GameRules.Key<GameRules.IntRule>> field_43874 = Codecs.idChecked(GameRules.Key::getName, this.field_43873::get);
	private final Codec<class_8329.class_8330> field_43875 = RecordCodecBuilder.create(
		instance -> instance.group(
					this.field_43874.fieldOf("game_rule_id").forGetter(arg -> arg.field_43877), Codec.INT.fieldOf("value").forGetter(arg -> arg.field_43878)
				)
				.apply(instance, (key, i) -> new class_8329.class_8330(key, i))
	);

	@Override
	public Stream<class_8291> method_50118(MinecraftServer minecraftServer, Random random, int i) {
		return Util.getRandomOrEmpty(this.field_43872, random).stream().flatMap(pair -> {
			GameRules.Key<GameRules.IntRule> key = (GameRules.Key<GameRules.IntRule>)pair.getFirst();
			int j = minecraftServer.getSaveProperties().getGameRules().get(key).get();
			IntProvider intProvider = (IntProvider)pair.getSecond();
			return Stream.generate(() -> intProvider.get(random)).filter(integer -> integer != j).limit((long)i).map(integer -> new class_8329.class_8330(key, integer));
		});
	}

	@Override
	public Codec<class_8291> method_50120() {
		return class_8289.method_50202(this.field_43875);
	}

	class class_8330 extends class_8277.class_8278 {
		final GameRules.Key<GameRules.IntRule> field_43877;
		final int field_43878;
		private final Text field_43879;

		class_8330(GameRules.Key<GameRules.IntRule> key, int i) {
			this.field_43877 = key;
			this.field_43878 = i;
			this.field_43879 = Text.translatable("rule.change_integer_gamerule", Text.translatable(key.getTranslationKey()), i);
		}

		@Override
		protected Text method_50166() {
			return this.field_43879;
		}

		@Override
		public void method_50165(MinecraftServer minecraftServer) {
			minecraftServer.getSaveProperties().getGameRules().get(this.field_43877).set(this.field_43878, minecraftServer);
		}
	}
}
