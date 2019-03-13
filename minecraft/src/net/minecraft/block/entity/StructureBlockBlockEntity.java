package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.enums.StructureBlockMode;
import net.minecraft.client.network.packet.BlockEntityUpdateS2CPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.processor.BlockRotStructureProcessor;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;

public class StructureBlockBlockEntity extends BlockEntity {
	private Identifier field_12102;
	private String author = "";
	private String metadata = "";
	private BlockPos field_12092 = new BlockPos(0, 1, 0);
	private BlockPos field_12100 = BlockPos.ORIGIN;
	private Mirror mirror = Mirror.NONE;
	private Rotation rotation = Rotation.ROT_0;
	private StructureBlockMode field_12094 = StructureBlockMode.field_12696;
	private boolean ignoreEntities = true;
	private boolean powered;
	private boolean showAir;
	private boolean showBoundingBox = true;
	private float integrity = 1.0F;
	private long seed;

	public StructureBlockBlockEntity() {
		super(BlockEntityType.STRUCTURE_BLOCK);
	}

	@Override
	public CompoundTag method_11007(CompoundTag compoundTag) {
		super.method_11007(compoundTag);
		compoundTag.putString("name", this.getStructureName());
		compoundTag.putString("author", this.author);
		compoundTag.putString("metadata", this.metadata);
		compoundTag.putInt("posX", this.field_12092.getX());
		compoundTag.putInt("posY", this.field_12092.getY());
		compoundTag.putInt("posZ", this.field_12092.getZ());
		compoundTag.putInt("sizeX", this.field_12100.getX());
		compoundTag.putInt("sizeY", this.field_12100.getY());
		compoundTag.putInt("sizeZ", this.field_12100.getZ());
		compoundTag.putString("rotation", this.rotation.toString());
		compoundTag.putString("mirror", this.mirror.toString());
		compoundTag.putString("mode", this.field_12094.toString());
		compoundTag.putBoolean("ignoreEntities", this.ignoreEntities);
		compoundTag.putBoolean("powered", this.powered);
		compoundTag.putBoolean("showair", this.showAir);
		compoundTag.putBoolean("showboundingbox", this.showBoundingBox);
		compoundTag.putFloat("integrity", this.integrity);
		compoundTag.putLong("seed", this.seed);
		return compoundTag;
	}

	@Override
	public void method_11014(CompoundTag compoundTag) {
		super.method_11014(compoundTag);
		this.setStructureName(compoundTag.getString("name"));
		this.author = compoundTag.getString("author");
		this.metadata = compoundTag.getString("metadata");
		int i = MathHelper.clamp(compoundTag.getInt("posX"), -32, 32);
		int j = MathHelper.clamp(compoundTag.getInt("posY"), -32, 32);
		int k = MathHelper.clamp(compoundTag.getInt("posZ"), -32, 32);
		this.field_12092 = new BlockPos(i, j, k);
		int l = MathHelper.clamp(compoundTag.getInt("sizeX"), 0, 32);
		int m = MathHelper.clamp(compoundTag.getInt("sizeY"), 0, 32);
		int n = MathHelper.clamp(compoundTag.getInt("sizeZ"), 0, 32);
		this.field_12100 = new BlockPos(l, m, n);

		try {
			this.rotation = Rotation.valueOf(compoundTag.getString("rotation"));
		} catch (IllegalArgumentException var11) {
			this.rotation = Rotation.ROT_0;
		}

		try {
			this.mirror = Mirror.valueOf(compoundTag.getString("mirror"));
		} catch (IllegalArgumentException var10) {
			this.mirror = Mirror.NONE;
		}

		try {
			this.field_12094 = StructureBlockMode.valueOf(compoundTag.getString("mode"));
		} catch (IllegalArgumentException var9) {
			this.field_12094 = StructureBlockMode.field_12696;
		}

		this.ignoreEntities = compoundTag.getBoolean("ignoreEntities");
		this.powered = compoundTag.getBoolean("powered");
		this.showAir = compoundTag.getBoolean("showair");
		this.showBoundingBox = compoundTag.getBoolean("showboundingbox");
		if (compoundTag.containsKey("integrity")) {
			this.integrity = compoundTag.getFloat("integrity");
		} else {
			this.integrity = 1.0F;
		}

		this.seed = compoundTag.getLong("seed");
		this.updateBlockMode();
	}

	private void updateBlockMode() {
		if (this.world != null) {
			BlockPos blockPos = this.method_11016();
			BlockState blockState = this.world.method_8320(blockPos);
			if (blockState.getBlock() == Blocks.field_10465) {
				this.world.method_8652(blockPos, blockState.method_11657(StructureBlock.field_11586, this.field_12094), 2);
			}
		}
	}

	@Nullable
	@Override
	public BlockEntityUpdateS2CPacket method_16886() {
		return new BlockEntityUpdateS2CPacket(this.field_11867, 7, this.method_16887());
	}

	@Override
	public CompoundTag method_16887() {
		return this.method_11007(new CompoundTag());
	}

	public boolean openScreen(PlayerEntity playerEntity) {
		if (!playerEntity.isCreativeLevelTwoOp()) {
			return false;
		} else {
			if (playerEntity.method_5770().isClient) {
				playerEntity.method_7303(this);
			}

			return true;
		}
	}

	public String getStructureName() {
		return this.field_12102 == null ? "" : this.field_12102.toString();
	}

	public boolean hasStructureName() {
		return this.field_12102 != null;
	}

	public void setStructureName(@Nullable String string) {
		this.method_11344(ChatUtil.isEmpty(string) ? null : Identifier.create(string));
	}

	public void method_11344(@Nullable Identifier identifier) {
		this.field_12102 = identifier;
	}

	public void setAuthor(LivingEntity livingEntity) {
		this.author = livingEntity.method_5477().getString();
	}

	@Environment(EnvType.CLIENT)
	public BlockPos method_11359() {
		return this.field_12092;
	}

	public void method_11378(BlockPos blockPos) {
		this.field_12092 = blockPos;
	}

	@Environment(EnvType.CLIENT)
	public BlockPos method_11349() {
		return this.field_12100;
	}

	public void method_11377(BlockPos blockPos) {
		this.field_12100 = blockPos;
	}

	@Environment(EnvType.CLIENT)
	public Mirror getMirror() {
		return this.mirror;
	}

	public void setMirror(Mirror mirror) {
		this.mirror = mirror;
	}

	@Environment(EnvType.CLIENT)
	public Rotation getRotation() {
		return this.rotation;
	}

	public void setRotation(Rotation rotation) {
		this.rotation = rotation;
	}

	@Environment(EnvType.CLIENT)
	public String getMetadata() {
		return this.metadata;
	}

	public void setMetadata(String string) {
		this.metadata = string;
	}

	public StructureBlockMode method_11374() {
		return this.field_12094;
	}

	public void method_11381(StructureBlockMode structureBlockMode) {
		this.field_12094 = structureBlockMode;
		BlockState blockState = this.world.method_8320(this.method_11016());
		if (blockState.getBlock() == Blocks.field_10465) {
			this.world.method_8652(this.method_11016(), blockState.method_11657(StructureBlock.field_11586, structureBlockMode), 2);
		}
	}

	@Environment(EnvType.CLIENT)
	public void cycleMode() {
		switch (this.method_11374()) {
			case field_12695:
				this.method_11381(StructureBlockMode.field_12697);
				break;
			case field_12697:
				this.method_11381(StructureBlockMode.field_12699);
				break;
			case field_12699:
				this.method_11381(StructureBlockMode.field_12696);
				break;
			case field_12696:
				this.method_11381(StructureBlockMode.field_12695);
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldIgnoreEntities() {
		return this.ignoreEntities;
	}

	public void setIgnoreEntities(boolean bl) {
		this.ignoreEntities = bl;
	}

	@Environment(EnvType.CLIENT)
	public float getIntegrity() {
		return this.integrity;
	}

	public void setIntegrity(float f) {
		this.integrity = f;
	}

	@Environment(EnvType.CLIENT)
	public long getSeed() {
		return this.seed;
	}

	public void setSeed(long l) {
		this.seed = l;
	}

	public boolean detectStructureSize() {
		if (this.field_12094 != StructureBlockMode.field_12695) {
			return false;
		} else {
			BlockPos blockPos = this.method_11016();
			int i = 80;
			BlockPos blockPos2 = new BlockPos(blockPos.getX() - 80, 0, blockPos.getZ() - 80);
			BlockPos blockPos3 = new BlockPos(blockPos.getX() + 80, 255, blockPos.getZ() + 80);
			List<StructureBlockBlockEntity> list = this.method_11369(blockPos2, blockPos3);
			List<StructureBlockBlockEntity> list2 = this.findCorners(list);
			if (list2.size() < 1) {
				return false;
			} else {
				MutableIntBoundingBox mutableIntBoundingBox = this.method_11355(blockPos, list2);
				if (mutableIntBoundingBox.maxX - mutableIntBoundingBox.minX > 1
					&& mutableIntBoundingBox.maxY - mutableIntBoundingBox.minY > 1
					&& mutableIntBoundingBox.maxZ - mutableIntBoundingBox.minZ > 1) {
					this.field_12092 = new BlockPos(
						mutableIntBoundingBox.minX - blockPos.getX() + 1, mutableIntBoundingBox.minY - blockPos.getY() + 1, mutableIntBoundingBox.minZ - blockPos.getZ() + 1
					);
					this.field_12100 = new BlockPos(
						mutableIntBoundingBox.maxX - mutableIntBoundingBox.minX - 1,
						mutableIntBoundingBox.maxY - mutableIntBoundingBox.minY - 1,
						mutableIntBoundingBox.maxZ - mutableIntBoundingBox.minZ - 1
					);
					this.markDirty();
					BlockState blockState = this.world.method_8320(blockPos);
					this.world.method_8413(blockPos, blockState, blockState, 3);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	private List<StructureBlockBlockEntity> findCorners(List<StructureBlockBlockEntity> list) {
		Predicate<StructureBlockBlockEntity> predicate = structureBlockBlockEntity -> structureBlockBlockEntity.field_12094 == StructureBlockMode.field_12699
				&& Objects.equals(this.field_12102, structureBlockBlockEntity.field_12102);
		return (List<StructureBlockBlockEntity>)list.stream().filter(predicate).collect(Collectors.toList());
	}

	private List<StructureBlockBlockEntity> method_11369(BlockPos blockPos, BlockPos blockPos2) {
		List<StructureBlockBlockEntity> list = Lists.<StructureBlockBlockEntity>newArrayList();

		for (BlockPos blockPos3 : BlockPos.iterateBoxPositions(blockPos, blockPos2)) {
			BlockState blockState = this.world.method_8320(blockPos3);
			if (blockState.getBlock() == Blocks.field_10465) {
				BlockEntity blockEntity = this.world.method_8321(blockPos3);
				if (blockEntity != null && blockEntity instanceof StructureBlockBlockEntity) {
					list.add((StructureBlockBlockEntity)blockEntity);
				}
			}
		}

		return list;
	}

	private MutableIntBoundingBox method_11355(BlockPos blockPos, List<StructureBlockBlockEntity> list) {
		MutableIntBoundingBox mutableIntBoundingBox;
		if (list.size() > 1) {
			BlockPos blockPos2 = ((StructureBlockBlockEntity)list.get(0)).method_11016();
			mutableIntBoundingBox = new MutableIntBoundingBox(blockPos2, blockPos2);
		} else {
			mutableIntBoundingBox = new MutableIntBoundingBox(blockPos, blockPos);
		}

		for (StructureBlockBlockEntity structureBlockBlockEntity : list) {
			BlockPos blockPos3 = structureBlockBlockEntity.method_11016();
			if (blockPos3.getX() < mutableIntBoundingBox.minX) {
				mutableIntBoundingBox.minX = blockPos3.getX();
			} else if (blockPos3.getX() > mutableIntBoundingBox.maxX) {
				mutableIntBoundingBox.maxX = blockPos3.getX();
			}

			if (blockPos3.getY() < mutableIntBoundingBox.minY) {
				mutableIntBoundingBox.minY = blockPos3.getY();
			} else if (blockPos3.getY() > mutableIntBoundingBox.maxY) {
				mutableIntBoundingBox.maxY = blockPos3.getY();
			}

			if (blockPos3.getZ() < mutableIntBoundingBox.minZ) {
				mutableIntBoundingBox.minZ = blockPos3.getZ();
			} else if (blockPos3.getZ() > mutableIntBoundingBox.maxZ) {
				mutableIntBoundingBox.maxZ = blockPos3.getZ();
			}
		}

		return mutableIntBoundingBox;
	}

	public boolean saveStructure() {
		return this.saveStructure(true);
	}

	public boolean saveStructure(boolean bl) {
		if (this.field_12094 == StructureBlockMode.field_12695 && !this.world.isClient && this.field_12102 != null) {
			BlockPos blockPos = this.method_11016().method_10081(this.field_12092);
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureManager structureManager = serverWorld.getStructureManager();

			Structure structure;
			try {
				structure = structureManager.method_15091(this.field_12102);
			} catch (InvalidIdentifierException var8) {
				return false;
			}

			structure.method_15174(this.world, blockPos, this.field_12100, !this.ignoreEntities, Blocks.field_10369);
			structure.setAuthor(this.author);
			if (bl) {
				try {
					return structureManager.method_15093(this.field_12102);
				} catch (InvalidIdentifierException var7) {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean loadStructure() {
		return this.loadStructure(true);
	}

	public boolean loadStructure(boolean bl) {
		if (this.field_12094 == StructureBlockMode.field_12697 && !this.world.isClient && this.field_12102 != null) {
			BlockPos blockPos = this.method_11016();
			BlockPos blockPos2 = blockPos.method_10081(this.field_12092);
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureManager structureManager = serverWorld.getStructureManager();

			Structure structure;
			try {
				structure = structureManager.method_15094(this.field_12102);
			} catch (InvalidIdentifierException var10) {
				return false;
			}

			if (structure == null) {
				return false;
			} else {
				if (!ChatUtil.isEmpty(structure.getAuthor())) {
					this.author = structure.getAuthor();
				}

				BlockPos blockPos3 = structure.method_15160();
				boolean bl2 = this.field_12100.equals(blockPos3);
				if (!bl2) {
					this.field_12100 = blockPos3;
					this.markDirty();
					BlockState blockState = this.world.method_8320(blockPos);
					this.world.method_8413(blockPos, blockState, blockState, 3);
				}

				if (bl && !bl2) {
					return false;
				} else {
					StructurePlacementData structurePlacementData = new StructurePlacementData()
						.setMirrored(this.mirror)
						.setRotation(this.rotation)
						.setIgnoreEntities(this.ignoreEntities)
						.setChunkPosition(null);
					if (this.integrity < 1.0F) {
						structurePlacementData.clearProcessors().method_16184(new BlockRotStructureProcessor(MathHelper.clamp(this.integrity, 0.0F, 1.0F))).setSeed(this.seed);
					}

					structure.method_15182(this.world, blockPos2, structurePlacementData);
					return true;
				}
			}
		} else {
			return false;
		}
	}

	public void unloadStructure() {
		if (this.field_12102 != null) {
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureManager structureManager = serverWorld.getStructureManager();
			structureManager.method_15087(this.field_12102);
		}
	}

	public boolean isStructureAvailable() {
		if (this.field_12094 == StructureBlockMode.field_12697 && !this.world.isClient && this.field_12102 != null) {
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureManager structureManager = serverWorld.getStructureManager();

			try {
				return structureManager.method_15094(this.field_12102) != null;
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

	public void setPowered(boolean bl) {
		this.powered = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldShowAir() {
		return this.showAir;
	}

	public void setShowAir(boolean bl) {
		this.showAir = bl;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldShowBoundingBox() {
		return this.showBoundingBox;
	}

	public void setShowBoundingBox(boolean bl) {
		this.showBoundingBox = bl;
	}

	public static enum Action {
		field_12108,
		field_12110,
		field_12109,
		field_12106;
	}
}
