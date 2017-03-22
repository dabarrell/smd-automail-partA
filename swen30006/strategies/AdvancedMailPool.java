package strategies;

import automail.Building;
import automail.IMailPool;
import automail.MailItem;
import com.sun.xml.internal.ws.api.pipe.Tube;
import exceptions.TubeFullException;

import java.util.*;

/**
 * Sample of what a MailPool could look like.
 * This one tosses the incoming mail on a pile and takes the outgoing mail from the top.
 */
public class AdvancedMailPool implements IMailPool {

    private HashMap<Integer, List<MailItem>> floors = new HashMap<>();

    public AdvancedMailPool(){
        for (int i = Building.LOWEST_FLOOR; i < Building.LOWEST_FLOOR + Building.FLOORS; i++) {
            floors.put(i, new ArrayList<>());
        }
    }

    @Override
    public void addToPool(MailItem mailItem){
        List<MailItem> floor = floors.get(mailItem.getDestFloor());

        double priority = getPriorityWeight(mailItem);

        for (int i = 0; i < floor.size(); i++) {
            if (priority > getPriorityWeight(floor.get(i))) {
                floor.add(i, mailItem);
                return;
            }
        }

        floor.add(mailItem);
    }
    
    public boolean isEmptyPool(){
        for(Map.Entry<Integer, List<MailItem>> entry : floors.entrySet()) {
            Integer floorNum = entry.getKey();
            List<MailItem> floor = entry.getValue();
            if (!floor.isEmpty()) {
                return false;
            }

        }
        return true;
    }

    public MailItem get(int floor, int remainingSpace) throws TubeFullException {
        for (MailItem item : floors.get(floor)) {
            if (item.getSize() <= remainingSpace) {
                return item;
            }
        }
        return floors.get(floor).get(0);
    }

    public void remove(int floor, MailItem item) {
        floors.get(floor).remove(item);
    }

    public HashMap<Integer, List<MailItem>> getFloors() {
        return floors;
    }

    public double getPriorityWeight(MailItem mailItem) {
        double priority = 0;
        switch (mailItem.getPriorityLevel()) {
            case "LOW":
                priority = 1;
                break;
            case "MEDIUM":
                priority = 1.75;
                break;
            case "HIGH":
                priority = 2.5;
                break;
        }
        return priority;
    }
}
