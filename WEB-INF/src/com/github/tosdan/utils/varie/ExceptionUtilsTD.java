package com.github.tosdan.utils.varie;

public class ExceptionUtilsTD {

	public static RuntimeException reThrow( Throwable t ) {
		return UncheckedThrower.<RuntimeException, RuntimeException> reThrow0(t);
	}

	public static class UncheckedThrower {
		public static <R> R reThrow( Throwable t ) {
			return UncheckedThrower.<RuntimeException, R> reThrow0(t);
		}

		@SuppressWarnings( "unchecked" )
		private static <E extends Throwable, R> R reThrow0( Throwable t ) throws E {
			throw (E) t;
		}
	}

}
