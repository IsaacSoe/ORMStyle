package io.github.zeshan.ORMStyle.example.mapping.domain.fig1;


import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "employee_fig1")
public class EmployeePerClassFig1 extends PersonPerClassFig1 {
    Double salary;
    String department;

    public EmployeePerClassFig1() {
        salary = 666.66;
        department = "Royal Vehicle Maintenance";
        name = "W. Sun";
    }
}