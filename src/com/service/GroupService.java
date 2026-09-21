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

public class GroupService {

	//Acts like a database to store the Expenses of a Group
	private Map<Integer,List<Expense>> db;
	
	public GroupService(){
		db=new HashMap<>();
	}
	
	public void addExpense(Expense exp) {
		db.putIfAbsent(exp.groupId(), new ArrayList<>());
		db.get(exp.groupId()).add(exp);
	}
	
	public List<Expense> getExpenses(Group group){
		if(!db.containsKey(group.id())) {
			return null;
		}
		return db.get(group.id());
	}
	
	public List<Split> combineExpense(Group group){
		List<Expense> expenses=getExpenses(group);
		Map<User, Double> combinedExpenseMap=new HashMap<>();
		for(Expense exp:expenses) {
			for(Split s:exp.splits()) {
				combinedExpenseMap.put(s.user(), combinedExpenseMap.getOrDefault(s.user(), 0.0)+s.amount().val());
			}
		}
		List<Split> finalSpilts=new ArrayList<>();
		for(User u:combinedExpenseMap.keySet()) {
			Split s=new Split(u,new Amount(combinedExpenseMap.get(u),new Currency("Rupees")));
			finalSpilts.add(s);
		}
		return finalSpilts;
	}
}
