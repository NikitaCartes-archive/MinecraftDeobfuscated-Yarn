package net.minecraft.command;

@FunctionalInterface
public interface ReturnValueConsumer {
	ReturnValueConsumer EMPTY = new ReturnValueConsumer() {
		@Override
		public void onResult(boolean bl, int i) {
		}

		public String toString() {
			return "<empty>";
		}
	};

	void onResult(boolean successful, int returnValue);

	default void onSuccess(int successful) {
		this.onResult(true, successful);
	}

	default void onFailure() {
		this.onResult(false, 0);
	}

	static ReturnValueConsumer chain(ReturnValueConsumer a, ReturnValueConsumer b) {
		if (a == EMPTY) {
			return b;
		} else {
			return b == EMPTY ? a : (successful, returnValue) -> {
				a.onResult(successful, returnValue);
				b.onResult(successful, returnValue);
			};
		}
	}
}
