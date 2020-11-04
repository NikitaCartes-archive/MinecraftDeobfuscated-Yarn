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
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Stainable;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ContainerLock;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.BeaconScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class BeaconBlockEntity extends BlockEntity implements NamedScreenHandlerFactory {
	public static final StatusEffect[][] EFFECTS_BY_LEVEL = new StatusEffect[][]{
		{StatusEffects.SPEED, StatusEffects.HASTE}, {StatusEffects.RESISTANCE, StatusEffects.JUMP_BOOST}, {StatusEffects.STRENGTH}, {StatusEffects.REGENERATION}
	};
	private static final Set<StatusEffect> EFFECTS = (Set<StatusEffect>)Arrays.stream(EFFECTS_BY_LEVEL).flatMap(Arrays::stream).collect(Collectors.toSet());
	private List<BeaconBlockEntity.BeamSegment> beamSegments = Lists.<BeaconBlockEntity.BeamSegment>newArrayList();
	private List<BeaconBlockEntity.BeamSegment> field_19178 = Lists.<BeaconBlockEntity.BeamSegment>newArrayList();
	private int level;
	private int field_19179;
	@Nullable
	private StatusEffect primary;
	@Nullable
	private StatusEffect secondary;
	@Nullable
	private Text customName;
	private ContainerLock lock = ContainerLock.EMPTY;
	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int index) {
			switch (index) {
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
		public void set(int index, int value) {
			switch (index) {
				case 0:
					BeaconBlockEntity.this.level = value;
					break;
				case 1:
					if (!BeaconBlockEntity.this.world.isClient && !BeaconBlockEntity.this.beamSegments.isEmpty()) {
						BeaconBlockEntity.playSound(BeaconBlockEntity.this.world, BeaconBlockEntity.this.pos, SoundEvents.BLOCK_BEACON_POWER_SELECT);
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

	public BeaconBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(BlockEntityType.BEACON, blockPos, blockState);
	}

	public static void tick(World world, BlockPos blockPos, BlockState blockState, BeaconBlockEntity beaconBlockEntity) {
		int i = blockPos.getX();
		int j = blockPos.getY();
		int k = blockPos.getZ();
		BlockPos blockPos2;
		if (beaconBlockEntity.field_19179 < j) {
			blockPos2 = blockPos;
			beaconBlockEntity.field_19178 = Lists.<BeaconBlockEntity.BeamSegment>newArrayList();
			beaconBlockEntity.field_19179 = blockPos.getY() - 1;
		} else {
			blockPos2 = new BlockPos(i, beaconBlockEntity.field_19179 + 1, k);
		}

		BeaconBlockEntity.BeamSegment beamSegment = beaconBlockEntity.field_19178.isEmpty()
			? null
			: (BeaconBlockEntity.BeamSegment)beaconBlockEntity.field_19178.get(beaconBlockEntity.field_19178.size() - 1);
		int l = world.getTopY(Heightmap.Type.WORLD_SURFACE, i, k);

		for (int m = 0; m < 10 && blockPos2.getY() <= l; m++) {
			BlockState blockState2 = world.getBlockState(blockPos2);
			Block block = blockState2.getBlock();
			if (block instanceof Stainable) {
				float[] fs = ((Stainable)block).getColor().getColorComponents();
				if (beaconBlockEntity.field_19178.size() <= 1) {
					beamSegment = new BeaconBlockEntity.BeamSegment(fs);
					beaconBlockEntity.field_19178.add(beamSegment);
				} else if (beamSegment != null) {
					if (Arrays.equals(fs, beamSegment.color)) {
						beamSegment.increaseHeight();
					} else {
						beamSegment = new BeaconBlockEntity.BeamSegment(
							new float[]{(beamSegment.color[0] + fs[0]) / 2.0F, (beamSegment.color[1] + fs[1]) / 2.0F, (beamSegment.color[2] + fs[2]) / 2.0F}
						);
						beaconBlockEntity.field_19178.add(beamSegment);
					}
				}
			} else {
				if (beamSegment == null || blockState2.getOpacity(world, blockPos2) >= 15 && !blockState2.isOf(Blocks.BEDROCK)) {
					beaconBlockEntity.field_19178.clear();
					beaconBlockEntity.field_19179 = l;
					break;
				}

				beamSegment.increaseHeight();
			}

			blockPos2 = blockPos2.up();
			beaconBlockEntity.field_19179++;
		}

		int m = beaconBlockEntity.level;
		if (world.getTime() % 80L == 0L) {
			if (!beaconBlockEntity.beamSegments.isEmpty()) {
				beaconBlockEntity.level = updateLevel(world, i, j, k);
			}

			if (beaconBlockEntity.level > 0 && !beaconBlockEntity.beamSegments.isEmpty()) {
				applyPlayerEffects(world, blockPos, beaconBlockEntity.level, beaconBlockEntity.primary, beaconBlockEntity.secondary);
				playSound(world, blockPos, SoundEvents.BLOCK_BEACON_AMBIENT);
			}
		}

		if (beaconBlockEntity.field_19179 >= l) {
			beaconBlockEntity.field_19179 = world.getBottomHeightLimit() - 1;
			boolean bl = m > 0;
			beaconBlockEntity.beamSegments = beaconBlockEntity.field_19178;
			if (!world.isClient) {
				boolean bl2 = beaconBlockEntity.level > 0;
				if (!bl && bl2) {
					playSound(world, blockPos, SoundEvents.BLOCK_BEACON_ACTIVATE);

					for (ServerPlayerEntity serverPlayerEntity : world.getNonSpectatingEntities(
						ServerPlayerEntity.class, new Box((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k).expand(10.0, 5.0, 10.0)
					)) {
						Criteria.CONSTRUCT_BEACON.trigger(serverPlayerEntity, beaconBlockEntity.level);
					}
				} else if (bl && !bl2) {
					playSound(world, blockPos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
				}
			}
		}
	}

	private static int updateLevel(World world, int x, int y, int z) {
		int i = 0;

		for (int j = 1; j <= 4; i = j++) {
			int k = y - j;
			if (k < world.getBottomHeightLimit()) {
				break;
			}

			boolean bl = true;

			for (int l = x - j; l <= x + j && bl; l++) {
				for (int m = z - j; m <= z + j; m++) {
					if (!world.getBlockState(new BlockPos(l, k, m)).isIn(BlockTags.BEACON_BASE_BLOCKS)) {
						bl = false;
						break;
					}
				}
			}

			if (!bl) {
				break;
			}
		}

		return i;
	}

	@Override
	public void markRemoved() {
		playSound(this.world, this.pos, SoundEvents.BLOCK_BEACON_DEACTIVATE);
		super.markRemoved();
	}

	private static void applyPlayerEffects(World world, BlockPos blockPos, int i, @Nullable StatusEffect statusEffect, @Nullable StatusEffect statusEffect2) {
		if (!world.isClient && statusEffect != null) {
			double d = (double)(i * 10 + 10);
			int j = 0;
			if (i >= 4 && statusEffect == statusEffect2) {
				j = 1;
			}

			int k = (9 + i * 2) * 20;
			Box box = new Box(blockPos).expand(d).stretch(0.0, (double)world.getTopHeightLimit(), 0.0);
			List<PlayerEntity> list = world.getNonSpectatingEntities(PlayerEntity.class, box);

			for (PlayerEntity playerEntity : list) {
				playerEntity.addStatusEffect(new StatusEffectInstance(statusEffect, k, j, true, true));
			}

			if (i >= 4 && statusEffect != statusEffect2 && statusEffect2 != null) {
				for (PlayerEntity playerEntity : list) {
					playerEntity.addStatusEffect(new StatusEffectInstance(statusEffect2, k, 0, true, true));
				}
			}
		}
	}

	public static void playSound(World world, BlockPos blockPos, SoundEvent soundEvent) {
		world.playSound(null, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
	}

	@Environment(EnvType.CLIENT)
	public List<BeaconBlockEntity.BeamSegment> getBeamSegments() {
		return (List<BeaconBlockEntity.BeamSegment>)(this.level == 0 ? ImmutableList.of() : this.beamSegments);
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
		return 256.0;
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

		this.lock = ContainerLock.fromTag(compoundTag);
	}

	@Override
	public CompoundTag toTag(CompoundTag tag) {
		super.toTag(tag);
		tag.putInt("Primary", StatusEffect.getRawId(this.primary));
		tag.putInt("Secondary", StatusEffect.getRawId(this.secondary));
		tag.putInt("Levels", this.level);
		if (this.customName != null) {
			tag.putString("CustomName", Text.Serializer.toJson(this.customName));
		}

		this.lock.toTag(tag);
		return tag;
	}

	public void setCustomName(@Nullable Text text) {
		this.customName = text;
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return LockableContainerBlockEntity.checkUnlocked(playerEntity, this.lock, this.getDisplayName())
			? new BeaconScreenHandler(i, playerInventory, this.propertyDelegate, ScreenHandlerContext.create(this.world, this.getPos()))
			: null;
	}

	@Override
	public Text getDisplayName() {
		return (Text)(this.customName != null ? this.customName : new TranslatableText("container.beacon"));
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		this.field_19179 = world.getBottomHeightLimit() - 1;
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
