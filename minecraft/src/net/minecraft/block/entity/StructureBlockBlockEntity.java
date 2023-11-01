package net.minecraft.block.entity;

import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.StringHelper;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;

public class StructureBlockBlockEntity extends BlockEntity {
	private static final int field_31367 = 5;
	public static final int field_31364 = 48;
	public static final int field_31365 = 48;
	public static final String AUTHOR_KEY = "author";
	@Nullable
	private Identifier templateName;
	private String author = "";
	private String metadata = "";
	private BlockPos offset = new BlockPos(0, 1, 0);
	private Vec3i size = Vec3i.ZERO;
	private BlockMirror mirror = BlockMirror.NONE;
	private BlockRotation rotation = BlockRotation.NONE;
	private StructureBlockMode mode;
	private boolean ignoreEntities = true;
	private boolean powered;
	private boolean showAir;
	private boolean showBoundingBox = true;
	private float integrity = 1.0F;
	private long seed;

	public StructureBlockBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityType.STRUCTURE_BLOCK, pos, state);
		this.mode = state.get(StructureBlock.MODE);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		super.writeNbt(nbt);
		nbt.putString("name", this.getTemplateName());
		nbt.putString("author", this.author);
		nbt.putString("metadata", this.metadata);
		nbt.putInt("posX", this.offset.getX());
		nbt.putInt("posY", this.offset.getY());
		nbt.putInt("posZ", this.offset.getZ());
		nbt.putInt("sizeX", this.size.getX());
		nbt.putInt("sizeY", this.size.getY());
		nbt.putInt("sizeZ", this.size.getZ());
		nbt.putString("rotation", this.rotation.toString());
		nbt.putString("mirror", this.mirror.toString());
		nbt.putString("mode", this.mode.toString());
		nbt.putBoolean("ignoreEntities", this.ignoreEntities);
		nbt.putBoolean("powered", this.powered);
		nbt.putBoolean("showair", this.showAir);
		nbt.putBoolean("showboundingbox", this.showBoundingBox);
		nbt.putFloat("integrity", this.integrity);
		nbt.putLong("seed", this.seed);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.setTemplateName(nbt.getString("name"));
		this.author = nbt.getString("author");
		this.metadata = nbt.getString("metadata");
		int i = MathHelper.clamp(nbt.getInt("posX"), -48, 48);
		int j = MathHelper.clamp(nbt.getInt("posY"), -48, 48);
		int k = MathHelper.clamp(nbt.getInt("posZ"), -48, 48);
		this.offset = new BlockPos(i, j, k);
		int l = MathHelper.clamp(nbt.getInt("sizeX"), 0, 48);
		int m = MathHelper.clamp(nbt.getInt("sizeY"), 0, 48);
		int n = MathHelper.clamp(nbt.getInt("sizeZ"), 0, 48);
		this.size = new Vec3i(l, m, n);

		try {
			this.rotation = BlockRotation.valueOf(nbt.getString("rotation"));
		} catch (IllegalArgumentException var11) {
			this.rotation = BlockRotation.NONE;
		}

		try {
			this.mirror = BlockMirror.valueOf(nbt.getString("mirror"));
		} catch (IllegalArgumentException var10) {
			this.mirror = BlockMirror.NONE;
		}

		try {
			this.mode = StructureBlockMode.valueOf(nbt.getString("mode"));
		} catch (IllegalArgumentException var9) {
			this.mode = StructureBlockMode.DATA;
		}

		this.ignoreEntities = nbt.getBoolean("ignoreEntities");
		this.powered = nbt.getBoolean("powered");
		this.showAir = nbt.getBoolean("showair");
		this.showBoundingBox = nbt.getBoolean("showboundingbox");
		if (nbt.contains("integrity")) {
			this.integrity = nbt.getFloat("integrity");
		} else {
			this.integrity = 1.0F;
		}

		this.seed = nbt.getLong("seed");
		this.updateBlockMode();
	}

	private void updateBlockMode() {
		if (this.world != null) {
			BlockPos blockPos = this.getPos();
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.isOf(Blocks.STRUCTURE_BLOCK)) {
				this.world.setBlockState(blockPos, blockState.with(StructureBlock.MODE, this.mode), Block.NOTIFY_LISTENERS);
			}
		}
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return this.createNbt();
	}

	public boolean openScreen(PlayerEntity player) {
		if (!player.isCreativeLevelTwoOp()) {
			return false;
		} else {
			if (player.getEntityWorld().isClient) {
				player.openStructureBlockScreen(this);
			}

			return true;
		}
	}

	public String getTemplateName() {
		return this.templateName == null ? "" : this.templateName.toString();
	}

	public String getStructurePath() {
		return this.templateName == null ? "" : this.templateName.getPath();
	}

	public boolean hasStructureName() {
		return this.templateName != null;
	}

	public void setTemplateName(@Nullable String templateName) {
		this.setTemplateName(StringHelper.isEmpty(templateName) ? null : Identifier.tryParse(templateName));
	}

	public void setTemplateName(@Nullable Identifier templateName) {
		this.templateName = templateName;
	}

	public void setAuthor(LivingEntity entity) {
		this.author = entity.getName().getString();
	}

	public BlockPos getOffset() {
		return this.offset;
	}

	public void setOffset(BlockPos offset) {
		this.offset = offset;
	}

	public Vec3i getSize() {
		return this.size;
	}

	public void setSize(Vec3i size) {
		this.size = size;
	}

	public BlockMirror getMirror() {
		return this.mirror;
	}

	public void setMirror(BlockMirror mirror) {
		this.mirror = mirror;
	}

	public BlockRotation getRotation() {
		return this.rotation;
	}

	public void setRotation(BlockRotation rotation) {
		this.rotation = rotation;
	}

	public String getMetadata() {
		return this.metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public StructureBlockMode getMode() {
		return this.mode;
	}

	public void setMode(StructureBlockMode mode) {
		this.mode = mode;
		BlockState blockState = this.world.getBlockState(this.getPos());
		if (blockState.isOf(Blocks.STRUCTURE_BLOCK)) {
			this.world.setBlockState(this.getPos(), blockState.with(StructureBlock.MODE, mode), Block.NOTIFY_LISTENERS);
		}
	}

	public boolean shouldIgnoreEntities() {
		return this.ignoreEntities;
	}

	public void setIgnoreEntities(boolean ignoreEntities) {
		this.ignoreEntities = ignoreEntities;
	}

	public float getIntegrity() {
		return this.integrity;
	}

	public void setIntegrity(float integrity) {
		this.integrity = integrity;
	}

	public long getSeed() {
		return this.seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public boolean detectStructureSize() {
		if (this.mode != StructureBlockMode.SAVE) {
			return false;
		} else {
			BlockPos blockPos = this.getPos();
			int i = 80;
			BlockPos blockPos2 = new BlockPos(blockPos.getX() - 80, this.world.getBottomY(), blockPos.getZ() - 80);
			BlockPos blockPos3 = new BlockPos(blockPos.getX() + 80, this.world.getTopY() - 1, blockPos.getZ() + 80);
			Stream<BlockPos> stream = this.streamCornerPos(blockPos2, blockPos3);
			return getStructureBox(blockPos, stream).filter(box -> {
				int ix = box.getMaxX() - box.getMinX();
				int j = box.getMaxY() - box.getMinY();
				int k = box.getMaxZ() - box.getMinZ();
				if (ix > 1 && j > 1 && k > 1) {
					this.offset = new BlockPos(box.getMinX() - blockPos.getX() + 1, box.getMinY() - blockPos.getY() + 1, box.getMinZ() - blockPos.getZ() + 1);
					this.size = new Vec3i(ix - 1, j - 1, k - 1);
					this.markDirty();
					BlockState blockState = this.world.getBlockState(blockPos);
					this.world.updateListeners(blockPos, blockState, blockState, Block.NOTIFY_ALL);
					return true;
				} else {
					return false;
				}
			}).isPresent();
		}
	}

	/**
	 * Streams positions of {@link StructureBlockMode#CORNER} mode structure blocks with matching names.
	 */
	private Stream<BlockPos> streamCornerPos(BlockPos start, BlockPos end) {
		return BlockPos.stream(start, end)
			.filter(pos -> this.world.getBlockState(pos).isOf(Blocks.STRUCTURE_BLOCK))
			.map(this.world::getBlockEntity)
			.filter(blockEntity -> blockEntity instanceof StructureBlockBlockEntity)
			.map(blockEntity -> (StructureBlockBlockEntity)blockEntity)
			.filter(blockEntity -> blockEntity.mode == StructureBlockMode.CORNER && Objects.equals(this.templateName, blockEntity.templateName))
			.map(BlockEntity::getPos);
	}

	private static Optional<BlockBox> getStructureBox(BlockPos pos, Stream<BlockPos> corners) {
		Iterator<BlockPos> iterator = corners.iterator();
		if (!iterator.hasNext()) {
			return Optional.empty();
		} else {
			BlockPos blockPos = (BlockPos)iterator.next();
			BlockBox blockBox = new BlockBox(blockPos);
			if (iterator.hasNext()) {
				iterator.forEachRemaining(blockBox::encompass);
			} else {
				blockBox.encompass(pos);
			}

			return Optional.of(blockBox);
		}
	}

	public boolean saveStructure() {
		return this.mode != StructureBlockMode.SAVE ? false : this.saveStructure(true);
	}

	public boolean saveStructure(boolean interactive) {
		if (this.templateName == null) {
			return false;
		} else {
			BlockPos blockPos = this.getPos().add(this.offset);
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureTemplateManager structureTemplateManager = serverWorld.getStructureTemplateManager();

			StructureTemplate structureTemplate;
			try {
				structureTemplate = structureTemplateManager.getTemplateOrBlank(this.templateName);
			} catch (InvalidIdentifierException var8) {
				return false;
			}

			structureTemplate.saveFromWorld(this.world, blockPos, this.size, !this.ignoreEntities, Blocks.STRUCTURE_VOID);
			structureTemplate.setAuthor(this.author);
			if (interactive) {
				try {
					return structureTemplateManager.saveTemplate(this.templateName);
				} catch (InvalidIdentifierException var7) {
					return false;
				}
			} else {
				return true;
			}
		}
	}

	public static Random createRandom(long seed) {
		return seed == 0L ? Random.create(Util.getMeasuringTimeMs()) : Random.create(seed);
	}

	public boolean loadAndTryPlaceStructure(ServerWorld world) {
		if (this.mode == StructureBlockMode.LOAD && this.templateName != null) {
			StructureTemplate structureTemplate = (StructureTemplate)world.getStructureTemplateManager().getTemplate(this.templateName).orElse(null);
			if (structureTemplate == null) {
				return false;
			} else if (structureTemplate.getSize().equals(this.size)) {
				this.loadAndPlaceStructure(world, structureTemplate);
				return true;
			} else {
				this.loadStructure(structureTemplate);
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean loadStructure(ServerWorld world) {
		StructureTemplate structureTemplate = this.getStructureTemplate(world);
		if (structureTemplate == null) {
			return false;
		} else {
			this.loadStructure(structureTemplate);
			return true;
		}
	}

	private void loadStructure(StructureTemplate template) {
		this.author = !StringHelper.isEmpty(template.getAuthor()) ? template.getAuthor() : "";
		this.size = template.getSize();
		this.markDirty();
	}

	public void loadAndPlaceStructure(ServerWorld world) {
		StructureTemplate structureTemplate = this.getStructureTemplate(world);
		if (structureTemplate != null) {
			this.loadAndPlaceStructure(world, structureTemplate);
		}
	}

	@Nullable
	private StructureTemplate getStructureTemplate(ServerWorld world) {
		return this.templateName == null ? null : (StructureTemplate)world.getStructureTemplateManager().getTemplate(this.templateName).orElse(null);
	}

	private void loadAndPlaceStructure(ServerWorld world, StructureTemplate template) {
		this.loadStructure(template);
		StructurePlacementData structurePlacementData = new StructurePlacementData()
			.setMirror(this.mirror)
			.setRotation(this.rotation)
			.setIgnoreEntities(this.ignoreEntities);
		if (this.integrity < 1.0F) {
			structurePlacementData.clearProcessors()
				.addProcessor(new BlockRotStructureProcessor(MathHelper.clamp(this.integrity, 0.0F, 1.0F)))
				.setRandom(createRandom(this.seed));
		}

		BlockPos blockPos = this.getPos().add(this.offset);
		template.place(world, blockPos, blockPos, structurePlacementData, createRandom(this.seed), 2);
	}

	public void unloadStructure() {
		if (this.templateName != null) {
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureTemplateManager structureTemplateManager = serverWorld.getStructureTemplateManager();
			structureTemplateManager.unloadTemplate(this.templateName);
		}
	}

	public boolean isStructureAvailable() {
		if (this.mode == StructureBlockMode.LOAD && !this.world.isClient && this.templateName != null) {
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureTemplateManager structureTemplateManager = serverWorld.getStructureTemplateManager();

			try {
				return structureTemplateManager.getTemplate(this.templateName).isPresent();
			} catch (InvalidIdentifierException var4) {
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean isPowered() {
		return this.powered;
	}

	public void setPowered(boolean powered) {
		this.powered = powered;
	}

	public boolean shouldShowAir() {
		return this.showAir;
	}

	public void setShowAir(boolean showAir) {
		this.showAir = showAir;
	}

	public boolean shouldShowBoundingBox() {
		return this.showBoundingBox;
	}

	public void setShowBoundingBox(boolean showBoundingBox) {
		this.showBoundingBox = showBoundingBox;
	}

	public static enum Action {
		UPDATE_DATA,
		SAVE_AREA,
		LOAD_AREA,
		SCAN_AREA;
	}
}
