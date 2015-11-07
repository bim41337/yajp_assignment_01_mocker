package de.oth.mocker;

import net.sf.cglib.proxy.*;
import java.util.HashMap;
import java.util.Map;

public class Mocker {

	private static Map<Integer, MockingInterceptor> objectInterceptorStorage = new HashMap<>();

	@SuppressWarnings("unchecked")
	private static <T> T getProxyObject(Class<T> clazz, MockingInterceptor interceptor) {
		Enhancer e = new Enhancer();
		T mockObject;
		e.setSuperclass(clazz);
		e.setCallback(interceptor);
		mockObject = (T) e.create();
		objectInterceptorStorage.put(System.identityHashCode(mockObject), interceptor);
		return mockObject;
	}

	public static <T> T mock(Class<T> clazz) {
		return getProxyObject(clazz, new MockingInterceptor());
	}

	@SuppressWarnings("unchecked")
	public static <T> T spy(T object) {
		return getProxyObject((Class<T>) object.getClass(), new SpyingInterceptor<T>(object));
	}

	public static <T> T verify(T mockObject, VerificationInformation info) {
		MockingInterceptor interceptor = objectInterceptorStorage.get(System.identityHashCode(mockObject));
		interceptor.setVerification(true);
		interceptor.setVerificationInfo(info);
		return mockObject;
	}

	public static <T> T verify(T mockObject) {
		return verify(mockObject, new VerificationInformation(VerificationType.TIMES, 1));
	}

	private static VerificationInformation validateVerificationRequest(VerificationType type, Integer count) {
		if (count < 1) {
			type = VerificationType.NEVER;
			count = -1;
		}
		return new VerificationInformation(type, count);
	}

	public static VerificationInformation times(Integer count) {
		return validateVerificationRequest(VerificationType.TIMES, count);
	}

	public static VerificationInformation atLeast(Integer count) {
		return validateVerificationRequest(VerificationType.MIN, count);
	}

	public static VerificationInformation atMost(Integer count) {
		return validateVerificationRequest(VerificationType.MAX, count);
	}

	public static VerificationInformation never() {
		return new VerificationInformation(VerificationType.NEVER, -1);
	}

}
