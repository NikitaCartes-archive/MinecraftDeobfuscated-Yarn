package net.minecraft.enchantment.effect.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record SummonEntityEnchantmentEffect(RegistryEntryList<EntityType<?>> entityTypes, boolean joinTeam) implements EnchantmentEntityEffect {
	public static final MapCodec<SummonEntityEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(
					RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).fieldOf("entity").forGetter(SummonEntityEnchantmentEffect::entityTypes),
					Codec.BOOL.optionalFieldOf("join_team", Boolean.valueOf(false)).forGetter(SummonEntityEnchantmentEffect::joinTeam)
				)
				.apply(instance, SummonEntityEnchantmentEffect::new)
	);

	@Override
	public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
		BlockPos blockPos = BlockPos.ofFloored(pos);
		if (World.isValid(blockPos)) {
			Optional<RegistryEntry<EntityType<?>>> optional = this.entityTypes().getRandom(world.getRandom());
			if (!optional.isEmpty()) {
				Entity entity = ((EntityType)((RegistryEntry)optional.get()).value()).spawn(world, blockPos, SpawnReason.TRIGGERED);
				if (entity != null) {
					if (entity instanceof LightningEntity lightningEntity && context.owner() instanceof ServerPlayerEntity serverPlayerEntity) {
						lightningEntity.setChanneler(serverPlayerEntity);
					}

					if (this.joinTeam && user.getScoreboardTeam() != null) {
						world.getScoreboard().addScoreHolderToTeam(entity.getNameForScoreboard(), user.getScoreboardTeam());
					}

					entity.refreshPositionAndAngles(pos.x, pos.y, pos.z, entity.getYaw(), entity.getPitch());
				}
			}
		}
	}

	@Override
	public MapCodec<SummonEntityEnchantmentEffect> getCodec() {
		return CODEC;
	}
}
