package fi.metropolia.expensetracker.controller;

import fi.metropolia.expensetracker.module.SalarySingle;
import fi.metropolia.expensetracker.module.Variables;

public interface Controller {

    public void initialize();


    public void setVariables(SalarySingle salary , Variables variables);
}
