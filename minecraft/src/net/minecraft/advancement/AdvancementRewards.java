package net.minecraft.advancement;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.function.LazyContainer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public record AdvancementRewards(int experience, List<RegistryKey<LootTable>> loot, List<Identifier> recipes, Optional<LazyContainer> function) {
	public static final Codec<AdvancementRewards> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.optionalFieldOf("experience", Integer.valueOf(0)).forGetter(AdvancementRewards::experience),
					RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).listOf().optionalFieldOf("loot", List.of()).forGetter(AdvancementRewards::loot),
					Identifier.CODEC.listOf().optionalFieldOf("recipes", List.of()).forGetter(AdvancementRewards::recipes),
					LazyContainer.CODEC.optionalFieldOf("function").forGetter(AdvancementRewards::function)
				)
				.apply(instance, AdvancementRewards::new)
	);
	public static final AdvancementRewards NONE = new AdvancementRewards(0, List.of(), List.of(), Optional.empty());

	public void apply(ServerPlayerEntity player) {
		player.addExperience(this.experience);
		LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(player.getServerWorld())
			.add(LootContextParameters.THIS_ENTITY, player)
			.add(LootContextParameters.ORIGIN, player.getPos())
			.build(LootContextTypes.ADVANCEMENT_REWARD);
		boolean bl = false;

		for (RegistryKey<LootTable> registryKey : this.loot) {
			for (ItemStack itemStack : player.server.getReloadableRegistries().getLootTable(registryKey).generateLoot(lootContextParameterSet)) {
				if (player.giveItemStack(itemStack)) {
					player.getWorld()
						.playSound(
							null,
							player.getX(),
							player.getY(),
							player.getZ(),
							SoundEvents.ENTITY_ITEM_PICKUP,
							SoundCategory.PLAYERS,
							0.2F,
							((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F
						);
					bl = true;
				} else {
					ItemEntity itemEntity = player.dropItem(itemStack, false);
					if (itemEntity != null) {
						itemEntity.resetPickupDelay();
						itemEntity.setOwner(player.getUuid());
					}
				}
			}
		}

		if (bl) {
			player.currentScreenHandler.sendContentUpdates();
		}

		if (!this.recipes.isEmpty()) {
			player.unlockRecipes(this.recipes);
		}

		MinecraftServer minecraftServer = player.server;
		this.function
			.flatMap(function -> function.get(minecraftServer.getCommandFunctionManager()))
			.ifPresent(function -> minecraftServer.getCommandFunctionManager().execute(function, player.getCommandSource().withSilent().withLevel(2)));
	}

	public static class Builder {
		private int experience;
		private final ImmutableList.Builder<RegistryKey<LootTable>> loot = ImmutableList.builder();
		private final ImmutableList.Builder<Identifier> recipes = ImmutableList.builder();
		private Optional<Identifier> function = Optional.empty();

		public static AdvancementRewards.Builder experience(int experience) {
			return new AdvancementRewards.Builder().setExperience(experience);
		}

		public AdvancementRewards.Builder setExperience(int experience) {
			this.experience += experience;
			return this;
		}

		public static AdvancementRewards.Builder loot(RegistryKey<LootTable> loot) {
			return new AdvancementRewards.Builder().addLoot(loot);
		}

		public AdvancementRewards.Builder addLoot(RegistryKey<LootTable> loot) {
			this.loot.add(loot);
			return this;
		}

		public static AdvancementRewards.Builder recipe(Identifier recipe) {
			return new AdvancementRewards.Builder().addRecipe(recipe);
		}

		public AdvancementRewards.Builder addRecipe(Identifier recipe) {
			this.recipes.add(recipe);
			return this;
		}

		public static AdvancementRewards.Builder function(Identifier function) {
			return new AdvancementRewards.Builder().setFunction(function);
		}

		public AdvancementRewards.Builder setFunction(Identifier function) {
			this.function = Optional.of(function);
			return this;
		}

		public AdvancementRewards build() {
			return new AdvancementRewards(this.experience, this.loot.build(), this.recipes.build(), this.function.map(LazyContainer::new));
		}
	}
}
