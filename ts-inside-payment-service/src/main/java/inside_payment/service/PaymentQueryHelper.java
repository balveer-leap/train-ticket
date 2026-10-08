package inside_payment.service;

import edu.fudan.common.util.Response;
import inside_payment.entity.Payment;
import inside_payment.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Helper for querying stored payments.
 */
@Component
public class PaymentQueryHelper {

    @Autowired
    PaymentRepository paymentRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentQueryHelper.class);

    public Response query(HttpHeaders headers, int limit){
        List<Payment> paymentList = paymentRepository.findAll();

        if (paymentList != null && !paymentList.isEmpty()) {
            PaymentServiceImpl.LOGGER.info(
                    "[query][Find all payment Success][size:{}]",
                    paymentList.size());
            List<Payment> limited = paymentList.size() > limit ? paymentList.subList(0, limit) : paymentList;
            return new Response<>(1, "Query Success", limited);
        } else {
            PaymentServiceImpl.LOGGER.warn(
                    "[query][Find all payment warn][{}]",
                    "No content");
            return new Response<>(0, "No Content", null);
        }
    }
}
