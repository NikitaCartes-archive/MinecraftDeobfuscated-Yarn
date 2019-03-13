package net.minecraft.block.entity;

import javax.annotation.Nullable;
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
import net.minecraft.util.Clearable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class LecternBlockEntity extends BlockEntity implements Clearable, NameableContainerProvider {
	private final Inventory inventory = new Inventory() {
		@Override
		public int getInvSize() {
			return 1;
		}

		@Override
		public boolean isInvEmpty() {
			return LecternBlockEntity.this.book.isEmpty();
		}

		@Override
		public ItemStack method_5438(int i) {
			return i == 0 ? LecternBlockEntity.this.book : ItemStack.EMPTY;
		}

		@Override
		public ItemStack method_5434(int i, int j) {
			if (i == 0) {
				ItemStack itemStack = LecternBlockEntity.this.book.split(j);
				if (LecternBlockEntity.this.book.isEmpty()) {
					LecternBlockEntity.this.onBookRemoved();
				}

				return itemStack;
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public ItemStack method_5441(int i) {
			if (i == 0) {
				ItemStack itemStack = LecternBlockEntity.this.book;
				LecternBlockEntity.this.book = ItemStack.EMPTY;
				LecternBlockEntity.this.onBookRemoved();
				return itemStack;
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public void method_5447(int i, ItemStack itemStack) {
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
		public boolean method_5443(PlayerEntity playerEntity) {
			if (LecternBlockEntity.this.world.method_8321(LecternBlockEntity.this.field_11867) != LecternBlockEntity.this) {
				return false;
			} else {
				return playerEntity.squaredDistanceTo(
							(double)LecternBlockEntity.this.field_11867.getX() + 0.5,
							(double)LecternBlockEntity.this.field_11867.getY() + 0.5,
							(double)LecternBlockEntity.this.field_11867.getZ() + 0.5
						)
						> 64.0
					? false
					: LecternBlockEntity.this.hasBook();
			}
		}

		@Override
		public boolean method_5437(int i, ItemStack itemStack) {
			return false;
		}

		@Override
		public void clear() {
		}
	};
	private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int i) {
			return i == 0 ? LecternBlockEntity.this.currentPage : 0;
		}

		@Override
		public void set(int i, int j) {
			if (i == 0) {
				LecternBlockEntity.this.setCurrentPage(j);
			}
		}

		@Override
		public int size() {
			return 1;
		}
	};
	private ItemStack book = ItemStack.EMPTY;
	private int currentPage;
	private int pageCount;

	public LecternBlockEntity() {
		super(BlockEntityType.LECTERN);
	}

	public ItemStack getBook() {
		return this.book;
	}

	public boolean hasBook() {
		Item item = this.book.getItem();
		return item == Items.field_8674 || item == Items.field_8360;
	}

	public void setBook(ItemStack itemStack) {
		this.setBook(itemStack, null);
	}

	private void onBookRemoved() {
		this.currentPage = 0;
		this.pageCount = 0;
		LecternBlock.method_17473(this.getWorld(), this.method_11016(), this.method_11010(), false);
	}

	public void setBook(ItemStack itemStack, @Nullable PlayerEntity playerEntity) {
		this.book = this.resolveBook(itemStack, playerEntity);
		this.currentPage = 0;
		this.pageCount = WrittenBookItem.getPageCount(this.book);
		this.markDirty();
	}

	private void setCurrentPage(int i) {
		int j = MathHelper.clamp(i, 0, this.pageCount - 1);
		if (j != this.currentPage) {
			this.currentPage = j;
			this.markDirty();
			LecternBlock.method_17471(this.getWorld(), this.method_11016(), this.method_11010());
		}
	}

	public int getCurrentPage() {
		return this.currentPage;
	}

	public int getComparatorOutput() {
		float f = this.pageCount > 1 ? (float)this.getCurrentPage() / ((float)this.pageCount - 1.0F) : 1.0F;
		return MathHelper.floor(f * 14.0F) + (this.hasBook() ? 1 : 0);
	}

	private ItemStack resolveBook(ItemStack itemStack, @Nullable PlayerEntity playerEntity) {
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
			string = playerEntity.method_5477().getString();
			textComponent = playerEntity.method_5476();
		}

		Vec3d vec3d = new Vec3d((double)this.field_11867.getX() + 0.5, (double)this.field_11867.getY() + 0.5, (double)this.field_11867.getZ() + 0.5);
		return new ServerCommandSource(
			CommandOutput.field_17395, vec3d, Vec2f.ZERO, (ServerWorld)this.world, 2, string, textComponent, this.world.getServer(), playerEntity
		);
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		if (compoundTag.containsKey("Book", 10)) {
			this.book = this.resolveBook(ItemStack.method_7915(compoundTag.getCompound("Book")), null);
		} else {
			this.book = ItemStack.EMPTY;
		}

		this.pageCount = WrittenBookItem.getPageCount(this.book);
		this.currentPage = MathHelper.clamp(compoundTag.getInt("Page"), 0, this.pageCount - 1);
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		if (!this.getBook().isEmpty()) {
			compoundTag.method_10566("Book", this.getBook().method_7953(new CompoundTag()));
			compoundTag.putInt("Page", this.currentPage);
		}

		return compoundTag;
	}

	@Override
	public void clear() {
		this.setBook(ItemStack.EMPTY);
	}

	@Override
	public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return new LecternContainer(i, this.inventory, this.propertyDelegate);
	}

	@Override
	public TextComponent method_5476() {
		return new TranslatableTextComponent("container.lectern");
	}
}
