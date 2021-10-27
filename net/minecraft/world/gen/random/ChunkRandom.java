/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world.gen.random;

import java.util.Random;
import java.util.function.LongFunction;
import net.minecraft.world.gen.random.AbstractRandom;
import net.minecraft.world.gen.random.AtomicSimpleRandom;
import net.minecraft.world.gen.random.RandomDeriver;
import net.minecraft.world.gen.random.Xoroshiro128PlusPlusRandom;

public class ChunkRandom
extends Random
implements AbstractRandom {
    private final AbstractRandom baseRandom;
    private int sampleCount;

    public ChunkRandom(AbstractRandom baseRandom) {
        super(0L);
        this.baseRandom = baseRandom;
    }

    public int getSampleCount() {
        return this.sampleCount;
    }

    @Override
    public AbstractRandom derive() {
        return this.baseRandom.derive();
    }

    @Override
    public RandomDeriver createRandomDeriver() {
        return this.baseRandom.createRandomDeriver();
    }

    @Override
    public int next(int count) {
        ++this.sampleCount;
        AbstractRandom abstractRandom = this.baseRandom;
        if (abstractRandom instanceof AtomicSimpleRandom) {
            AtomicSimpleRandom atomicSimpleRandom = (AtomicSimpleRandom)abstractRandom;
            return atomicSimpleRandom.next(count);
        }
        return (int)(this.baseRandom.nextLong() >>> 64 - count);
    }

    @Override
    public synchronized void setSeed(long l) {
        if (this.baseRandom == null) {
            return;
        }
        this.baseRandom.setSeed(l);
    }

    /**
     * Seeds the randomizer to create population features such as decorators and animals.
     * 
     * <p>This method takes in the world seed and the negative-most block coordinates of the
     * chunk. The coordinate pair provided is equivalent to (chunkX * 16, chunkZ * 16). The
     * three values are mixed together through some layers of hashing to produce the
     * population seed.
     * 
     * <p>This function has been proved to be reversible through some exploitation of the underlying
     * nextLong() weaknesses. It is also important to remember that since setSeed()
     * truncates the 16 upper bits of world seed, only the 48 lowest bits affect the population
     * seed output.
     */
    public long setPopulationSeed(long worldSeed, int blockX, int blockZ) {
        this.setSeed(worldSeed);
        long l = this.nextLong() | 1L;
        long m = this.nextLong() | 1L;
        long n = (long)blockX * l + (long)blockZ * m ^ worldSeed;
        this.setSeed(n);
        return n;
    }

    /**
     * Seeds the randomizer to generate a given feature.
     * 
     * The salt, in the form of {@code index + 10000 * step} assures that each feature is seeded
     * differently, making the decoration feel more random. Even though it does a good job
     * at doing so, many entropy issues arise from the salt being so small and result in
     * weird alignments between features that have an index close apart.
     * 
     * @param index the index of the feature in the feature list
     * @param step the generation step's ordinal for this feature
     * @param populationSeed the population seed computed in {@link #setPopulationSeed(long, int, int)}
     */
    public void setDecoratorSeed(long populationSeed, int index, int step) {
        long l = populationSeed + (long)index + (long)(10000 * step);
        this.setSeed(l);
    }

    /**
     * Seeds the randomizer to generate larger features such as caves, ravines, mineshafts
     * and strongholds. It is also used to initiate structure start behavior such as rotation.
     * 
     * <p>Similar to the population seed, only the 48 lowest bits of the world seed affect the
     * output since it the upper 16 bits are truncated in the setSeed() call.
     */
    public void setCarverSeed(long worldSeed, int chunkX, int chunkZ) {
        this.setSeed(worldSeed);
        long l = this.nextLong();
        long m = this.nextLong();
        long n = (long)chunkX * l ^ (long)chunkZ * m ^ worldSeed;
        this.setSeed(n);
    }

    /**
     * Seeds the randomizer to determine the start position of structure features such as
     * temples, monuments and buried treasures within a region.
     * 
     * <p>The region coordinates pair corresponds to the coordinates of the region the seeded
     * chunk lies in. For example, a swamp hut region is 32 by 32 chunks meaning that all
     * chunks that lie within that region get seeded the same way.
     * 
     * <p>Similarly, the upper 16 bits of world seed also do not affect the region seed because
     * they get truncated in the setSeed() call.
     */
    public void setRegionSeed(long worldSeed, int regionX, int regionZ, int salt) {
        long l = (long)regionX * 341873128712L + (long)regionZ * 132897987541L + worldSeed + (long)salt;
        this.setSeed(l);
    }

    public static Random getSlimeRandom(int chunkX, int chunkZ, long worldSeed, long scrambler) {
        return new Random(worldSeed + (long)(chunkX * chunkX * 4987142) + (long)(chunkX * 5947611) + (long)(chunkZ * chunkZ) * 4392871L + (long)(chunkZ * 389711) ^ scrambler);
    }

    public static enum RandomProvider {
        LEGACY(AtomicSimpleRandom::new),
        XOROSHIRO(Xoroshiro128PlusPlusRandom::new);

        private final LongFunction<AbstractRandom> provider;

        private RandomProvider(LongFunction<AbstractRandom> provider) {
            this.provider = provider;
        }

        public AbstractRandom create(long seed) {
            return this.provider.apply(seed);
        }
    }
}

