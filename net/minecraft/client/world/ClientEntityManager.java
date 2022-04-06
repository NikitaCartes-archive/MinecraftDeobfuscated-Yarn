/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.world;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.entity.Entity;
import net.minecraft.util.annotation.Debug;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.entity.EntityChangeListener;
import net.minecraft.world.entity.EntityHandler;
import net.minecraft.world.entity.EntityIndex;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.entity.EntityLookup;
import net.minecraft.world.entity.EntityTrackingSection;
import net.minecraft.world.entity.EntityTrackingStatus;
import net.minecraft.world.entity.SectionedEntityCache;
import net.minecraft.world.entity.SimpleEntityLookup;
import org.slf4j.Logger;

public class ClientEntityManager<T extends EntityLike> {
    static final Logger LOGGER = LogUtils.getLogger();
    final EntityHandler<T> handler;
    final EntityIndex<T> index;
    final SectionedEntityCache<T> cache;
    private final LongSet tickingChunkSections = new LongOpenHashSet();
    private final EntityLookup<T> lookup;

    public ClientEntityManager(Class<T> entityClass, EntityHandler<T> handler) {
        this.index = new EntityIndex();
        this.cache = new SectionedEntityCache<T>(entityClass, pos -> this.tickingChunkSections.contains(pos) ? EntityTrackingStatus.TICKING : EntityTrackingStatus.TRACKED);
        this.handler = handler;
        this.lookup = new SimpleEntityLookup<T>(this.index, this.cache);
    }

    public void startTicking(ChunkPos pos) {
        long l = pos.toLong();
        this.tickingChunkSections.add(l);
        this.cache.getTrackingSections(l).forEach(sections -> {
            EntityTrackingStatus entityTrackingStatus = sections.swapStatus(EntityTrackingStatus.TICKING);
            if (!entityTrackingStatus.shouldTick()) {
                sections.stream().filter(e -> !e.isPlayer()).forEach(this.handler::startTicking);
            }
        });
    }

    public void stopTicking(ChunkPos pos) {
        long l = pos.toLong();
        this.tickingChunkSections.remove(l);
        this.cache.getTrackingSections(l).forEach(sections -> {
            EntityTrackingStatus entityTrackingStatus = sections.swapStatus(EntityTrackingStatus.TRACKED);
            if (entityTrackingStatus.shouldTick()) {
                sections.stream().filter(e -> !e.isPlayer()).forEach(this.handler::stopTicking);
            }
        });
    }

    public EntityLookup<T> getLookup() {
        return this.lookup;
    }

    public void addEntity(T entity) {
        this.index.add(entity);
        long l = ChunkSectionPos.toLong(entity.getBlockPos());
        EntityTrackingSection<T> entityTrackingSection = this.cache.getTrackingSection(l);
        entityTrackingSection.add(entity);
        entity.setChangeListener(new Listener(this, entity, l, entityTrackingSection));
        this.handler.create(entity);
        this.handler.startTracking(entity);
        if (entity.isPlayer() || entityTrackingSection.getStatus().shouldTick()) {
            this.handler.startTicking(entity);
        }
    }

    @Debug
    public int getEntityCount() {
        return this.index.size();
    }

    void removeIfEmpty(long packedChunkSection, EntityTrackingSection<T> entities) {
        if (entities.isEmpty()) {
            this.cache.removeSection(packedChunkSection);
        }
    }

    @Debug
    public String getDebugString() {
        return this.index.size() + "," + this.cache.sectionCount() + "," + this.tickingChunkSections.size();
    }

    static class Listener
    implements EntityChangeListener {
        private final T entity;
        private long lastSectionPos;
        private EntityTrackingSection<T> section;
        final /* synthetic */ ClientEntityManager manager;

        /*
         * WARNING - Possible parameter corruption
         * WARNING - void declaration
         */
        Listener(T entity, long pos, EntityTrackingSection<T> section) {
            void var3_3;
            this.manager = clientEntityManager;
            this.entity = entity;
            this.lastSectionPos = var3_3;
            this.section = section;
        }

        @Override
        public void updateEntityPosition() {
            BlockPos blockPos = this.entity.getBlockPos();
            long l = ChunkSectionPos.toLong(blockPos);
            if (l != this.lastSectionPos) {
                EntityTrackingStatus entityTrackingStatus = this.section.getStatus();
                if (!this.section.remove(this.entity)) {
                    LOGGER.warn("Entity {} wasn't found in section {} (moving to {})", this.entity, ChunkSectionPos.from(this.lastSectionPos), l);
                }
                this.manager.removeIfEmpty(this.lastSectionPos, this.section);
                EntityTrackingSection entityTrackingSection = this.manager.cache.getTrackingSection(l);
                entityTrackingSection.add(this.entity);
                this.section = entityTrackingSection;
                this.lastSectionPos = l;
                this.manager.handler.updateLoadStatus(this.entity);
                if (!this.entity.isPlayer()) {
                    boolean bl = entityTrackingStatus.shouldTick();
                    boolean bl2 = entityTrackingSection.getStatus().shouldTick();
                    if (bl && !bl2) {
                        this.manager.handler.stopTicking(this.entity);
                    } else if (!bl && bl2) {
                        this.manager.handler.startTicking(this.entity);
                    }
                }
            }
        }

        @Override
        public void remove(Entity.RemovalReason reason) {
            EntityTrackingStatus entityTrackingStatus;
            if (!this.section.remove(this.entity)) {
                LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", new Object[]{this.entity, ChunkSectionPos.from(this.lastSectionPos), reason});
            }
            if ((entityTrackingStatus = this.section.getStatus()).shouldTick() || this.entity.isPlayer()) {
                this.manager.handler.stopTicking(this.entity);
            }
            this.manager.handler.stopTracking(this.entity);
            this.manager.handler.destroy(this.entity);
            this.manager.index.remove(this.entity);
            this.entity.setChangeListener(NONE);
            this.manager.removeIfEmpty(this.lastSectionPos, this.section);
        }
    }
}

