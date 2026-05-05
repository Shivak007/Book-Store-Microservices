package com.bookstore.admin.aspect;

import com.bookstore.admin.entity.AuditLog;
import com.bookstore.admin.repository.AuditLogRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
public class AuditAspect {
    
    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);
    
    private final AuditLogRepository auditLogRepository;
    
    public AuditAspect(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping) && execution(* com.bookstore.admin.controller..*(..))")
    public Object auditPostOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        return auditOperation(joinPoint, "CREATE");
    }

    @Around("@annotation(org.springframework.web.bind.annotation.PutMapping) && execution(* com.bookstore.admin.controller..*(..))")
    public Object auditPutOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        return auditOperation(joinPoint, "UPDATE");
    }

    @Around("@annotation(org.springframework.web.bind.annotation.DeleteMapping) && execution(* com.bookstore.admin.controller..*(..))")
    public Object auditDeleteOperations(ProceedingJoinPoint joinPoint) throws Throwable {
        return auditOperation(joinPoint, "DELETE");
    }

    private Object auditOperation(ProceedingJoinPoint joinPoint, String action) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Long adminId = getCurrentAdminId();
        Long targetId = extractTargetId(joinPoint.getArgs());

        log.info("Admin {} performing {} action on {} with targetId: {}", adminId, action, methodName, targetId);

        try {
            Object result = joinPoint.proceed();
            
            // Log successful operation
            AuditLog auditLog = new AuditLog();
            auditLog.setAdminId(adminId);
            auditLog.setAction(String.format("%s_%s", action, methodName.toUpperCase()));
            auditLog.setTargetId(targetId);
            auditLog.setTimestamp(LocalDateTime.now());
            
            auditLogRepository.save(auditLog);
            
            log.info("Audit log created for admin {} action: {}", adminId, auditLog.getAction());
            return result;
            
        } catch (Exception e) {
            log.error("Failed to execute {} action for admin {}: {}", action, adminId, e.getMessage());
            throw e;
        }
    }

    private Long getCurrentAdminId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            // For simplicity, we'll use a hardcoded admin ID for now
            // In a real implementation, you'd fetch the admin from the database
            return 1L; // This should be replaced with actual admin ID lookup
        }
        return null;
    }

    private Long extractTargetId(Object[] args) {
        if (args != null && args.length > 0) {
            for (Object arg : args) {
                if (arg instanceof Long) {
                    return (Long) arg;
                }
                // You can add more sophisticated logic to extract IDs from complex objects
            }
        }
        return null;
    }
}
