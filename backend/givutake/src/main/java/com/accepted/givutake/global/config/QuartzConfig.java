package com.accepted.givutake.global.config;

import com.accepted.givutake.funding.UpdateStateTaskJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail dailyTaskJobDetail() {
        return JobBuilder.newJob(UpdateStateTaskJob.class)
                .withIdentity("dailyTask")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger dailyTaskTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule("0 0 0 * * ?")
                .withMisfireHandlingInstructionFireAndProceed(); // 미스파이어 발생 시 즉시 실행

        return TriggerBuilder.newTrigger()
                .forJob(dailyTaskJobDetail())
                .withIdentity("dailyTaskTrigger")
                .withSchedule(scheduleBuilder)
                .build();
    }

    @Bean
    public Trigger startupTrigger() {
        // 서버 시작 시 실행
        return TriggerBuilder.newTrigger()
                .forJob(dailyTaskJobDetail())
                .withIdentity("startupTrigger")
                .startNow() // 서버 시작 시 즉시 실행
                .build();
    }
}
