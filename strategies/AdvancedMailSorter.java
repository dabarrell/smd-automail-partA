package strategies;

import automail.*;
import exceptions.TubeFullException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * A sample class for sorting mail:  this strategy just takes a MailItem
 * from the MailPool (if there is one) and attempts to add it to the Robot's storageTube.
 * If the MailItem doesn't fit, it will tell the robot to start delivering (return true).
 */
public class AdvancedMailSorter implements IMailSorter{

    public AdvancedMailPool advancedMailPool;

    int nextFloor = -1;

	public AdvancedMailSorter(AdvancedMailPool advancedMailPool) {
		this.advancedMailPool = advancedMailPool;
	}
    /**
     * Fills the storage tube
     */
    @Override
    public boolean fillStorageTube(StorageTube tube) {

        try{
            if (!advancedMailPool.isEmptyPool()) {
                if (nextFloor == -1 || advancedMailPool.getFloors().get(nextFloor).isEmpty()) {
                    nextFloor = getNextFloor(nextFloor);
                    if (nextFloor == -1) {
                        return true;
                    }
                }

	            MailItem mailItem = advancedMailPool.get(nextFloor, tube.MAXIMUM_CAPACITY - tube.getTotalOfSizes());
	            /** Add the item to the tube */
	            tube.addItem(mailItem);
                advancedMailPool.remove(nextFloor, mailItem);
            }
        }
        /** Refer to TubeFullException.java --
         *  Usage below illustrates need to handle this exception. However you should
         *  structure your code to avoid the need to catch this exception for normal operation
         */
        catch(TubeFullException e){
            nextFloor = -1;
        	return true;
        }      
        /** 
         * Handles the case where the last delivery time has elapsed and there are no more
         * items to deliver.
         */
        if(Clock.Time() > Clock.LAST_DELIVERY_TIME && advancedMailPool.isEmptyPool() && !tube.isEmpty()){
            nextFloor = -1;
            return true;
        }
        return false;

    }

    public int getNextFloor(int prevFloor) {

        HashMap<Integer, List<MailItem>> floors = advancedMailPool.getFloors();

        int nextFloor = -1;
        double highestPriority = 0;

        if (prevFloor == -1) {
            for (int i = Building.LOWEST_FLOOR; i < Building.LOWEST_FLOOR + Building.FLOORS; i++) {
                double totalPriority = 0;
                List<MailItem> floor = floors.get(i);
                if (!floor.isEmpty()) {
                    for (MailItem mailItem : floor) {
                        totalPriority += advancedMailPool.getPriorityWeight(mailItem);
                    }
                }
                if (totalPriority > highestPriority) {
                    highestPriority = totalPriority;
                    nextFloor = i;
                }
            }
        } else if (prevFloor > Building.MAILROOM_LOCATION) {
            int distanceFromPrevFloor = Building.FLOORS + 1;
            //printMailItems();
            for (int i = Building.MAILROOM_LOCATION; i < Building.LOWEST_FLOOR + Building.FLOORS; i++) {
                if (!floors.get(i).isEmpty() && Math.abs(prevFloor - i) < distanceFromPrevFloor) {
                    distanceFromPrevFloor = Math.abs(prevFloor - i);
                    nextFloor = i;
                }
            }
        } else {
            int distanceFromPrevFloor = Building.FLOORS + 1;
            //printMailItems();
            for (int i = Building.LOWEST_FLOOR; i <= Building.MAILROOM_LOCATION; i++) {
                if (!floors.get(i).isEmpty() && Math.abs(prevFloor - i) < distanceFromPrevFloor) {
                    distanceFromPrevFloor = Math.abs(prevFloor - i);
                    nextFloor = i;
                }
            }
        }
        return nextFloor;
    }

    public void printMailItems() {
        for(Map.Entry<Integer, List<MailItem>> entry : advancedMailPool.getFloors().entrySet()) {
            Integer floorNum = entry.getKey();
            List<MailItem> floor = entry.getValue();
            System.out.println("Floor: " + floorNum + ", size: " + floor.size());
        }
    }
}
