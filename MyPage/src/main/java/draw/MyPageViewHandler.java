package draw;

import draw.config.kafka.KafkaProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class MyPageViewHandler {

    @Autowired
    private MyPageRepository myPageRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void whenRaffleRequested_then_UPDATE(@Payload RaffleRequested raffleRequested) {
        try {
            if (!raffleRequested.validate()) return;
            System.out.println("\nMyPage.MyPageViewHandler.24\n##############################################");
            System.out.println("UPDATE when RaffleRequested");
            System.out.println("##############################################\n");
            System.out.println("\nMyPage.MyPageViewHandler.27\n##### listener RaffleRequested : " + raffleRequested.toJson() + "\n");   

            MyPage myPage = myPageRepository.findByItemNo( raffleRequested.getItemNo() );
            if( myPage == null ) myPage = new MyPage();            

            myPage.setUserId( raffleRequested.getUserId() );
            myPage.setItemNo( raffleRequested.getItemNo() );
            myPage.setDrawDate( raffleRequested.getRaffleDate() );
            myPage.setDrawResult( raffleRequested.getWin() );

            if (raffleRequested.getWin() == true ) {
                myPage.setWin( 1 );
                myPage.setLoss( 0 );
            }
            else{
                myPage.setWin( 0 );
                myPage.setLoss( 1 );
            }
            myPage.setDrawStatus("Raffle Completed");
            myPageRepository.save(myPage);

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whenRaffleDropped_then_UPDATE(@Payload RaffleDropped raffleDropped) {
        try {
            if (!raffleDropped.validate()) return;
            System.out.println("\nMyPage.MyPageViewHandler.50\n##############################################");
            System.out.println("UPDATE when raffleDropped");
            System.out.println("##############################################\n");
            System.out.println("\nMyPage.MyPageViewHandler.53\n##### listener raffleDropped : " + raffleDropped.toJson() + "\n");   
                        
            // 요청 취소의 경우 ItemNo가 존재하는 경우에만 상태변경
            MyPage myPage = myPageRepository.findByItemNo( raffleDropped.getItemNo() );
            if( myPage != null ){  
                myPage.setUserId( raffleDropped.getUserId() );
                myPage.setItemNo( raffleDropped.getItemNo() );
                myPage.setDrawDate( raffleDropped.getRaffleDate() );
                myPage.setDrawResult( raffleDropped.getWin() );
                if (raffleDropped.getWin() == true ) {
                    myPage.setWin( 1 );
                    myPage.setLoss( 0 );
                }
                else{
                    myPage.setWin( 0 );
                    myPage.setLoss( 1 );
                }
                myPage.setDrawStatus("Raffle Dropped");
                myPageRepository.save(myPage);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }



}

