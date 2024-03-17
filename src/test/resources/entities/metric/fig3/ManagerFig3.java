package io.github.zeshan.ORMStyle.example.domain.fig3;


import io.github.zeshan.ORMStyle.example.mapping.domain.fig3.EmployeeFig3;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("manager_fig3")
public class ManagerFig3 extends EmployeeFig3 {

    public Double bonus;

    public ManagerFig3() {
        bonus = 10086.0;
    }
}