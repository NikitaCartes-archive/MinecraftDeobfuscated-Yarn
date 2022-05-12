/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.structure.pool;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.block.JigsawBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.pool.EmptyPoolElement;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.StructureType;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class StructurePoolBasedGenerator {
    static final Logger LOGGER = LogUtils.getLogger();

    public static Optional<StructureType.StructurePosition> generate(StructureType.Context context, RegistryEntry<StructurePool> structurePool, Optional<Identifier> id, int i, BlockPos pos, boolean bl, Optional<Heightmap.Type> heightmapType, int j) {
        BlockPos blockPos;
        DynamicRegistryManager dynamicRegistryManager = context.dynamicRegistryManager();
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        StructureManager structureManager = context.structureManager();
        HeightLimitView heightLimitView = context.world();
        ChunkRandom chunkRandom = context.random();
        Registry<StructurePool> registry = dynamicRegistryManager.get(Registry.STRUCTURE_POOL_KEY);
        BlockRotation blockRotation = BlockRotation.random(chunkRandom);
        StructurePool structurePool2 = structurePool.value();
        StructurePoolElement structurePoolElement = structurePool2.getRandomElement(chunkRandom);
        if (structurePoolElement == EmptyPoolElement.INSTANCE) {
            return Optional.empty();
        }
        if (id.isPresent()) {
            Identifier identifier = id.get();
            Optional<BlockPos> optional = StructurePoolBasedGenerator.method_43566(structurePoolElement, identifier, pos, blockRotation, structureManager, chunkRandom);
            if (optional.isEmpty()) {
                LOGGER.error("No starting jigsaw {} found in start pool {}", (Object)identifier, (Object)structurePool.getKey().get().getValue());
                return Optional.empty();
            }
            blockPos = optional.get();
        } else {
            blockPos = pos;
        }
        BlockPos vec3i = blockPos.subtract(pos);
        BlockPos blockPos2 = pos.subtract(vec3i);
        PoolStructurePiece poolStructurePiece = new PoolStructurePiece(structureManager, structurePoolElement, blockPos2, structurePoolElement.getGroundLevelDelta(), blockRotation, structurePoolElement.getBoundingBox(structureManager, blockPos2, blockRotation));
        BlockBox blockBox = poolStructurePiece.getBoundingBox();
        int k = (blockBox.getMaxX() + blockBox.getMinX()) / 2;
        int l = (blockBox.getMaxZ() + blockBox.getMinZ()) / 2;
        int m = heightmapType.isPresent() ? pos.getY() + chunkGenerator.getHeightOnGround(k, l, heightmapType.get(), heightLimitView, context.noiseConfig()) : blockPos2.getY();
        int n = blockBox.getMinY() + poolStructurePiece.getGroundLevelDelta();
        poolStructurePiece.translate(0, m - n, 0);
        int o = m + vec3i.getY();
        return Optional.of(new StructureType.StructurePosition(new BlockPos(k, o, l), structurePiecesCollector -> {
            ArrayList<PoolStructurePiece> list = Lists.newArrayList();
            list.add(poolStructurePiece);
            if (i <= 0) {
                return;
            }
            Box box = new Box(k - j, o - j, l - j, k + j + 1, o + j + 1, l + j + 1);
            VoxelShape voxelShape = VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(box), VoxelShapes.cuboid(Box.from(blockBox)), BooleanBiFunction.ONLY_FIRST);
            StructurePoolBasedGenerator.generate(context.noiseConfig(), i, bl, chunkGenerator, structureManager, heightLimitView, chunkRandom, registry, poolStructurePiece, list, voxelShape);
            list.forEach(structurePiecesCollector::addPiece);
        }));
    }

    private static Optional<BlockPos> method_43566(StructurePoolElement structurePoolElement, Identifier identifier, BlockPos blockPos, BlockRotation blockRotation, StructureManager structureManager, ChunkRandom chunkRandom) {
        List<Structure.StructureBlockInfo> list = structurePoolElement.getStructureBlockInfos(structureManager, blockPos, blockRotation, chunkRandom);
        Optional<BlockPos> optional = Optional.empty();
        for (Structure.StructureBlockInfo structureBlockInfo : list) {
            Identifier identifier2 = Identifier.tryParse(structureBlockInfo.nbt.getString("name"));
            if (!identifier.equals(identifier2)) continue;
            optional = Optional.of(structureBlockInfo.pos);
            break;
        }
        return optional;
    }

    private static void generate(NoiseConfig noiseConfig, int maxSize, boolean modifyBoundingBox, ChunkGenerator chunkGenerator, StructureManager structureManager, HeightLimitView heightLimitView, AbstractRandom random, Registry<StructurePool> structurePoolRegistry, PoolStructurePiece firstPiece, List<PoolStructurePiece> pieces, VoxelShape pieceShape) {
        StructurePoolGenerator structurePoolGenerator = new StructurePoolGenerator(structurePoolRegistry, maxSize, chunkGenerator, structureManager, pieces, random);
        structurePoolGenerator.structurePieces.addLast(new ShapedPoolStructurePiece(firstPiece, new MutableObject<VoxelShape>(pieceShape), 0));
        while (!structurePoolGenerator.structurePieces.isEmpty()) {
            ShapedPoolStructurePiece shapedPoolStructurePiece = structurePoolGenerator.structurePieces.removeFirst();
            structurePoolGenerator.generatePiece(shapedPoolStructurePiece.piece, shapedPoolStructurePiece.pieceShape, shapedPoolStructurePiece.currentSize, modifyBoundingBox, heightLimitView, noiseConfig);
        }
    }

    public static boolean generate(ServerWorld world, RegistryEntry<StructurePool> structurePool, Identifier id, int i, BlockPos pos, boolean keepJigsaws) {
        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
        StructureManager structureManager = world.getStructureManager();
        StructureAccessor structureAccessor = world.getStructureAccessor();
        AbstractRandom abstractRandom = world.getRandom();
        StructureType.Context context = new StructureType.Context(world.getRegistryManager(), chunkGenerator, chunkGenerator.getBiomeSource(), world.getChunkManager().getNoiseConfig(), structureManager, world.getSeed(), new ChunkPos(pos), world, registryEntry -> true);
        Optional<StructureType.StructurePosition> optional = StructurePoolBasedGenerator.generate(context, structurePool, Optional.of(id), i, pos, false, Optional.empty(), 128);
        if (optional.isPresent()) {
            StructurePiecesCollector structurePiecesCollector = optional.get().generate();
            for (StructurePiece structurePiece : structurePiecesCollector.toList().pieces()) {
                if (!(structurePiece instanceof PoolStructurePiece)) continue;
                PoolStructurePiece poolStructurePiece = (PoolStructurePiece)structurePiece;
                poolStructurePiece.generate((StructureWorldAccess)world, structureAccessor, chunkGenerator, abstractRandom, BlockBox.infinite(), pos, keepJigsaws);
            }
            return true;
        }
        return false;
    }

    static final class StructurePoolGenerator {
        private final Registry<StructurePool> registry;
        private final int maxSize;
        private final ChunkGenerator chunkGenerator;
        private final StructureManager structureManager;
        private final List<? super PoolStructurePiece> children;
        private final AbstractRandom random;
        final Deque<ShapedPoolStructurePiece> structurePieces = Queues.newArrayDeque();

        StructurePoolGenerator(Registry<StructurePool> registry, int maxSize, ChunkGenerator chunkGenerator, StructureManager structureManager, List<? super PoolStructurePiece> children, AbstractRandom random) {
            this.registry = registry;
            this.maxSize = maxSize;
            this.chunkGenerator = chunkGenerator;
            this.structureManager = structureManager;
            this.children = children;
            this.random = random;
        }

        void generatePiece(PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int minY, boolean modifyBoundingBox, HeightLimitView world, NoiseConfig noiseConfig) {
            StructurePoolElement structurePoolElement = piece.getPoolElement();
            BlockPos blockPos = piece.getPos();
            BlockRotation blockRotation = piece.getRotation();
            StructurePool.Projection projection = structurePoolElement.getProjection();
            boolean bl = projection == StructurePool.Projection.RIGID;
            MutableObject<VoxelShape> mutableObject = new MutableObject<VoxelShape>();
            BlockBox blockBox = piece.getBoundingBox();
            int i = blockBox.getMinY();
            block0: for (Structure.StructureBlockInfo structureBlockInfo2 : structurePoolElement.getStructureBlockInfos(this.structureManager, blockPos, blockRotation, this.random)) {
                StructurePoolElement structurePoolElement2;
                MutableObject<Object> mutableObject2;
                Direction direction = JigsawBlock.getFacing(structureBlockInfo2.state);
                BlockPos blockPos2 = structureBlockInfo2.pos;
                BlockPos blockPos3 = blockPos2.offset(direction);
                int j = blockPos2.getY() - i;
                int k = -1;
                Identifier identifier = new Identifier(structureBlockInfo2.nbt.getString("pool"));
                Optional<StructurePool> optional = this.registry.getOrEmpty(identifier);
                if (!optional.isPresent() || optional.get().getElementCount() == 0 && !Objects.equals(identifier, StructurePools.EMPTY.getValue())) {
                    LOGGER.warn("Empty or non-existent pool: {}", (Object)identifier);
                    continue;
                }
                Identifier identifier2 = optional.get().getTerminatorsId();
                Optional<StructurePool> optional2 = this.registry.getOrEmpty(identifier2);
                if (!optional2.isPresent() || optional2.get().getElementCount() == 0 && !Objects.equals(identifier2, StructurePools.EMPTY.getValue())) {
                    LOGGER.warn("Empty or non-existent fallback pool: {}", (Object)identifier2);
                    continue;
                }
                boolean bl2 = blockBox.contains(blockPos3);
                if (bl2) {
                    mutableObject2 = mutableObject;
                    if (mutableObject.getValue() == null) {
                        mutableObject.setValue(VoxelShapes.cuboid(Box.from(blockBox)));
                    }
                } else {
                    mutableObject2 = pieceShape;
                }
                ArrayList<StructurePoolElement> list = Lists.newArrayList();
                if (minY != this.maxSize) {
                    list.addAll(optional.get().getElementIndicesInRandomOrder(this.random));
                }
                list.addAll(optional2.get().getElementIndicesInRandomOrder(this.random));
                Iterator iterator = list.iterator();
                while (iterator.hasNext() && (structurePoolElement2 = (StructurePoolElement)iterator.next()) != EmptyPoolElement.INSTANCE) {
                    for (BlockRotation blockRotation2 : BlockRotation.randomRotationOrder(this.random)) {
                        List<Structure.StructureBlockInfo> list2 = structurePoolElement2.getStructureBlockInfos(this.structureManager, BlockPos.ORIGIN, blockRotation2, this.random);
                        BlockBox blockBox2 = structurePoolElement2.getBoundingBox(this.structureManager, BlockPos.ORIGIN, blockRotation2);
                        int l = !modifyBoundingBox || blockBox2.getBlockCountY() > 16 ? 0 : list2.stream().mapToInt(structureBlockInfo -> {
                            if (!blockBox2.contains(structureBlockInfo.pos.offset(JigsawBlock.getFacing(structureBlockInfo.state)))) {
                                return 0;
                            }
                            Identifier identifier = new Identifier(structureBlockInfo.nbt.getString("pool"));
                            Optional<StructurePool> optional = this.registry.getOrEmpty(identifier);
                            Optional<Integer> optional2 = optional.flatMap(pool -> this.registry.getOrEmpty(pool.getTerminatorsId()));
                            int i = optional.map(pool -> pool.getHighestY(this.structureManager)).orElse(0);
                            int j = optional2.map(pool -> pool.getHighestY(this.structureManager)).orElse(0);
                            return Math.max(i, j);
                        }).max().orElse(0);
                        for (Structure.StructureBlockInfo structureBlockInfo22 : list2) {
                            int t;
                            int r;
                            int p;
                            if (!JigsawBlock.attachmentMatches(structureBlockInfo2, structureBlockInfo22)) continue;
                            BlockPos blockPos4 = structureBlockInfo22.pos;
                            BlockPos blockPos5 = blockPos3.subtract(blockPos4);
                            BlockBox blockBox3 = structurePoolElement2.getBoundingBox(this.structureManager, blockPos5, blockRotation2);
                            int m = blockBox3.getMinY();
                            StructurePool.Projection projection2 = structurePoolElement2.getProjection();
                            boolean bl3 = projection2 == StructurePool.Projection.RIGID;
                            int n = blockPos4.getY();
                            int o = j - n + JigsawBlock.getFacing(structureBlockInfo2.state).getOffsetY();
                            if (bl && bl3) {
                                p = i + o;
                            } else {
                                if (k == -1) {
                                    k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
                                }
                                p = k - n;
                            }
                            int q = p - m;
                            BlockBox blockBox4 = blockBox3.offset(0, q, 0);
                            BlockPos blockPos6 = blockPos5.add(0, q, 0);
                            if (l > 0) {
                                r = Math.max(l + 1, blockBox4.getMaxY() - blockBox4.getMinY());
                                blockBox4.encompass(new BlockPos(blockBox4.getMinX(), blockBox4.getMinY() + r, blockBox4.getMinZ()));
                            }
                            if (VoxelShapes.matchesAnywhere((VoxelShape)mutableObject2.getValue(), VoxelShapes.cuboid(Box.from(blockBox4).contract(0.25)), BooleanBiFunction.ONLY_SECOND)) continue;
                            mutableObject2.setValue(VoxelShapes.combine((VoxelShape)mutableObject2.getValue(), VoxelShapes.cuboid(Box.from(blockBox4)), BooleanBiFunction.ONLY_FIRST));
                            r = piece.getGroundLevelDelta();
                            int s = bl3 ? r - o : structurePoolElement2.getGroundLevelDelta();
                            PoolStructurePiece poolStructurePiece = new PoolStructurePiece(this.structureManager, structurePoolElement2, blockPos6, s, blockRotation2, blockBox4);
                            if (bl) {
                                t = i + j;
                            } else if (bl3) {
                                t = p + n;
                            } else {
                                if (k == -1) {
                                    k = this.chunkGenerator.getHeightOnGround(blockPos2.getX(), blockPos2.getZ(), Heightmap.Type.WORLD_SURFACE_WG, world, noiseConfig);
                                }
                                t = k + o / 2;
                            }
                            piece.addJunction(new JigsawJunction(blockPos3.getX(), t - j + r, blockPos3.getZ(), o, projection2));
                            poolStructurePiece.addJunction(new JigsawJunction(blockPos2.getX(), t - n + s, blockPos2.getZ(), -o, projection));
                            this.children.add(poolStructurePiece);
                            if (minY + 1 > this.maxSize) continue block0;
                            this.structurePieces.addLast(new ShapedPoolStructurePiece(poolStructurePiece, mutableObject2, minY + 1));
                            continue block0;
                        }
                    }
                }
            }
        }
    }

    static final class ShapedPoolStructurePiece {
        final PoolStructurePiece piece;
        final MutableObject<VoxelShape> pieceShape;
        final int currentSize;

        ShapedPoolStructurePiece(PoolStructurePiece piece, MutableObject<VoxelShape> pieceShape, int currentSize) {
            this.piece = piece;
            this.pieceShape = pieceShape;
            this.currentSize = currentSize;
        }
    }
}

