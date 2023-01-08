package com.driver;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@Repository
public class OrderRepository {
    private HashMap<String, Order> orderDb;
    private HashMap<String, DeliveryPartner> deliveryPartnerDb;

    private HashMap<String, List<String>> orderToDelPartnerDb;
    private HashMap<String, List<String>> partnerDb;

    public OrderRepository() {
    }

    public OrderRepository(HashMap<String, Order> orderMap, HashMap<String, DeliveryPartner> deliveryPartnerMap, HashMap<String, List<String>> orderToDeliveryPartner, HashMap<String, List<String>> partnerIdTimeMap) {
        this.orderDb = orderMap;
        this.deliveryPartnerDb = deliveryPartnerMap;
        this.orderToDelPartnerDb = orderToDeliveryPartner;
        this.partnerDb = partnerIdTimeMap;
    }
    public void addOrder(Order order)
    {
        orderDb.put(order.getId(),order);
    }

    public void addPartner(String partnerId)
    {
        DeliveryPartner deliverypartner = new DeliveryPartner();
//        deliverypartner.setId(partnerId);
//        deliverypartner.setNumberOfOrders(0);
        deliveryPartnerDb.put(partnerId,deliverypartner);
    }

    public void addOrderPartnerId(String orderId, String partnerId)
    {
        List<String> orderIdList = new ArrayList<>();
        List<String> timeList = new ArrayList<>();

        if(orderDb.containsKey(orderId) && deliveryPartnerDb.containsKey(partnerId)) {
            if (orderToDelPartnerDb.containsKey(partnerId)) {
                orderIdList = orderToDelPartnerDb.get(partnerId);
            }

            orderIdList.add(orderId);

            orderToDelPartnerDb.put(partnerId, orderIdList);

            Order order= orderDb.get(orderId);
            String time= String.valueOf(order.getDeliveryTime());

            if(partnerDb.containsKey(partnerId)){
                timeList=partnerDb.get(partnerId);
            }

            timeList.add(time);

            partnerDb.put(time, timeList);
        }
    }

    public Order getOrderById(String orderId)
    {
        return orderDb.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId)
    {
        return deliveryPartnerDb.get(partnerId);
    }

    public Integer getOrderCountByPartnerId(String partnerId)
    {
        List<String> orderList = new ArrayList<>();
        int count=0;
        if(orderToDelPartnerDb.containsKey(partnerId)){
            orderList = orderToDelPartnerDb.get(partnerId);
            count =  orderList.size();
        }
        return count;

    }

    public List<String> getOrdersByPartnerId(String partnerId)
    {
        List<String> orderList = new ArrayList<>();

        if(orderToDelPartnerDb.containsKey(partnerId)){
            orderList = orderToDelPartnerDb.get(partnerId);
        }
        return orderList;
    }

    public List<String> getAllOrders()
    {
        return new ArrayList<>(orderDb.keySet());
    }

    public Integer getCountOfUnassignedOrders()
    {
        int totalOrders = orderDb.size();

        for(String orders: orderDb.keySet()){
            for(String partner: orderToDelPartnerDb.keySet()){
                List<String> orderList = new ArrayList<>();
                orderList = orderToDelPartnerDb.get(partner);
                for(String order: orderList){
                    if(orders.equals(order)){
                        totalOrders--;
                        break;
                    }
                }
            }
        }
        return totalOrders;
    }

    public Integer getOrdersleftAfterGivenTimeByPartnerId(String time, String partnerId)
    {
        Order order = new Order();

        int convertedTime = order.convertTimeToInteger(time);
        int count = 0;
        if(partnerDb.containsKey(partnerId)){
            List<String> timeList = new ArrayList<>();
            timeList = partnerDb.get(partnerId);
            for(String times: timeList){
                int newTime  = order.convertTimeToInteger(times);
                if(newTime>convertedTime){
                    count++;
                }
            }
        }
        return count;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId)
    {
        int maximumTime = 0;
        String time ="";
        Order order = new Order();
        List<String> list = new ArrayList<>();
        if(partnerDb.containsKey(partnerId)){
            list=  partnerDb.get(partnerId);
            for(String times: list){
                int newTime = order.convertTimeToInteger(times);
                if(newTime>maximumTime){
                    maximumTime = newTime;
                }
            }
        }
        time=Integer.toString(maximumTime);
        return time;
    }

    public void deletePartnerId(String partnerId)
    {
        if(deliveryPartnerDb.containsKey(partnerId)){
            deliveryPartnerDb.remove(partnerId);
        }
        if(orderToDelPartnerDb.containsKey(partnerId)){
            orderToDelPartnerDb.remove(partnerId);
        }
        if(partnerDb.containsKey(partnerId)){
            partnerDb.remove(partnerId);
        }
    }

    public void deleteOrderById(String orderId)
    {
        if(orderDb.containsKey(orderId)){
            orderDb.remove(orderId);
        }
        List<String> orderList = new ArrayList<>();
        for(String partnerId: orderToDelPartnerDb.keySet()){
            orderList = orderToDelPartnerDb.get(partnerId);
            for(String orders: orderList){
                if(orders.equals(orderId)){
                    orderList.remove(orders);
                }
            }
            orderToDelPartnerDb.put(partnerId,orderList);
        }
        Order order = new Order();
        String time= String.valueOf(orderDb.get(orderId));

        List<String> timeList = new ArrayList<>();
        for(String partner: partnerDb.keySet()){
            timeList=partnerDb.get(partner);
            for(String times: timeList){
                if(times.equals(time)){
                    timeList.remove(times);
                }
            }
            partnerDb.put(partner,timeList);
        }
    }
}