package tk.project.goodsstorage.timer;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Order(10)
@Component
public class ExecutionTransactionTimeAdvice {
    @Around("@annotation(tk.project.goodsstorage.timer.TaskExecutionTransactionTime)")
    public Object executionTime(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object targetMethodResult = point.proceed();
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;

        log.info("Execution with transaction time of " + point.getSignature().toShortString() + ": " + time + " ms");
        return targetMethodResult;
    }
}
