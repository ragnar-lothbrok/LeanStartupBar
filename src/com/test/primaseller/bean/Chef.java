package com.test.primaseller.bean;

import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.test.primaseller.enums.ItemEnum;
import com.test.primaseller.log.FileLog;
import com.test.primaseller.service.RestaurantService;

public class Chef implements Runnable {

	ExecutorService executorService = Executors.newFixedThreadPool(5);

	private BlockingQueue<Order> chefOrderQueue;

	public Chef(BlockingQueue<Order> chefOrderQueue) {
		super();
		this.chefOrderQueue = chefOrderQueue;
	}

	public void run() {
		while (true) {
			if (chefOrderQueue.size() > 0) {
				final Order order = chefOrderQueue.poll();
				if (order != null) {
					Runnable runnable = new Runnable() {
						public void run() {
							FileLog.writeToFile("Chef" + (order.getChefId() + 1)
									+ ": Picked up " + order.getOrderId()
									+ " at "
									+ RestaurantService.dateFormat.format(new Date())
									+ " from Waiter : "
									+ (order.getWaiterId() + 1));
							
							FileLog.writeToFile("Chef"
									+ (order.getChefId() + 1)
									+ ": Cooking "
									+ ItemEnum.getItemEnum(order
											.getItemNumber()) + "...  ");
							try {
								Thread.currentThread();
								Thread.sleep(ItemEnum.getItemCookingTime(order
										.getItemNumber()) * 1000*60);
								FileLog.writeToFile("Chef"
										+ (order.getChefId() + 1)
										+ ": Finished making "
										+ ItemEnum.getItemEnum(order
												.getItemNumber())
										+ " for "
										+ order.getOrderId()
										+ " at "
										+ RestaurantService.dateFormat
												.format(new Date()));
								RestaurantService.getChefsStatus()[order.getChefId()] = true;
							} catch (InterruptedException exception) {
								FileLog.writeToFile("Exception occured : "
										+ exception.getMessage());
							}
						}
					};

					executorService.submit(runnable);
				}
			}
		}
	}

}
