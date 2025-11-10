package com.cruisely.security;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Note for sharing possibility of ETag filtering
 */
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface ETagFilterBinding {
}
