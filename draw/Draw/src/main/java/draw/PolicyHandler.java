package draw;

import draw.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired DrawRepository drawRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverResultConfirmed_DrawSms(@Payload ResultConfirmed resultConfirmed){

        if(!resultConfirmed.validate()) return;

        System.out.println("\n\n##### listener DrawSms : " + resultConfirmed.toJson() + "\n\n");



        // Sample Logic //
        // Draw draw = new Draw();
        // drawRepository.save(draw);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRaffleDropped_DrawSms(@Payload RaffleDropped raffleDropped){

        if(!raffleDropped.validate()) return;

        System.out.println("\n\n##### listener DrawSms : " + raffleDropped.toJson() + "\n\n");



        // Sample Logic //
        // Draw draw = new Draw();
        // drawRepository.save(draw);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRaffleRequested_DrawSms(@Payload RaffleRequested raffleRequested){

        if(!raffleRequested.validate()) return;

        System.out.println("\n\n##### listener DrawSms : " + raffleRequested.toJson() + "\n\n");



        // Sample Logic //
        // Draw draw = new Draw();
        // drawRepository.save(draw);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverResultConfirmed_DrawSms(@Payload ResultConfirmed resultConfirmed){

        if(!resultConfirmed.validate()) return;

        System.out.println("\n\n##### listener DrawSms : " + resultConfirmed.toJson() + "\n\n");



        // Sample Logic //
        // Draw draw = new Draw();
        // drawRepository.save(draw);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}