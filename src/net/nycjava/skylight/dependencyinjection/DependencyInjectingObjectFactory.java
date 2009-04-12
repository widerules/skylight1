package net.nycjava.skylight.dependencyinjection;

import static java.lang.String.format;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class DependencyInjectingObjectFactory {
	private interface ObjectSource<T> {
		T getObject();
	}

	private class ImplementationClassObjectSource<T, S extends T> implements ObjectSource<T> {
		private Class<S> clazz;

		ImplementationClassObjectSource(Class<S> anInterface) {
			clazz = anInterface;
		}

		@Override
		public T getObject() {
			final T object;
			try {
				object = clazz.newInstance();
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			}
			injectDependencies(object);
			return object;
		}
	}

	private class ImplementationClassSingletonObjectSource<T, S extends T> implements ObjectSource<T> {
		private Class<S> clazz;

		private S singleObject;

		ImplementationClassSingletonObjectSource(Class<S> anInterface) {
			clazz = anInterface;
		}

		@Override
		public T getObject() {
			if (singleObject == null) {
				try {
					singleObject = clazz.newInstance();
				} catch (IllegalAccessException e) {
					throw new RuntimeException(e);
				} catch (InstantiationException e) {
					throw new RuntimeException(e);
				}
				injectDependencies(singleObject);
			}
			return singleObject;
		}
	}

	private class SingletonObjectSource<T> implements ObjectSource<T> {
		private T singleObject;

		private boolean dependenciesInjected;

		SingletonObjectSource(T aSingleObject) {
			singleObject = aSingleObject;
		}

		@Override
		public T getObject() {
			if (!dependenciesInjected) {
				injectDependencies(singleObject);
				dependenciesInjected = true;
			}
			return singleObject;
		}
	}

	private Map<Class<?>, ObjectSource<?>> implementationSources = new HashMap<Class<?>, ObjectSource<?>>();

	public <T, S extends T> void registerImplementationClass(Class<T> anInterface, Class<S> anImplementationClass) {
		checkInterfaceNotAlreadyRegistered(anInterface);
		implementationSources.put(anInterface, new ImplementationClassObjectSource<T, S>(anImplementationClass));
	}

	public <T, S extends T> void registerSingletonImplementationClass(Class<T> anInterface,
			Class<S> anImplementationClass) {
		checkInterfaceNotAlreadyRegistered(anInterface);
		implementationSources.put(anInterface,
				new ImplementationClassSingletonObjectSource<T, S>(anImplementationClass));
	}

	public <T, S extends T> void registerImplementationObject(Class<T> anInterface, S anImplementationObject) {
		checkInterfaceNotAlreadyRegistered(anInterface);
		implementationSources.put(anInterface, new SingletonObjectSource<T>(anImplementationObject));
	}

	public <T> T getObject(Class<T> anInteface) {
		if (implementationSources.containsKey(anInteface)) {
			return createProxy(anInteface);
		}

		throw new RuntimeException(format("No source was registered for interface %s.", anInteface.getCanonicalName()));
	}

	private void checkInterfaceNotAlreadyRegistered(Class<?> anInterface) {
		if (implementationSources.containsKey(anInterface)) {
			throw new RuntimeException(format(
					"Unable to register for the interface %s as it is already registered: %s.", anInterface
							.getCanonicalName(), implementationSources.get(anInterface).toString()));
		}
	}

	private <T> void injectDependencies(T anObject) {
		Class<?> interfaceOfObject = anObject.getClass();
		while (interfaceOfObject != null) {
			injectDependencies(anObject, (Class<T>) interfaceOfObject);
			interfaceOfObject = interfaceOfObject.getSuperclass();
		}
	}

	private <T, S extends T> void injectDependencies(S anObject, Class<T> aClass) {
		for (Field field : aClass.getDeclaredFields()) {
			if (field.isAnnotationPresent(Dependency.class)) {
				field.setAccessible(true);
				try {
					if (field.get(anObject) == null) {
						Class classOfDependency = field.getType();
						field.set(anObject, getObject(classOfDependency));
					}
				} catch (IllegalArgumentException e) {
					throw new RuntimeException(e);
				} catch (IllegalAccessException e) {
					throw new RuntimeException(format("Unable to access field %s.", field.getName()), e);
				}
			}
		}
	}

	private <T> T createProxy(final Class<T> anInterface) {
		return (T) Proxy.newProxyInstance(DependencyInjectingObjectFactory.class.getClassLoader(),
				new Class[] { anInterface }, new InvocationHandler() {
					private T object;

					@Override
					public Object invoke(Object aProxy, Method aMethod, Object[] anArrayOfArguments) throws Throwable {
						if (object == null) {
							object = (T) implementationSources.get(anInterface).getObject();
						}
						return aMethod.invoke(object, anArrayOfArguments);
					}
				});
	}
}
