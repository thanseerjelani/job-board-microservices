package com.jobboard.notifications.listener;

import com.jobboard.notifications.config.RabbitMQConfig;
import com.jobboard.notifications.dto.JobPostedEvent;
import com.jobboard.notifications.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobPostedListener {

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.JOB_POSTED_QUEUE)
    public void handleJobPostedEvent(JobPostedEvent event) {
        log.info("Received Job Posted event: {}", event);

        try {
            // In a real application, you would:
            // 1. Query database for users interested in this category
            // 2. Send email to each interested user

            // For demo, we'll just log and send to the poster
            emailService.sendJobPostedNotification(
                    event.getPostedByEmail(),
                    event.getTitle(),
                    event.getCompanyName(),
                    event.getLocation()
            );

            log.info("Successfully processed Job Posted event for job: {}", event.getTitle());
        } catch (Exception e) {
            log.error("Error processing Job Posted event: {}", e.getMessage(), e);
            throw e; // Re-throw to trigger RabbitMQ retry mechanism
        }
    }
}