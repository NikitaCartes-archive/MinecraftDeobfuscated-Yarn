package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PhaseManager {
	private static final Logger LOGGER = LogManager.getLogger();
	private final EnderDragonEntity dragon;
	private final Phase[] phases = new Phase[PhaseType.count()];
	private Phase current;

	public PhaseManager(EnderDragonEntity enderDragonEntity) {
		this.dragon = enderDragonEntity;
		this.setPhase(PhaseType.HOVER);
	}

	public void setPhase(PhaseType<?> phaseType) {
		if (this.current == null || phaseType != this.current.method_6849()) {
			if (this.current != null) {
				this.current.endPhase();
			}

			this.current = this.create((PhaseType<Phase>)phaseType);
			if (!this.dragon.field_6002.isClient) {
				this.dragon.method_5841().set(EnderDragonEntity.field_7013, phaseType.getTypeId());
			}

			LOGGER.debug("Dragon is now in phase {} on the {}", phaseType, this.dragon.field_6002.isClient ? "client" : "server");
			this.current.beginPhase();
		}
	}

	public Phase getCurrent() {
		return this.current;
	}

	public <T extends Phase> T create(PhaseType<T> phaseType) {
		int i = phaseType.getTypeId();
		if (this.phases[i] == null) {
			this.phases[i] = phaseType.create(this.dragon);
		}

		return (T)this.phases[i];
	}
}
