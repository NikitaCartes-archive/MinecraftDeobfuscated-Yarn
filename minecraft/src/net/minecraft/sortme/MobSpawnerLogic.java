package net.minecraft.sortme;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3730;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.WeightedPicker;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.ChunkSaveHandlerImpl;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MobSpawnerLogic {
	private static final Logger LOGGER = LogManager.getLogger();
	private int spawnDelay = 20;
	private final List<MobSpawnerEntry> spawnPotentials = Lists.<MobSpawnerEntry>newArrayList();
	private MobSpawnerEntry spawnEntry = new MobSpawnerEntry();
	private double field_9161;
	private double field_9159;
	private int minSpawnDelay = 200;
	private int maxSpawnDelay = 800;
	private int spawnCount = 4;
	private Entity field_9153;
	private int maxNearbyEntities = 6;
	private int requiredPlayerRange = 16;
	private int spawnRange = 4;

	@Nullable
	private Identifier method_8281() {
		String string = this.spawnEntry.getEntityTag().getString("id");

		try {
			return ChatUtil.isEmpty(string) ? null : new Identifier(string);
		} catch (InvalidIdentifierException var4) {
			BlockPos blockPos = this.getPos();
			LOGGER.warn(
				"Invalid entity id '{}' at spawner {}:[{},{},{}]", string, this.getWorld().dimension.getType(), blockPos.getX(), blockPos.getY(), blockPos.getZ()
			);
			return null;
		}
	}

	public void method_8274(EntityType<?> entityType) {
		this.spawnEntry.getEntityTag().putString("id", Registry.ENTITY_TYPE.getId(entityType).toString());
	}

	private boolean method_8284() {
		BlockPos blockPos = this.getPos();
		return this.getWorld()
			.findClosestVisiblePlayer((double)blockPos.getX() + 0.5, (double)blockPos.getY() + 0.5, (double)blockPos.getZ() + 0.5, (double)this.requiredPlayerRange);
	}

	public void update() {
		if (!this.method_8284()) {
			this.field_9159 = this.field_9161;
		} else {
			BlockPos blockPos = this.getPos();
			if (this.getWorld().isRemote) {
				double d = (double)((float)blockPos.getX() + this.getWorld().random.nextFloat());
				double e = (double)((float)blockPos.getY() + this.getWorld().random.nextFloat());
				double f = (double)((float)blockPos.getZ() + this.getWorld().random.nextFloat());
				this.getWorld().method_8406(ParticleTypes.field_11251, d, e, f, 0.0, 0.0, 0.0);
				this.getWorld().method_8406(ParticleTypes.field_11240, d, e, f, 0.0, 0.0, 0.0);
				if (this.spawnDelay > 0) {
					this.spawnDelay--;
				}

				this.field_9159 = this.field_9161;
				this.field_9161 = (this.field_9161 + (double)(1000.0F / ((float)this.spawnDelay + 200.0F))) % 360.0;
			} else {
				if (this.spawnDelay == -1) {
					this.method_8282();
				}

				if (this.spawnDelay > 0) {
					this.spawnDelay--;
					return;
				}

				boolean bl = false;

				for (int i = 0; i < this.spawnCount; i++) {
					CompoundTag compoundTag = this.spawnEntry.getEntityTag();
					ListTag listTag = compoundTag.getList("Pos", 6);
					World world = this.getWorld();
					int j = listTag.size();
					double g = j >= 1
						? listTag.getDouble(0)
						: (double)blockPos.getX() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
					double h = j >= 2 ? listTag.getDouble(1) : (double)(blockPos.getY() + world.random.nextInt(3) - 1);
					double k = j >= 3
						? listTag.getDouble(2)
						: (double)blockPos.getZ() + (world.random.nextDouble() - world.random.nextDouble()) * (double)this.spawnRange + 0.5;
					Entity entity = ChunkSaveHandlerImpl.method_12399(compoundTag, world, g, h, k, false);
					if (entity == null) {
						this.method_8282();
						return;
					}

					int l = world.getVisibleEntities(
							entity.getClass(),
							new BoundingBox(
									(double)blockPos.getX(),
									(double)blockPos.getY(),
									(double)blockPos.getZ(),
									(double)(blockPos.getX() + 1),
									(double)(blockPos.getY() + 1),
									(double)(blockPos.getZ() + 1)
								)
								.expand((double)this.spawnRange)
						)
						.size();
					if (l >= this.maxNearbyEntities) {
						this.method_8282();
						return;
					}

					MobEntity mobEntity = entity instanceof MobEntity ? (MobEntity)entity : null;
					entity.setPositionAndAngles(entity.x, entity.y, entity.z, world.random.nextFloat() * 360.0F, 0.0F);
					if (mobEntity == null || mobEntity.method_5979(world, class_3730.field_16469) && mobEntity.method_5950()) {
						if (this.spawnEntry.getEntityTag().getSize() == 1 && this.spawnEntry.getEntityTag().containsKey("id", 8) && entity instanceof MobEntity) {
							((MobEntity)entity).method_5943(world, world.getLocalDifficulty(new BlockPos(entity)), class_3730.field_16469, null, null);
						}

						ChunkSaveHandlerImpl.method_12394(entity, world);
						world.fireWorldEvent(2004, blockPos, 0);
						if (mobEntity != null) {
							mobEntity.method_5990();
						}

						bl = true;
					}
				}

				if (bl) {
					this.method_8282();
				}
			}
		}
	}

	private void method_8282() {
		if (this.maxSpawnDelay <= this.minSpawnDelay) {
			this.spawnDelay = this.minSpawnDelay;
		} else {
			this.spawnDelay = this.minSpawnDelay + this.getWorld().random.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
		}

		if (!this.spawnPotentials.isEmpty()) {
			this.setSpawnEntry(WeightedPicker.getRandom(this.getWorld().random, this.spawnPotentials));
		}

		this.method_8273(1);
	}

	public void deserialize(CompoundTag compoundTag) {
		this.spawnDelay = compoundTag.getShort("Delay");
		this.spawnPotentials.clear();
		if (compoundTag.containsKey("SpawnPotentials", 9)) {
			ListTag listTag = compoundTag.getList("SpawnPotentials", 10);

			for (int i = 0; i < listTag.size(); i++) {
				this.spawnPotentials.add(new MobSpawnerEntry(listTag.getCompoundTag(i)));
			}
		}

		if (compoundTag.containsKey("SpawnData", 10)) {
			this.setSpawnEntry(new MobSpawnerEntry(1, compoundTag.getCompound("SpawnData")));
		} else if (!this.spawnPotentials.isEmpty()) {
			this.setSpawnEntry(WeightedPicker.getRandom(this.getWorld().random, this.spawnPotentials));
		}

		if (compoundTag.containsKey("MinSpawnDelay", 99)) {
			this.minSpawnDelay = compoundTag.getShort("MinSpawnDelay");
			this.maxSpawnDelay = compoundTag.getShort("MaxSpawnDelay");
			this.spawnCount = compoundTag.getShort("SpawnCount");
		}

		if (compoundTag.containsKey("MaxNearbyEntities", 99)) {
			this.maxNearbyEntities = compoundTag.getShort("MaxNearbyEntities");
			this.requiredPlayerRange = compoundTag.getShort("RequiredPlayerRange");
		}

		if (compoundTag.containsKey("SpawnRange", 99)) {
			this.spawnRange = compoundTag.getShort("SpawnRange");
		}

		if (this.getWorld() != null) {
			this.field_9153 = null;
		}
	}

	public CompoundTag serialize(CompoundTag compoundTag) {
		Identifier identifier = this.method_8281();
		if (identifier == null) {
			return compoundTag;
		} else {
			compoundTag.putShort("Delay", (short)this.spawnDelay);
			compoundTag.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
			compoundTag.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
			compoundTag.putShort("SpawnCount", (short)this.spawnCount);
			compoundTag.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
			compoundTag.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
			compoundTag.putShort("SpawnRange", (short)this.spawnRange);
			compoundTag.put("SpawnData", this.spawnEntry.getEntityTag().copy());
			ListTag listTag = new ListTag();
			if (this.spawnPotentials.isEmpty()) {
				listTag.add((Tag)this.spawnEntry.serialize());
			} else {
				for (MobSpawnerEntry mobSpawnerEntry : this.spawnPotentials) {
					listTag.add((Tag)mobSpawnerEntry.serialize());
				}
			}

			compoundTag.put("SpawnPotentials", listTag);
			return compoundTag;
		}
	}

	@Environment(EnvType.CLIENT)
	public Entity method_8283() {
		if (this.field_9153 == null) {
			this.field_9153 = ChunkSaveHandlerImpl.method_12378(this.spawnEntry.getEntityTag(), this.getWorld(), false);
			if (this.spawnEntry.getEntityTag().getSize() == 1 && this.spawnEntry.getEntityTag().containsKey("id", 8) && this.field_9153 instanceof MobEntity) {
				((MobEntity)this.field_9153)
					.method_5943(this.getWorld(), this.getWorld().getLocalDifficulty(new BlockPos(this.field_9153)), class_3730.field_16469, null, null);
			}
		}

		return this.field_9153;
	}

	public boolean method_8275(int i) {
		if (i == 1 && this.getWorld().isRemote) {
			this.spawnDelay = this.minSpawnDelay;
			return true;
		} else {
			return false;
		}
	}

	public void setSpawnEntry(MobSpawnerEntry mobSpawnerEntry) {
		this.spawnEntry = mobSpawnerEntry;
	}

	public abstract void method_8273(int i);

	public abstract World getWorld();

	public abstract BlockPos getPos();

	@Environment(EnvType.CLIENT)
	public double method_8278() {
		return this.field_9161;
	}

	@Environment(EnvType.CLIENT)
	public double method_8279() {
		return this.field_9159;
	}
}
