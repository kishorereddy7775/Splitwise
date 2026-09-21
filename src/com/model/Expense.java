package com.model;

import java.util.List;

public record Expense(int groupId, List<Split> splits) {

}
