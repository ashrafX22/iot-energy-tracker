package org.userservice.userservice.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class TimeExecutionAspect {

    @Pointcut("execution(* org.userservice.userservice.controller.*.*(..))")
    public void controllerMethod (){}

    @Around("controllerMethod()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.info("Execution time of {} is {}ms", joinPoint.getSignature().getName(), endTime - startTime);

        return result;
    }
}
