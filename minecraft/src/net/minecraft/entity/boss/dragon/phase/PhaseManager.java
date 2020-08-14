package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PhaseManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final EnderDragonEntity dragon;
	private final Phase[] phases = new Phase[PhaseType.count()];
	private Phase current;

	public PhaseManager(EnderDragonEntity dragon) {
		this.dragon = dragon;
		this.setPhase(PhaseType.HOVER);
	}

	public void setPhase(PhaseType<?> type) {
		if (this.current == null || type != this.current.getType()) {
			if (this.current != null) {
				this.current.endPhase();
			}

			this.current = this.create((PhaseType<Phase>)type);
			if (!this.dragon.world.isClient) {
				this.dragon.getDataTracker().set(EnderDragonEntity.PHASE_TYPE, type.getTypeId());
			}

			LOGGER.debug("Dragon is now in phase {} on the {}", type, this.dragon.world.isClient ? "client" : "server");
			this.current.beginPhase();
		}
	}

	public Phase getCurrent() {
		return this.current;
	}

	public <T extends Phase> T create(PhaseType<T> type) {
		int i = type.getTypeId();
		if (this.phases[i] == null) {
			this.phases[i] = type.create(this.dragon);
		}

		return (T)this.phases[i];
	}
}
