package net.minecraft.block.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TooltipAppender;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.FletchingScreenHandler;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class FletchingTableBlockEntity extends LockableContainerBlockEntity implements SidedInventory {
	public static final int field_50917 = 0;
	public static final int field_50918 = 1;
	public static final int field_50919 = 2;
	public static final int field_50920 = 3;
	public static final int field_50921 = 0;
	public static final int field_50922 = 1;
	public static final int field_50923 = 2;
	public static final int field_50924 = 3;
	public static final int field_50925 = 4;
	public static final int field_50926 = 5;
	public static final int field_50927 = 6;
	short progress;
	public static final char field_50928 = 'a';
	public static final char field_50929 = 'j';
	public static final char field_50930 = 'a';
	public static final char field_50931 = 'p';
	public static final int field_50932 = 200;
	char quality;
	char impurities;
	char nextLevelImpurities;
	boolean explored;
	short processTime;
	private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(3, ItemStack.EMPTY);
	private static final int[] INPUT_SLOTS = new int[]{0};
	private static final int[] OUTPUT_SLOTS = new int[]{1};
	protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
		@Override
		public int get(int index) {
			return switch (index) {
				case 0 -> FletchingTableBlockEntity.this.progress;
				case 1 -> FletchingTableBlockEntity.this.quality;
				case 2 -> FletchingTableBlockEntity.this.impurities;
				case 3 -> FletchingTableBlockEntity.this.nextLevelImpurities;
				case 4 -> FletchingTableBlockEntity.this.processTime;
				case 5 -> FletchingTableBlockEntity.this.explored ? 1 : 0;
				default -> 0;
			};
		}

		@Override
		public void set(int index, int value) {
			if (index == 0) {
				FletchingTableBlockEntity.this.progress = (short)value;
			}
		}

		@Override
		public int size() {
			return 6;
		}
	};

	public FletchingTableBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.FLETCHING, pos, state);
	}

	@Override
	protected Text getContainerName() {
		return Text.translatable("container.fletching");
	}

	@Override
	protected DefaultedList<ItemStack> getHeldStacks() {
		return this.inventory;
	}

	@Override
	protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
		this.inventory = inventory;
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return new FletchingScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
	}

	@Override
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		Inventories.readNbt(nbt, this.inventory, registryLookup);
		this.quality = nbt.getString("quality").charAt(0);
		this.impurities = nbt.getString("impurities").charAt(0);
		this.nextLevelImpurities = nbt.getString("nextLevelImpurities").charAt(0);
		this.processTime = nbt.getShort("processsTime");
		this.explored = nbt.getBoolean("explored");
		this.progress = nbt.getShort("progresss");
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		nbt.putShort("progresss", this.progress);
		nbt.putString("quality", String.valueOf(this.quality));
		nbt.putString("impurities", String.valueOf(this.impurities));
		nbt.putString("nextLevelImpurities", String.valueOf(this.nextLevelImpurities));
		nbt.putShort("processsTime", this.processTime);
		nbt.putBoolean("explored", this.explored);
		Inventories.writeNbt(nbt, this.inventory, registryLookup);
	}

	@Override
	public void readComponents(ComponentMap components) {
		super.readComponents(components);
		FletchingTableBlockEntity.FletchingComponent fletchingComponent = components.getOrDefault(
			DataComponentTypes.FLETCHING, FletchingTableBlockEntity.FletchingComponent.DEFAULT
		);
		this.quality = fletchingComponent.getQuality();
		this.impurities = fletchingComponent.getImpurities();
		this.nextLevelImpurities = fletchingComponent.getNextLevelImpurities();
		this.processTime = fletchingComponent.getProcessTime();
		this.explored = fletchingComponent.isExplored();
	}

	@Override
	public void addComponents(ComponentMap.Builder componentMapBuilder) {
		super.addComponents(componentMapBuilder);
		componentMapBuilder.add(
			DataComponentTypes.FLETCHING,
			new FletchingTableBlockEntity.FletchingComponent(this.quality, this.impurities, this.nextLevelImpurities, this.processTime, this.explored)
		);
	}

	@Override
	public void removeFromCopiedStackNbt(NbtCompound nbt) {
		super.removeFromCopiedStackNbt(nbt);
		nbt.remove("quality");
		nbt.remove("impurities");
		nbt.remove("nextLevelImpurities");
		nbt.remove("processsTime");
		nbt.remove("explored");
	}

	@Override
	public int size() {
		return this.inventory.size();
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return side == Direction.DOWN ? OUTPUT_SLOTS : INPUT_SLOTS;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack) {
		if (slot == 1) {
			return false;
		} else if (slot == 0) {
			return this.processTime == 0 ? false : stackResinMatches(stack, this.quality, this.impurities);
		} else {
			return slot != 2 ? true : this.progress == 0 && stack.isOf(Items.FEATHER);
		}
	}

	public static boolean stackResinMatches(ItemStack stack, char quality, char impurities) {
		if (!stack.isOf(Items.TOXIC_RESIN)) {
			return false;
		} else {
			FletchingTableBlockEntity.ResinComponent resinComponent = stack.getComponents().get(DataComponentTypes.RESIN);
			if (resinComponent == null) {
				throw new IllegalStateException("Resin item without resin quality");
			} else {
				return quality == resinComponent.getQuality() && resinComponent.getImpurities() == impurities;
			}
		}
	}

	public static ItemStack getResinStackWith(char quality, char impurities) {
		if (quality > 'j') {
			return new ItemStack(Items.AMBER_GEM);
		} else {
			ItemStack itemStack = new ItemStack(Items.TOXIC_RESIN);
			itemStack.set(DataComponentTypes.RESIN, new FletchingTableBlockEntity.ResinComponent(quality, impurities));
			return itemStack;
		}
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
		return this.isValid(slot, stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return slot != 0;
	}

	public static void serverTick(World world, BlockPos pos, BlockState state, FletchingTableBlockEntity blockEntity) {
		if (blockEntity.processTime == 0) {
			blockEntity.processTime = (short)world.random.nextBetweenExclusive(10, 200);
			int i = 10;
			blockEntity.quality = (char)(97 + world.random.nextInt(10));
			blockEntity.impurities = FletchingTableBlockEntity.ResinComponent.getRandomImpurities(world.getRandom());
			blockEntity.nextLevelImpurities = FletchingTableBlockEntity.ResinComponent.getRandomImpurities(world.getRandom());
			blockEntity.explored = false;
		}

		ItemStack itemStack = blockEntity.inventory.get(1);
		if (itemStack.isEmpty() || itemStack.getCount() != itemStack.getMaxCount()) {
			if (blockEntity.progress > 0) {
				blockEntity.progress--;
				if (blockEntity.progress <= 0) {
					ItemStack itemStack2 = getResinStackWith((char)(blockEntity.quality + 1), blockEntity.nextLevelImpurities);
					if (!itemStack.isEmpty()) {
						itemStack2.setCount(itemStack.getCount() + 1);
					}

					blockEntity.inventory.set(2, Items.FEATHER.getDefaultStack());
					blockEntity.inventory.set(1, itemStack2);
					blockEntity.explored = true;
					markDirty(world, pos, state);
				}
			}

			ItemStack itemStack2 = blockEntity.inventory.get(0);
			if (!itemStack2.isEmpty()) {
				if (blockEntity.progress <= 0 && blockEntity.inventory.get(2).isOf(Items.FEATHER)) {
					blockEntity.inventory.set(2, ItemStack.EMPTY);
					blockEntity.progress = blockEntity.processTime;
					itemStack2.decrement(1);
					markDirty(world, pos, state);
				}
			}
		}
	}

	public static final class FletchingComponent implements TooltipAppender {
		public static final Codec<FletchingTableBlockEntity.FletchingComponent> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.CHARACTER.fieldOf("quality").forGetter(FletchingTableBlockEntity.FletchingComponent::getQuality),
						Codecs.CHARACTER.fieldOf("impurities").forGetter(FletchingTableBlockEntity.FletchingComponent::getImpurities),
						Codecs.CHARACTER.fieldOf("next_level_impurities").forGetter(FletchingTableBlockEntity.FletchingComponent::getNextLevelImpurities),
						Codec.SHORT.fieldOf("processs_time").forGetter(FletchingTableBlockEntity.FletchingComponent::getProcessTime),
						Codec.BOOL.optionalFieldOf("explored", Boolean.valueOf(false)).forGetter(FletchingTableBlockEntity.FletchingComponent::isExplored)
					)
					.apply(instance, FletchingTableBlockEntity.FletchingComponent::new)
		);
		public static final PacketCodec<ByteBuf, FletchingTableBlockEntity.FletchingComponent> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.CHARACTER,
			FletchingTableBlockEntity.FletchingComponent::getQuality,
			PacketCodecs.CHARACTER,
			FletchingTableBlockEntity.FletchingComponent::getImpurities,
			PacketCodecs.CHARACTER,
			FletchingTableBlockEntity.FletchingComponent::getNextLevelImpurities,
			PacketCodecs.SHORT,
			FletchingTableBlockEntity.FletchingComponent::getProcessTime,
			PacketCodecs.BOOL,
			FletchingTableBlockEntity.FletchingComponent::isExplored,
			FletchingTableBlockEntity.FletchingComponent::new
		);
		public static final FletchingTableBlockEntity.FletchingComponent DEFAULT = new FletchingTableBlockEntity.FletchingComponent('a', 'a', 'a', (short)0, false);
		private final char quality;
		private final char impurities;
		private final char nextLevelImpurities;
		private final short processTime;
		private final boolean explored;

		public FletchingComponent(char quality, char impurities, char nextLevelImpurities, short processTime, boolean explored) {
			this.quality = quality;
			this.impurities = impurities;
			this.nextLevelImpurities = nextLevelImpurities;
			this.processTime = processTime;
			this.explored = explored;
		}

		public char getQuality() {
			return this.quality;
		}

		public char getImpurities() {
			return this.impurities;
		}

		public char getNextLevelImpurities() {
			return this.nextLevelImpurities;
		}

		public short getProcessTime() {
			return this.processTime;
		}

		public boolean isExplored() {
			return this.explored;
		}

		public boolean equals(Object o) {
			if (o == this) {
				return true;
			} else if (o != null && o.getClass() == this.getClass()) {
				FletchingTableBlockEntity.FletchingComponent fletchingComponent = (FletchingTableBlockEntity.FletchingComponent)o;
				return this.quality == fletchingComponent.quality
					&& this.impurities == fletchingComponent.impurities
					&& this.nextLevelImpurities == fletchingComponent.nextLevelImpurities
					&& this.processTime == fletchingComponent.processTime
					&& this.explored == fletchingComponent.explored;
			} else {
				return false;
			}
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.quality, this.impurities, this.nextLevelImpurities, this.processTime, this.explored});
		}

		public String toString() {
			return "Fletching[quality="
				+ this.quality
				+ ", impurities="
				+ this.impurities
				+ ", nextLevelImpurities="
				+ this.nextLevelImpurities
				+ ", processsTime="
				+ this.processTime
				+ ", explored="
				+ this.explored
				+ "]";
		}

		@Override
		public void appendTooltip(Consumer<Text> textConsumer, TooltipContext context) {
			textConsumer.accept(Text.translatable("block.minecraft.fletching_table.from"));
			textConsumer.accept(ScreenTexts.space().append(FletchingTableBlockEntity.ResinComponent.getQualityTooltip(this.quality)).formatted(Formatting.GRAY));
			textConsumer.accept(ScreenTexts.space().append(FletchingTableBlockEntity.ResinComponent.getImpuritiesTooltip(this.impurities)).formatted(Formatting.GRAY));
			textConsumer.accept(Text.translatable("block.minecraft.fletching_table.to"));
			textConsumer.accept(
				ScreenTexts.space()
					.append(
						this.quality >= 'j'
							? Text.translatable("item.minecraft.amber_gem").formatted(Formatting.GOLD)
							: FletchingTableBlockEntity.ResinComponent.getImpuritiesTooltip(!this.explored ? "unknown" : this.nextLevelImpurities).formatted(Formatting.GRAY)
					)
			);
		}
	}

	public static final class ResinComponent implements TooltipAppender {
		public static final Codec<FletchingTableBlockEntity.ResinComponent> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						Codecs.CHARACTER.fieldOf("quality").forGetter(FletchingTableBlockEntity.ResinComponent::getQuality),
						Codecs.CHARACTER.fieldOf("impurities").forGetter(FletchingTableBlockEntity.ResinComponent::getImpurities)
					)
					.apply(instance, FletchingTableBlockEntity.ResinComponent::new)
		);
		public static final PacketCodec<ByteBuf, FletchingTableBlockEntity.ResinComponent> PACKET_CODEC = PacketCodec.tuple(
			PacketCodecs.CHARACTER,
			FletchingTableBlockEntity.ResinComponent::getQuality,
			PacketCodecs.CHARACTER,
			FletchingTableBlockEntity.ResinComponent::getImpurities,
			FletchingTableBlockEntity.ResinComponent::new
		);
		public static final FletchingTableBlockEntity.ResinComponent DEFAULT = new FletchingTableBlockEntity.ResinComponent('a', 'a');
		private final char quality;
		private final char impurities;

		public ResinComponent(char quality, char impurities) {
			this.quality = quality;
			this.impurities = impurities;
		}

		@Override
		public void appendTooltip(Consumer<Text> textConsumer, TooltipContext context) {
			textConsumer.accept(getQualityTooltip(this.quality).formatted(Formatting.GRAY));
			textConsumer.accept(getImpuritiesTooltip(this.impurities).formatted(Formatting.GRAY));
		}

		public static MutableText getQualityTooltip(char quality) {
			return Text.translatable("item.resin.quality", Text.translatable("item.resin.clarity.adjective." + quality));
		}

		public static MutableText getImpuritiesTooltip(Object impurities) {
			return Text.translatable("item.resin.impurities", Text.translatable("item.resin.impurity.adjective." + impurities));
		}

		public char getQuality() {
			return this.quality;
		}

		public char getImpurities() {
			return this.impurities;
		}

		public static char getRandomImpurities(Random random) {
			int i = 16;
			return (char)(97 + random.nextInt(16));
		}

		public boolean equals(Object o) {
			return !(o instanceof FletchingTableBlockEntity.ResinComponent resinComponent)
				? false
				: this.quality == resinComponent.quality && this.impurities == resinComponent.impurities;
		}

		public int hashCode() {
			return Objects.hash(new Object[]{this.quality, this.impurities});
		}
	}
}
