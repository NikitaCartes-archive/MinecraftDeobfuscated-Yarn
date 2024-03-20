package net.minecraft.block.vault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.block.spawner.EntityDetector;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.dynamic.Codecs;

public record VaultConfig(
	RegistryKey<LootTable> lootTable,
	double activationRange,
	double deactivationRange,
	ItemStack keyItem,
	Optional<RegistryKey<LootTable>> overrideLootTableToDisplay,
	EntityDetector playerDetector,
	EntityDetector.Selector entitySelector
) {
	static final String CONFIG_KEY = "config";
	public static VaultConfig DEFAULT = new VaultConfig();
	public static Codec<VaultConfig> codec = Codecs.validate(
		RecordCodecBuilder.create(
			instance -> instance.group(
						RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).optionalFieldOf("loot_table", DEFAULT.lootTable()).forGetter(VaultConfig::lootTable),
						Codec.DOUBLE.optionalFieldOf("activation_range", Double.valueOf(DEFAULT.activationRange())).forGetter(VaultConfig::activationRange),
						Codec.DOUBLE.optionalFieldOf("deactivation_range", Double.valueOf(DEFAULT.deactivationRange())).forGetter(VaultConfig::deactivationRange),
						ItemStack.createOptionalCodec("key_item").forGetter(VaultConfig::keyItem),
						RegistryKey.createCodec(RegistryKeys.LOOT_TABLE).optionalFieldOf("override_loot_table_to_display").forGetter(VaultConfig::overrideLootTableToDisplay)
					)
					.apply(instance, VaultConfig::new)
		),
		VaultConfig::validate
	);

	private VaultConfig() {
		this(
			LootTables.TRIAL_CHAMBERS_REWARD_CHEST,
			4.0,
			4.5,
			new ItemStack(Items.TRIAL_KEY),
			Optional.empty(),
			EntityDetector.NON_SPECTATOR_PLAYERS,
			EntityDetector.Selector.IN_WORLD
		);
	}

	public VaultConfig(
		RegistryKey<LootTable> lootTable,
		double activationRange,
		double deactivationRange,
		ItemStack keyItem,
		Optional<RegistryKey<LootTable>> overrideLootTableToDisplay
	) {
		this(lootTable, activationRange, deactivationRange, keyItem, overrideLootTableToDisplay, DEFAULT.playerDetector(), DEFAULT.entitySelector());
	}

	private DataResult<VaultConfig> validate() {
		return this.activationRange > this.deactivationRange
			? DataResult.error(() -> "Activation range must (" + this.activationRange + ") be less or equal to deactivation range (" + this.deactivationRange + ")")
			: DataResult.success(this);
	}
}
