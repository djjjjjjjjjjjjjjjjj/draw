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
    @Autowired OrderRepository orderRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAuthCancelled_CancelAuthentication(@Payload AuthCancelled authCancelled){

        if(!authCancelled.validate()) return;    
        orderRepository.deleteById( authCancelled.getOrderRequestId() );
        System.out.println("\nOrder Request.PolicyHandler.20\n##############################################" + 
                           "\n" + authCancelled.getOrderRequestName() + " Order Cancelled" + 
                           "\nAuthentication Failed" + 
                           "\n##############################################\n" );
        System.out.println("Order PolicyHandler.24\n##### listener Cancel Authentication : " + authCancelled.toJson() + "\n");


    }

    // 인증 성공시 정책 수신(비동기)
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverAuthCertified_OrderComplate(@Payload AuthCertified authCertified){
        if(!authCertified.validate()) return;

        System.out.println("\nOrder Request.PolicyHandler.20\n##############################################" + 
        "\n" + authCertified.getOrderRequestName() + " Order Complated" + 
        "\nAuthentication Success" + 
        "\n##############################################\n" );

        Order order = orderRepository.findByItemNo( authCertified.getItemNo() );
        order.setItemNo(authCertified.getItemNo());
        order.setPrice(159000.0);
        order.setSize(280);
        order.setUserId(authCertified.getUserId());
        orderRepository.save(order);
    }

    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}