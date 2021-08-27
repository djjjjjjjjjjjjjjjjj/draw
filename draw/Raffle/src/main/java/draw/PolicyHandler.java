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
    @Autowired RaffleRepository raffleRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrerRequested_RaffleCheck(@Payload OrerRequested orerRequested){

        if(!orerRequested.validate()) return;

        System.out.println("\n\n##### listener RaffleCheck : " + orerRequested.toJson() + "\n\n");



        // Sample Logic //
        // Raffle raffle = new Raffle();
        // raffleRepository.save(raffle);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDrawChecked_RaffleCheck(@Payload DrawChecked drawChecked){

        if(!drawChecked.validate()) return;

        System.out.println("\n\n##### listener RaffleCheck : " + drawChecked.toJson() + "\n\n");



        // Sample Logic //
        // Raffle raffle = new Raffle();
        // raffleRepository.save(raffle);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDrawCanceled_RaffleCancel(@Payload DrawCanceled drawCanceled){

        if(!drawCanceled.validate()) return;

        System.out.println("\n\n##### listener RaffleCancel : " + drawCanceled.toJson() + "\n\n");



        // Sample Logic //
        // Raffle raffle = new Raffle();
        // raffleRepository.save(raffle);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCanceled_RaffleCancel(@Payload OrderCanceled orderCanceled){

        if(!orderCanceled.validate()) return;

        System.out.println("\n\n##### listener RaffleCancel : " + orderCanceled.toJson() + "\n\n");



        // Sample Logic //
        // Raffle raffle = new Raffle();
        // raffleRepository.save(raffle);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDrawRequested_RaffleRequest(@Payload DrawRequested drawRequested){

        if(!drawRequested.validate()) return;

        System.out.println("\n\n##### listener RaffleRequest : " + drawRequested.toJson() + "\n\n");



        // Sample Logic //
        // Raffle raffle = new Raffle();
        // raffleRepository.save(raffle);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}