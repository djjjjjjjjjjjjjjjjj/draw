package draw;

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
    @Autowired DrawRepository drawRepository;

   
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRaffleDropped_DrawSms(@Payload RaffleDropped raffleDropped){

        if(!raffleDropped.validate()) return;

        //응모 취소가 완료 되었습니다..
        System.out.println("\nDraw.PolicyHandler.30\n##############################################" + 
        "\n" + raffleDropped.getUserId() + " Draw Drop Completed" + 
        "\n응모일자 : " + raffleDropped.getRaffleDate() +
        "\n##############################################\n" );
        System.out.println("\n\n##### listener DrawSms : " + raffleDropped.toJson() + "\n\n");



        // Sample Logic //
        // Draw draw = new Draw();
        // drawRepository.save(draw);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRaffleRequested_DrawSms(@Payload RaffleRequested raffleRequested){

        if(!raffleRequested.validate()) return;

        // 응모가 완료되었습니다.
        System.out.println("\nDraw.PolicyHandler.30\n##############################################" + 
        "\n" + raffleRequested.getUserId() + " Draw Completed" + 
        "\n응모일자 : " + raffleRequested.getRaffleDate() +
        "\n##############################################\n" );
        System.out.println("\n\n##### listener DrawSms : " + raffleRequested.toJson() + "\n\n");



        // Sample Logic //
        // Draw draw = new Draw();
        // drawRepository.save(draw);

    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverResultConfirmed_DrawSms(@Payload ResultConfirmed resultConfirmed){

        if(!resultConfirmed.validate()) return;

        //응모 결과는 XXX 입니다.
        // if (resultConfirmed.getWin() == true ) {
            // Draw draw = new Draw();
         if (resultConfirmed.getUserId() == "dj@sk.com" ) {
            
         
            System.out.println("\nDraw.PolicyHandler.30\n##############################################" + 
            "\n" + resultConfirmed.getUserId() + " Raffle Completed" + 
            "\n응모일자 : " + resultConfirmed.getRaffleDate() +
            "\n당첨여부 : 당첨 " +
            "\n축하합니다. 바로 구매 해주세요 " +
            "\n##############################################\n" );
        }
        else {

            System.out.println("\nDraw.PolicyHandler.30\n##############################################" + 
            "\n" + resultConfirmed.getUserId() + " Raffle Completed" + 
            "\n응모일자 : " + resultConfirmed.getRaffleDate() +
            "\n당첨여부 : 미당첨 " +
            "\n아쉽지만 다름에 진행되는 DRAW에 도전해주세요 " +
            "\n##############################################\n" );

        }
        System.out.println("\n\n##### listener DrawSms : " + resultConfirmed.toJson() + "\n\n");



        // Sample Logic //
        // Draw draw = new Draw();
        // drawRepository.save(draw);

    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}