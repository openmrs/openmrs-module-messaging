package org.openmrs.module.messaging.schema;

import java.util.List;

import org.openmrs.module.messaging.util.ReflectionUtils;

public abstract class BaseAddressFactory<A extends MessagingAddress> implements AddressFactory<A> {

	public Class<?> getAddressClass() {
		List<Class<?>> genericParameters = ReflectionUtils.getTypeArguments(BaseAddressFactory.class, getClass());
		for (Class<?> c : genericParameters) {
			if (ReflectionUtils.classExtendsClass(c, MessagingAddress.class)) {
				return c;
			}
		}
		return null;
	}
}
