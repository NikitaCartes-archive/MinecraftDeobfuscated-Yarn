package net.minecraft.block.entity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvironmentInterface;
import net.fabricmc.api.EnvironmentInterfaces;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.block.ChestAnimationProgress;
import net.minecraft.container.Container;
import net.minecraft.container.GenericContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

@EnvironmentInterfaces({@EnvironmentInterface(
		value = EnvType.CLIENT,
		itf = ChestAnimationProgress.class
	)})
public class ChestBlockEntity extends LootableContainerBlockEntity implements ChestAnimationProgress, Tickable {
	private DefaultedList<ItemStack> inventory = DefaultedList.create(27, ItemStack.EMPTY);
	protected float animationAngle;
	protected float lastAnimationAngle;
	protected int viewerCount;
	private int ticksOpen;

	protected ChestBlockEntity(BlockEntityType<?> blockEntityType) {
		super(blockEntityType);
	}

	public ChestBlockEntity() {
		this(BlockEntityType.field_11914);
	}

	@Override
	public int getInvSize() {
		return 27;
	}

	@Override
	public boolean isInvEmpty() {
		for (ItemStack itemStack : this.inventory) {
			if (!itemStack.isEmpty()) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected Text getContainerName() {
		return new TranslatableText("container.chest");
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.inventory = DefaultedList.create(this.getInvSize(), ItemStack.EMPTY);
		if (!this.deserializeLootTable(compoundTag)) {
			Inventories.fromTag(compoundTag, this.inventory);
		}
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (!this.serializeLootTable(compoundTag)) {
			Inventories.toTag(compoundTag, this.inventory);
		}

		return compoundTag;
	}

	@Override
	public void tick() {
		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		this.ticksOpen++;
		this.viewerCount = tickViewerCount(this.world, this, this.ticksOpen, i, j, k, this.viewerCount);
		this.lastAnimationAngle = this.animationAngle;
		float f = 0.1F;
		if (this.viewerCount > 0 && this.animationAngle == 0.0F) {
			this.playSound(SoundEvents.field_14982);
		}

		if (this.viewerCount == 0 && this.animationAngle > 0.0F || this.viewerCount > 0 && this.animationAngle < 1.0F) {
			float g = this.animationAngle;
			if (this.viewerCount > 0) {
				this.animationAngle += 0.1F;
			} else {
				this.animationAngle -= 0.1F;
			}

			if (this.animationAngle > 1.0F) {
				this.animationAngle = 1.0F;
			}

			float h = 0.5F;
			if (this.animationAngle < 0.5F && g >= 0.5F) {
				this.playSound(SoundEvents.field_14823);
			}

			if (this.animationAngle < 0.0F) {
				this.animationAngle = 0.0F;
			}
		}
	}

	public static int tickViewerCount(World world, LockableContainerBlockEntity lockableContainerBlockEntity, int i, int j, int k, int l, int m) {
		if (!world.isClient && m != 0 && (i + j + k + l) % 200 == 0) {
			m = countViewers(world, lockableContainerBlockEntity, j, k, l);
		}

		return m;
	}

	public static int countViewers(World world, LockableContainerBlockEntity lockableContainerBlockEntity, int i, int j, int k) {
		int l = 0;
		float f = 5.0F;

		for (PlayerEntity playerEntity : world.getEntities(
			PlayerEntity.class,
			new Box(
				(double)((float)i - 5.0F),
				(double)((float)j - 5.0F),
				(double)((float)k - 5.0F),
				(double)((float)(i + 1) + 5.0F),
				(double)((float)(j + 1) + 5.0F),
				(double)((float)(k + 1) + 5.0F)
			)
		)) {
			if (playerEntity.container instanceof GenericContainer) {
				Inventory inventory = ((GenericContainer)playerEntity.container).getInventory();
				if (inventory == lockableContainerBlockEntity || inventory instanceof DoubleInventory && ((DoubleInventory)inventory).isPart(lockableContainerBlockEntity)) {
					l++;
				}
			}
		}

		return l;
	}

	private void playSound(SoundEvent soundEvent) {
		ChestType chestType = this.getCachedState().get(ChestBlock.CHEST_TYPE);
		if (chestType != ChestType.field_12574) {
			double d = (double)this.pos.getX() + 0.5;
			double e = (double)this.pos.getY() + 0.5;
			double f = (double)this.pos.getZ() + 0.5;
			if (chestType == ChestType.field_12571) {
				Direction direction = ChestBlock.getFacing(this.getCachedState());
				d += (double)direction.getOffsetX() * 0.5;
				f += (double)direction.getOffsetZ() * 0.5;
			}

			this.world.playSound(null, d, e, f, soundEvent, SoundCategory.field_15245, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
		}
	}

	@Override
	public boolean onBlockAction(int i, int j) {
		if (i == 1) {
			this.viewerCount = j;
			return true;
		} else {
			return super.onBlockAction(i, j);
		}
	}

	@Override
	public void onInvOpen(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			if (this.viewerCount < 0) {
				this.viewerCount = 0;
			}

			this.viewerCount++;
			this.onInvOpenOrClose();
		}
	}

	@Override
	public void onInvClose(PlayerEntity playerEntity) {
		if (!playerEntity.isSpectator()) {
			this.viewerCount--;
			this.onInvOpenOrClose();
		}
	}

	protected void onInvOpenOrClose() {
		Block block = this.getCachedState().getBlock();
		if (block instanceof ChestBlock) {
			this.world.addBlockAction(this.pos, block, 1, this.viewerCount);
			this.world.updateNeighborsAlways(this.pos, block);
		}
	}

	@Override
	protected DefaultedList<ItemStack> getInvStackList() {
		return this.inventory;
	}

	@Override
	protected void setInvStackList(DefaultedList<ItemStack> defaultedList) {
		this.inventory = defaultedList;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float getAnimationProgress(float f) {
		return MathHelper.lerp(f, this.lastAnimationAngle, this.animationAngle);
	}

	public static int getPlayersLookingInChestCount(BlockView blockView, BlockPos blockPos) {
		BlockState blockState = blockView.getBlockState(blockPos);
		if (blockState.getBlock().hasBlockEntity()) {
			BlockEntity blockEntity = blockView.getBlockEntity(blockPos);
			if (blockEntity instanceof ChestBlockEntity) {
				return ((ChestBlockEntity)blockEntity).viewerCount;
			}
		}

		return 0;
	}

	public static void copyInventory(ChestBlockEntity chestBlockEntity, ChestBlockEntity chestBlockEntity2) {
		DefaultedList<ItemStack> defaultedList = chestBlockEntity.getInvStackList();
		chestBlockEntity.setInvStackList(chestBlockEntity2.getInvStackList());
		chestBlockEntity2.setInvStackList(defaultedList);
	}

	@Override
	protected Container createContainer(int i, PlayerInventory playerInventory) {
		return GenericContainer.createGeneric9x3(i, playerInventory, this);
	}
}
