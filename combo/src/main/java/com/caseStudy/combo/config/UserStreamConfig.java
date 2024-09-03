package com.caseStudy.combo.config;

import com.caseStudy.combo.TopicConstants;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class UserStreamConfig {

    @Bean
    public NewTopic greetingTopic(){
        return TopicBuilder.name(TopicConstants.USER)
                .partitions(2)
                .replicas(1)
                .build();
    }


    @Bean
    public NewTopic greetingOutputTopic(){
        return TopicBuilder.name(TopicConstants.USER_OUTPUT)
                .partitions(2)
                .replicas(1)
                .build();
    }
}
