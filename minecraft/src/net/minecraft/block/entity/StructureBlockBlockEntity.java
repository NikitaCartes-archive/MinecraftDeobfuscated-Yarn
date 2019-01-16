package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_3492;
import net.minecraft.class_3499;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StructureBlock;
import net.minecraft.block.enums.StructureMode;
import net.minecraft.client.network.packet.BlockEntityUpdateClientPacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sortme.structures.StructureManager;
import net.minecraft.sortme.structures.processor.BlockRotStructureProcessor;
import net.minecraft.util.ChatUtil;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableIntBoundingBox;

public class StructureBlockBlockEntity extends BlockEntity {
	private Identifier structureName;
	private String author = "";
	private String metadata = "";
	private BlockPos offset = new BlockPos(0, 1, 0);
	private BlockPos size = BlockPos.ORIGIN;
	private Mirror mirror = Mirror.NONE;
	private Rotation rotation = Rotation.ROT_0;
	private StructureMode mode = StructureMode.field_12696;
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
	public CompoundTag toTag(CompoundTag compoundTag) {
		super.toTag(compoundTag);
		compoundTag.putString("name", this.getStructureName());
		compoundTag.putString("author", this.author);
		compoundTag.putString("metadata", this.metadata);
		compoundTag.putInt("posX", this.offset.getX());
		compoundTag.putInt("posY", this.offset.getY());
		compoundTag.putInt("posZ", this.offset.getZ());
		compoundTag.putInt("sizeX", this.size.getX());
		compoundTag.putInt("sizeY", this.size.getY());
		compoundTag.putInt("sizeZ", this.size.getZ());
		compoundTag.putString("rotation", this.rotation.toString());
		compoundTag.putString("mirror", this.mirror.toString());
		compoundTag.putString("mode", this.mode.toString());
		compoundTag.putBoolean("ignoreEntities", this.ignoreEntities);
		compoundTag.putBoolean("powered", this.powered);
		compoundTag.putBoolean("showair", this.showAir);
		compoundTag.putBoolean("showboundingbox", this.showBoundingBox);
		compoundTag.putFloat("integrity", this.integrity);
		compoundTag.putLong("seed", this.seed);
		return compoundTag;
	}

	@Override
	public void fromTag(CompoundTag compoundTag) {
		super.fromTag(compoundTag);
		this.setStructureName(compoundTag.getString("name"));
		this.author = compoundTag.getString("author");
		this.metadata = compoundTag.getString("metadata");
		int i = MathHelper.clamp(compoundTag.getInt("posX"), -32, 32);
		int j = MathHelper.clamp(compoundTag.getInt("posY"), -32, 32);
		int k = MathHelper.clamp(compoundTag.getInt("posZ"), -32, 32);
		this.offset = new BlockPos(i, j, k);
		int l = MathHelper.clamp(compoundTag.getInt("sizeX"), 0, 32);
		int m = MathHelper.clamp(compoundTag.getInt("sizeY"), 0, 32);
		int n = MathHelper.clamp(compoundTag.getInt("sizeZ"), 0, 32);
		this.size = new BlockPos(l, m, n);

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
			this.mode = StructureMode.valueOf(compoundTag.getString("mode"));
		} catch (IllegalArgumentException var9) {
			this.mode = StructureMode.field_12696;
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
			BlockPos blockPos = this.getPos();
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.getBlock() == Blocks.field_10465) {
				this.world.setBlockState(blockPos, blockState.with(StructureBlock.field_11586, this.mode), 2);
			}
		}
	}

	@Nullable
	@Override
	public BlockEntityUpdateClientPacket toUpdatePacket() {
		return new BlockEntityUpdateClientPacket(this.pos, 7, this.toInitialChunkDataTag());
	}

	@Override
	public CompoundTag toInitialChunkDataTag() {
		return this.toTag(new CompoundTag());
	}

	public boolean openGui(PlayerEntity playerEntity) {
		if (!playerEntity.method_7338()) {
			return false;
		} else {
			if (playerEntity.getEntityWorld().isClient) {
				playerEntity.openStructureBlockGui(this);
			}

			return true;
		}
	}

	public String getStructureName() {
		return this.structureName == null ? "" : this.structureName.toString();
	}

	public boolean hasStructureName() {
		return this.structureName != null;
	}

	public void setStructureName(@Nullable String string) {
		this.setStructureName(ChatUtil.isEmpty(string) ? null : Identifier.create(string));
	}

	public void setStructureName(@Nullable Identifier identifier) {
		this.structureName = identifier;
	}

	public void setAuthor(LivingEntity livingEntity) {
		this.author = livingEntity.getName().getString();
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getOffset() {
		return this.offset;
	}

	public void setOffset(BlockPos blockPos) {
		this.offset = blockPos;
	}

	@Environment(EnvType.CLIENT)
	public BlockPos getSize() {
		return this.size;
	}

	public void setSize(BlockPos blockPos) {
		this.size = blockPos;
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

	public StructureMode getMode() {
		return this.mode;
	}

	public void setMode(StructureMode structureMode) {
		this.mode = structureMode;
		BlockState blockState = this.world.getBlockState(this.getPos());
		if (blockState.getBlock() == Blocks.field_10465) {
			this.world.setBlockState(this.getPos(), blockState.with(StructureBlock.field_11586, structureMode), 2);
		}
	}

	@Environment(EnvType.CLIENT)
	public void method_11380() {
		switch (this.getMode()) {
			case field_12695:
				this.setMode(StructureMode.field_12697);
				break;
			case field_12697:
				this.setMode(StructureMode.field_12699);
				break;
			case field_12699:
				this.setMode(StructureMode.field_12696);
				break;
			case field_12696:
				this.setMode(StructureMode.field_12695);
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

	public boolean method_11383() {
		if (this.mode != StructureMode.field_12695) {
			return false;
		} else {
			BlockPos blockPos = this.getPos();
			int i = 80;
			BlockPos blockPos2 = new BlockPos(blockPos.getX() - 80, 0, blockPos.getZ() - 80);
			BlockPos blockPos3 = new BlockPos(blockPos.getX() + 80, 255, blockPos.getZ() + 80);
			List<StructureBlockBlockEntity> list = this.method_11369(blockPos2, blockPos3);
			List<StructureBlockBlockEntity> list2 = this.method_11364(list);
			if (list2.size() < 1) {
				return false;
			} else {
				MutableIntBoundingBox mutableIntBoundingBox = this.method_11355(blockPos, list2);
				if (mutableIntBoundingBox.maxX - mutableIntBoundingBox.minX > 1
					&& mutableIntBoundingBox.maxY - mutableIntBoundingBox.minY > 1
					&& mutableIntBoundingBox.maxZ - mutableIntBoundingBox.minZ > 1) {
					this.offset = new BlockPos(
						mutableIntBoundingBox.minX - blockPos.getX() + 1, mutableIntBoundingBox.minY - blockPos.getY() + 1, mutableIntBoundingBox.minZ - blockPos.getZ() + 1
					);
					this.size = new BlockPos(
						mutableIntBoundingBox.maxX - mutableIntBoundingBox.minX - 1,
						mutableIntBoundingBox.maxY - mutableIntBoundingBox.minY - 1,
						mutableIntBoundingBox.maxZ - mutableIntBoundingBox.minZ - 1
					);
					this.markDirty();
					BlockState blockState = this.world.getBlockState(blockPos);
					this.world.updateListeners(blockPos, blockState, blockState, 3);
					return true;
				} else {
					return false;
				}
			}
		}
	}

	private List<StructureBlockBlockEntity> method_11364(List<StructureBlockBlockEntity> list) {
		Predicate<StructureBlockBlockEntity> predicate = structureBlockBlockEntity -> structureBlockBlockEntity.mode == StructureMode.field_12699
				&& Objects.equals(this.structureName, structureBlockBlockEntity.structureName);
		return (List<StructureBlockBlockEntity>)list.stream().filter(predicate).collect(Collectors.toList());
	}

	private List<StructureBlockBlockEntity> method_11369(BlockPos blockPos, BlockPos blockPos2) {
		List<StructureBlockBlockEntity> list = Lists.<StructureBlockBlockEntity>newArrayList();

		for (BlockPos blockPos3 : BlockPos.iterateBoxPositions(blockPos, blockPos2)) {
			BlockState blockState = this.world.getBlockState(blockPos3);
			if (blockState.getBlock() == Blocks.field_10465) {
				BlockEntity blockEntity = this.world.getBlockEntity(blockPos3);
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
			BlockPos blockPos2 = ((StructureBlockBlockEntity)list.get(0)).getPos();
			mutableIntBoundingBox = new MutableIntBoundingBox(blockPos2, blockPos2);
		} else {
			mutableIntBoundingBox = new MutableIntBoundingBox(blockPos, blockPos);
		}

		for (StructureBlockBlockEntity structureBlockBlockEntity : list) {
			BlockPos blockPos3 = structureBlockBlockEntity.getPos();
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

	public boolean method_11365() {
		return this.method_11366(true);
	}

	public boolean method_11366(boolean bl) {
		if (this.mode == StructureMode.field_12695 && !this.world.isClient && this.structureName != null) {
			BlockPos blockPos = this.getPos().add(this.offset);
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureManager structureManager = serverWorld.method_14183();

			class_3499 lv;
			try {
				lv = structureManager.method_15091(this.structureName);
			} catch (InvalidIdentifierException var8) {
				return false;
			}

			lv.method_15174(this.world, blockPos, this.size, !this.ignoreEntities, Blocks.field_10369);
			lv.method_15161(this.author);
			if (bl) {
				try {
					return structureManager.method_15093(this.structureName);
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

	public boolean method_11376() {
		return this.method_11368(true);
	}

	public boolean method_11368(boolean bl) {
		if (this.mode == StructureMode.field_12697 && !this.world.isClient && this.structureName != null) {
			BlockPos blockPos = this.getPos();
			BlockPos blockPos2 = blockPos.add(this.offset);
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureManager structureManager = serverWorld.method_14183();

			class_3499 lv;
			try {
				lv = structureManager.method_15094(this.structureName);
			} catch (InvalidIdentifierException var10) {
				return false;
			}

			if (lv == null) {
				return false;
			} else {
				if (!ChatUtil.isEmpty(lv.method_15181())) {
					this.author = lv.method_15181();
				}

				BlockPos blockPos3 = lv.method_15160();
				boolean bl2 = this.size.equals(blockPos3);
				if (!bl2) {
					this.size = blockPos3;
					this.markDirty();
					BlockState blockState = this.world.getBlockState(blockPos);
					this.world.updateListeners(blockPos, blockState, blockState, 3);
				}

				if (bl && !bl2) {
					return false;
				} else {
					class_3492 lv2 = new class_3492().method_15125(this.mirror).method_15123(this.rotation).method_15133(this.ignoreEntities).method_15130(null);
					if (this.integrity < 1.0F) {
						lv2.method_16183().method_16184(new BlockRotStructureProcessor(MathHelper.clamp(this.integrity, 0.0F, 1.0F))).method_15118(this.seed);
					}

					lv.method_15182(this.world, blockPos2, lv2);
					return true;
				}
			}
		} else {
			return false;
		}
	}

	public void method_11361() {
		if (this.structureName != null) {
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureManager structureManager = serverWorld.method_14183();
			structureManager.method_15087(this.structureName);
		}
	}

	public boolean method_11372() {
		if (this.mode == StructureMode.field_12697 && !this.world.isClient && this.structureName != null) {
			ServerWorld serverWorld = (ServerWorld)this.world;
			StructureManager structureManager = serverWorld.method_14183();

			try {
				return structureManager.method_15094(this.structureName) != null;
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
