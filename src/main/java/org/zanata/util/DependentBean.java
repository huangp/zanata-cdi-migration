package org.zanata.util;

import java.io.Serializable;
import javax.inject.Provider;

import org.apache.deltaspike.core.api.provider.DependentProvider;
import com.google.common.annotations.VisibleForTesting;

/**
 * @author Patrick Huang
 *         <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
public class DependentBean<T> implements Provider<T>, AutoCloseable,
        Serializable {
    private final DependentProvider<T> provider;
    private final T bean;

    public DependentBean(DependentProvider<T> provider) {
        this.provider = provider;
        bean = null;
    }

    @VisibleForTesting
    public DependentBean(T bean) {
        this.bean = bean;
        provider = null;
    }

    @Override
    public void close() throws Exception {
        if (provider != null) {
            provider.destroy();
        } else {
            //N.B. lifecycle method will not be run in test
        }
    }

    @Override
    public T get() {
        if (provider != null) {
            return provider.get();
        } else {
            return bean;
        }
    }
}
