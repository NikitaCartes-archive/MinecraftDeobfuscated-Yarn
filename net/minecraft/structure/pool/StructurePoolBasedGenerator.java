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
import java.util.Optional;
import net.minecraft.block.JigsawBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.JigsawJunction;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePiecesCollector;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
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
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.noise.NoiseConfig;
import net.minecraft.world.gen.structure.Structure;
import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

public class StructurePoolBasedGenerator {
    static final Logger LOGGER = LogUtils.getLogger();

    public static Optional<Structure.StructurePosition> generate(Structure.Context context, RegistryEntry<StructurePool> structurePool, Optional<Identifier> id, int size, BlockPos pos, boolean useExpansionHack, Optional<Heightmap.Type> projectStartToHeightmap, int maxDistanceFromCenter) {
        BlockPos blockPos;
        DynamicRegistryManager dynamicRegistryManager = context.dynamicRegistryManager();
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        StructureTemplateManager structureTemplateManager = context.structureTemplateManager();
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
            Optional<BlockPos> optional = StructurePoolBasedGenerator.findStartingJigsawPos(structurePoolElement, identifier, pos, blockRotation, structureTemplateManager, chunkRandom);
            if (optional.isEmpty()) {
                LOGGER.error("No starting jigsaw {} found in start pool {}", (Object)identifier, (Object)structurePool.getKey().map(key -> key.getValue().toString()).orElse("<unregistered>"));
                return Optional.empty();
            }
            blockPos = optional.get();
        } else {
            blockPos = pos;
        }
        BlockPos vec3i = blockPos.subtract(pos);
        BlockPos blockPos2 = pos.subtract(vec3i);
        PoolStructurePiece poolStructurePiece = new PoolStructurePiece(structureTemplateManager, structurePoolElement, blockPos2, structurePoolElement.getGroundLevelDelta(), blockRotation, structurePoolElement.getBoundingBox(structureTemplateManager, blockPos2, blockRotation));
        BlockBox blockBox = poolStructurePiece.getBoundingBox();
        int i = (blockBox.getMaxX() + blockBox.getMinX()) / 2;
        int j = (blockBox.getMaxZ() + blockBox.getMinZ()) / 2;
        int k = projectStartToHeightmap.isPresent() ? pos.getY() + chunkGenerator.getHeightOnGround(i, j, projectStartToHeightmap.get(), heightLimitView, context.noiseConfig()) : blockPos2.getY();
        int l = blockBox.getMinY() + poolStructurePiece.getGroundLevelDelta();
        poolStructurePiece.translate(0, k - l, 0);
        int m = k + vec3i.getY();
        return Optional.of(new Structure.StructurePosition(new BlockPos(i, m, j), collector -> {
            ArrayList<PoolStructurePiece> list = Lists.newArrayList();
            list.add(poolStructurePiece);
            if (size <= 0) {
                return;
            }
            Box box = new Box(i - maxDistanceFromCenter, m - maxDistanceFromCenter, j - maxDistanceFromCenter, i + maxDistanceFromCenter + 1, m + maxDistanceFromCenter + 1, j + maxDistanceFromCenter + 1);
            VoxelShape voxelShape = VoxelShapes.combineAndSimplify(VoxelShapes.cuboid(box), VoxelShapes.cuboid(Box.from(blockBox)), BooleanBiFunction.ONLY_FIRST);
            StructurePoolBasedGenerator.generate(context.noiseConfig(), size, useExpansionHack, chunkGenerator, structureTemplateManager, heightLimitView, chunkRandom, registry, poolStructurePiece, list, voxelShape);
            list.forEach(collector::addPiece);
        }));
    }

    private static Optional<BlockPos> findStartingJigsawPos(StructurePoolElement pool, Identifier id, BlockPos pos, BlockRotation rotation, StructureTemplateManager structureManager, ChunkRandom random) {
        List<StructureTemplate.StructureBlockInfo> list = pool.getStructureBlockInfos(structureManager, pos, rotation, random);
        Optional<BlockPos> optional = Optional.empty();
        for (StructureTemplate.StructureBlockInfo structureBlockInfo : list) {
            Identifier identifier = Identifier.tryParse(structureBlockInfo.nbt.getString("name"));
            if (!id.equals(identifier)) continue;
            optional = Optional.of(structureBlockInfo.pos);
            break;
        }
        return optional;
    }

    private static void generate(NoiseConfig noiseConfig, int maxSize, boolean modifyBoundingBox, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, HeightLimitView heightLimitView, Random random, Registry<StructurePool> structurePoolRegistry, PoolStructurePiece firstPiece, List<PoolStructurePiece> pieces, VoxelShape pieceShape) {
        StructurePoolGenerator structurePoolGenerator = new StructurePoolGenerator(structurePoolRegistry, maxSize, chunkGenerator, structureTemplateManager, pieces, random);
        structurePoolGenerator.structurePieces.addLast(new ShapedPoolStructurePiece(firstPiece, new MutableObject<VoxelShape>(pieceShape), 0));
        while (!structurePoolGenerator.structurePieces.isEmpty()) {
            ShapedPoolStructurePiece shapedPoolStructurePiece = structurePoolGenerator.structurePieces.removeFirst();
            structurePoolGenerator.generatePiece(shapedPoolStructurePiece.piece, shapedPoolStructurePiece.pieceShape, shapedPoolStructurePiece.currentSize, modifyBoundingBox, heightLimitView, noiseConfig);
        }
    }

    public static boolean generate(ServerWorld world, RegistryEntry<StructurePool> structurePool, Identifier id, int size, BlockPos pos, boolean keepJigsaws) {
        ChunkGenerator chunkGenerator = world.getChunkManager().getChunkGenerator();
        StructureTemplateManager structureTemplateManager = world.getStructureTemplateManager();
        StructureAccessor structureAccessor = world.getStructureAccessor();
        Random random = world.getRandom();
        Structure.Context context = new Structure.Context(world.getRegistryManager(), chunkGenerator, chunkGenerator.getBiomeSource(), world.getChunkManager().getNoiseConfig(), structureTemplateManager, world.getSeed(), new ChunkPos(pos), world, biome -> true);
        Optional<Structure.StructurePosition> optional = StructurePoolBasedGenerator.generate(context, structurePool, Optional.of(id), size, pos, false, Optional.empty(), 128);
        if (optional.isPresent()) {
            StructurePiecesCollector structurePiecesCollector = optional.get().generate();
            for (StructurePiece structurePiece : structurePiecesCollector.toList().pieces()) {
                if (!(structurePiece instanceof PoolStructurePiece)) continue;
                PoolStructurePiece poolStructurePiece = (PoolStructurePiece)structurePiece;
                poolStructurePiece.generate((StructureWorldAccess)world, structureAccessor, chunkGenerator, random, BlockBox.infinite(), pos, keepJigsaws);
            }
            return true;
        }
        return false;
    }

    static final class StructurePoolGenerator {
        private final Registry<StructurePool> registry;
        private final int maxSize;
        private final ChunkGenerator chunkGenerator;
        private final StructureTemplateManager structureTemplateManager;
        private final List<? super PoolStructurePiece> children;
        private final Random random;
        final Deque<ShapedPoolStructurePiece> structurePieces = Queues.newArrayDeque();

        StructurePoolGenerator(Registry<StructurePool> registry, int maxSize, ChunkGenerator chunkGenerator, StructureTemplateManager structureTemplateManager, List<? super PoolStructurePiece> children, Random random) {
            this.registry = registry;
            this.maxSize = maxSize;
            this.chunkGenerator = chunkGenerator;
            this.structureTemplateManager = structureTemplateManager;
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
            block0: for (StructureTemplate.StructureBlockInfo structureBlockInfo : structurePoolElement.getStructureBlockInfos(this.structureTemplateManager, blockPos, blockRotation, this.random)) {
                StructurePoolElement structurePoolElement2;
                MutableObject<Object> mutableObject2;
                Direction direction = JigsawBlock.getFacing(structureBlockInfo.state);
                BlockPos blockPos2 = structureBlockInfo.pos;
                BlockPos blockPos3 = blockPos2.offset(direction);
                int j = blockPos2.getY() - i;
                int k = -1;
                RegistryKey<StructurePool> registryKey = StructurePoolGenerator.getPoolKey(structureBlockInfo);
                Optional<RegistryEntry.Reference<StructurePool>> optional = this.registry.getEntry(registryKey);
                if (optional.isEmpty()) {
                    LOGGER.warn("Empty or non-existent pool: {}", (Object)registryKey.getValue());
                    continue;
                }
                RegistryEntry registryEntry = optional.get();
                if (((StructurePool)registryEntry.value()).getElementCount() == 0 && !registryEntry.matchesKey(StructurePools.EMPTY)) {
                    LOGGER.warn("Empty or non-existent pool: {}", (Object)registryKey.getValue());
                    continue;
                }
                RegistryEntry<StructurePool> registryEntry2 = ((StructurePool)registryEntry.value()).getFallback();
                if (registryEntry2.value().getElementCount() == 0 && !registryEntry2.matchesKey(StructurePools.EMPTY)) {
                    LOGGER.warn("Empty or non-existent fallback pool: {}", (Object)registryEntry2.getKey().map(key -> key.getValue().toString()).orElse("<unregistered>"));
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
                    list.addAll(((StructurePool)registryEntry.value()).getElementIndicesInRandomOrder(this.random));
                }
                list.addAll(registryEntry2.value().getElementIndicesInRandomOrder(this.random));
                Iterator iterator = list.iterator();
                while (iterator.hasNext() && (structurePoolElement2 = (StructurePoolElement)iterator.next()) != EmptyPoolElement.INSTANCE) {
                    for (BlockRotation blockRotation2 : BlockRotation.randomRotationOrder(this.random)) {
                        List<StructureTemplate.StructureBlockInfo> list2 = structurePoolElement2.getStructureBlockInfos(this.structureTemplateManager, BlockPos.ORIGIN, blockRotation2, this.random);
                        BlockBox blockBox2 = structurePoolElement2.getBoundingBox(this.structureTemplateManager, BlockPos.ORIGIN, blockRotation2);
                        int l = !modifyBoundingBox || blockBox2.getBlockCountY() > 16 ? 0 : list2.stream().mapToInt(blockInfo -> {
                            if (!blockBox2.contains(blockInfo.pos.offset(JigsawBlock.getFacing(blockInfo.state)))) {
                                return 0;
                            }
                            RegistryKey<StructurePool> registryKey = StructurePoolGenerator.getPoolKey(blockInfo);
                            Optional<RegistryEntry.Reference<StructurePool>> optional = this.registry.getEntry(registryKey);
                            Optional<RegistryEntry> optional2 = optional.map(entry -> ((StructurePool)entry.value()).getFallback());
                            int i = optional.map(entry -> ((StructurePool)entry.value()).getHighestY(this.structureTemplateManager)).orElse(0);
                            int j = optional2.map(entry -> ((StructurePool)entry.value()).getHighestY(this.structureTemplateManager)).orElse(0);
                            return Math.max(i, j);
                        }).max().orElse(0);
                        for (StructureTemplate.StructureBlockInfo structureBlockInfo2 : list2) {
                            int t;
                            int r;
                            int p;
                            if (!JigsawBlock.attachmentMatches(structureBlockInfo, structureBlockInfo2)) continue;
                            BlockPos blockPos4 = structureBlockInfo2.pos;
                            BlockPos blockPos5 = blockPos3.subtract(blockPos4);
                            BlockBox blockBox3 = structurePoolElement2.getBoundingBox(this.structureTemplateManager, blockPos5, blockRotation2);
                            int m = blockBox3.getMinY();
                            StructurePool.Projection projection2 = structurePoolElement2.getProjection();
                            boolean bl3 = projection2 == StructurePool.Projection.RIGID;
                            int n = blockPos4.getY();
                            int o = j - n + JigsawBlock.getFacing(structureBlockInfo.state).getOffsetY();
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
                            PoolStructurePiece poolStructurePiece = new PoolStructurePiece(this.structureTemplateManager, structurePoolElement2, blockPos6, s, blockRotation2, blockBox4);
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

        private static RegistryKey<StructurePool> getPoolKey(StructureTemplate.StructureBlockInfo blockInfo) {
            return RegistryKey.of(Registry.STRUCTURE_POOL_KEY, new Identifier(blockInfo.nbt.getString("pool")));
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

