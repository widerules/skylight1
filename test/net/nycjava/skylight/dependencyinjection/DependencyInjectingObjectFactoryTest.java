package net.nycjava.skylight.dependencyinjection;

import junit.framework.TestCase;

public class DependencyInjectingObjectFactoryTest extends TestCase {
	static interface Y {
		int square(int aValue);
	}

	static class X implements Y {
		static X lastXCreated;

		public X() {
			lastXCreated = this;
		}

		@Dependency
		private Y foo;

		private Y bar;

		@Override
		public int square(int aValue) {
			return aValue * aValue;
		}
	}

	public void testRegisterImplementationClass() {
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		dependencyInjectingObjectFactory.registerImplementationClass(Y.class, X.class);
		Y priorLastXCreated = X.lastXCreated;
		Y y = dependencyInjectingObjectFactory.getObject(Y.class);
		assertSame(priorLastXCreated, X.lastXCreated);
		assertEquals(100, y.square(10));
		assertNotSame(priorLastXCreated, X.lastXCreated);
		assertNotNull(X.lastXCreated.foo);
		assertNull(X.lastXCreated.bar);
	}

	public void testRegisterSingletonImplementationClass() {
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		dependencyInjectingObjectFactory.registerSingletonImplementationClass(Y.class, X.class);
		Y priorLastXCreated = X.lastXCreated;
		Y y = dependencyInjectingObjectFactory.getObject(Y.class);
		assertSame(priorLastXCreated, X.lastXCreated);
		assertEquals(100, y.square(10));
		assertNotSame(priorLastXCreated, X.lastXCreated);
		assertNotNull(X.lastXCreated.foo);
		assertNull(X.lastXCreated.bar);
		Y moreRecentPriorLastXCreated = X.lastXCreated;
		dependencyInjectingObjectFactory.getObject(Y.class);
		assertSame(moreRecentPriorLastXCreated, X.lastXCreated);
	}

	public void testRegisterImplementationObject() {
		DependencyInjectingObjectFactory dependencyInjectingObjectFactory = new DependencyInjectingObjectFactory();
		X x = new X();
		dependencyInjectingObjectFactory.registerImplementationObject(Y.class, x);
		assertNull(x.foo);
		assertNull(x.bar);
		Y y = dependencyInjectingObjectFactory.getObject(Y.class);
		assertNull(x.foo);
		assertNull(x.bar);
		assertEquals(100, y.square(10));
		assertNotNull(x.foo);
		assertNull(x.bar);
	}
}
