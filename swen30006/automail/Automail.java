package automail;

import strategies.*;

public class Automail {
	      
    public Robot robot;
    public IMailPool mailPool;
    
    Automail(IMailDelivery delivery, Boolean args2) {
        IMailSorter sorter;

    /** CHANGE NOTHING ABOVE HERE */
    	if (args2) {
            /** Initialize the MailPool */
            AdvancedMailPool advancedMailPool = new AdvancedMailPool();
            mailPool = advancedMailPool;

            /** Initialize the MailSorter */
            sorter = new AdvancedMailSorter(advancedMailPool);
        } else {

            /** CHANGE NOTHING BELOW HERE */

            SimpleMailPool simpleMailPool = new SimpleMailPool();
            mailPool = simpleMailPool;

            /** Initialize the MailSorter */
            sorter = new SimpleMailSorter(simpleMailPool);
        }
    	
    	/** Initialize robot */
    	robot = new Robot(sorter, delivery);
    	
    }
    
}
