package tk.project.goodsstorage.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Order(Integer.MAX_VALUE - 2)
@Component
public class ExecutionTransactionTimeAdvice {
    @Around("@annotation(tk.project.goodsstorage.annotations.TaskExecutionTransactionTime)")
    public Object executionTime(final ProceedingJoinPoint point) throws Throwable {
        final long startTime = System.currentTimeMillis();
        final Object targetMethodResult = point.proceed();
        final long endTime = System.currentTimeMillis();
        final long time = endTime - startTime;

        log.info("Execution with transaction time of " + point.getSignature().toShortString() + ": " + time + " ms");
        return targetMethodResult;
    }
}
