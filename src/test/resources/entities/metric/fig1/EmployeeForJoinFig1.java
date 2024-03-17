package io.github.zeshan.ORMStyle.example.mapping.domain.fig1;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "employee_fig1")
public class EmployeeForJoinFig1 extends PersonJoinFig1 {
    Double salary;
    String department;

    public EmployeeForJoinFig1() {
        salary = 666.66;
        department = "Royal Vehicle Maintenance";
        name = "W. Sun";
    }
}