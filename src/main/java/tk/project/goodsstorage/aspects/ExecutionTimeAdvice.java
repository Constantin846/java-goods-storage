package tk.project.goodsstorage.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAdvice {
    @Around("@annotation(tk.project.goodsstorage.annotations.TaskExecutionTime)")
    public Object executionTime(final ProceedingJoinPoint point) throws Throwable {
        final long startTime = System.currentTimeMillis();
        final Object targetMethodResult = point.proceed();
        final long endTime = System.currentTimeMillis();
        final long time = endTime - startTime;

        log.info("Execution time of " + point.getSignature().toShortString() + ": " + time + " ms");
        return targetMethodResult;
    }
}
