package net.minecraft.entity.boss.dragon.phase;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;

public class PhaseType<T extends Phase> {
	private static PhaseType<?>[] types = new PhaseType[0];
	public static final PhaseType<HoldingPatternPhase> field_7069 = register(HoldingPatternPhase.class, "HoldingPattern");
	public static final PhaseType<StrafePlayerPhase> field_7076 = register(StrafePlayerPhase.class, "StrafePlayer");
	public static final PhaseType<LandingApproachPhase> field_7071 = register(LandingApproachPhase.class, "LandingApproach");
	public static final PhaseType<LandingPhase> field_7067 = register(LandingPhase.class, "Landing");
	public static final PhaseType<TakeoffPhase> field_7077 = register(TakeoffPhase.class, "Takeoff");
	public static final PhaseType<SittingFlamingPhase> field_7072 = register(SittingFlamingPhase.class, "SittingFlaming");
	public static final PhaseType<SittingScanningPhase> field_7081 = register(SittingScanningPhase.class, "SittingScanning");
	public static final PhaseType<SittingAttackingPhase> field_7073 = register(SittingAttackingPhase.class, "SittingAttacking");
	public static final PhaseType<ChargingPlayerPhase> field_7078 = register(ChargingPlayerPhase.class, "ChargingPlayer");
	public static final PhaseType<DyingPhase> field_7068 = register(DyingPhase.class, "Dying");
	public static final PhaseType<HoverPhase> field_7075 = register(HoverPhase.class, "Hover");
	private final Class<? extends Phase> phaseClass;
	private final int id;
	private final String name;

	private PhaseType(int i, Class<? extends Phase> class_, String string) {
		this.id = i;
		this.phaseClass = class_;
		this.name = string;
	}

	public Phase create(EnderDragonEntity enderDragonEntity) {
		try {
			Constructor<? extends Phase> constructor = this.getConstructor();
			return (Phase)constructor.newInstance(enderDragonEntity);
		} catch (Exception var3) {
			throw new Error(var3);
		}
	}

	protected Constructor<? extends Phase> getConstructor() throws NoSuchMethodException {
		return this.phaseClass.getConstructor(EnderDragonEntity.class);
	}

	public int getTypeId() {
		return this.id;
	}

	public String toString() {
		return this.name + " (#" + this.id + ")";
	}

	public static PhaseType<?> getFromId(int i) {
		return i >= 0 && i < types.length ? types[i] : field_7069;
	}

	public static int count() {
		return types.length;
	}

	private static <T extends Phase> PhaseType<T> register(Class<T> class_, String string) {
		PhaseType<T> phaseType = new PhaseType<>(types.length, class_, string);
		types = (PhaseType<?>[])Arrays.copyOf(types, types.length + 1);
		types[phaseType.getTypeId()] = phaseType;
		return phaseType;
	}
}
