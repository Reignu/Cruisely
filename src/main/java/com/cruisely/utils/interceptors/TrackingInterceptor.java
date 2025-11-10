package com.cruisely.utils.interceptors;

import lombok.extern.java.Log;
import com.cruisely.entities.common.BaseEntity;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log
public class TrackingInterceptor {
    @Resource
    private SessionContext context;

    @AroundInvoke
    public Object logActions(InvocationContext context) throws Exception {

        String resultString;
        Object[] params = context.getParameters() != null ? context.getParameters() : new Object[]{};

        try {
            Object result = context.proceed();
            resultString = result == null ? "without return value" : "with returned value \"" + result + "\"";
            log.info(getLogString(context.getMethod().toString(), params, resultString));
            return result;
        } catch (Exception e) {
            resultString = String.format("with exception thrown: \"%s\", \ncause: \"%s\", \nmessage: \"%s\"", e, e.getCause(), e.getMessage());
            log.severe(getLogString(context.getMethod().toString(), params, resultString));
            throw e;
        }
    }

    private String getLogString(String methodName, Object[] parameters, String resultString) {
        List<Object> paramsStrings = methodName.contains("Password") ? List.of("Hidden") :
                Arrays.stream(parameters)
                        .map(param -> {
                            if (param instanceof BaseEntity) {
                                BaseEntity entity = (BaseEntity) param;
                                return String.format("Entity: %s, \nidentifier: %s, version: %s\n", entity, entity.getIdentifier(), entity.getVersion());
                            }
                            return param;
                        }).collect(Collectors.toList());

        return String.format("Method \"%s\" " +
                "\ncalled by user \"%s\" " +
                "\nwith parameters: \n%s, " +
                "\n%s", methodName, context.getCallerPrincipal().getName(), paramsStrings, resultString);
    }
}
