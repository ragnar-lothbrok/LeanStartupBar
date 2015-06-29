package com.test.primaseller.bean;

import java.util.Date;
import java.util.concurrent.BlockingQueue;

import com.test.primaseller.service.RestaurantService;

public class Waiter implements Runnable {

	private BlockingQueue<Order> waitWaiterQueue;

	private BlockingQueue<Order> waiterChefQueue;

	private BlockingQueue<Order> chefOrderQueue;

	public Waiter(BlockingQueue<Order> waitWaiterQueue,
			BlockingQueue<Order> waiterChefQueue,
			BlockingQueue<Order> chefOrderQueue) {
		super();
		this.waitWaiterQueue = waitWaiterQueue;
		this.waiterChefQueue = waiterChefQueue;
		this.chefOrderQueue = chefOrderQueue;
	}

	public void run() {
		while (true) {
			synchronized (this) {
				if (waitWaiterQueue.size() > 0) {
					Integer waiterNumber = RestaurantService.getWaiter();
					if (waiterNumber != -1) {
						Order order = waitWaiterQueue.poll();
						if (order != null) {
							order.setWaiterId(waiterNumber);
							order.setWaiterConsumeTime(new Date());
							waiterChefQueue.offer(order);
						} else {
							RestaurantService.getWaitersStatus()[waiterNumber] = true;
						}
					}
				}
			}
			synchronized (this) {
				if (waiterChefQueue.size() > 0) {
					Integer chefNumber = RestaurantService.getChef();
					if (chefNumber != -1) {
						Order order = waiterChefQueue.poll();
						if (order != null) {
							order.setChefId(chefNumber);
							chefOrderQueue.offer(order);
							RestaurantService.getWaitersStatus()[order.getWaiterId()] = true;
						} else {
							RestaurantService.getChefsStatus()[chefNumber] = true;
						}
					}
				}
			}
		}
	}

}
