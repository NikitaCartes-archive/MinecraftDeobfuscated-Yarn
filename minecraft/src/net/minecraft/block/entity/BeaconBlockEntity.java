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
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.world.Heightmap;

public class BeaconBlockEntity extends BlockEntity implements NameableContainerProvider, Tickable {
	public static final StatusEffect[][] EFFECTS_BY_LEVEL = new StatusEffect[][]{
		{StatusEffects.field_5904, StatusEffects.field_5917},
		{StatusEffects.field_5907, StatusEffects.field_5913},
		{StatusEffects.field_5910},
		{StatusEffects.field_5924}
	};
	private static final Set<StatusEffect> EFFECTS = (Set<StatusEffect>)Arrays.stream(EFFECTS_BY_LEVEL).flatMap(Arrays::stream).collect(Collectors.toSet());
	private List<BeaconBlockEntity.BeamSegment> beamSegments = Lists.<BeaconBlockEntity.BeamSegment>newArrayList();
	private List<BeaconBlockEntity.BeamSegment> field_19178 = Lists.<BeaconBlockEntity.BeamSegment>newArrayList();
	private int level = 0;
	private int field_19179 = -1;
	@Nullable
	private StatusEffect primary;
	@Nullable
	private StatusEffect secondary;
	@Nullable
	private TextComponent customName;
	private ContainerLock lock = ContainerLock.NONE;
	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int i) {
			switch (i) {
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
		public void set(int i, int j) {
			switch (i) {
				case 0:
					BeaconBlockEntity.this.level = j;
					break;
				case 1:
					if (!BeaconBlockEntity.this.world.isClient && !BeaconBlockEntity.this.beamSegments.isEmpty()) {
						BeaconBlockEntity.this.playSound(SoundEvents.field_14891);
					}

					BeaconBlockEntity.this.primary = BeaconBlockEntity.getPotionEffectById(j);
					break;
				case 2:
					BeaconBlockEntity.this.secondary = BeaconBlockEntity.getPotionEffectById(j);
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
		int l = this.world.getTop(Heightmap.Type.WORLD_SURFACE, i, k);

		for (int m = 0; m < 10 && blockPos.getY() <= l; m++) {
			BlockState blockState = this.world.getBlockState(blockPos);
			Block block = blockState.getBlock();
			if (block instanceof ColoredBlock) {
				float[] fs = ((ColoredBlock)block).getColor().getColorComponents();
				if (blockPos.getY() <= j + 1) {
					beamSegment = new BeaconBlockEntity.BeamSegment(fs);
					this.field_19178.add(beamSegment);
				} else if (Arrays.equals(fs, beamSegment.color)) {
					beamSegment.increaseHeight();
				} else {
					beamSegment = new BeaconBlockEntity.BeamSegment(
						new float[]{(beamSegment.color[0] + fs[0]) / 2.0F, (beamSegment.color[1] + fs[1]) / 2.0F, (beamSegment.color[2] + fs[2]) / 2.0F}
					);
					this.field_19178.add(beamSegment);
				}
			} else {
				if (blockState.getLightSubtracted(this.world, blockPos) >= 15 && block != Blocks.field_9987) {
					this.field_19178.clear();
					this.field_19179 = l;
					break;
				}

				beamSegment.increaseHeight();
			}

			blockPos = blockPos.up();
			this.field_19179++;
		}

		if (this.field_19179 >= l) {
			this.field_19179 = -1;
			int m = this.level;
			boolean bl = this.level > 0 && !this.beamSegments.isEmpty();
			this.beamSegments = this.field_19178;
			if (!this.beamSegments.isEmpty() && this.world.getTime() % 80L == 0L) {
				this.method_20293(i, j, k);
			}

			if (!this.world.isClient) {
				if (m < this.level) {
					for (ServerPlayerEntity serverPlayerEntity : this.world
						.getEntities(ServerPlayerEntity.class, new BoundingBox((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k).expand(10.0, 5.0, 10.0))) {
						Criterions.CONSTRUCT_BEACON.handle(serverPlayerEntity, this);
					}
				}

				boolean bl2 = this.level > 0 && !this.beamSegments.isEmpty();
				if (bl != bl2) {
					this.playSound(bl2 ? SoundEvents.field_14703 : SoundEvents.field_15176);
				}
			}
		}

		if (this.world.getTime() % 80L == 0L && this.level > 0 && !this.beamSegments.isEmpty()) {
			this.applyPlayerEffects();
			this.playSound(SoundEvents.field_15045);
		}
	}

	private void method_20293(int i, int j, int k) {
		this.level = 0;

		for (int l = 1; l <= 4; this.level = l++) {
			int m = j - l;
			if (m < 0) {
				break;
			}

			boolean bl = true;

			for (int n = i - l; n <= i + l && bl; n++) {
				for (int o = k - l; o <= k + l; o++) {
					Block block = this.world.getBlockState(new BlockPos(n, m, o)).getBlock();
					if (block != Blocks.field_10234 && block != Blocks.field_10205 && block != Blocks.field_10201 && block != Blocks.field_10085) {
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

	private void applyPlayerEffects() {
		if (!this.world.isClient && this.primary != null) {
			double d = (double)(this.level * 10 + 10);
			int i = 0;
			if (this.level >= 4 && this.primary == this.secondary) {
				i = 1;
			}

			int j = (9 + this.level * 2) * 20;
			BoundingBox boundingBox = new BoundingBox(this.pos).expand(d).stretch(0.0, (double)this.world.getHeight(), 0.0);
			List<PlayerEntity> list = this.world.getEntities(PlayerEntity.class, boundingBox);

			for (PlayerEntity playerEntity : list) {
				playerEntity.addPotionEffect(new StatusEffectInstance(this.primary, j, i, true, true));
			}

			if (this.level >= 4 && this.primary != this.secondary && this.secondary != null) {
				for (PlayerEntity playerEntity : list) {
					playerEntity.addPotionEffect(new StatusEffectInstance(this.secondary, j, 0, true, true));
				}
			}
		}
	}

	public void playSound(SoundEvent soundEvent) {
		this.world.playSound(null, this.pos, soundEvent, SoundCategory.field_15245, 1.0F, 1.0F);
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
	private static StatusEffect getPotionEffectById(int i) {
		StatusEffect statusEffect = StatusEffect.byRawId(i);
		return EFFECTS.contains(statusEffect) ? statusEffect : null;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.primary = getPotionEffectById(compoundTag.getInt("Primary"));
		this.secondary = getPotionEffectById(compoundTag.getInt("Secondary"));
		if (compoundTag.containsKey("CustomName", 8)) {
			this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}

		this.lock = ContainerLock.deserialize(compoundTag);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.putInt("Primary", StatusEffect.getRawId(this.primary));
		compoundTag.putInt("Secondary", StatusEffect.getRawId(this.secondary));
		if (this.customName != null) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.customName));
		}

		this.lock.serialize(compoundTag);
		return compoundTag;
	}

	public void setCustomName(@Nullable TextComponent textComponent) {
		this.customName = textComponent;
	}

	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return LockableContainerBlockEntity.checkUnlocked(playerEntity, this.lock, this.getDisplayName())
			? new BeaconContainer(i, playerInventory, this.propertyDelegate, BlockContext.create(this.world, this.getPos()))
			: null;
	}

	@Override
	public TextComponent getDisplayName() {
		return (TextComponent)(this.customName != null ? this.customName : new TranslatableTextComponent("container.beacon"));
	}

	public static class BeamSegment {
		private final float[] color;
		private int height;

		public BeamSegment(float[] fs) {
			this.color = fs;
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
