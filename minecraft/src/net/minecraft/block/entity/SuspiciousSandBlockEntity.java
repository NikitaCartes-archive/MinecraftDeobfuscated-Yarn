package net.minecraft.block.entity;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;

public class SuspiciousSandBlockEntity extends BlockEntity {
	private static final Logger field_42801 = LogUtils.getLogger();
	private static final String LOOT_TABLE_NBT_KEY = "loot_table";
	private static final String LOOT_TABLE_SEED_NBT_KEY = "loot_table_seed";
	private static final String HIT_DIRECTION_NBT_KEY = "hit_direction";
	private static final String ITEM_NBT_KEY = "item";
	private static final int field_42806 = 10;
	private static final int field_42807 = 40;
	private static final int field_42808 = 10;
	private int brushesCount;
	private long nextDustTime;
	private long nextBrushTime;
	private ItemStack item = ItemStack.EMPTY;
	@Nullable
	private Direction hitDirection;
	@Nullable
	private Identifier lootTable;
	private long lootTableSeed;

	public SuspiciousSandBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.SUSPICIOUS_SAND, pos, state);
	}

	public boolean brush(long worldTime, PlayerEntity player, Direction hitDirection) {
		if (this.hitDirection == null) {
			this.hitDirection = hitDirection;
		}

		this.nextDustTime = worldTime + 40L;
		if (worldTime >= this.nextBrushTime && this.world instanceof ServerWorld) {
			this.nextBrushTime = worldTime + 10L;
			this.generateItem(player);
			int i = this.getDustedLevel();
			if (++this.brushesCount >= 10) {
				this.finishBrushing(player);
				return true;
			} else {
				this.world.scheduleBlockTick(this.getPos(), Blocks.SUSPICIOUS_SAND, 40);
				int j = this.getDustedLevel();
				if (i != j) {
					BlockState blockState = this.getCachedState();
					BlockState blockState2 = blockState.with(Properties.DUSTED, Integer.valueOf(j));
					this.world.setBlockState(this.getPos(), blockState2, Block.NOTIFY_ALL);
				}

				return false;
			}
		} else {
			return false;
		}
	}

	public void generateItem(PlayerEntity player) {
		if (this.lootTable != null && this.world != null && !this.world.isClient() && this.world.getServer() != null) {
			LootTable lootTable = this.world.getServer().getLootManager().getTable(this.lootTable);
			if (player instanceof ServerPlayerEntity serverPlayerEntity) {
				Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger(serverPlayerEntity, this.lootTable);
			}

			LootContext.Builder builder = new LootContext.Builder((ServerWorld)this.world)
				.parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(this.pos))
				.random(this.lootTableSeed)
				.luck(player.getLuck())
				.parameter(LootContextParameters.THIS_ENTITY, player);
			ObjectArrayList<ItemStack> objectArrayList = lootTable.generateLoot(builder.build(LootContextTypes.CHEST));

			this.item = switch (objectArrayList.size()) {
				case 0 -> ItemStack.EMPTY;
				case 1 -> (ItemStack)objectArrayList.get(0);
				default -> {
					field_42801.warn("Expected max 1 loot from loot table " + this.lootTable + " got " + objectArrayList.size());
					yield objectArrayList.get(0);
				}
			};
			this.lootTable = null;
			this.markDirty();
		}
	}

	private void finishBrushing(PlayerEntity player) {
		if (this.world != null && this.world.getServer() != null) {
			this.spawnItem(player);
			this.world.syncWorldEvent(3008, this.getPos(), Block.getRawIdFromState(this.getCachedState()));
			this.world.setBlockState(this.pos, Blocks.SAND.getDefaultState(), Block.NOTIFY_ALL);
		}
	}

	private void spawnItem(PlayerEntity player) {
		if (this.world != null && this.world.getServer() != null) {
			this.generateItem(player);
			if (!this.item.isEmpty()) {
				double d = (double)EntityType.ITEM.getWidth();
				double e = 1.0 - d;
				double f = d / 2.0;
				Direction direction = (Direction)Objects.requireNonNullElse(this.hitDirection, Direction.UP);
				BlockPos blockPos = this.pos.offset(direction, 1);
				double g = Math.floor((double)blockPos.getX()) + 0.5 * e + f;
				double h = Math.floor((double)blockPos.getY() + 0.5) + (double)(EntityType.ITEM.getHeight() / 2.0F);
				double i = Math.floor((double)blockPos.getZ()) + 0.5 * e + f;
				ItemEntity itemEntity = new ItemEntity(this.world, g, h, i, this.item.split(this.world.random.nextInt(21) + 10));
				itemEntity.setVelocity(Vec3d.ZERO);
				this.world.spawnEntity(itemEntity);
				this.item = ItemStack.EMPTY;
			}
		}
	}

	public void scheduledTick() {
		if (this.world != null) {
			if (this.brushesCount != 0 && this.world.getTime() >= this.nextDustTime) {
				int i = this.getDustedLevel();
				this.brushesCount = Math.max(0, this.brushesCount - 2);
				int j = this.getDustedLevel();
				if (i != j) {
					this.world.setBlockState(this.getPos(), this.getCachedState().with(Properties.DUSTED, Integer.valueOf(j)), Block.NOTIFY_ALL);
				}

				int k = 4;
				this.nextDustTime = this.world.getTime() + 4L;
			}

			if (this.brushesCount == 0) {
				this.hitDirection = null;
				this.nextDustTime = 0L;
				this.nextBrushTime = 0L;
			} else {
				this.world.scheduleBlockTick(this.getPos(), Blocks.SUSPICIOUS_SAND, (int)(this.nextDustTime - this.world.getTime()));
			}
		}
	}

	private boolean readLootTableFromNbt(NbtCompound nbt) {
		if (nbt.contains("loot_table", NbtElement.STRING_TYPE)) {
			this.lootTable = new Identifier(nbt.getString("loot_table"));
			this.lootTableSeed = nbt.getLong("loot_table_seed");
			return true;
		} else {
			return false;
		}
	}

	private boolean writeLootTableToNbt(NbtCompound nbt) {
		if (this.lootTable == null) {
			return false;
		} else {
			nbt.putString("loot_table", this.lootTable.toString());
			if (this.lootTableSeed != 0L) {
				nbt.putLong("loot_table_seed", this.lootTableSeed);
			}

			return true;
		}
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		NbtCompound nbtCompound = super.toInitialChunkDataNbt();
		if (this.hitDirection != null) {
			nbtCompound.putInt("hit_direction", this.hitDirection.ordinal());
		}

		nbtCompound.put("item", this.item.writeNbt(new NbtCompound()));
		return nbtCompound;
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		if (!this.readLootTableFromNbt(nbt) && nbt.contains("item")) {
			this.item = ItemStack.fromNbt(nbt.getCompound("item"));
		}

		if (nbt.contains("hit_direction")) {
			this.hitDirection = Direction.values()[nbt.getInt("hit_direction")];
		}
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		if (!this.writeLootTableToNbt(nbt)) {
			nbt.put("item", this.item.writeNbt(new NbtCompound()));
		}
	}

	public void setLootTable(Identifier lootTable, long seed) {
		this.lootTable = lootTable;
		this.lootTableSeed = seed;
	}

	private int getDustedLevel() {
		if (this.brushesCount == 0) {
			return 0;
		} else if (this.brushesCount < 3) {
			return 1;
		} else {
			return this.brushesCount < 6 ? 2 : 3;
		}
	}

	@Nullable
	public Direction getHitDirection() {
		return this.hitDirection;
	}

	public ItemStack getItem() {
		return this.item;
	}
}
