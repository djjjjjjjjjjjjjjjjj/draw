package draw;

import draw.Raffle;
import draw.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import java.util.Date;

@Service
public class PolicyHandler{
    @Autowired RaffleRepository raffleRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDrawChecked_RaffleCheck(@Payload DrawChecked drawChecked){

        if(!drawChecked.validate()) return;

        System.out.println("\n\n##### listener RaffleCheck : " + drawChecked.toJson() + "\n\n");

        System.out.println("\nRaffleCheck.PolicyHandler.44\n##############################################");
        System.out.println("당첨결과는: ");
        if (drawChecked.getWin() == true ) {
            System.out.println("축하합니다. 당첨입니다. 바로 구매 해주세요 ");

        }else{
            System.out.println("아쉽지만 다름에 진행되는 DRAW에 도전해주세요 ");
        }
        System.out.println("##############################################\n");

      
        Raffle raffle = raffleRepository.findByItemNo( drawChecked.getItemNo() );
        ResultConfirmed resultConfirmed = new ResultConfirmed();
           
        resultConfirmed.setItemNo(drawChecked.getItemNo());
        resultConfirmed.setUserId(drawChecked.getUserId());
        resultConfirmed.setWin(drawChecked.getWin());
        resultConfirmed.setRaffleDate(new Date());
        resultConfirmed.publish();
        
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDrawCanceled_RaffleCancel(@Payload DrawCanceled drawCanceled){

        if(!drawCanceled.validate()) return;

        System.out.println("\n\n##### listener RaffleCancel : " + drawCanceled.toJson() + "\n\n");
        System.out.println("\nRaffle.PolicyHandler.224\n##############################################");
        System.out.println("Cancel Draw : Raffle Exist");
        System.out.println("RaffleDropped");
        System.out.println("##############################################\n");

        Raffle raffle = raffleRepository.findByItemNo( drawCanceled.getItemNo() );
        raffleRepository.deleteById(raffle.getId());


        // Sample Logic //
        // Raffle raffle = new Raffle();
        // raffleRepository.save(raffle);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverOrderCanceled_RaffleCancel(@Payload OrderCanceled orderCanceled){

        if(!orderCanceled.validate()) return;

        System.out.println("\n\n##### listener RaffleCancel : " + orderCanceled.toJson() + "\n\n");
        System.out.println("\nRaffle.PolicyHandler.224\n##############################################");
        System.out.println("Cancel Order : Raffle Exist");
        System.out.println("RaffleDropped");
        System.out.println("##############################################\n");

        Raffle raffle = raffleRepository.findByItemNo( orderCanceled.getItemNo() );
        raffleRepository.deleteById(raffle.getId());


        // Sample Logic //
        // Raffle raffle = new Raffle();
        // raffleRepository.save(raffle);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverDrawRequested_RaffleRequest(@Payload DrawRequested drawRequested){

        if(!drawRequested.validate()) return;

        System.out.println("\n\n##### listener RaffleRequest : " + drawRequested.toJson() + "\n\n");


        System.out.println("\nRaffle.PolicyHandler.92\n##############################################");
        System.out.println("RaffleRequested");
        System.out.println("##############################################\n");

        // Raffle raffle = raffleRepository.findByItemNo( drawRequested.getItemNo() );
        Raffle raffle = new Raffle();
        raffle.setItemNo(drawRequested.getItemNo());
        raffle.setUserId(drawRequested.getUserId());

        if(drawRequested.getUserId() == "dj@sk.com" ){
            raffle.setWin(true);
        }else{
            raffle.setWin(false);
        }

        raffle.setRaffleDate(new Date());
        raffleRepository.save(raffle);

        // Sample Logic //
        // Raffle raffle = new Raffle();
        // raffleRepository.save(raffle);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}