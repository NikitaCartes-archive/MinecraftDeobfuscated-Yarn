package net.minecraft.world;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GridTickable;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.state.property.Properties;
import net.minecraft.util.collection.PackedIntegerArray;
import net.minecraft.util.collection.PaletteStorage;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class Grid {
	public static final PacketCodec<RegistryByteBuf, Grid> PACKET_CODEC = new PacketCodec<RegistryByteBuf, Grid>() {
		public Grid decode(RegistryByteBuf registryByteBuf) {
			int i = registryByteBuf.readVarInt();
			int j = registryByteBuf.readVarInt();
			int k = registryByteBuf.readVarInt();
			List<BlockState> list = registryByteBuf.readList(packetByteBuf -> Block.getStateFromRawId(packetByteBuf.readVarInt()));
			int l = MathHelper.ceilLog2(list.size());
			BlockState[] blockStates = new BlockState[i * j * k];
			PackedIntegerArray packedIntegerArray = new PackedIntegerArray(l, blockStates.length, registryByteBuf.readLongArray());

			for (int m = 0; m < blockStates.length; m++) {
				blockStates[m] = (BlockState)list.get(packedIntegerArray.get(m));
			}

			ArrayList<BlockPos> arrayList = registryByteBuf.readCollection(ArrayList::new, BlockPos.PACKET_CODEC);
			return new Grid(blockStates, arrayList, i, j, k);
		}

		public void encode(RegistryByteBuf registryByteBuf, Grid grid) {
			registryByteBuf.writeVarInt(grid.xSize);
			registryByteBuf.writeVarInt(grid.ySize);
			registryByteBuf.writeVarInt(grid.zSize);
			Reference2IntMap<BlockState> reference2IntMap = new Reference2IntOpenHashMap<>();
			List<BlockState> list = new ArrayList();
			reference2IntMap.defaultReturnValue(-1);

			for (BlockState blockState : grid.states) {
				int i = list.size();
				int j = reference2IntMap.putIfAbsent(blockState, i);
				if (j == -1) {
					list.add(blockState);
				}
			}

			registryByteBuf.writeCollection(list, (packetByteBuf, blockStatex) -> packetByteBuf.writeVarInt(Block.getRawIdFromState(blockStatex)));
			int k = MathHelper.ceilLog2(list.size());
			PaletteStorage paletteStorage = new PackedIntegerArray(k, grid.xSize * grid.ySize * grid.zSize);
			int l = 0;

			for (BlockState blockState2 : grid.states) {
				paletteStorage.set(l++, reference2IntMap.getInt(blockState2));
			}

			registryByteBuf.writeLongArray(paletteStorage.getData());
			registryByteBuf.writeCollection(grid.tickables, BlockPos.PACKET_CODEC);
		}
	};
	private static final int field_50519 = -1;
	private static final BlockState AIR = Blocks.AIR.getDefaultState();
	final BlockState[] states;
	final List<BlockPos> tickables;
	final int xSize;
	final int ySize;
	final int zSize;

	Grid(BlockState[] states, List<BlockPos> tickables, int xSize, int ySize, int zSize) {
		this.states = states;
		this.tickables = tickables;
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
	}

	public Grid(int xSize, int ySize, int zSize) {
		this.states = new BlockState[xSize * ySize * zSize];
		Arrays.fill(this.states, AIR);
		this.tickables = new ArrayList();
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
	}

	public void setBlockStateAt(int x, int y, int z, BlockState state) {
		int i = this.getIndexForPos(x, y, z);
		if (i == -1) {
			throw new IllegalStateException("Block was out of bounds");
		} else {
			this.states[i] = state;
		}
	}

	public void addPosition(BlockPos pos) {
		this.tickables.add(pos);
	}

	public void tick(World world, Vec3d pos, Direction movementDirection) {
		this.tickables
			.forEach(
				tickablePos -> {
					BlockState blockState = this.getBlockState(tickablePos.getX(), tickablePos.getY(), tickablePos.getZ());
					if (blockState.getBlock() instanceof GridTickable gridTickable) {
						gridTickable.tick(
							world, this, blockState, tickablePos, pos.add((double)tickablePos.getX(), (double)tickablePos.getY(), (double)tickablePos.getZ()), movementDirection
						);
					}
				}
			);
	}

	public BlockState getBlockState(int x, int y, int z) {
		int i = this.getIndexForPos(x, y, z);
		return i == -1 ? AIR : this.states[i];
	}

	public BlockState getBlockState(BlockPos pos) {
		return this.getBlockState(pos.getX(), pos.getY(), pos.getZ());
	}

	private int getIndexForPos(int x, int y, int z) {
		return x >= 0 && y >= 0 && z >= 0 && x < this.xSize && y < this.ySize && z < this.zSize ? (x + z * this.xSize) * this.ySize + y : -1;
	}

	public int getXSize() {
		return this.xSize;
	}

	public int getYSize() {
		return this.ySize;
	}

	public int getZSize() {
		return this.zSize;
	}

	public Grid copy() {
		return new Grid((BlockState[])Arrays.copyOf(this.states, this.states.length), new ArrayList(this.tickables), this.xSize, this.ySize, this.zSize);
	}

	public void method_58976(BlockPos pos, World world) {
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int i = 0; i < this.zSize; i++) {
			for (int j = 0; j < this.xSize; j++) {
				for (int k = 0; k < this.ySize; k++) {
					mutable.set(pos, j, k, i);
					BlockState blockState = this.getBlockState(j, k, i);
					if (!blockState.isAir()) {
						FluidState fluidState = world.getFluidState(mutable);
						if (fluidState.isOf(Fluids.WATER)) {
							blockState = blockState.withIfExists(Properties.WATERLOGGED, Boolean.valueOf(true));
						}

						world.setBlockState(mutable, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
					}
				}
			}
		}

		for (int i = 0; i < this.zSize; i++) {
			for (int j = 0; j < this.xSize; j++) {
				for (int kx = 0; kx < this.ySize; kx++) {
					mutable.set(pos, j, kx, i);
					world.updateNeighbors(mutable, this.getBlockState(j, kx, i).getBlock());
				}
			}
		}
	}

	public static Grid readFromNbt(RegistryEntryLookup<Block> blockRegistryLookup, NbtCompound nbt) {
		int i = nbt.getInt("size_x");
		int j = nbt.getInt("size_y");
		int k = nbt.getInt("size_z");
		BlockState[] blockStates = new BlockState[i * j * k];
		NbtList nbtList = nbt.getList("palette", NbtElement.COMPOUND_TYPE);
		List<BlockState> list = new ArrayList();

		for (int l = 0; l < nbtList.size(); l++) {
			list.add(NbtHelper.toBlockState(blockRegistryLookup, nbtList.getCompound(l)));
		}

		int[] is = nbt.getIntArray("blocks");
		if (is.length != blockStates.length) {
			return new Grid(i, j, k);
		} else {
			for (int m = 0; m < is.length; m++) {
				int n = is[m];
				blockStates[m] = n < list.size() ? (BlockState)list.get(n) : Blocks.AIR.getDefaultState();
			}

			List<BlockPos> list2 = new ArrayList();
			if (nbt.contains("tickables", NbtElement.LONG_ARRAY_TYPE)) {
				Arrays.stream(nbt.getLongArray("tickables")).mapToObj(BlockPos::fromLong).forEach(list2::add);
			}

			return new Grid(blockStates, list2, i, j, k);
		}
	}

	public NbtElement writeToNbt() {
		NbtCompound nbtCompound = new NbtCompound();
		nbtCompound.putInt("size_x", this.xSize);
		nbtCompound.putInt("size_y", this.ySize);
		nbtCompound.putInt("size_z", this.zSize);
		Reference2IntMap<BlockState> reference2IntMap = new Reference2IntOpenHashMap<>();
		reference2IntMap.defaultReturnValue(-1);
		NbtList nbtList = new NbtList();
		int[] is = new int[this.states.length];

		for (int i = 0; i < this.states.length; i++) {
			BlockState blockState = this.states[i];
			int j = nbtList.size();
			int k = reference2IntMap.putIfAbsent(blockState, j);
			if (k == -1) {
				nbtList.add(NbtHelper.fromBlockState(blockState));
				is[i] = j;
			} else {
				is[i] = k;
			}
		}

		nbtCompound.put("palette", nbtList);
		nbtCompound.put("blocks", new NbtIntArray(is));
		nbtCompound.putLongArray("tickables", this.tickables.stream().mapToLong(BlockPos::asLong).toArray());
		return nbtCompound;
	}
}
