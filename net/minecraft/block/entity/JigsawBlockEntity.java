/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.JigsawBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePoolBasedGenerator;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;

public class JigsawBlockEntity
extends BlockEntity {
    private Identifier name = new Identifier("empty");
    private Identifier target = new Identifier("empty");
    private Identifier pool = new Identifier("empty");
    private Joint joint = Joint.ROLLABLE;
    private String finalState = "minecraft:air";

    public JigsawBlockEntity(BlockEntityType<?> blockEntityType) {
        super(blockEntityType);
    }

    public JigsawBlockEntity() {
        this(BlockEntityType.JIGSAW);
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getName() {
        return this.name;
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getTarget() {
        return this.target;
    }

    @Environment(value=EnvType.CLIENT)
    public Identifier getPool() {
        return this.pool;
    }

    @Environment(value=EnvType.CLIENT)
    public String getFinalState() {
        return this.finalState;
    }

    @Environment(value=EnvType.CLIENT)
    public Joint getJoint() {
        return this.joint;
    }

    public void setAttachmentType(Identifier value) {
        this.name = value;
    }

    public void setTargetPool(Identifier target) {
        this.target = target;
    }

    public void setPool(Identifier pool) {
        this.pool = pool;
    }

    public void setFinalState(String finalState) {
        this.finalState = finalState;
    }

    public void setJoint(Joint joint) {
        this.joint = joint;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("name", this.name.toString());
        nbt.putString("target", this.target.toString());
        nbt.putString("pool", this.pool.toString());
        nbt.putString("final_state", this.finalState);
        nbt.putString("joint", this.joint.asString());
        return nbt;
    }

    @Override
    public void fromTag(BlockState state, NbtCompound tag) {
        super.fromTag(state, tag);
        this.name = new Identifier(tag.getString("name"));
        this.target = new Identifier(tag.getString("target"));
        this.pool = new Identifier(tag.getString("pool"));
        this.finalState = tag.getString("final_state");
        this.joint = Joint.byName(tag.getString("joint")).orElseGet(() -> JigsawBlock.getFacing(state).getAxis().isHorizontal() ? Joint.ALIGNED : Joint.ROLLABLE);
    }

    @Override
    @Nullable
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return new BlockEntityUpdateS2CPacket(this.pos, 12, this.toInitialChunkDataNbt());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.writeNbt(new NbtCompound());
    }

    public void generate(ServerWorld world, int maxDepth, boolean keepJigsaws) {
        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
        StructureManager structureManager = world.getStructureManager();
        StructureAccessor structureAccessor = world.getStructureAccessor();
        Random random = world.getRandom();
        BlockPos blockPos = this.getPos();
        ArrayList<PoolStructurePiece> list = Lists.newArrayList();
        Structure structure = new Structure();
        structure.saveFromWorld(world, blockPos, new BlockPos(1, 1, 1), false, null);
        SinglePoolElement structurePoolElement = new SinglePoolElement(structure);
        PoolStructurePiece poolStructurePiece = new PoolStructurePiece(structureManager, structurePoolElement, blockPos, 1, BlockRotation.NONE, new BlockBox(blockPos, blockPos));
        StructurePoolBasedGenerator.method_27230(world.getRegistryManager(), poolStructurePiece, maxDepth, PoolStructurePiece::new, chunkGenerator, structureManager, list, random);
        for (PoolStructurePiece poolStructurePiece2 : list) {
            poolStructurePiece2.generate((StructureWorldAccess)world, structureAccessor, chunkGenerator, random, BlockBox.infinite(), blockPos, keepJigsaws);
        }
    }

    public static enum Joint implements StringIdentifiable
    {
        ROLLABLE("rollable"),
        ALIGNED("aligned");

        private final String name;

        private Joint(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public static Optional<Joint> byName(String name) {
            return Arrays.stream(Joint.values()).filter(joint -> joint.asString().equals(name)).findFirst();
        }
    }
}

