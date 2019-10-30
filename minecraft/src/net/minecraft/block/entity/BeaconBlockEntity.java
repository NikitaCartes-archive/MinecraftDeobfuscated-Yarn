package net.minecraft.block.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.block.ColoredBlock;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.container.BeaconContainer;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.ContainerLock;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap;

public class BeaconBlockEntity extends BlockEntity implements NameableContainerProvider, Tickable {
	public static final StatusEffect[][] EFFECTS_BY_LEVEL = new StatusEffect[][]{
		{StatusEffects.SPEED, StatusEffects.HASTE}, {StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST}, {StatusEffects.STRENGTH}, {StatusEffects.REGENERATION}
	};
	private static final Set<StatusEffect> EFFECTS = (Set<StatusEffect>)Arrays.stream(EFFECTS_BY_LEVEL).flatMap(Arrays::stream).collect(Collectors.toSet());
	private List<BeaconBlockEntity.BeamSegment> beamSegments = Lists.<BeaconBlockEntity.BeamSegment>newArrayList();
	private List<BeaconBlockEntity.BeamSegment> field_19178 = Lists.<BeaconBlockEntity.BeamSegment>newArrayList();
	private int level;
	private int field_19179 = -1;
	@Nullable
	private StatusEffect primary;
	@Nullable
	private StatusEffect secondary;
	@Nullable
	private Text customName;
	private ContainerLock lock = ContainerLock.NONE;
	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int key) {
			switch (key) {
				case 0:
					return BeaconBlockEntity.this.level;
				case 1:
					return StatusEffect.getRawId(BeaconBlockEntity.this.primary);
				case 2:
					return StatusEffect.getRawId(BeaconBlockEntity.this.secondary);
				default:
					return 0;
			}
		}

		@Override
		public void set(int key, int value) {
			switch (key) {
				case 0:
					BeaconBlockEntity.this.level = value;
					break;
				case 1:
					if (!BeaconBlockEntity.this.world.isClient && !BeaconBlockEntity.this.beamSegments.isEmpty()) {
						BeaconBlockEntity.this.playSound(SoundEvents.BLOCK_BEACON_POWER_SELECT);
					}

					BeaconBlockEntity.this.primary = BeaconBlockEntity.getPotionEffectById(value);
					break;
				case 2:
					BeaconBlockEntity.this.secondary = BeaconBlockEntity.getPotionEffectById(value);
			}
		}

		@Override
		public int size() {
			return 3;
		}
	};

	public BeaconBlockEntity() {
		super(BlockEntityType.BEACON);
	}

	@Override
	public void tick() {
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		BlockPos blockPos;
		if (this.field_19179 < j) {
			blockPos = this.pos;
			this.field_19178 = Lists.<BeaconBlockEntity.BeamSegment>newArrayList();
			this.field_19179 = blockPos.getY() - 1;
		} else {
			blockPos = new BlockPos(i, this.field_19179 + 1, k);
		}

		BeaconBlockEntity.BeamSegment beamSegment = this.field_19178.isEmpty()
			? null
			: (BeaconBlockEntity.BeamSegment)this.field_19178.get(this.field_19178.size() - 1);
		int l = this.world.getTopY(Heightmap.Type.WORLD_SURFACE, i, k);

		for (int m = 0; m < 10 && blockPos.getY() <= l; m++) {
			BlockState blockState = this.world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (block instanceof ColoredBlock) {
				float[] fs = ((ColoredBlock)block).getColor().getColorComponents();
				if (this.field_19178.size() <= 1) {
					beamSegment = new BeaconBlockEntity.BeamSegment(fs);
					this.field_19178.add(beamSegment);
				} else if (beamSegment != null) {
					if (Arrays.equals(fs, beamSegment.color)) {
						beamSegment.increaseHeight();
					} else {
						beamSegment = new BeaconBlockEntity.BeamSegment(
							new float[]{(beamSegment.color[0] + fs[0]) / 2.0F, (beamSegment.color[1] + fs[1]) / 2.0F, (beamSegment.color[2] + fs[2]) / 2.0F}
						);
						this.field_19178.add(beamSegment);
					}
				}
			} else {
				if (beamSegment == null || blockState.getOpacity(this.world, blockPos) >= 15 && block != Blocks.BEDROCK) {
					this.field_19178.clear();
					this.field_19179 = l;
					break;
				}

				beamSegment.increaseHeight();
			}

			blockPos = blockPos.up();
			this.field_19179++;
		}

		int m = this.level;
		if (this.world.getTime() % 80L == 0L) {
			if (!this.beamSegments.isEmpty()) {
				this.updateLevel(i, j, k);
			}

			if (this.level > 0 && !this.beamSegments.isEmpty()) {
				this.applyPlayerEffects();
				this.playSound(SoundEvents.BLOCK_BEACON_AMBIENT);
			}
		}

		if (this.field_19179 >= l) {
			this.field_19179 = -1;
			boolean bl = m > 0;
			this.beamSegments = this.field_19178;
			if (!this.world.isClient) {
				boolean bl2 = this.level > 0;
				if (!bl && bl2) {
					this.playSound(SoundEvents.BLOCK_BEACON_ACTIVATE);

					for (ServerPlayerEntity serverPlayerEntity : this.world
						.getNonSpectatingEntities(
							ServerPlayerEntity.class, new Box((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k).expand(10.0, 5.0, 10.0)
						)) {
						Criterions.CONSTRUCT_BEACON.trigger(serverPlayerEntity, this);
					}
				} else if (bl && !bl2) {
					this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
				}
			}
		}
	}

	private void updateLevel(int x, int y, int z) {
		this.level = 0;

		for (int i = 1; i <= 4; this.level = i++) {
			int j = y - i;
			if (j < 0) {
				break;
			}

			boolean bl = true;

			for (int k = x - i; k <= x + i && bl; k++) {
				for (int l = z - i; l <= z + i; l++) {
					Block block = this.world.getBlockState(new BlockPos(k, j, l)).getBlock();
					if (block != Blocks.EMERALD_BLOCK && block != Blocks.GOLD_BLOCK && block != Blocks.DIAMOND_BLOCK && block != Blocks.IRON_BLOCK) {
						bl = false;
						break;
					}
				}
			}

			if (!bl) {
				break;
			}
		}
	}

	@Override
	public void markRemoved() {
		this.playSound(SoundEvents.BLOCK_BEACON_DEACTIVATE);
		super.markRemoved();
	}

	private void applyPlayerEffects() {
		if (!this.world.isClient && this.primary != null) {
			double d = (double)(this.level * 10 + 10);
			int i = 0;
			if (this.level >= 4 && this.primary == this.secondary) {
				i = 1;
			}

			int j = (9 + this.level * 2) * 20;
			Box box = new Box(this.pos).expand(d).stretch(0.0, (double)this.world.getHeight(), 0.0);
			List<PlayerEntity> list = this.world.getNonSpectatingEntities(PlayerEntity.class, box);

			for (PlayerEntity playerEntity : list) {
				playerEntity.addStatusEffect(new StatusEffectInstance(this.primary, j, i, true, true));
			}

			if (this.level >= 4 && this.primary != this.secondary && this.secondary != null) {
				for (PlayerEntity playerEntity : list) {
					playerEntity.addStatusEffect(new StatusEffectInstance(this.secondary, j, 0, true, true));
				}
			}
		}
	}

	public void playSound(SoundEvent soundEvent) {
		this.world.playSound(null, this.pos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public List<BeaconBlockEntity.BeamSegment> getBeamSegments() {
		return (List<BeaconBlockEntity.BeamSegment>)(this.level == 0 ? ImmutableList.of() : this.beamSegments);
	}

	public int getLevel() {
		return this.level;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return new BlockEntityUpdateS2CPacket(this.pos, 3, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public double getSquaredRenderDistance() {
		return 65536.0;
	}

	@Nullable
	private static StatusEffect getPotionEffectById(int id) {
		StatusEffect statusEffect = StatusEffect.byRawId(id);
		return EFFECTS.contains(statusEffect) ? statusEffect : null;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.primary = getPotionEffectById(compoundTag.getInt("Primary"));
		this.secondary = getPotionEffectById(compoundTag.getInt("Secondary"));
		if (compoundTag.contains("CustomName", 8)) {
			this.customName = Text.Serializer.fromJson(compoundTag.getString("CustomName"));
		}

		this.lock = ContainerLock.deserialize(compoundTag);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.putInt("Primary", StatusEffect.getRawId(this.primary));
		compoundTag.putInt("Secondary", StatusEffect.getRawId(this.secondary));
		compoundTag.putInt("Levels", this.level);
		if (this.customName != null) {
			compoundTag.putString("CustomName", Text.Serializer.toJson(this.customName));
		}

		this.lock.serialize(compoundTag);
		return compoundTag;
	}

	public void setCustomName(@Nullable Text text) {
		this.customName = text;
	}

	@Nullable
	@Override
	public Container createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return LockableContainerBlockEntity.checkUnlocked(playerEntity, this.lock, this.getDisplayName())
			? new BeaconContainer(syncId, playerInventory, this.propertyDelegate, BlockContext.create(this.world, this.getPos()))
			: null;
	}

	@Override
	public Text getDisplayName() {
		return (Text)(this.customName != null ? this.customName : new TranslatableText("container.beacon"));
	}

	public static class BeamSegment {
		private final float[] color;
		private int height;

		public BeamSegment(float[] color) {
			this.color = color;
			this.height = 1;
		}

		protected void increaseHeight() {
			this.height++;
		}

		@Environment(EnvType.CLIENT)
		public float[] getColor() {
			return this.color;
		}

		@Environment(EnvType.CLIENT)
		public int getHeight() {
			return this.height;
		}
	}
}
