package com.caseStudy.combo.topology;

import com.caseStudy.combo.TopicConstants;
import com.caseStudy.combo.dto.User;
import com.caseStudy.combo.repository.RedisUserRepository;
import com.caseStudy.combo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserStreamTopology {

    @Autowired
    RedisUserRepository redisUserRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    public void  process(StreamsBuilder streamsBuilder){
        // Consume Stream from greeting topic
        var userMiddlewareStreamCaptured =  streamsBuilder
                .stream(
                        TopicConstants.USER,
                        Consumed.with(Serdes.String(),
                                new JsonSerde<>(User.class)));


        userMiddlewareStreamCaptured.print(Printed.<String, User>toSysOut().withLabel("userStream"));

        // apply upper class function
        var userMap = userMiddlewareStreamCaptured.mapValues((readOnlyKey, value) ->
                new User(value.userId(),  value.name(), value.email(), value.phone()));
        userMap.foreach((key, value) -> {
            if (value != null) {
                try {
                    com.caseStudy.combo.model.User user = com.caseStudy.combo.model.User.builder()
                            .userId(value.userId())
                            .name(value.name())
                            .phone(value.phone())
                            .email(value.email())
                            .build();

                    redisUserRepository.putIfAbsent(user);
                    userRepository.saveAndFlush(user);

                    log.info("Processed user: {}", user);
                } catch (Exception e) {
                    log.error("Error processing user: {}", value, e);
                }
            } else {
                log.warn("Received null value for key: {}", key);
            }
        });

        userMap.to(TopicConstants.USER_OUTPUT, Produced.with(Serdes.String(), new JsonSerde<>(User.class)));
    }
}
