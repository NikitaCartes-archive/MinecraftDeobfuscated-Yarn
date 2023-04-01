package net.minecraft;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.Util;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.intprovider.ClampedNormalIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.random.Random;

public interface class_8390 {
	MapCodec<class_8390> field_44030 = class_8390.class_8393.field_44041.dispatchMap(class_8390::method_50601, arg -> (Codec)arg.field_44043.get());
	IntProvider field_44031 = ClampedNormalIntProvider.of(1.0F, 3.0F, 1, 10);
	class_8390 field_44032 = new class_8390() {
		@Override
		public boolean method_50602(ServerPlayerEntity serverPlayerEntity, int i, boolean bl) {
			throw new AssertionError("You forgot to implement the hack!");
		}

		@Override
		public class_8390.class_8393 method_50601() {
			return class_8390.class_8393.PER_PROPOSAL;
		}

		@Override
		public Text method_50604() {
			return Text.translatable("vote.count_per_proposal.description");
		}
	};
	class_8390 field_44033 = new class_8390() {
		@Override
		public boolean method_50602(ServerPlayerEntity serverPlayerEntity, int i, boolean bl) {
			throw new AssertionError("You forgot to implement the hack!");
		}

		@Override
		public class_8390.class_8393 method_50601() {
			return class_8390.class_8393.PER_OPTION;
		}

		@Override
		public Text method_50604() {
			return Text.translatable("vote.count_per_option.description");
		}
	};

	boolean method_50602(ServerPlayerEntity serverPlayerEntity, int i, boolean bl);

	class_8390.class_8393 method_50601();

	Text method_50604();

	public static record class_8391(class_8390 material, int count) {
		public static final Codec<class_8390.class_8391> field_44034 = RecordCodecBuilder.create(
			instance -> instance.group(
						class_8390.field_44030.forGetter(class_8390.class_8391::material), Codec.INT.fieldOf("count").forGetter(class_8390.class_8391::count)
					)
					.apply(instance, class_8390.class_8391::new)
		);

		public boolean method_50605(ServerPlayerEntity serverPlayerEntity, boolean bl) {
			return this.material.method_50602(serverPlayerEntity, this.count, bl);
		}

		public Text method_50607(boolean bl) {
			return (Text)(!bl && this.count == 1 ? this.material.method_50604() : Text.translatable("vote.cost_diplay", this.count, this.material.method_50604()));
		}
	}

	public static record class_8392(Text displayName) implements class_8390 {
		static final Codec<class_8390.class_8392> field_44035 = Codecs.TEXT.xmap(class_8390.class_8392::new, class_8390.class_8392::displayName);

		@Override
		public class_8390.class_8393 method_50601() {
			return class_8390.class_8393.CUSTOM;
		}

		@Override
		public boolean method_50602(ServerPlayerEntity serverPlayerEntity, int i, boolean bl) {
			return true;
		}

		@Override
		public Text method_50604() {
			return this.displayName;
		}
	}

	public static enum class_8393 implements StringIdentifiable {
		PER_PROPOSAL("per_proposal", () -> com.mojang.serialization.Codec.unit(class_8390.field_44032)) {
			@Override
			public Optional<class_8390.class_8391> method_50609(Random random) {
				int i = class_8390.field_44031.get(random);
				return Optional.of(new class_8390.class_8391(class_8390.field_44032, i));
			}
		},
		PER_OPTION("per_option", () -> com.mojang.serialization.Codec.unit(class_8390.field_44033)) {
			@Override
			public Optional<class_8390.class_8391> method_50609(Random random) {
				int i = class_8390.field_44031.get(random);
				return Optional.of(new class_8390.class_8391(class_8390.field_44033, i));
			}
		},
		ITEM("item", () -> class_8390.class_8394.field_44045) {
			@Override
			public Optional<class_8390.class_8391> method_50609(Random random) {
				return Registries.ITEM
					.getRandom(random)
					.flatMap(
						reference -> {
							if (reference.value() == Items.AIR) {
								return Optional.empty();
							} else {
								int i = ClampedNormalIntProvider.of(5.0F, 5.0F, 1, ((Item)reference.value()).getMaxCount()).get(random);
								String string = class_8390.class_8394.method_50617(reference.registryKey());
								String string2 = ((Item)reference.value()).getTranslationKey();
								return Optional.of(
									new class_8390.class_8391(new class_8390.class_8394(reference.registryKey(), string.equals(string2) ? Optional.empty() : Optional.of(string2)), i)
								);
							}
						}
					);
			}
		},
		RESOURCE("resource", () -> class_8390.class_8395.field_44048) {
			@Override
			public Optional<class_8390.class_8391> method_50609(Random random) {
				return Util.getRandomOrEmpty(Arrays.asList(class_8390.class_8395.values()), random).map(arg -> {
					int i = arg.field_44050.get(random);
					return new class_8390.class_8391(arg, i);
				});
			}
		},
		CUSTOM("custom", () -> class_8390.class_8392.field_44035) {
			@Override
			public Optional<class_8390.class_8391> method_50609(Random random) {
				return Optional.empty();
			}
		};

		public static final com.mojang.serialization.Codec<class_8390.class_8393> field_44041 = StringIdentifiable.createCodec(class_8390.class_8393::values);
		private final String field_44042;
		final Supplier<com.mojang.serialization.Codec<? extends class_8390>> field_44043;

		class_8393(String string2, Supplier<com.mojang.serialization.Codec<? extends class_8390>> supplier) {
			this.field_44042 = string2;
			this.field_44043 = supplier;
		}

		@Override
		public String asString() {
			return this.field_44042;
		}

		public abstract Optional<class_8390.class_8391> method_50609(Random random);
	}

	public static record class_8394(RegistryKey<Item> id, Optional<String> translationKey) implements class_8390 {
		static final Codec<class_8390.class_8394> field_44045 = RecordCodecBuilder.create(
			instance -> instance.group(
						RegistryKey.createCodec(RegistryKeys.ITEM).fieldOf("item").forGetter(class_8390.class_8394::id),
						Codec.STRING.optionalFieldOf("translation_key").forGetter(class_8390.class_8394::translationKey)
					)
					.apply(instance, class_8390.class_8394::new)
		);

		@Override
		public class_8390.class_8393 method_50601() {
			return class_8390.class_8393.ITEM;
		}

		@Override
		public boolean method_50602(ServerPlayerEntity serverPlayerEntity, int i, boolean bl) {
			int j = bl ? 0 : i;
			int k = serverPlayerEntity.getInventory().remove(itemStack -> itemStack.matchesKey(this.id), j, serverPlayerEntity.playerScreenHandler.getCraftingInput());
			return k >= i;
		}

		@Override
		public Text method_50604() {
			return Text.translatable((String)this.translationKey.orElseGet(() -> method_50617(this.id)));
		}

		public static String method_50617(RegistryKey<Item> registryKey) {
			return Util.createTranslationKey("item", registryKey.getValue());
		}
	}

	public static enum class_8395 implements StringIdentifiable, class_8390 {
		XP("xp", ClampedNormalIntProvider.of(5.0F, 5.0F, 1, 30)) {
			private static final Text field_44052 = Text.translatable("vote.cost.xp");

			@Override
			public boolean method_50602(ServerPlayerEntity serverPlayerEntity, int i, boolean bl) {
				if (serverPlayerEntity.experienceLevel < i) {
					return false;
				} else {
					if (!bl) {
						serverPlayerEntity.addExperienceLevels(-i);
					}

					return true;
				}
			}

			@Override
			public Text method_50604() {
				return field_44052;
			}
		},
		HEALTH("health", ClampedNormalIntProvider.of(2.0F, 3.0F, 1, 20)) {
			private static final Text field_44053 = Text.translatable("vote.cost.health");

			@Override
			public boolean method_50602(ServerPlayerEntity serverPlayerEntity, int i, boolean bl) {
				float f = serverPlayerEntity.getHealth();
				if (f < (float)i) {
					return false;
				} else {
					if (!bl) {
						serverPlayerEntity.setHealth(f - (float)i);
						serverPlayerEntity.damageWithModifier(serverPlayerEntity.getWorld().getDamageSources().generic(), 0.0F);
					}

					return true;
				}
			}

			@Override
			public Text method_50604() {
				return field_44053;
			}
		};

		public static final com.mojang.serialization.Codec<class_8390.class_8395> field_44048 = StringIdentifiable.createCodec(class_8390.class_8395::values);
		private final String field_44049;
		final IntProvider field_44050;

		@Override
		public String asString() {
			return this.field_44049;
		}

		class_8395(String string2, IntProvider intProvider) {
			this.field_44049 = string2;
			this.field_44050 = intProvider;
		}

		@Override
		public class_8390.class_8393 method_50601() {
			return class_8390.class_8393.RESOURCE;
		}
	}
}
