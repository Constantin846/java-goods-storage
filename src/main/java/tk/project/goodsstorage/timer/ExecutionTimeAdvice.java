package tk.project.goodsstorage.timer;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ExecutionTimeAdvice {
    @Around("@annotation(tk.project.goodsstorage.timer.TaskExecutionTime)")
    public Object executionTime(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object targetMethodResult = point.proceed();
        long endTime = System.currentTimeMillis();
        long time = endTime - startTime;

        log.info("Execution time of " + point.getSignature().toShortString() + ": " + time + " ms");
        return targetMethodResult;
    }
}
