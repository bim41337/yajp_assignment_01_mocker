package de.oth.mocker;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodProxy;

public class SpyingInterceptor<T> extends MockingInterceptor {
	
	// Attributes
	private T substituteObject;
	
	// Constructor
	public SpyingInterceptor(T subObj) {
		super();
		substituteObject = subObj;
	}
	
	// Methods
	
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		super.intercept(obj, method, args, proxy);
		return method.invoke(substituteObject, args);
	}
	
}
