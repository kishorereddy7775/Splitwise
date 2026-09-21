package com.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.model.Amount;
import com.model.Currency;
import com.model.Expense;
import com.model.Group;
import com.model.PaymentSettlement;
import com.model.Split;
import com.model.User;

public class SplitService {
	
	private GroupService groupService;
	public SplitService(GroupService groupService) {
		this.groupService=groupService;
	}
	
	public void addEqualSplit(Group group, User user, Double amount) {
		int members = getMembers(group);
		double share = amount/members;
		List<Split> splits=new ArrayList<>();
		for(User u:group.users()) {
			if(u.equals(user)) {
				splits.add(getSplit(u,amount-share));
			}else {
				splits.add(getSplit(u,-share));
			}
		}
		Expense expense = new Expense(group.id(),splits);
		groupService.addExpense(expense);
	}
	
	public void addExactSplit(Group group, List<Split> splits) {
		Expense expense = new Expense(group.id(),splits);
		groupService.addExpense(expense);
	}
	
	public void addPercentSplit(Group group, User user, double amount, Map<User, Double> percentage) {
		List<Split> splits=new ArrayList<>();
		for(User u:group.users()) {
			double share = amount * percentage.get(u) * 0.01;
			if(u.equals(user)) {
				splits.add(getSplit(u,amount-share));
			}else {
				splits.add(getSplit(u,-share));
			}
		}
		Expense expense = new Expense(group.id(),splits);
		groupService.addExpense(expense);
	}
	
	public PaymentSettlement getFinalSettlement(Group group) {
		List<Split> splits=groupService.combineExpense(group);
		return getSettlement(splits);
	}
	
	private int getMembers(Group group) {
		return group.users().size();
	}
	
	private Split getSplit(User user, Double amount) {
		return new Split(user,new Amount(amount,new Currency("Rupees")));
	}
	
	private PaymentSettlement getSettlement(List<Split> splits) {
		
		PriorityQueue<Split> positiveOnes = new PriorityQueue<>((a,b)->(Double.compare(b.amount().val(), a.amount().val())));
		PriorityQueue<Split> negativeOnes = new PriorityQueue<>((a,b)->(Double.compare(a.amount().val(), b.amount().val())));
		for(Split s:splits) {
			if(s.amount().val()<0) {
				negativeOnes.add(s);
			}else {
				positiveOnes.add(s);
			}
		}
		Map<User,Map<User,Double>> settlement=new HashMap<>();
		while(!positiveOnes.isEmpty() && !negativeOnes.isEmpty()) {
			Split HighPositive=positiveOnes.poll();
			Split SmallNegative=negativeOnes.poll();
			double paid = Math.min(Math.abs(SmallNegative.amount().val()), HighPositive.amount().val());
			double remaining = HighPositive.amount().val()-paid;
			settlement.putIfAbsent(SmallNegative.user(),new HashMap<>());
			settlement.get(SmallNegative.user()).putIfAbsent(HighPositive.user(), paid);
			if(HighPositive.amount().val()==Math.abs(SmallNegative.amount().val())) {
				continue;
			}else if(remaining>0) {
				positiveOnes.add(new Split(HighPositive.user(),new Amount(remaining,new Currency("Rupees"))));
				
			}else {
				negativeOnes.add(new Split(SmallNegative.user(),new Amount(SmallNegative.amount().val()-paid,new Currency("Rupees"))));
			}
		}
		return new PaymentSettlement(settlement);
	}

}
