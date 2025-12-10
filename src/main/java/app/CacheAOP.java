package app;

import jakarta.persistence.EntityManagerFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CacheAOP {

    @Autowired
    private EntityManagerFactory emf;

    @Around("@annotation(CacheMe)")
    public Object logCacheStats(ProceedingJoinPoint jp) throws Throwable {
        Statistics stats = emf.unwrap(SessionFactory.class).getStatistics();
        long hitsprev = stats.getSecondLevelCacheHitCount();
        long missesprev = stats.getSecondLevelCacheMissCount();
        Object result = jp.proceed();
        long hits = stats.getSecondLevelCacheHitCount();
        long misses = stats.getSecondLevelCacheMissCount();

        System.out.printf("[%s] cache hits: %d (+%d new), misses: %d (+%d new)%n", jp.getSignature(), hits, hits - hitsprev, misses, misses - missesprev);

        return result;
    }
}