package com.trainticket.service;

import com.trainticket.model.Money;
import com.trainticket.entity.Payment;
import com.trainticket.repository.PaymentRepository;
import edu.fudan.common.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author  Administrator
 * @date 2017/6/23.
 */
@Service
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    PaymentRepository paymentRepository;

    private final RefundPolicy refundPolicy = new RefundPolicy();

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    public Response pay(Payment info, HttpHeaders headers){

        if(paymentRepository.findByOrderId(info.getOrderId()) == null){
            Payment payment = new Payment();
            payment.setOrderId(info.getOrderId());
            payment.setPrice(info.getPrice());
            payment.setUserId(info.getUserId());
            paymentRepository.save(payment);
            PaymentServiceImpl.LOGGER.info("[pay][Pay Success][Refundable: {}]", refundPolicy.isRefundable(payment));
            return new Response<>(2, "Pay Success", null);
        }else{
            PaymentServiceImpl.LOGGER.warn("[pay][Pay Failed][Order not found with order id][PaymentId: {}, OrderId: {}]",info.getId(),info.getOrderId());
            return new Response<>(0, "Pay Failed, order not found with order id" +info.getOrderId(), null);
        }
    }

    @Override
    public Response addMoney(Payment info, HttpHeaders headers){
        if (info.getPrice() == null || info.getPrice().trim().isEmpty()) {
            PaymentServiceImpl.LOGGER.warn("[addMoney][Invalid price][UserId: {}]", info.getUserId());
            return new Response<>(0, "Add Money Failed, invalid price", null);
        }
        Money newMoney = buildMoney(info.getUserId(), info.getPrice());
        PaymentServiceImpl.LOGGER.info("[addMoney][Money added][UserId: {}, Amount: {}]", info.getUserId(), info.getPrice());
        return new Response<>(1, "Money Added Successfully", newMoney);
    }

    private Money buildMoney(String userId, String amount) {
        Money money = new Money();
        money.setUserId(userId);
        money.setMoney(amount.trim());
        return money;
    }

    @Override
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

    @Override
    public void initDefaultPayment(Payment payment, HttpHeaders headers){
        PaymentServiceImpl.LOGGER.info("[initDefaultPayment][Start init default payment][PaymentId: {}]", payment.getId());
        Optional<Payment> paymentTemp = paymentRepository.findById(payment.getId());
        if(!paymentTemp.isPresent()){
            paymentRepository.save(payment);
            PaymentServiceImpl.LOGGER.error("[initPayment][Init payment error][Payment not found][PaymentId: {}]",payment.getId());
        }else{
            PaymentServiceImpl.LOGGER.info("[initPayment][Init Payment Already Exists][PaymentId: {}]", payment.getId());
        }
    }
}
