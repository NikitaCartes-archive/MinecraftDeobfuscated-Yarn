package net.minecraft.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class CorruptedPotatoPeelsBlock extends Block {
	public static final MapCodec<CorruptedPotatoPeelsBlock> CODEC = RecordCodecBuilder.mapCodec(
		instance -> instance.group(createSettingsCodec()).apply(instance, CorruptedPotatoPeelsBlock::new)
	);

	public CorruptedPotatoPeelsBlock(AbstractBlock.Settings settings) {
		super(settings);
	}

	@Override
	protected MapCodec<CorruptedPotatoPeelsBlock> getCodec() {
		return CODEC;
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
		if (random.nextInt(500) == 0) {
			world.playSound(
				(double)pos.getX() + 0.5,
				(double)pos.getY() + 0.5,
				(double)pos.getZ() + 0.5,
				SoundEvents.ENTITY_WITCH_AMBIENT,
				SoundCategory.BLOCKS,
				0.5F,
				random.nextFloat() * 0.2F + 0.3F,
				false
			);
		}

		if (random.nextInt(2) == 0) {
			double d = (double)pos.getX() + random.nextDouble();
			double e = (double)pos.getY() + 0.5 + random.nextDouble();
			double f = (double)pos.getZ() + random.nextDouble();
			double g = ((double)random.nextFloat() - 0.5) * 0.5;
			double h = -((double)random.nextFloat() - 0.5) * 1.5;
			double i = ((double)random.nextFloat() - 0.5) * 0.5;
			world.addParticle(ParticleTypes.ENCHANT, d, e, f, g, h, i);
		}
	}

	@Override
	protected void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.randomTick(state, world, pos, random);
		List<Entity> list = world.getOtherEntities((Entity)null, Box.enclosing(pos, pos.offset(Direction.UP)));
		int i = 0;
		if (!list.isEmpty()) {
			for (Entity entity : list) {
				if (entity instanceof LivingEntity) {
					i++;
				}
			}
		}

		if (i <= 0) {
			if (random.nextInt(20) == 0) {
				int j = random.nextInt(1000);
				if (j < 500) {
					Entity entityx = EntityType.ENDERMAN.create(world, entityxx -> {
					}, pos, SpawnReason.NATURAL, true, false);
					world.spawnEntity(entityx);
				} else if (j < 900) {
					Entity entityx = EntityType.POISONOUS_POTATO_ZOMBIE.create(world, entityxx -> {
					}, pos, SpawnReason.NATURAL, true, false);
					world.spawnEntity(entityx);
				} else if (j < 980) {
					Entity entityx = EntityType.ITEM
						.create(world, entityxx -> entityxx.setStack(new ItemStack(Items.POISONOUS_POTATO)), pos, SpawnReason.NATURAL, true, false);
					world.spawnEntity(entityx);
				} else if (j == 999) {
					Entity entityx = EntityType.GHAST.create(world, entityxx -> {
					}, pos.offset(Direction.UP, 2), SpawnReason.NATURAL, true, false);
					world.spawnEntity(entityx);
				} else {
					Entity entityx = EntityType.BATATO.create(world, entityxx -> {
					}, pos, SpawnReason.NATURAL, true, false);
					world.spawnEntity(entityx);
				}
			}
		}
	}
}
