package io.github.zeshan.ORMStyle.example.domain.fig1;

import io.github.zeshan.ORMStyle.example.mapping.domain.fig1.PersonPerClassFig1;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "student_fig1")
public class StudentFig1 extends PersonPerClassFig1 {

    public String university;

    public StudentFig1() {
        super();
        university = "ECUST";
        name = "studentName";
    }
}