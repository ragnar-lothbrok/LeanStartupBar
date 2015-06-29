package com.test.primaseller.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.test.primaseller.bean.Chef;
import com.test.primaseller.bean.Order;
import com.test.primaseller.bean.Waiter;
import com.test.primaseller.enums.ItemEnum;
import com.test.primaseller.log.FileLog;

/**
 * 
 * @author raghunandanG
 * 
 * @main
 *
 */
public class RestaurantService {

	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(
			"HH:mm:ss a");

	// Will keep the entries before sending them to waiter
	private volatile BlockingQueue<Order> waitWaiterQueue = new ArrayBlockingQueue<Order>(
			100);

	// Will keep the orders in waiter queue
	private volatile BlockingQueue<Order> waiterChefQueue = null;

	// will keep the order in chef queue for cooking.
	private volatile BlockingQueue<Order> chefOrderQueue = null;

	// Will keep the status of waiter that he is available or not.
	private static volatile boolean waiterStatusArr[];

	// Will keep the status of Chef that he is available or not.
	private static volatile boolean chefStatusArr[];

	synchronized public static boolean[] getWaitersStatus() {
		return waiterStatusArr;
	}

	synchronized public static boolean[] getChefsStatus() {
		return chefStatusArr;
	}

	public static void main(String[] args) {

		RestaurantService restaurant = new RestaurantService();

		if (args.length != 2) {
			System.out.println("Please pass count of Chef and Waiter in CLA.");
			return;
		}
		restaurant.waiterChefQueue = new ArrayBlockingQueue<Order>(
				Integer.parseInt(args[0].substring(args[0].indexOf('=') + 1,
						args[0].length())));
		waiterStatusArr = new boolean[Integer.parseInt(args[0].substring(
				args[0].indexOf('=') + 1, args[0].length()))];
		Arrays.fill(waiterStatusArr, true);

		restaurant.chefOrderQueue = new ArrayBlockingQueue<Order>(
				Integer.parseInt(args[1].substring(args[1].indexOf('=') + 2,
						args[1].length())));
		chefStatusArr = new boolean[Integer.parseInt(args[1].substring(
				args[1].indexOf('=') + 2, args[1].length()))];
		Arrays.fill(chefStatusArr, true);

		Thread waiterThread = new Thread(new Waiter(restaurant.waitWaiterQueue,
				restaurant.waiterChefQueue, restaurant.chefOrderQueue));
		waiterThread.start();

		Thread chefThread = new Thread(new Chef(restaurant.chefOrderQueue));
		chefThread.start();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			while (true) {
				System.out.println("Enteritemid:");
				String line = br.readLine();
				try {
					line = line.trim();
					int itemNumber = Integer.parseInt(line);
					if (itemNumber >= 0
							&& ItemEnum.values().length >= itemNumber) {
						Order order = new Order();
						order.setItemNumber(itemNumber);
						order.setOrderTime(new Date());
						order.setOrderId("ORD"
								+ OrderGeneratorService.getInstance()
										.getOrderNumber());
						restaurant.waitWaiterQueue.offer(order);
					} else {
						FileLog.writeToFile(
								"Invalid Item Number : " + itemNumber);
					}
				} catch (Exception exception) {
					/*FileLog.writeToFile(
							"Excepiton occured : " + line
									+ " Only Number vales are allowed. ");*/
				}
			}
		} catch (Exception exception) {
			/*FileLog.writeToFile(
					"Excepiton occured : " + exception.getMessage());*/
		}
	}

	public synchronized static Integer getWaiter() {
		Integer waiterNumber = -1;
		for (int i = 0; i < waiterStatusArr.length; i++) {
			if (waiterStatusArr[i] == true) {
				waiterStatusArr[i] = false;
				waiterNumber = i;
				break;
			}
		}
		return waiterNumber;
	}

	public synchronized static Integer getChef() {
		Integer chefNumber = -1;
		for (int i = 0; i < chefStatusArr.length; i++) {
			if (chefStatusArr[i] == true) {
				chefStatusArr[i] = false;
				chefNumber = i;
				break;
			}
		}
		return chefNumber;
	}

}
