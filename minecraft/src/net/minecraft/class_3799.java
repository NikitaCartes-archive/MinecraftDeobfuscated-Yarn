package net.minecraft;

import com.mojang.bridge.Bridge;
import com.mojang.bridge.game.GameSession;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.Language;
import com.mojang.bridge.game.PerformanceMetrics;
import com.mojang.bridge.game.RunningGame;
import com.mojang.bridge.launcher.Launcher;
import com.mojang.bridge.launcher.SessionEventListener;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class class_3799 implements RunningGame {
	private final class_310 field_16757;
	@Nullable
	private final Launcher field_16755;
	private SessionEventListener field_16756 = SessionEventListener.NONE;

	public class_3799(class_310 arg) {
		this.field_16757 = arg;
		this.field_16755 = Bridge.getLauncher();
		if (this.field_16755 != null) {
			this.field_16755.registerGame(this);
		}
	}

	@Override
	public GameVersion getVersion() {
		return class_155.method_16673();
	}

	@Override
	public Language getSelectedLanguage() {
		return this.field_16757.method_1526().method_4669();
	}

	@Nullable
	@Override
	public GameSession getCurrentSession() {
		class_638 lv = this.field_16757.field_1687;
		return lv == null ? null : new class_3801(lv, this.field_16757.field_1724, this.field_16757.field_1724.field_3944);
	}

	@Override
	public PerformanceMetrics getPerformanceMetrics() {
		class_3517 lv = this.field_16757.method_1570();
		long l = 2147483647L;
		long m = -2147483648L;
		long n = 0L;

		for (long o : lv.method_15246()) {
			l = Math.min(l, o);
			m = Math.max(m, o);
			n += o;
		}

		return new class_3799.class_3800((int)l, (int)m, (int)(n / (long)lv.method_15246().length), lv.method_15246().length);
	}

	@Override
	public void setSessionEventListener(SessionEventListener sessionEventListener) {
		this.field_16756 = sessionEventListener;
	}

	public void method_16687() {
		this.field_16756.onStartGameSession(this.getCurrentSession());
	}

	public void method_16688() {
		this.field_16756.onLeaveGameSession(this.getCurrentSession());
	}

	@Environment(EnvType.CLIENT)
	static class class_3800 implements PerformanceMetrics {
		private final int field_16761;
		private final int field_16760;
		private final int field_16759;
		private final int field_16758;

		public class_3800(int i, int j, int k, int l) {
			this.field_16761 = i;
			this.field_16760 = j;
			this.field_16759 = k;
			this.field_16758 = l;
		}

		@Override
		public int getMinTime() {
			return this.field_16761;
		}

		@Override
		public int getMaxTime() {
			return this.field_16760;
		}

		@Override
		public int getAverageTime() {
			return this.field_16759;
		}

		@Override
		public int getSampleCount() {
			return this.field_16758;
		}
	}
}
