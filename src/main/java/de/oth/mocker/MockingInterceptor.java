package de.oth.mocker;

import net.sf.cglib.proxy.*;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;

public class MockingInterceptor implements MethodInterceptor {

	// Attributes
	private HashMap<Method, HashMap<Object[], Integer>> history;
	private boolean isVerification;
	private VerificationInformation verificationInfo;

	// Constructor
	public MockingInterceptor() {
		history = new HashMap<Method, HashMap<Object[], Integer>>();
		isVerification = false;
	}

	// Methods

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if (isVerification) {
			verifyOccurences(method, args);
		} else {
			addMethodCallToHistory(method, args);
		}
		return getDefaultReturnValue(method);
	}

	private void verifyOccurences(Method method, Object[] args) {
		checkHistoryforOccurence(method, args);
		isVerification = false;
	}

	private void checkHistoryforOccurence(Method method, Object[] args) {
		Integer expectedCallCount = verificationInfo.getCount();
		Entry<Object[], Integer> entry = getMatchingEntryFromHistory(method, args);
		String messagePrefix = "Failed: Verification failure: ";
		// If the verification fails, build message and throw error
		switch (verificationInfo.getType()) {
			case MAX:
				if (entry != null && entry.getValue() > expectedCallCount) {
					throw new AssertionError(messagePrefix + method.getName() + " was called more often than "
							+ expectedCallCount + " (" + entry.getValue() + ")");
				}
				break;
			case MIN:
				if (entry.getValue() < expectedCallCount) {
					throw new AssertionError(messagePrefix + method.getName() + " was called less often than "
							+ expectedCallCount + " (" + entry.getValue() + ")");
				}
				break;
			case TIMES:
				if (entry.getValue() != expectedCallCount) {
					throw new AssertionError(messagePrefix + method.getName() + " was not called " + expectedCallCount
							+ " time(s) (" + entry.getValue() + ")");
				}
				break;
			case NEVER:
				if (entry != null && entry.getValue() > 0) {
					throw new AssertionError(messagePrefix + method.getName() + " should never have been called");
				}
				break;
			default:
				; // Null statement
				break;
		}
	}

	private Object getDefaultReturnValue(Method method) {
		Object returnValue;
		Class<?> type = method.getReturnType();
		if (type.isPrimitive() && !void.class.equals(type)) {
			// Create a primitive type array (initialized with default value)
			// and get its first entry
			returnValue = Array.get(Array.newInstance(type, 1), 0);
		} else if (String.class.equals(type)) {
			// As always, strings are privileged
			returnValue = "";
		} else {
			returnValue = null;
		}
		return returnValue;
	}

	private void addMethodCallToHistory(Method method, Object[] args) {
		HashMap<Object[], Integer> subMap;
		if (history.containsKey(method)) {
			addMethodCallToSubMap(method, args);
		} else {
			subMap = new HashMap<Object[], Integer>();
			subMap.put(args, 1);
			history.put(method, subMap);
		}
	}

	private void addMethodCallToSubMap(Method method, Object[] args) {
		HashMap<Object[], Integer> subMap = history.get(method);
		Entry<Object[], Integer> match = getMatchingEntryFromHistory(method, args);
		if (match == null) {
			subMap.put(args, 1);
		} else {
			match.setValue(match.getValue() + 1); // writes through to history
		}
	}

	private Entry<Object[], Integer> getMatchingEntryFromHistory(Method method, Object[] args) {
		HashMap<Object[], Integer> subMap;
		int i;
		if (history.containsKey(method)) {
			subMap = history.get(method);
			for (Entry<Object[], Integer> entry : subMap.entrySet()) {
				if (entry.getKey().length == args.length) {
					// Check all entries for equality with method arguments
					for (i = 0; i < args.length; i++) {
						if (!entry.getKey()[i].equals(args[i]))
							break;
					}
					if (args.length == 0 || i == args.length) {
						// Both lengths are zero or the loop
						// went all the way through, so it's a match
						return entry;
					}
				}
			}
		}
		return null;
	}

	// Helping methods

	public void setVerification(boolean isVerification) {
		this.isVerification = isVerification;
	}

	public void setVerificationInfo(VerificationInformation verificationInfo) {
		this.verificationInfo = verificationInfo;
	}

}
