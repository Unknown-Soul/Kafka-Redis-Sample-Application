package com.caseStudy.combo.service;

import com.caseStudy.combo.TopicConstants;
import com.caseStudy.combo.dto.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class SendDataToProducer {


    static KafkaProducer<String, String> producer = new KafkaProducer<String, String>(producerProps());

    public void publishData(User user){
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        String greetingJSON = null;
        try {
            greetingJSON = objectMapper.writeValueAsString(user);
            var recordMetaData =  publishMessageSync(TopicConstants.USER, null, greetingJSON);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }


    }


    public static Map<String, Object> producerProps(){

        Map<String,Object> propsMap = new HashMap<>();
        propsMap.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "172.29.33.147:9092");
        propsMap.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        propsMap.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return propsMap;

    }


    private static RecordMetadata publishMessageSync(String topicName, String key, String greetingJSON) {
        ProducerRecord<String,String> producerRecord = new ProducerRecord<>(topicName,key,greetingJSON);
        RecordMetadata recordMetadata = null;

        try {
            log.info("producerRecord : " + producerRecord);
            recordMetadata = producer.send(producerRecord).get();
        } catch (InterruptedException e) {
            log.error("InterruptedException in  publishMessageSync : {}  ", e.getMessage(), e);
        } catch (ExecutionException e) {
            log.error("ExecutionException in  publishMessageSync : {}  ", e.getMessage(), e);
        }catch(Exception e){
            log.error("Exception in  publishMessageSync : {}  ", e.getMessage(), e);
        }
        return recordMetadata;

    }
}
