package com.model;

import java.util.List;

public record Group(int id, String name, List<User> users) {

}
