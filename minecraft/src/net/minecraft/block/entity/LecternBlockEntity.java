package net.minecraft.block.entity;

import javax.annotation.Nullable;
import net.minecraft.class_3829;
import net.minecraft.block.LecternBlock;
import net.minecraft.container.Container;
import net.minecraft.container.LecternContainer;
import net.minecraft.container.NameableContainerProvider;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.StringTextComponent;
import net.minecraft.text.TextComponent;
import net.minecraft.text.TranslatableTextComponent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class LecternBlockEntity extends BlockEntity implements class_3829, NameableContainerProvider {
	private final Inventory field_17386 = new Inventory() {
		@Override
		public int getInvSize() {
			return 1;
		}

		@Override
		public boolean isInvEmpty() {
			return LecternBlockEntity.this.field_17388.isEmpty();
		}

		@Override
		public ItemStack getInvStack(int i) {
			return i == 0 ? LecternBlockEntity.this.field_17388 : ItemStack.EMPTY;
		}

		@Override
		public ItemStack takeInvStack(int i, int j) {
			if (i == 0) {
				ItemStack itemStack = LecternBlockEntity.this.field_17388.split(j);
				if (LecternBlockEntity.this.field_17388.isEmpty()) {
					LecternBlockEntity.this.method_17525();
				}

				return itemStack;
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public ItemStack removeInvStack(int i) {
			if (i == 0) {
				ItemStack itemStack = LecternBlockEntity.this.field_17388;
				LecternBlockEntity.this.field_17388 = ItemStack.EMPTY;
				LecternBlockEntity.this.method_17525();
				return itemStack;
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public void setInvStack(int i, ItemStack itemStack) {
		}

		@Override
		public int getInvMaxStackAmount() {
			return 1;
		}

		@Override
		public void markDirty() {
			LecternBlockEntity.this.markDirty();
		}

		@Override
		public boolean canPlayerUseInv(PlayerEntity playerEntity) {
			if (LecternBlockEntity.this.world.getBlockEntity(LecternBlockEntity.this.pos) != LecternBlockEntity.this) {
				return false;
			} else {
				return playerEntity.squaredDistanceTo(
							(double)LecternBlockEntity.this.pos.getX() + 0.5, (double)LecternBlockEntity.this.pos.getY() + 0.5, (double)LecternBlockEntity.this.pos.getZ() + 0.5
						)
						> 64.0
					? false
					: LecternBlockEntity.this.method_17522();
			}
		}

		@Override
		public boolean isValidInvStack(int i, ItemStack itemStack) {
			return false;
		}

		@Override
		public void clearInv() {
		}
	};
	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int i) {
			return i == 0 ? LecternBlockEntity.this.field_17389 : 0;
		}

		@Override
		public void set(int i, int j) {
			if (i == 0) {
				LecternBlockEntity.this.method_17511(j);
			}
		}

		@Override
		public int size() {
			return 1;
		}
	};
	private ItemStack field_17388 = ItemStack.EMPTY;
	private int field_17389;
	private int field_17390;

	public LecternBlockEntity() {
		super(BlockEntityType.LECTERN);
	}

	public ItemStack method_17520() {
		return this.field_17388;
	}

	public boolean method_17522() {
		Item item = this.field_17388.getItem();
		return item == Items.field_8674 || item == Items.field_8360;
	}

	public void method_17513(ItemStack itemStack) {
		this.method_17514(itemStack, null);
	}

	private void method_17525() {
		this.field_17389 = 0;
		this.field_17390 = 0;
		LecternBlock.method_17473(this.getWorld(), this.getPos(), this.getCachedState(), false);
	}

	public void method_17514(ItemStack itemStack, @Nullable PlayerEntity playerEntity) {
		this.field_17388 = this.method_17518(itemStack, playerEntity);
		this.field_17389 = 0;
		this.field_17390 = WrittenBookItem.method_17443(this.field_17388);
		this.markDirty();
	}

	private void method_17511(int i) {
		int j = MathHelper.clamp(i, 0, this.field_17390 - 1);
		if (j != this.field_17389) {
			this.field_17389 = j;
			this.markDirty();
			LecternBlock.method_17471(this.getWorld(), this.getPos(), this.getCachedState());
		}
	}

	public int method_17523() {
		return this.field_17389;
	}

	public int method_17524() {
		float f = this.field_17390 > 1 ? (float)this.method_17523() / ((float)this.field_17390 - 1.0F) : 1.0F;
		return MathHelper.floor(f * 14.0F) + (this.method_17522() ? 1 : 0);
	}

	private ItemStack method_17518(ItemStack itemStack, @Nullable PlayerEntity playerEntity) {
		if (this.world instanceof ServerWorld && itemStack.getItem() == Items.field_8360) {
			WrittenBookItem.method_8054(itemStack, this.method_17512(playerEntity), playerEntity);
		}

		return itemStack;
	}

	private ServerCommandSource method_17512(@Nullable PlayerEntity playerEntity) {
		String string;
		TextComponent textComponent;
		if (playerEntity == null) {
			string = "Lectern";
			textComponent = new StringTextComponent("Lectern");
		} else {
			string = playerEntity.getName().getString();
			textComponent = playerEntity.getDisplayName();
		}

		Vec3d vec3d = new Vec3d((double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5);
		return new ServerCommandSource(
			CommandOutput.field_17395, vec3d, Vec2f.ZERO, (ServerWorld)this.world, 2, string, textComponent, this.world.getServer(), playerEntity
		);
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		if (compoundTag.containsKey("Book", 10)) {
			this.field_17388 = this.method_17518(ItemStack.fromTag(compoundTag.getCompound("Book")), null);
		} else {
			this.field_17388 = ItemStack.EMPTY;
		}

		this.field_17390 = WrittenBookItem.method_17443(this.field_17388);
		this.field_17389 = MathHelper.clamp(compoundTag.getInt("Page"), 0, this.field_17390 - 1);
	}

	@Override
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		if (!this.method_17520().isEmpty()) {
			compoundTag.put("Book", this.method_17520().toTag(new CompoundTag()));
			compoundTag.putInt("Page", this.field_17389);
		}

		return compoundTag;
	}

	@Override
	public void clearInv() {
		this.method_17513(ItemStack.EMPTY);
	}

	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new LecternContainer(i, this.field_17386, this.propertyDelegate);
	}

	@Override
	public TextComponent getDisplayName() {
		return new TranslatableTextComponent("container.lectern");
	}
}
