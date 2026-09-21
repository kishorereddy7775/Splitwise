package com.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model.Amount;
import com.model.Currency;
import com.model.Group;
import com.model.Split;
import com.model.User;
import com.service.GroupService;
import com.service.SplitService;

public class SplitwiseTest {

	public static void main(String[] args) {
		GroupService groupService = new GroupService();
		SplitService splitService = new SplitService(groupService); 
		
		User u1 = new User("1","Rohit");
		User u2 = new User("2","Ramu");
		User u3 = new User("3","Ravi");
		Group g1 = new Group(1,"ThunderBuddies",List.of(u1,u2,u3));
		
		Split s1 = new Split(u1,new Amount(400.0,new Currency("Rupees")));
		Split s2 = new Split(u2,new Amount(-200.0,new Currency("Rupees")));
		Split s3 = new Split(u3,new Amount(-200.0,new Currency("Rupees")));
		splitService.addExactSplit(g1, List.of(s1,s2,s3));
		
		Split s4 = new Split(u1,new Amount(-400.0,new Currency("Rupees")));
		Split s5 = new Split(u2,new Amount(800.0,new Currency("Rupees")));
		Split s6 = new Split(u3,new Amount(-400.0,new Currency("Rupees")));
		splitService.addExactSplit(g1, List.of(s4,s5,s6));
		
		Split s7 = new Split(u1,new Amount(-800.0,new Currency("Rupees")));
		Split s8 = new Split(u2,new Amount(-300.0,new Currency("Rupees")));
		Split s9 = new Split(u3,new Amount(1100.0,new Currency("Rupees")));
		splitService.addExactSplit(g1, List.of(s7,s8,s9));
		
		
		splitService.addEqualSplit(g1, u1, 1500.0);
		Map<User,Double> map=new HashMap<>();
		map.put(u1, 25.0);
		map.put(u2, 35.0);
		map.put(u3, 40.0);
		splitService.addPercentSplit(g1, u3, 1900.0, map);
		Map<User,Map<User,Double>> payments = splitService.getFinalSettlement(g1).payments();
		
		for(User sender:payments.keySet()) {
			for(User receiver:payments.get(sender).keySet()) {
				System.out.println(sender.name()+"-->"+receiver.name()+"-->"+payments.get(sender).get(receiver));
			}
		}
		
	}

}
