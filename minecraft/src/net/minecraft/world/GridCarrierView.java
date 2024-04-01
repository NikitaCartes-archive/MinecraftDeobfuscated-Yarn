package net.minecraft.world;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.GridCarrierEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.border.WorldBorder;

public class GridCarrierView implements BlockView, CollisionView {
	protected final World world;
	protected final GridCarrierEntity gridCarrier;
	private Grid grid = new Grid(0, 0, 0);
	protected RegistryEntry<Biome> biome;
	private Box gridBox = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

	public GridCarrierView(World world, GridCarrierEntity gridCarrier) {
		this.world = world;
		this.gridCarrier = gridCarrier;
		this.biome = world.getRegistryManager().get(RegistryKeys.BIOME).entryOf(BiomeKeys.PLAINS);
		this.setGridPosition(gridCarrier.getX(), gridCarrier.getY(), gridCarrier.getZ());
	}

	public void setGridPosition(double x, double y, double z) {
		this.gridBox = new Box(x, y, z, x + (double)this.grid.getXSize() + 1.0, y + (double)this.grid.getYSize() + 1.0, z + (double)this.grid.getZSize() + 1.0);
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
		this.setGridPosition(this.gridCarrier.getX(), this.gridCarrier.getY(), this.gridCarrier.getZ());
	}

	public void setBiome(RegistryEntry<Biome> biome) {
		this.biome = biome;
	}

	public World getWorld() {
		return this.world;
	}

	@Override
	public BlockState getBlockState(BlockPos pos) {
		return this.grid.getBlockState(pos);
	}

	@Override
	public FluidState getFluidState(BlockPos pos) {
		return this.getBlockState(pos).getFluidState();
	}

	@Override
	public boolean isPotato() {
		return false;
	}

	@Nullable
	@Override
	public BlockEntity getBlockEntity(BlockPos pos) {
		return null;
	}

	@Override
	public int getHeight() {
		return this.grid.getYSize();
	}

	@Override
	public int getBottomY() {
		return 0;
	}

	public UUID getGridCarrierUuid() {
		return this.gridCarrier.getUuid();
	}

	public GridCarrierEntity getGridCarrier() {
		return this.gridCarrier;
	}

	public Grid getGrid() {
		return this.grid;
	}

	public RegistryEntry<Biome> getBiome() {
		return this.biome;
	}

	public Box getGridBox() {
		return this.gridBox;
	}

	public Box getRenderOffsetGridBox() {
		Vec3d vec3d = this.getRenderOFfset();
		return this.gridBox.offset(-vec3d.x, -vec3d.y, -vec3d.z);
	}

	@Override
	public WorldBorder getWorldBorder() {
		return this.world.getWorldBorder();
	}

	@Nullable
	@Override
	public BlockView getChunkAsView(int chunkX, int chunkZ) {
		return this;
	}

	@Override
	public List<VoxelShape> getEntityCollisions(@Nullable Entity entity, Box box) {
		return List.of();
	}

	public Vec3d getRenderOFfset() {
		return new Vec3d(
			this.gridCarrier.getX() - this.gridCarrier.lastRenderX,
			this.gridCarrier.getY() - this.gridCarrier.lastRenderY,
			this.gridCarrier.getZ() - this.gridCarrier.lastRenderZ
		);
	}
}
