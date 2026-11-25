package com.coforge.hsbcdma.logs;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class GlobalLoggingAspect {

    @Around("execution(* com.coforge..controller..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String method = joinPoint.getSignature().toShortString();

        log.info("➡️ Controller Start: {}", method);

        Object result = joinPoint.proceed();

        log.info("⬅️ Controller End: {} | Time: {} ms",
                method,
                System.currentTimeMillis() - start);

        return result;
    }

    @Around("execution(* com.coforge..service..*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {

        String method = joinPoint.getSignature().toShortString();

        log.debug("🔵 Service Start: {}", method);

        Object result = joinPoint.proceed();

        log.debug("🟢 Service End: {}", method);

        return result;
    }
}
