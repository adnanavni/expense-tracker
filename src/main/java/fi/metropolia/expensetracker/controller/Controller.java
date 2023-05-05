package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.module.SalarySingle;
import fi.metropolia.expensetracker.module.Variables;
/**
 * Interface to most of the controllers to set basic methods to every object.
 * */
public interface Controller {

    public void initialize();


    public void setVariables(SalarySingle salary, Variables variables);
}
