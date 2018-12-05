package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.CriterionCriterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.block.StainedGlassPaneBlock;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.container.BeaconContainer;
import net.minecraft.container.Container;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraft.util.math.Direction;

public class BeaconBlockEntity extends LockableContainerBlockEntity implements SidedInventory, Tickable {
	public static final StatusEffect[][] EFFECTS = new StatusEffect[][]{
		{StatusEffects.field_5904, StatusEffects.field_5917},
		{StatusEffects.field_5907, StatusEffects.field_5913},
		{StatusEffects.field_5910},
		{StatusEffects.field_5924}
	};
	private static final Set<StatusEffect> field_11798 = (Set<StatusEffect>)Arrays.stream(EFFECTS).flatMap(Arrays::stream).collect(Collectors.toSet());
	private final List<BeaconBlockEntity.class_2581> field_11796 = Lists.<BeaconBlockEntity.class_2581>newArrayList();
	@Environment(EnvType.CLIENT)
	private long field_11797;
	@Environment(EnvType.CLIENT)
	private float field_11802;
	private boolean field_11794;
	private boolean field_11792;
	private int levels = -1;
	@Nullable
	private StatusEffect primary;
	@Nullable
	private StatusEffect secondary;
	private ItemStack stack = ItemStack.EMPTY;
	private TextComponent customName;

	public BeaconBlockEntity() {
		super(BlockEntityType.BEACON);
	}

	@Override
	public void tick() {
		if (this.world.getTime() % 80L == 0L) {
			this.method_10941();
			if (this.field_11794) {
				this.method_10938(SoundEvents.field_15045);
			}
		}

		if (!this.world.isRemote && this.field_11794 != this.field_11792) {
			this.field_11792 = this.field_11794;
			this.method_10938(this.field_11794 ? SoundEvents.field_14703 : SoundEvents.field_15176);
		}
	}

	public void method_10941() {
		if (this.world != null) {
			this.method_10935();
			this.method_10940();
		}
	}

	public void method_10938(SoundEvent soundEvent) {
		this.world.playSound(null, this.pos, soundEvent, SoundCategory.field_15245, 1.0F, 1.0F);
	}

	private void method_10940() {
		if (this.field_11794 && this.levels > 0 && !this.world.isRemote && this.primary != null) {
			double d = (double)(this.levels * 10 + 10);
			int i = 0;
			if (this.levels >= 4 && this.primary == this.secondary) {
				i = 1;
			}

			int j = (9 + this.levels * 2) * 20;
			int k = this.pos.getX();
			int l = this.pos.getY();
			int m = this.pos.getZ();
			BoundingBox boundingBox = new BoundingBox((double)k, (double)l, (double)m, (double)(k + 1), (double)(l + 1), (double)(m + 1))
				.expand(d)
				.stretch(0.0, (double)this.world.getHeight(), 0.0);
			List<PlayerEntity> list = this.world.getVisibleEntities(PlayerEntity.class, boundingBox);

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

	private void method_10935() {
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		int l = this.levels;
		this.levels = 0;
		this.field_11796.clear();
		this.field_11794 = true;
		BeaconBlockEntity.class_2581 lv = new BeaconBlockEntity.class_2581(DyeColor.WHITE.getColorComponents());
		this.field_11796.add(lv);
		boolean bl = true;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int m = j + 1; m < 256; m++) {
			BlockState blockState = this.world.getBlockState(mutable.set(i, m, k));
			Block block = blockState.getBlock();
			float[] fs;
			if (block instanceof StainedGlassBlock) {
				fs = ((StainedGlassBlock)block).getColor().getColorComponents();
			} else {
				if (!(block instanceof StainedGlassPaneBlock)) {
					if (blockState.method_11581(this.world, mutable) >= 15 && block != Blocks.field_9987) {
						this.field_11794 = false;
						this.field_11796.clear();
						break;
					}

					lv.method_10942();
					continue;
				}

				fs = ((StainedGlassPaneBlock)block).getColor().getColorComponents();
			}

			if (!bl) {
				fs = new float[]{(lv.method_10944()[0] + fs[0]) / 2.0F, (lv.method_10944()[1] + fs[1]) / 2.0F, (lv.method_10944()[2] + fs[2]) / 2.0F};
			}

			if (Arrays.equals(fs, lv.method_10944())) {
				lv.method_10942();
			} else {
				lv = new BeaconBlockEntity.class_2581(fs);
				this.field_11796.add(lv);
			}

			bl = false;
		}

		if (this.field_11794) {
			for (int m = 1; m <= 4; this.levels = m++) {
				int n = j - m;
				if (n < 0) {
					break;
				}

				boolean bl2 = true;

				for (int o = i - m; o <= i + m && bl2; o++) {
					for (int p = k - m; p <= k + m; p++) {
						Block block2 = this.world.getBlockState(new BlockPos(o, n, p)).getBlock();
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
				this.field_11794 = false;
			}
		}

		if (!this.world.isRemote && l < this.levels) {
			for (ServerPlayerEntity serverPlayerEntity : this.world
				.getVisibleEntities(
					ServerPlayerEntity.class, new BoundingBox((double)i, (double)j, (double)k, (double)i, (double)(j - 4), (double)k).expand(10.0, 5.0, 10.0)
				)) {
				CriterionCriterions.CONSTRUCT_BEACON.handle(serverPlayerEntity, this);
			}
		}
	}

	@Environment(EnvType.CLIENT)
	public List<BeaconBlockEntity.class_2581> method_10937() {
		return this.field_11796;
	}

	@Environment(EnvType.CLIENT)
	public float method_10933() {
		if (!this.field_11794) {
			return 0.0F;
		} else {
			int i = (int)(this.world.getTime() - this.field_11797);
			this.field_11797 = this.world.getTime();
			if (i > 1) {
				this.field_11802 -= (float)i / 40.0F;
				if (this.field_11802 < 0.0F) {
					this.field_11802 = 0.0F;
				}
			}

			this.field_11802 += 0.025F;
			if (this.field_11802 > 1.0F) {
				this.field_11802 = 1.0F;
			}

			return this.field_11802;
		}
	}

	public int getLevel() {
		return this.levels;
	}

	@Nullable
	@Override
	public BlockEntityUpdateClientPacket toUpdatePacket() {
		return new BlockEntityUpdateClientPacket(this.pos, 3, this.toInitialChunkDataTag());
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
		return field_11798.contains(statusEffect) ? statusEffect : null;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.primary = getPotionEffectById(compoundTag.getInt("Primary"));
		this.secondary = getPotionEffectById(compoundTag.getInt("Secondary"));
		this.levels = compoundTag.getInt("Levels");
		if (compoundTag.containsKey("CustomName", 8)) {
			this.customName = TextComponent.Serializer.fromJsonString(compoundTag.getString("CustomName"));
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.putInt("Primary", StatusEffect.getRawId(this.primary));
		compoundTag.putInt("Secondary", StatusEffect.getRawId(this.secondary));
		compoundTag.putInt("Levels", this.levels);
		if (this.customName != null) {
			compoundTag.putString("CustomName", TextComponent.Serializer.toJsonString(this.customName));
		}

		return compoundTag;
	}

	@Override
	public int getInvSize() {
		return 1;
	}

	@Override
	public boolean isInvEmpty() {
		return this.stack.isEmpty();
	}

	@Override
	public ItemStack getInvStack(int i) {
		return i == 0 ? this.stack : ItemStack.EMPTY;
	}

	@Override
	public ItemStack takeInvStack(int i, int j) {
		if (i != 0 || this.stack.isEmpty()) {
			return ItemStack.EMPTY;
		} else if (j >= this.stack.getAmount()) {
			ItemStack itemStack = this.stack;
			this.stack = ItemStack.EMPTY;
			return itemStack;
		} else {
			return this.stack.split(j);
		}
	}

	@Override
	public ItemStack removeInvStack(int i) {
		if (i == 0) {
			ItemStack itemStack = this.stack;
			this.stack = ItemStack.EMPTY;
			return itemStack;
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInvStack(int i, ItemStack itemStack) {
		if (i == 0) {
			this.stack = itemStack;
		}
	}

	@Override
	public TextComponent getName() {
		return (TextComponent)(this.customName != null ? this.customName : new TranslatableTextComponent("container.beacon"));
	}

	@Override
	public boolean hasCustomName() {
		return this.customName != null;
	}

	public void setCustomName(@Nullable TextComponent textComponent) {
		this.customName = textComponent;
	}

	@Override
	public int getInvMaxStackAmount() {
		return 1;
	}

	@Override
	public boolean canPlayerUseInv(PlayerEntity playerEntity) {
		return this.world.getBlockEntity(this.pos) != this
			? false
			: !(playerEntity.squaredDistanceTo((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5) > 64.0);
	}

	@Override
	public void onInvOpen(PlayerEntity playerEntity) {
	}

	@Override
	public void onInvClose(PlayerEntity playerEntity) {
	}

	@Override
	public boolean isValidInvStack(int i, ItemStack itemStack) {
		return itemStack.getItem() == Items.field_8687
			|| itemStack.getItem() == Items.field_8477
			|| itemStack.getItem() == Items.field_8695
			|| itemStack.getItem() == Items.field_8620;
	}

	@Override
	public String getContainerId() {
		return "minecraft:beacon";
	}

	@Override
	public Container createContainer(PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new BeaconContainer(playerInventory, this);
	}

	@Override
	public int getInvProperty(int i) {
		switch (i) {
			case 0:
				return this.levels;
			case 1:
				return StatusEffect.getRawId(this.primary);
			case 2:
				return StatusEffect.getRawId(this.secondary);
			default:
				return 0;
		}
	}

	@Override
	public void setInvProperty(int i, int j) {
		switch (i) {
			case 0:
				this.levels = j;
				break;
			case 1:
				this.primary = getPotionEffectById(j);
				break;
			case 2:
				this.secondary = getPotionEffectById(j);
		}

		if (!this.world.isRemote && i == 1 && this.field_11794) {
			this.method_10938(SoundEvents.field_14891);
		}
	}

	@Override
	public int getInvPropertyCount() {
		return 3;
	}

	@Override
	public void clearInv() {
		this.stack = ItemStack.EMPTY;
	}

	@Override
	public boolean method_11004(int i, int j) {
		if (i == 1) {
			this.method_10941();
			return true;
		} else {
			return super.method_11004(i, j);
		}
	}

	@Override
	public int[] method_5494(Direction direction) {
		return new int[0];
	}

	@Override
	public boolean method_5492(int i, ItemStack itemStack, @Nullable Direction direction) {
		return false;
	}

	@Override
	public boolean method_5493(int i, ItemStack itemStack, Direction direction) {
		return false;
	}

	public static class class_2581 {
		private final float[] field_11805;
		private int field_11804;

		public class_2581(float[] fs) {
			this.field_11805 = fs;
			this.field_11804 = 1;
		}

		protected void method_10942() {
			this.field_11804++;
		}

		public float[] method_10944() {
			return this.field_11805;
		}

		@Environment(EnvType.CLIENT)
		public int method_10943() {
			return this.field_11804;
		}
	}
}
