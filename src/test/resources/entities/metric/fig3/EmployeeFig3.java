package io.github.zeshan.ORMStyle.example.domain.fig3;


import io.github.zeshan.ORMStyle.example.mapping.domain.fig3.PersonSingleTableFig3;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("employee_fig3")
public class EmployeeFig3 extends PersonSingleTableFig3 {

    public Double salary;

    public EmployeeFig3() {
        super();
        salary = 3.0;
    }
}