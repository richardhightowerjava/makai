package com.caucho.pilot;

import io.jamp.core.AmpPublish;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: cmathias
 * Date: 5/1/13
 * Time: 11:09 PM
 * To change this template use File | Settings | File Templates.
 */
@AmpPublish(exported=true)
public class EntryService {

    private Logger logger = Logger.getLogger("EntryService");

    List<Employee> employees = new ArrayList<Employee>();

    public boolean addEmployee(Employee employee) {
        employee.setId(generateId());
        employees.add(employee);
        return true;
    }

    public boolean removeEmployee(Employee employee) {
        employees.remove(employee);
        return true;
    }

    public List<Employee> list() {
        return employees;
    }


    private long generateId() {
        return System.currentTimeMillis();
    }

}