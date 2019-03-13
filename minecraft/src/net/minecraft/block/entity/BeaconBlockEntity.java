package net.minecraft.block.entity;

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
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.block.StainedGlassPaneBlock;
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
import net.minecraft.util.DyeColor;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;

public class BeaconBlockEntity extends BlockEntity implements NameableContainerProvider, Tickable {
	public static final StatusEffect[][] EFFECTS_BY_LEVEL = new StatusEffect[][]{
		{StatusEffects.field_5904, StatusEffects.field_5917},
		{StatusEffects.field_5907, StatusEffects.field_5913},
		{StatusEffects.field_5910},
		{StatusEffects.field_5924}
	};
	private static final Set<StatusEffect> EFFECTS = (Set<StatusEffect>)Arrays.stream(EFFECTS_BY_LEVEL).flatMap(Arrays::stream).collect(Collectors.toSet());
	private final List<BeaconBlockEntity.BeamSegment> beamSegments = Lists.<BeaconBlockEntity.BeamSegment>newArrayList();
	@Environment(EnvType.CLIENT)
	private long lastBeamTextureOffsetUpdateTime;
	@Environment(EnvType.CLIENT)
	private float beamTextureOffset;
	private boolean active;
	private boolean lastActive;
	private int levels = -1;
	@Nullable
	private StatusEffect primary;
	@Nullable
	private StatusEffect secondary;
	private TextComponent field_11793;
	private ContainerLock lock = ContainerLock.NONE;
	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int i) {
			switch (i) {
				case 0:
					return BeaconBlockEntity.this.levels;
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
					BeaconBlockEntity.this.levels = j;
					break;
				case 1:
					BeaconBlockEntity.this.primary = BeaconBlockEntity.getPotionEffectById(j);
					break;
				case 2:
					BeaconBlockEntity.this.secondary = BeaconBlockEntity.getPotionEffectById(j);
			}

			if (!BeaconBlockEntity.this.world.isClient && i == 1 && BeaconBlockEntity.this.active) {
				BeaconBlockEntity.this.method_10938(SoundEvents.field_14891);
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
		if (this.world.getTime() % 80L == 0L) {
			this.update();
			if (this.active) {
				this.method_10938(SoundEvents.field_15045);
			}
		}

		if (!this.world.isClient && this.active != this.lastActive) {
			this.lastActive = this.active;
			this.method_10938(this.active ? SoundEvents.field_14703 : SoundEvents.field_15176);
		}
	}

	public void update() {
		if (this.world != null) {
			this.updateBeamColors();
			this.applyPlayerEffects();
		}
	}

	public void method_10938(SoundEvent soundEvent) {
		this.world.method_8396(null, this.field_11867, soundEvent, SoundCategory.field_15245, 1.0F, 1.0F);
	}

	private void applyPlayerEffects() {
		if (this.active && this.levels > 0 && !this.world.isClient && this.primary != null) {
			double d = (double)(this.levels * 10 + 10);
			int i = 0;
			if (this.levels >= 4 && this.primary == this.secondary) {
				i = 1;
			}

			int j = (9 + this.levels * 2) * 20;
			int k = this.field_11867.getX();
			int l = this.field_11867.getY();
			int m = this.field_11867.getZ();
			BoundingBox boundingBox = new BoundingBox((double)k, (double)l, (double)m, (double)(k + 1), (double)(l + 1), (double)(m + 1))
				.expand(d)
				.stretch(0.0, (double)this.world.getHeight(), 0.0);
			List<PlayerEntity> list = this.world.method_18467(PlayerEntity.class, boundingBox);

			for (PlayerEntity playerEntity : list) {
				playerEntity.addPotionEffect(new StatusEffectInstance(this.primary, j, i, true, true));
			}

			if (this.levels >= 4 && this.primary != this.secondary && this.secondary != null) {
				for (PlayerEntity playerEntity : list) {
					playerEntity.addPotionEffect(new StatusEffectInstance(this.secondary, j, 0, true, true));
				}
			}
		}
	}

	private void updateBeamColors() {
		int i = this.field_11867.getX();
		int j = this.field_11867.getY();
		int k = this.field_11867.getZ();
		int l = this.levels;
		this.levels = 0;
		this.beamSegments.clear();
		this.active = true;
		BeaconBlockEntity.BeamSegment beamSegment = new BeaconBlockEntity.BeamSegment(DyeColor.field_7952.getColorComponents());
		this.beamSegments.add(beamSegment);
		boolean bl = true;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int m = j + 1; m < 256; m++) {
			BlockState blockState = this.world.method_8320(mutable.set(i, m, k));
			Block block = blockState.getBlock();
			float[] fs;
			if (block instanceof StainedGlassBlock) {
				fs = ((StainedGlassBlock)block).getColor().getColorComponents();
			} else {
				if (!(block instanceof StainedGlassPaneBlock)) {
					if (blockState.method_11581(this.world, mutable) >= 15 && block != Blocks.field_9987) {
						this.active = false;
						this.beamSegments.clear();
						break;
					}

					beamSegment.increaseHeight();
					continue;
				}

				fs = ((StainedGlassPaneBlock)block).getColor().getColorComponents();
			}

			if (!bl) {
				fs = new float[]{(beamSegment.getColor()[0] + fs[0]) / 2.0F, (beamSegment.getColor()[1] + fs[1]) / 2.0F, (beamSegment.getColor()[2] + fs[2]) / 2.0F};
			}

			if (Arrays.equals(fs, beamSegment.getColor())) {
				beamSegment.increaseHeight();
			} else {
				beamSegment = new BeaconBlockEntity.BeamSegment(fs);
				this.beamSegments.add(beamSegment);
			}

			bl = false;
		}

		if (this.active) {
			for (int m = 1; m <= 4; this.levels = m++) {
				int n = j - m;
				if (n < 0) {
					break;
				}

				boolean bl2 = true;

				for (int o = i - m; o <= i + m && bl2; o++) {
					for (int p = k - m; p <= k + m; p++) {
						Block block2 = this.world.method_8320(new BlockPos(o, n, p)).getBlock();
						if (block2 != Blocks.field_10234 && block2 != Blocks.field_10205 && block2 != Blocks.field_10201 && block2 != Blocks.field_10085) {
							bl2 = false;
							break;
						}
					}
				}

				if (!bl2) {
					break;
				}
			}

			if (this.levels == 0) {
				this.active = false;
			}
		}

		if (!this.world.isClient && l < this.levels) {
			for (ServerPlayerEntity serverPlayerEntity : this.world
				.method_18467(ServerPlayerEntity.class, new BoundingBox((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k).expand(10.0, 5.0, 10.0))) {
				Criterions.CONSTRUCT_BEACON.method_8812(serverPlayerEntity, this);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public List<BeaconBlockEntity.BeamSegment> getBeamSegments() {
		return this.beamSegments;
	}

	@Environment(EnvType.CLIENT)
	public float getBeamTextureOffset() {
		if (!this.active) {
			return 0.0F;
		} else {
			int i = (int)(this.world.getTime() - this.lastBeamTextureOffsetUpdateTime);
			this.lastBeamTextureOffsetUpdateTime = this.world.getTime();
			if (i > 1) {
				this.beamTextureOffset -= (float)i / 40.0F;
				if (this.beamTextureOffset < 0.0F) {
					this.beamTextureOffset = 0.0F;
				}
			}

			this.beamTextureOffset += 0.025F;
			if (this.beamTextureOffset > 1.0F) {
				this.beamTextureOffset = 1.0F;
			}

			return this.beamTextureOffset;
		}
	}

	public int getLevel() {
		return this.levels;
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket method_16886() {
		return new BlockEntityUpdateS2CPacket(this.field_11867, 3, this.method_16887());
	}

	@Override
	public CompoundTag method_16887() {
		return this.method_11007(new CompoundTag());
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
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.primary = getPotionEffectById(compoundTag.getInt("Primary"));
		this.secondary = getPotionEffectById(compoundTag.getInt("Secondary"));
		this.levels = compoundTag.getInt("Levels");
		if (compoundTag.containsKey("CustomName", 8)) {
			this.field_11793 = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}

		this.lock = ContainerLock.method_5473(compoundTag);
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		compoundTag.putInt("Primary", StatusEffect.getRawId(this.primary));
		compoundTag.putInt("Secondary", StatusEffect.getRawId(this.secondary));
		compoundTag.putInt("Levels", this.levels);
		if (this.field_11793 != null) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.field_11793));
		}

		this.lock.method_5474(compoundTag);
		return compoundTag;
	}

	public void method_10936(@Nullable TextComponent textComponent) {
		this.field_11793 = textComponent;
	}

	@Override
	public boolean onBlockAction(int i, int j) {
		if (i == 1) {
			this.update();
			return true;
		} else {
			return super.onBlockAction(i, j);
		}
	}

	@Nullable
	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return LockableContainerBlockEntity.method_17487(playerEntity, this.lock, this.method_5476())
			? new BeaconContainer(i, playerInventory, this.propertyDelegate, BlockContext.method_17392(this.world, this.method_11016()))
			: null;
	}

	@Override
	public TextComponent method_5476() {
		return (TextComponent)(this.field_11793 != null ? this.field_11793 : new TranslatableTextComponent("container.beacon"));
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

		public float[] getColor() {
			return this.color;
		}

		@Environment(EnvType.CLIENT)
		public int getHeight() {
			return this.height;
		}
	}
}
