package net.minecraft.client.gl;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.lwjgl.opengl.ARBTimerQuery;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL33;

@Environment(EnvType.CLIENT)
public class GlTimer {
	private int queryId;

	public static Optional<GlTimer> getInstance() {
		return GlTimer.InstanceHolder.INSTANCE;
	}

	public void beginProfile() {
		RenderSystem.assertOnRenderThread();
		if (this.queryId != 0) {
			throw new IllegalStateException("Current profile not ended");
		} else {
			this.queryId = GL32C.glGenQueries();
			GL32C.glBeginQuery(GL33.GL_TIME_ELAPSED, this.queryId);
		}
	}

	public GlTimer.Query endProfile() {
		RenderSystem.assertOnRenderThread();
		if (this.queryId == 0) {
			throw new IllegalStateException("endProfile called before beginProfile");
		} else {
			GL32C.glEndQuery(GL33.GL_TIME_ELAPSED);
			GlTimer.Query query = new GlTimer.Query(this.queryId);
			this.queryId = 0;
			return query;
		}
	}

	@Environment(EnvType.CLIENT)
	static class InstanceHolder {
		static final Optional<GlTimer> INSTANCE = Optional.ofNullable(create());

		private InstanceHolder() {
		}

		@Nullable
		private static GlTimer create() {
			return !GL.getCapabilities().GL_ARB_timer_query ? null : new GlTimer();
		}
	}

	@Environment(EnvType.CLIENT)
	public static class Query {
		private static final long MISSING = 0L;
		private static final long CLOSED = -1L;
		private final int queryId;
		private long result;

		Query(int queryId) {
			this.queryId = queryId;
		}

		public void close() {
			RenderSystem.assertOnRenderThread();
			if (this.result == 0L) {
				this.result = -1L;
				GL32C.glDeleteQueries(this.queryId);
			}
		}

		public boolean isResultAvailable() {
			RenderSystem.assertOnRenderThread();
			if (this.result != 0L) {
				return true;
			} else if (1 == GL32C.glGetQueryObjecti(this.queryId, GL15.GL_QUERY_RESULT_AVAILABLE)) {
				this.result = ARBTimerQuery.glGetQueryObjecti64(this.queryId, GL15.GL_QUERY_RESULT);
				GL32C.glDeleteQueries(this.queryId);
				return true;
			} else {
				return false;
			}
		}

		public long queryResult() {
			RenderSystem.assertOnRenderThread();
			if (this.result == 0L) {
				this.result = ARBTimerQuery.glGetQueryObjecti64(this.queryId, GL15.GL_QUERY_RESULT);
				GL32C.glDeleteQueries(this.queryId);
			}

			return this.result;
		}
	}
}
